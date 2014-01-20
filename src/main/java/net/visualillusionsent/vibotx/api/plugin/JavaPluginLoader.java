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
/*
 * Some source code derived and adapted from CanaryLib
 *
 * Copyright © 2012-2014, FallenMoonNetwork/CanaryMod Team
 * Licenced under the BSD 3-Clause License
 * https://github.com/CanaryModTeam/CanaryLib/
 */
package net.visualillusionsent.vibotx.api.plugin;

import net.visualillusionsent.utils.JarUtils;
import net.visualillusionsent.utils.PropertiesFile;
import net.visualillusionsent.vibotx.CommandParser;
import net.visualillusionsent.vibotx.VIBotX;
import net.visualillusionsent.vibotx.api.events.EventHandler;
import org.pircbotx.Colors;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import static net.visualillusionsent.vibotx.VIBotX.log;

/**
 * Plugin Loading and Management Class
 * <p/>
 * Handles loading, unloading, enabling, disabling, and dependency resolving
 * <p/>
 * Some source code derived and adapted from CanaryLib
 *
 * @author Jason (darkdiplomat) [VIBotX & CanaryLib]
 * @author Chris (damagefilter) [CanaryLib]
 * @author Jos Kuijpers [CanaryLib]
 */
public final class JavaPluginLoader {
    private final Map<String, JavaPlugin> plugins; // This is keyed to set Plugin name
    private final Map<String, Manifest> pluginMF; // This is keyed to main class name
    private final PropertiesFile pluginCheck;
    private static final Object lock = new Object();

    public JavaPluginLoader() {
        plugins = new ConcurrentHashMap<>();
        pluginMF = new ConcurrentHashMap<>();
        this.pluginCheck = new PropertiesFile(new File(VIBotX.getUniverse(), "enabled-plugins.cfg").getAbsolutePath());
    }

    public final void scanPlugins() {
        if (!plugins.isEmpty()) {
            return;
        }
        log.info("Scanning for java plugins ...");
        File dir = new File(VIBotX.getUniverse(), "plugins/");
        if (!dir.exists()) {
            log.error("Failed to scan for plugins. 'plugins/' is not a directory. Creating...");
            dir.mkdir();
            return;
        }
        else if (!dir.isDirectory()) {
            log.error("Failed to scan for plugins. 'plugins/' is not a directory but a file...");
            return;
        }
        ArrayList<File> jars = new ArrayList<>();
        for (File jarFile : dir.listFiles(new JarFileFilter())) {
            jars.add(jarFile);
        }
        HashMap<File, Manifest> canLoad = new HashMap<>();
        for (File jar : jars) {
            Manifest check = scan(jar);
            if (check == null) {
                continue;
            }
            else {
                canLoad.put(jar, check);
            }
        }
        pluginCheck.save(); // Save any new plugins we have discovered
        for (Map.Entry<File, Manifest> load : canLoad.entrySet()) {
            load(load.getKey(), load.getValue(), false);
        }
    }

    /**
     * Get the Manifest from a jar file
     *
     * @param jar
     *         the plugin jar file
     *
     * @return a {@link Manifest} if no errors occurred; {@code null} if unable to load the plugin
     */
    private final Manifest scan(File jar) {
        Manifest mf;
        try {
            mf = JarUtils.getManifest(jar.getAbsolutePath());
            Attributes mainAtt = mf.getMainAttributes();

            if (!mainAtt.containsKey(PluginManifestAttributes.PLUGINCLASS.getValue())) {
                log.error("Failed to read Plugin-Class for '" + jar.getName() + "'");
                return null;
            }

            if (!mainAtt.containsKey(PluginManifestAttributes.PLUGINNAME.getValue())) {
                log.error("Failed to read Plugin-Name for '" + jar.getName() + "'");
                return null;
            }

            if (pluginCheck.containsKey(mainAtt.getValue("Plugin-Name"))) {
                if (!pluginCheck.getBoolean(mainAtt.getValue("Plugin-Name"))) {
                    return null;
                }
            }
            else {
                pluginCheck.setBoolean(mainAtt.getValue("Plugin-Name"), true);
            }

        }
        catch (Throwable ex) {
            log.error("Exception while loading plugin jar '" + jar.getName() + "' (MANIFEST.mf missing?)", ex);
            return null;
        }
        return mf;
    }

    private JavaPlugin unsafeScanForPlugin(String name) {
        File dir = new File(VIBotX.getUniverse(), "plugins/");
        if (!dir.isDirectory()) {
            return null;
        }
        for (File jar : dir.listFiles(new JarFileFilter())) {
            Manifest mf = scan(jar);
            if (mf != null) {
                load(jar, mf, false);
            }
        }
        return null;
    }

    /**
     * The class loader
     * The pluginName should come as full file name with file extension
     *
     * @param pluginJar
     * @param mf
     *
     * @return
     */
    private final boolean load(File pluginJar, Manifest mf, boolean skipExistanceCheck) {
        try {
            String name = mf.getMainAttributes().getValue("Plugin-Name");
            String mainClass = mf.getMainAttributes().getValue("Plugin-Class");
            if (plugins.containsKey(name) && !skipExistanceCheck) {
                log.error(name + " is already loaded, skipping");
                return false; // Already loaded
            }

            pluginMF.put(simpleMain(mainClass), mf);
            URLClassLoader loader;
            try {
                loader = new URLClassLoader(new URL[]{ pluginJar.toURI().toURL() }, Thread.currentThread().getContextClassLoader());
            }
            catch (MalformedURLException ex) {
                log.error("Exception while loading class", ex);
                return false;
            }
            Class<?> pluginClass = loader.loadClass(mainClass);
            JavaPlugin plugin = (JavaPlugin) pluginClass.newInstance();
            synchronized (lock) {
                this.plugins.put(name, plugin);
            }
        }
        catch (Throwable ex) {
            log.error("Exception while loading plugin '" + pluginJar + "'", ex);
            return false;
        }

        return true;
    }

    private final String simpleMain(String main) {
        int last = main.lastIndexOf('.');
        return main.substring(last != -1 ? last + 1 : 0, main.length());
    }

    /**
     * Enables the given plugin. Loads the plugin if not loaded (and available)
     *
     * @param name
     *         the name of the {@link Plugin}
     *
     * @return {@code true} on success, {@code false} on failure
     */
    public final boolean enablePlugin(String name) {
        JavaPlugin plugin = this.getPlugin(name);
        if (plugin == null) {
            // Plugin is NIL - lets see if we have it on disk
            plugin = unsafeScanForPlugin(name);
        }
        return enablePlugin(plugin);
    }

    /* Same as public boolean enablePlugin(String name) */
    private final boolean enablePlugin(JavaPlugin plugin) {
        if (plugin == null) {
            return false;
        }

        // The plugin must be disabled to enable
        if (!plugin.isDisabled()) {
            return true; // already enabled
        }

        // Set the plugin as enabled and send enable message
        boolean enabled = false;
        boolean needNewInstance = true;
        if (plugins.containsValue(plugin)) {
            try {
                if (!plugin.isClosed()) {
                    enabled = plugin.enable();
                    needNewInstance = false;
                }
            }
            catch (Throwable t) {
                // If the plugin is in development, they may need to know where something failed.
                log.error("Could not enable " + plugin.getName(), t);
            }
        }
        if (needNewInstance) {
            try {
                File file = new File(VIBotX.getUniverse(), plugin.getJarPath());
                Manifest mf = JarUtils.getManifest(plugin.getJarPath());
                enabled = load(file, mf, true);
            }
            catch (Throwable t) {
                // If the plugin is in development, they may need to know where something failed.
                log.error("Could not enable " + plugin.getName(), t);
            }
        }

        if (enabled) {
            plugin.flagEnabled();
            log.info("Enabled " + plugin.getName() + ", Version " + plugin.getVersion());
        }
        else {
            // Clean Up
            CommandParser.getInstance().removePluginCommands(plugin);
            EventHandler.getInstance().unregisterPluginListeners(plugin);
        }
        return enabled;
    }

    /** Enables all plugins, used when starting up the server. */
    public final void enableAllPlugins() {
        int enabled = 0;
        for (JavaPlugin plugin : plugins.values()) {
            if (enablePlugin(plugin)) {
                enabled++;
            }
        }
        log.info("Enabled " + enabled + " plugins.");
    }

    /**
     * Disables the given plugin
     *
     * @param name
     *         the name of the {@link Plugin}
     *
     * @return {@code true} on success, {@code false} on failure
     */
    public final boolean disablePlugin(String name) {
        return disablePlugin(plugins.get(name));
    }

    /* Same as public boolean disablePlugin(String name) */
    private final boolean disablePlugin(JavaPlugin plugin) {
        /* Plugin must exist before disabling*/
        if (plugin == null) {
            return false;
        }

        /* Plugin must also be enabled to disable */
        if (plugin.isDisabled()) {
            return true;
        }

        /* Set the plugin as disabled, and send disable message */
        try {
            plugin.flagDisabled();
        }
        catch (Throwable thrown) {
            log.error("An error occurred while disabling Plugin: " + plugin.getName(), thrown);
        }

        // Clean Up
        CommandParser.getInstance().removePluginCommands(plugin);
        EventHandler.getInstance().unregisterPluginListeners(plugin);

        log.info("Disabled " + plugin.getName() + ", Version " + plugin.getVersion());
        return true;
    }

    /** Disables all plugins, used when shutting down the server. */
    public final void disableAllPlugins() {
        for (JavaPlugin plugin : this.getPlugins()) {
            disablePlugin(plugin);
        }
    }

    /**
     * Reload the specified plugin
     *
     * @param name
     *
     * @return {@code true} on success; {@code false} on failure which probably means the plugin is now not enabled nor loaded
     */
    public boolean reloadPlugin(String name) {
        JavaPlugin plugin = this.getPlugin(name);

        // Plugin must exist before reloading
        if (plugin == null) {
            log.warning("Could not reload " + name + ". It doesn't exist.");
            return false;
        }

        // Disable the plugin
        disablePlugin(plugin);
        String jarPath = plugin.getJarPath(); //Store where the plugin is
        log.debug(jarPath);
        synchronized (lock) {
            plugins.remove(plugin.getName());
            plugin.closePlugin();
            /* Remove Manifest reference */
            pluginMF.remove(plugin.getClass().getSimpleName());
        }

        // Reload the plugin by loading its package again
        boolean test = false;
        try {
            test = load(new File(jarPath), JarUtils.getManifest(jarPath), false);
            if (test) {
                test = enablePlugin(plugin.getName()); // We have a name, not the new instance. Don't pass the plugin directly.
            }
        }
        catch (Throwable thrown) {
            log.error("Error while reloading Plugin: " + plugin.getName(), thrown);
        }
        return test;
    }

    /**
     * Get the Plugin with specified name.
     *
     * @param name
     *
     * @return The plugin for the given name, or null on failure.
     */
    public final JavaPlugin getPlugin(String name) {
        synchronized (lock) {
            return plugins.get(name);
        }
    }

    /**
     * Gets an unmodifiable collection of currently loaded Plugins
     *
     * @return unmodifiable collection of Plugins
     */
    public final Collection<JavaPlugin> getPlugins() {
        synchronized (lock) {
            return Collections.unmodifiableCollection(plugins.values());
        }
    }

    /**
     * Get a list of plugin-names
     *
     * @return String array of Plugin names
     */
    public final String[] getPluginList() {
        ArrayList<String> list = new ArrayList<>();
        String[] ret = new String[list.size()];

        synchronized (lock) {
            list.addAll(plugins.keySet());
        }

        return list.toArray(ret);
    }

    /**
     * Get a list of plugins for shoeing to the player
     * The format is: (color)pluginname where color is light green for Enabled or light red for disabled
     *
     * @return readable list of plugins
     */
    public final String getReadablePluginList() {
        StringBuilder sb = new StringBuilder();

        synchronized (lock) {
            for (JavaPlugin plugin : plugins.values()) {
                if (!plugin.isDisabled()) {
                    sb.append(Colors.GREEN).append(plugin.getName()).append(Colors.WHITE).append(", ");
                }
                else {
                    sb.append(Colors.RED).append(plugin.getName()).append(Colors.WHITE).append(", ");
                }
            }
        }
        String str = sb.toString();

        if (str.length() > 1) {
            return str.substring(0, str.length() - 1);
        }
        else {
            return null;
        }
    }

    final Manifest getPluginManifest(JavaPlugin plugin) {
        return pluginMF.get(plugin.getClass().getSimpleName());
    }

    private final class JarFileFilter implements FilenameFilter {

        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(".jar");
        }
    }
}
