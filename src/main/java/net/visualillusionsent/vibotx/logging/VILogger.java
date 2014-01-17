/*
 * This file is part of VIBotX.
 *
 * Copyright © 2014 Visual Illusions Entertainment
 *
 * VIBotX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/lgpl.html.
 */
package net.visualillusionsent.vibotx.logging;

import net.visualillusionsent.utils.FileUtils;
import net.visualillusionsent.utils.JarUtils;
import net.visualillusionsent.utils.SystemUtils;
import net.visualillusionsent.utils.UnmodifiablePropertiesFile;
import net.visualillusionsent.utils.UtilityException;
import net.visualillusionsent.vibotx.VIBotX;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static net.visualillusionsent.vibotx.logging.Level.ACTION;
import static net.visualillusionsent.vibotx.logging.Level.ALL;
import static net.visualillusionsent.vibotx.logging.Level.CHANNELINFO;
import static net.visualillusionsent.vibotx.logging.Level.COMMAND;
import static net.visualillusionsent.vibotx.logging.Level.CONFIG;
import static net.visualillusionsent.vibotx.logging.Level.CONNECT;
import static net.visualillusionsent.vibotx.logging.Level.DEBUG;
import static net.visualillusionsent.vibotx.logging.Level.DISCONNECT;
import static net.visualillusionsent.vibotx.logging.Level.ERROR;
import static net.visualillusionsent.vibotx.logging.Level.FINE;
import static net.visualillusionsent.vibotx.logging.Level.FINER;
import static net.visualillusionsent.vibotx.logging.Level.FINEST;
import static net.visualillusionsent.vibotx.logging.Level.FINGER;
import static net.visualillusionsent.vibotx.logging.Level.INFO;
import static net.visualillusionsent.vibotx.logging.Level.INVITE;
import static net.visualillusionsent.vibotx.logging.Level.JOIN;
import static net.visualillusionsent.vibotx.logging.Level.KICK;
import static net.visualillusionsent.vibotx.logging.Level.MESSAGE;
import static net.visualillusionsent.vibotx.logging.Level.NOTICE;
import static net.visualillusionsent.vibotx.logging.Level.PART;
import static net.visualillusionsent.vibotx.logging.Level.PING;
import static net.visualillusionsent.vibotx.logging.Level.PRIVATEMESSAGE;
import static net.visualillusionsent.vibotx.logging.Level.SEVERE;
import static net.visualillusionsent.vibotx.logging.Level.WARNING;

/**
 * @author Jason (darkdiplomat)
 */
public final class VILogger extends Logger {

    /* VIBotX Logging Levels Enabled Configuration */
    private static final UnmodifiablePropertiesFile log_levels_enabled;

    static {
        File log_props = new File(VIBotX.getUniverse(), "log_levels.cfg");
        if (!log_props.exists()) {
            FileUtils.cloneFileFromJar(JarUtils.getJarPath(VILogger.class), "resources/default_log_levels.cfg", "log_levels.cfg");
        }
        log_levels_enabled = new UnmodifiablePropertiesFile(VIBotX.getUniverse().getAbsolutePath().concat("/log_levels.cfg"));
    }

    public VILogger(String name) {
        super(name, null);
        this.setLevel(Level.ALL);
        this.addHandler(new ConsoleHandler());
        try {
            File log_dir = new File(VIBotX.getUniverse(), "logs/" + name + "/");
            if (!log_dir.exists()) {
                log_dir.mkdirs();
            }
            if (log_dir.isDirectory()) {
                this.addHandler(new FileHandler("logs/global-log%g.log", 655360, 20, true)); //5Mb file size limit, 20 files max
                this.addHandler(new FileHandler("logs/" + name + "/log%g.log", 655360, 5, true)); //5Mb file size limit, 5 files max
            }
        }
        catch (IOException ioex) {
            // Shouldn't be any issues with this
        }
        for (Handler handler : this.getHandlers()) {
            handler.setLevel(Level.ALL);
            handler.setFormatter(new BotLogFormat());
            try {
                handler.setEncoding("UTF-8");
            }
            catch (UnsupportedEncodingException ueex) {
                // Shouldn't be any issues with this
            }
        }
        for (Handler handler : this.getHandlers()) {
            handler.setLevel(Level.ALL);
            handler.setFormatter(new BotLogFormat());
            try {
                handler.setEncoding("UTF-8");
            }
            catch (UnsupportedEncodingException ueex) {
                // Shouldn't be any issues with this
            }
        }
    }

    @Override
    public final void setLevel(Level newLevel) { //Don't allow Level to be changed to anything other than all
        if (ALL == newLevel) {
            super.setLevel(ALL);
        }
    }

    public final void action(String msg) {
        if (!checkLevelEnabled(ACTION)) {
            return;
        }
        log(ACTION, msg);
    }

    public final void channelinfo(String msg) {
        if (!checkLevelEnabled(CHANNELINFO)) {
            return;
        }
        log(CHANNELINFO, msg);
    }

    public final void command(String msg) {
        if (!checkLevelEnabled(COMMAND)) {
            return;
        }
        log(COMMAND, msg);
    }

    @Override
    public final void config(String msg) {
        if (!checkLevelEnabled(CONFIG)) {
            return;
        }
        log(CONFIG, msg);
    }

    public final void connect(String msg) {
        if (!checkLevelEnabled(CONNECT)) {
            return;
        }
        log(CONNECT, msg);
    }

    public final void debug(String msg) {
        if (!checkLevelEnabled(DEBUG)) {
            return;
        }
        log(DEBUG, msg);
    }

    public final void debug(String msg, Throwable thrown) {
        if (!checkLevelEnabled(DEBUG)) {
            return;
        }
        log(DEBUG, msg, thrown);
    }

    public final void disconnect(String msg) {
        if (!checkLevelEnabled(DISCONNECT)) {
            return;
        }
        log(DISCONNECT, msg);
    }

    public final void error(String msg) {
        if (!checkLevelEnabled(ERROR)) {
            return;
        }
        log(ERROR, msg);
    }

    public final void error(String msg, Throwable thrown) {
        if (!checkLevelEnabled(ERROR)) {
            return;
        }
        log(ERROR, msg, thrown);
    }

    @Override
    public final void fine(String msg) {
        if (!checkLevelEnabled(FINE)) {
            return;
        }
        log(FINE, msg);
    }

    @Override
    public final void finer(String msg) {
        if (!checkLevelEnabled(FINER)) {
            return;
        }
        log(FINER, msg);
    }

    @Override
    public final void finest(String msg) {
        if (!checkLevelEnabled(FINEST)) {
            return;
        }
        log(FINEST, msg);
    }

    public final void finger(String msg) {
        if (!checkLevelEnabled(FINGER)) {
            return;
        }
        log(FINGER, msg);
    }

    @Override
    public final void info(String msg) {
        if (!checkLevelEnabled(INFO)) {
            return;
        }
        log(INFO, msg);
    }

    public final void invite(String msg) {
        if (!checkLevelEnabled(INVITE)) {
            return;
        }
        log(INVITE, msg);
    }

    public final void join(String msg) {
        if (!checkLevelEnabled(JOIN)) {
            return;
        }
        log(JOIN, msg);
    }

    public final void kick(String msg) {
        if (!checkLevelEnabled(KICK)) {
            return;
        }
        log(KICK, msg);
    }

    public final void message(String msg) {
        if (!checkLevelEnabled(MESSAGE)) {
            return;
        }
        log(MESSAGE, msg);
    }

    public final void notice(String msg) {
        if (!checkLevelEnabled(NOTICE)) {
            return;
        }
        log(NOTICE, msg);
    }

    public final void part(String msg) {
        if (!checkLevelEnabled(PART)) {
            return;
        }
        log(PART, msg);
    }

    public final void ping(String msg) {
        if (!checkLevelEnabled(PING)) {
            return;
        }
        log(PING, msg);
    }

    public final void privatemessage(String msg) {
        if (!checkLevelEnabled(PRIVATEMESSAGE)) {
            return;
        }
        log(PRIVATEMESSAGE, msg);
    }

    @Override
    public final void severe(String msg) {
        if (!checkLevelEnabled(SEVERE)) {
            return;
        }
        log(SEVERE, msg);
    }

    public final void severe(String msg, Throwable thrown) {
        if (!checkLevelEnabled(SEVERE)) {
            return;
        }
        log(SEVERE, msg, thrown);
    }

    @Override
    public final void warning(String msg) {
        if (!checkLevelEnabled(WARNING)) {
            return;
        }
        log(WARNING, msg);
    }

    @Override
    public void log(Level level, String msg) {
        if (!checkLevelEnabled(level)) {
            return;
        }
        super.log(level, msg);
    }

    @Override
    public void log(Level level, String msg, Throwable thrown) {
        if (!checkLevelEnabled(level)) {
            return;
        }
        super.log(level, msg, thrown);
    }

    private final boolean checkLevelEnabled(Level level) {
        try {
            return log_levels_enabled.getBoolean(level.getName().toLowerCase().concat(".enabled"));
        }
        catch (UtilityException uex) {
            System.err.println("Missing Log Level Key for Level: '" + level.getName() + "'. Defaulting to allow logging....");
            return true;
        }
    }

    /**
     * Bot Logger Formatter
     *
     * @author Jason (darkdiplomat)
     */
    private final class BotLogFormat extends SimpleFormatter {

        /**
         * The {@link java.text.SimpleDateFormat} to use for logging
         */
        private final SimpleDateFormat dateform = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        private final String loggerLevel = " [%s] [%s] ";

        /**
         * Formats the log output
         *
         * @param rec
         *         the {@link java.util.logging.LogRecord} to be formatted
         *
         * @return formatted {@link java.util.logging.LogRecord} as a {@link String}
         */
        @Override
        public final String format(LogRecord rec) {
            StringBuilder message = new StringBuilder();

            message.append(dateform.format(rec.getMillis()));
            message.append(String.format(loggerLevel, rec.getLoggerName(), rec.getLevel().getName()));
            message.append(rec.getMessage());
            message.append(SystemUtils.LINE_SEP);

            if (rec.getThrown() != null) {
                StringWriter stringwriter = new StringWriter();
                rec.getThrown().printStackTrace(new PrintWriter(stringwriter));
                message.append(stringwriter.toString());
            }

            return message.toString();
        }
    }
}
