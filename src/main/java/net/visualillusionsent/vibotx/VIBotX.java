/*
 * This file is part of VIBotX.
 *
 * Copyright © 2012-2014 Visual Illusions Entertainment
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
package net.visualillusionsent.vibotx;

import net.visualillusionsent.utils.JarUtils;
import net.visualillusionsent.utils.ProgramChecker;
import net.visualillusionsent.utils.ProgramStatus;
import net.visualillusionsent.utils.StringUtils;
import net.visualillusionsent.vibotx.api.plugin.JavaPluginLoader;
import net.visualillusionsent.vibotx.api.plugin.Plugin;
import net.visualillusionsent.vibotx.api.plugin.PluginManifestAttributes;
import net.visualillusionsent.vibotx.configuration.ConfigurationManager;
import net.visualillusionsent.vibotx.logging.VILogger;
import org.pircbotx.Colors;
import org.pircbotx.Configuration;
import org.pircbotx.IdentServer;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.Manifest;

import static net.visualillusionsent.vibotx.api.plugin.PluginManifestAttributes.*;

/**
 * VIBotX - The Visual Illusions IRC Bot
 *
 * @author Jason (darkdiplomat)
 */
public final class VIBotX extends PircBotX implements Plugin {
    public static final File universe;
    public static final VILogger log;
    protected static VIBotX bot;
    private static JavaPluginLoader jpLoader;
    private static ProgramChecker pCheck;
    private static Manifest mf;

    static {
        String universe_path = System.getProperty("vibotx.universe.path", ".");
        File tempUni = new File(".");
        try {
            tempUni = new File(universe_path).getCanonicalFile();
            if (!tempUni.exists()) {
                tempUni = new File(".").getCanonicalFile();
            }
        } catch (IOException e) {
            //TODO?
        }
        universe = tempUni;
        log = new VILogger("VIBotX");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                if (jpLoader != null) {
                    jpLoader.disableAllPlugins();
                }
                log.close();
            }
        });
    }

    public VIBotX(Configuration<VIBotX> configuration) {
        super(configuration);
        jpLoader = new JavaPluginLoader();
    }

    public static void main(String[] args) {
        if (System.console() == null && !GraphicsEnvironment.isHeadless()) { // Check if we just double clicked the jar to run it...
            /* //TODO: WIP
            // Create a Console
            BotConsoleGUI.createAndShowGUI();
            */
            System.exit(1);
            return; // just in case
        }
        log.info("VIBotX - Visual Illusions IRC Bot is starting...");
        pokeManifest();
        try {
            String tempVersion = getVersionStatic();
            String statusStr = "STABLE";
            if (tempVersion.contains("-SNAPSHOT")) {
                tempVersion = tempVersion.replace("-SNAPSHOT", "");
                statusStr = "SNAPSHOT";
            }
            pCheck = new ProgramChecker("VIBotX", StringUtils.stringToLongArray(tempVersion, "."), getStatusURL(), ProgramStatus.fromString(statusStr));
        } catch (Exception ex) {
            log.debug("Failed to initiation ProgramChecker", ex);
        }
        if (pCheck != null) {
            ProgramChecker.Status status = pCheck.checkStatus();
            if (status == ProgramChecker.Status.UPDATE) {
                pCheck.getStatusMessage();
            }
        }

        Configuration.Builder<VIBotX> cfgbuild = new Configuration.Builder<VIBotX>()
                .setVersion("VIBotX " + getVersionStatic() + ", Visual Illusions IRC Bot")
                .setRealName("VIBotX " + getVersionStatic() + ", Visual Illusions IRC Bot")
                .setShutdownHookEnabled(true)
                .addListener(new HeyListen());
        try {
            ConfigurationManager.loadConfig(universe, cfgbuild);
        } catch (FirstTimeRunException ftrex) {
            System.err.println(ftrex.getMessage());
            System.exit(837);
        } catch (Exception ex) {
            System.err.println("VIBotX was unable to read the configuration file...");
            ex.printStackTrace();
            System.exit(830);
        }

        if (cfgbuild.isIdentServerEnabled()) {
            try {
                IdentServer.startServer(); // Need to boot this up in order to use it...
            } catch (RuntimeException rtex) {
                log.error("An error occurred with the IdentServer. IdentServer will not be in use", rtex);
                cfgbuild.setIdentServerEnabled(false); // If we don't set it to false, it errors again at bot.connect
            }
        }

        bot = new VIBotX(cfgbuild.buildConfiguration());
        jpLoader.scanPlugins();
        jpLoader.enableAllPlugins();
        try {
            bot.connect();
        } catch (IrcException | IOException ex) {
            log.error("CONNECTION ERROR: ", ex);
        }
    }

    public final void msgNickServ(String message) {
        bot.sendIRC().message("NICKSERV", message);
    }

    public static File getUniverse() {
        return universe;
    }

    public static JavaPluginLoader jpload() {
        return jpLoader;
    }

    public static String getProgramStatusMessage() {
        ProgramChecker.Status status = pCheck.checkStatus();
        switch (status) {
            case ERROR:
                return Colors.RED.concat(pCheck.getStatusMessage());
            case UPDATE:
                return Colors.YELLOW.concat(pCheck.getStatusMessage());
            default:
                return Colors.GREEN.concat(pCheck.getStatusMessage());
        }
    }

    private static void pokeManifest() {
        if (mf != null) {
            return;
        }
        try {
            mf = JarUtils.getManifest(VIBotX.class);
        } catch (IOException ioex) {
            mf = new Manifest();
        }
    }

    private static URL getStatusURL() {
        if (mf.getMainAttributes().containsKey(STATUSURL.getValue())) {
            try {
                return new URL(mf.getMainAttributes().getValue(STATUSURL.getValue()));
            } catch (MalformedURLException e) {
                log.error("There was a bug that has caused a MalformedURLException when checking the VIBotX Status." +
                        "Verify your VIBotX build is good or report this error.");
            }
        }
        return null;
    }

    public static String getJenkinsBuild() {
        if (mf.getMainAttributes().containsKey(JENKINSBUILD.getValue())) {
            return mf.getMainAttributes().getValue(JENKINSBUILD.getValue());
        }
        return "0";
    }

    public static String getIssuesURL() {
        if (mf.getMainAttributes().containsKey(ISSUESURL.getValue())) {
            return mf.getMainAttributes().getValue(ISSUESURL.getValue());
        }
        return "UNKNOWN";
    }

    public static String getWebsiteURL() {
        if (mf.getMainAttributes().containsKey(WEBSITEURL.getValue())) {
            return mf.getMainAttributes().getValue(WEBSITEURL.getValue());
        }
        return "UNKNOWN";
    }

    public static String getDevelopers() {
        if (mf.getMainAttributes().containsKey(DEVELOPERS.getValue())) {
            return mf.getMainAttributes().getValue(DEVELOPERS.getValue());
        }
        return "UNKNOWN";
    }

    /* Plugin Methods */
    @Override
    public final String getName() {
        return getNick();
    }

    @Override
    public String getJarName() {
        String jarpath = getJarPath();
        int lastSlash = jarpath.lastIndexOf(File.separatorChar);
        int lastDot = jarpath.lastIndexOf('.');

        if (lastSlash >= 0) {
            return jarpath.substring(lastSlash + 1, lastDot); //Adjust 1 past slash
        }
        return jarpath.substring(0, lastDot);
    }

    @Override
    public String getJarPath() {
        return JarUtils.getJarPath(getClass());
    }

    @Override
    public String getVersion() {
        return getVersionStatic();
    }

    public static String getVersionStatic() {
        if (mf.getMainAttributes().containsKey(PluginManifestAttributes.VERSION.getValue())) {
            return mf.getMainAttributes().getValue(PluginManifestAttributes.VERSION.getValue());
        }
        return "UNDEFINED";
    }

    @Override
    public VILogger getPluginLogger() {
        return log;
    }
    /* END Plugin Methods*/
}
