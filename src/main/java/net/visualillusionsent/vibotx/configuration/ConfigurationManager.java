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
package net.visualillusionsent.vibotx.configuration;

import net.visualillusionsent.utils.FileUtils;
import net.visualillusionsent.utils.JarUtils;
import net.visualillusionsent.utils.PropertiesFile;
import net.visualillusionsent.vibotx.FirstTimeRunException;
import net.visualillusionsent.vibotx.VIBotX;
import org.pircbotx.Configuration;
import org.pircbotx.UtilSSLSocketFactory;

import javax.net.ssl.SSLSocketFactory;
import java.io.File;
import java.nio.charset.Charset;

/**
 * Configuration Manager and Loader
 *
 * @author Jason (darkdiplomat)
 */
public final class ConfigurationManager {
    private static boolean ghostNick;

    private ConfigurationManager() {
    }

    public static void loadConfig(File universe, Configuration.Builder<VIBotX> cfgbuild) throws Exception {
        testForConfig(universe);
        PropertiesFile cfg = new PropertiesFile(universe.getAbsolutePath().concat("/vibotx.cfg"));

        ghostNick = cfg.getBoolean("ghost.nick");

        cfgbuild.setServer(cfg.getString("server.hostname"), cfg.getInt("server.port"))
                .setServerPassword(emptyToNull(cfg.getString("server.password")))
                .setName(cfg.getString("bot.nick"))
                .setLogin(cfg.getString("bot.ident"))
                .setNickservPassword(emptyToNull(cfg.getString("bot.nickserv.pass")))
                .setIdentServerEnabled(cfg.getBoolean("use.ident"))
                .setEncoding(Charset.forName("UTF-8"))
                .setMessageDelay(cfg.getLong("message.delay"));

        if (cfg.getBoolean("use.ssl")) {
            if (cfg.getBoolean("ssl.trustall")) {
                cfgbuild.setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates());
            }
            else {
                cfgbuild.setSocketFactory(SSLSocketFactory.getDefault());
            }
        }

        for (String chan : cfg.getStringArray("default.channels")) {
            if (chan.contains(":")) {
                String[] chanKey = chan.split(":", 1);
                cfgbuild.addAutoJoinChannel(chanKey[0], chanKey[1]);
            }
            else {
                cfgbuild.addAutoJoinChannel(chan);
            }
        }

        //Initialize BotOpsManager now
        BotOpsManager.touch(universe);
    }

    private static final void testForConfig(File universe) throws Exception {
        File cfg = new File(universe, "vibotx.cfg");
        if (!cfg.exists()) {
            FileUtils.cloneFileFromJar(JarUtils.getJarPath(VIBotX.class), "resources/default.cfg", cfg.getAbsolutePath());
            throw new FirstTimeRunException();
        }
    }

    private static String emptyToNull(String value) {
        if (value.isEmpty())
            return null;
        return value;
    }

    public static boolean shouldGhostNick() {
        return ghostNick;
    }
}
