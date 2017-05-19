/*
 * Copyright (C) 2017 Orange
 *
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE.txt' in this package distribution
 * or at 'http://www.apache.org/licenses/LICENSE-2.0'.
 */
package com.orange.oswe.demo.woofer.webfront.domain;

/**
 * Helper class that computes possible link actions between me and a user
 */
public class UserLinkHelper {
	public enum LinkAction {
		NONE, SUBSCRIBE, UNSUBSCRIBE
	}

	public static LinkAction getLinkAction(User me, User other) {
		if (me == null || me.equals(other)) {
			return LinkAction.NONE;
		} else if (me.getFollowees().contains(other)) {
			return LinkAction.UNSUBSCRIBE;
		} else {
			return LinkAction.SUBSCRIBE;
		}
	}
}
