/*
 * Copyright (C) 2017 Orange
 *
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE.txt' in this package distribution
 * or at 'http://www.apache.org/licenses/LICENSE-2.0'.
 */
package com.orange.oswe.demo.woofer.webfront.service;

import javax.servlet.http.HttpSession;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionCreationEvent;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;

/**
 * A service that keeps track of anonymous and authenticated sessions
 */
@Service("usersService")
public class LoggedInUsersService {

    private static final Logger logger = LoggerFactory.getLogger(LoggedInUsersService.class);

    private static final String ANONYMOUS = "_anonymous_";

    @Autowired
    private GaugeService gaugeService;

    private final Multimap<String, String> user2SessionIds = TreeMultimap.create();

    private String getUserName(Authentication authentication, String dflt) {
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof User) {
            return ((User) authentication.getPrincipal()).getUsername();
        } else {
            // anonymous
            return dflt;
        }
    }

    private String getSessionId(Authentication authentication, String dflt) {
        if (authentication != null && authentication.isAuthenticated() && authentication.getDetails() instanceof WebAuthenticationDetails) {
            String sessionId = ((WebAuthenticationDetails) authentication.getDetails()).getSessionId();
            return sessionId == null ? dflt : sessionId;
        } else {
            // anonymous
            return dflt;
        }
    }

    private void updateGauge() {
        int anonymous = countAnonymousConnections();
        gaugeService.submit("sessions.anonymous.count", anonymous);
        gaugeService.submit("sessions.authenticated.count", countAllConnections() - anonymous);
    }

    @EventListener
    void onAuthenticationSuccess(InteractiveAuthenticationSuccessEvent event) {
        Authentication authentication = event.getAuthentication();
        String userName = getUserName(authentication, ANONYMOUS);
        String sessionId = getSessionId(authentication, null);
        logger.info("authentication successful: {} ({})", userName, sessionId);
        // remove session ID from anonymous
        user2SessionIds.remove(ANONYMOUS, sessionId);
        // add session ID to user
        user2SessionIds.put(userName, sessionId);

        // update gauge
        updateGauge();
    }

    @EventListener
    void onSessionCreated(SessionCreationEvent event) {
        HttpSession session = (HttpSession) event.getSource();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = getUserName(authentication, ANONYMOUS);
        String sessionId = getSessionId(authentication, session.getId());
        logger.info("session created: {} ({})", userName, sessionId);
        // remove session ID from anonymous
        user2SessionIds.remove(ANONYMOUS, sessionId);
        // add session ID to user
        user2SessionIds.put(userName, sessionId);

        // update gauge
        updateGauge();
    }

    @EventListener
    void onSessionDestroyed(SessionDestroyedEvent event) {
        HttpSession session = (HttpSession) event.getSource();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = getUserName(authentication, ANONYMOUS);
        String sessionId = getSessionId(authentication, session.getId());
        logger.info("session destroyed: {} ({})", userName, sessionId);
        // remove session ID from user
        user2SessionIds.remove(userName, sessionId);

        // update gauge
        updateGauge();
    }

    public boolean isConnected(String userName) {
        return userName != null && countConnectionsFor(userName) > 0;
    }

    public int countConnectionsFor(String userName) {
        return userName == null ? 0 : user2SessionIds.get(userName).size();
    }

    public int countAnonymousConnections() {
        return countConnectionsFor(ANONYMOUS);
    }

    public int countAllConnections() {
        return user2SessionIds.size();
    }
}
