/*
 * Copyright (C) 2017 Orange
 *
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE.txt' in this package distribution
 * or at 'http://www.apache.org/licenses/LICENSE-2.0'.
 */
package ch.qos.logback.core.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import ch.qos.logback.classic.pattern.ThrowableHandlingConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.util.LevelToSyslogSeverity;
import ch.qos.logback.core.Layout;

/**
 * Specific appender for RTLog (internal Orange log collector)
 *
 * Complies to <a href="http://shp.itn.ftgroup/sites/rtlog/Autres%20collecteur%20format%20des%20logs/Format%20des%20messages.aspx">expected format</a>,
 * pushes automatically all {@link org.slf4j.MDC} context as RTLog metadata fields
 */
public class RTLogAppender extends SyslogAppenderBase<ILoggingEvent> {

    String localHostName;
    String program;
    String product;
    String logfile;
    String basicat;
    int facility;
    ThrowableHandlingConverter throwableConverter;

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getBasicat() {
        return basicat;
    }

    public void setBasicat(String basicat) {
        this.basicat = basicat;
    }

    public ThrowableHandlingConverter getThrowableConverter() {
        return throwableConverter;
    }

    public void setThrowableConverter(ThrowableHandlingConverter throwableConverter) {
        this.throwableConverter = throwableConverter;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getLogfile() {
        return logfile;
    }

    public void setLogfile(String logfile) {
        this.logfile = logfile;
    }

    @Override
    public void start() {
        facility = SyslogAppenderBase.facilityStringToint(getFacility());
        localHostName = getLocalHostname();

        super.start();
    }

    @Override
    public SyslogOutputStream createOutputStream() throws SocketException, UnknownHostException {
        return new SyslogOutputStream(getSyslogHost(), getPort());
    }

    @Override
    public Layout<ILoggingEvent> buildLayout() {
        return null;
    }

    /**
     * Convert a level to equivalent syslog severity. Only levels for printing
     * methods i.e DEBUG, WARN, INFO and ERROR are converted.
     *
     * @see ch.qos.logback.core.net.SyslogAppenderBase#getSeverityForEvent(Object)
     */
    @Override
    public int getSeverityForEvent(Object eventObject) {
        ILoggingEvent event = (ILoggingEvent) eventObject;
        return LevelToSyslogSeverity.convert(event);
    }

    @Override
    protected void append(ILoggingEvent event) {
        if (!isStarted()) {
            return;
        }

        try {
            StringBuilder sb = new StringBuilder();

            int pri = facility + LevelToSyslogSeverity.convert(event);

            // PRI
            sb.append("<");
            sb.append(pri);
            sb.append(">");

            // VERSION
            sb.append("1 ");

            // TIMESTAMP (ISO 8601)
            Instant timestamp = Instant.ofEpochMilli(event.getTimeStamp());
            sb.append(DateTimeFormatter.ISO_INSTANT.format(timestamp));
            sb.append(' ');

            // HOSTNAME
            sb.append(localHostName);
            sb.append(' ');

            // PROGRAM
            sb.append(program);
            sb.append(' ');

            // PID
            sb.append("- ");

            // MSG ID
            sb.append("- ");

            // META Sequence ID​
            sb.append("[meta sequenceid=\"0000\"]");

            // META RTLog
            sb.append("[rtlog@1368 rtlog_river=\"rtl\"");
            // basicat
            sb.append(" basicat=\"");
            sb.append(basicat);
            sb.append('\"');

            // logger
            sb.append(" logger=\"");
            sb.append(event.getLoggerName());
            sb.append('\"');

            // thread
            sb.append(" thread=\"");
            sb.append(event.getThreadName());
            sb.append('\"');

            // product MD
            if(product != null) {
                sb.append(" produit=\"");
                sb.append(product);
                sb.append('\"');
            }

            // logfile MD
            if(logfile != null) {
                sb.append(" logfile=\"");
                sb.append(logfile);
                sb.append('\"');
            }
            // then all MDC as MD
            Map<String, String> ctx = event.getMDCPropertyMap();
            for(Map.Entry<String, String> e : ctx.entrySet()) {
                sb.append(' ');
                sb.append(e.getKey());
                sb.append("=\"");
                sb.append(e.getValue());
                sb.append('\"');
            }
            sb.append("] ");
            String prefix = sb.toString();

            // level (as a String)
            sb.append(event.getLevel().toString());
            sb.append(' ');

            // message
            sb.append(event.getFormattedMessage());

            // then stack trace
            IThrowableProxy tp = event.getThrowableProxy();
            if (tp != null) {
                String convertedStackTrace = throwableConverter.convert(event);
                if(convertedStackTrace != null && !convertedStackTrace.isEmpty()) {
                    sb.append('\n');
                    sb.append(convertedStackTrace);
                }
            }

            // then push
            String msg = sb.toString();
            sos.write(msg.getBytes(charset));
            sos.flush();
        } catch (IOException ioe) {
            addError("Failed to send diagram to " + syslogHost, ioe);
        }
    }

    /**
     * This method gets the network name of the machine we are running on.
     * Returns "UNKNOWN_LOCALHOST" in the unlikely case where the host name
     * cannot be found.
     * @return String the name of the local host
     */
    public String getLocalHostname() {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            return addr.getHostName();
        } catch (UnknownHostException uhe) {
            addError("Could not determine local host name", uhe);
            return "UNKNOWN_LOCALHOST";
        }
    }

}
