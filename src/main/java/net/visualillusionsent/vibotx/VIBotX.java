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
import net.visualillusionsent.vibotx.api.events.EventHandler;
import net.visualillusionsent.vibotx.api.plugin.JavaPluginLoader;
import net.visualillusionsent.vibotx.api.plugin.Plugin;
import net.visualillusionsent.vibotx.configuration.ConfigurationManager;
import net.visualillusionsent.vibotx.logging.VILogger;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;

import java.io.File;
import java.io.IOException;

/**
 * VIBotX - The Visual Illusions IRC Bot
 *
 * @author Jason (darkdiplomat)
 */
public final class VIBotX extends PircBotX implements Plugin {
    public static final File universe;
    public static final VILogger log;
    public static final EventHandler eventHandler = new EventHandler();
    protected static VIBotX bot;
    private static JavaPluginLoader jpLoader;

    static {
        String universe_path = System.getProperty("vibotx.universe.path", ".");
        File tempUni = new File(".");
        try {
            tempUni = new File(universe_path).getCanonicalFile();
            if (!tempUni.exists()) {
                tempUni = new File(".").getCanonicalFile();
            }
        }
        catch (IOException e) {
        }
        universe = tempUni;
        log = new VILogger("VIBotX");
    }

    public VIBotX(Configuration<VIBotX> configuration) {
        super(configuration);
        jpLoader = new JavaPluginLoader();
    }

    public static void main(String[] args) {
        log.info("VIBotX - Visual Illusions IRC Bot is starting...");
        Configuration.Builder<VIBotX> cfgbuild = new Configuration.Builder()
                .setVersion("VIBotX " + getVersionStatic() + ", The Visual Illusions IRC Bot")
                .setRealName("VIBotX " + getVersionStatic() + ", The Visual Illusions IRC Bot")
                .setShutdownHookEnabled(true)
                .addListener(new HeyListen());
        try {
            ConfigurationManager.loadConfig(universe, cfgbuild);
        }
        catch (FirstTimeRunException ftrex) {
            System.err.println("VIBotX is running for the first time and needs to be configured. Please configure VIBotX and relaunch.");
            System.exit(837);
        }
        catch (Exception ex) {
            System.err.println("VIBotX was unable to read the configuration file...");
            ex.printStackTrace();
            System.exit(830);
        }

        bot = new VIBotX(cfgbuild.buildConfiguration());
        bot.jpLoader.scanPlugins();
        bot.jpLoader.enableAllPlugins();
        try {
            bot.connect();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (IrcException e) {
            e.printStackTrace();
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

    public static final String getVersionStatic() {
        try {
            return JarUtils.getManifest(VIBotX.class).getMainAttributes().getValue("Version");
        }
        catch (IOException e) {
            return "UNDEFINED";
        }
    }

    @Override
    public VILogger getPluginLogger() {
        return log;
    }
    /* END Plugin Methods*/
}
