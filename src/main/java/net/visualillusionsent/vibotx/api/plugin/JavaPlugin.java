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
package net.visualillusionsent.vibotx.api.plugin;

import net.visualillusionsent.utils.JarUtils;
import net.visualillusionsent.vibotx.VIBotX;
import net.visualillusionsent.vibotx.api.events.EventListener;
import net.visualillusionsent.vibotx.api.events.EventMethodSignatureException;
import net.visualillusionsent.vibotx.logging.VILogger;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;

/**
 * @author Jason (darkdiplomat)
 */
public abstract class JavaPlugin implements Plugin {
    private boolean disabled = true, closed = false;
    private VILogger logger;

    /**
     * Runs the {@code BotPlugin} enable code to check if enabling can happen<br>
     *
     * @return {@code true} if successfully enabled; {@code false} if failed its
     * checks
     */
    public abstract boolean enable();

    /**
     * Disables the {@code BotPlugin}
     */
    public abstract void disable();

    /**
     * Gets whether this Plugin is disabled
     *
     * @return {@code true} if disabled; {@code false} if enabled
     */
    public final boolean isDisabled() {
        return disabled;
    }

    /* Convenience Methods */
    @Override
    public final String getName() {
        return VIBotX.jpload().getPluginManifest(this).getMainAttributes().getValue("Plugin-Name");
    }

    @Override
    public final String getJarPath() {
        return JarUtils.getJarPath(getClass());
    }

    @Override
    public final String getJarName() {
        String jarPath = getJarPath();
        int lastSlash = jarPath.lastIndexOf(File.separatorChar);
        int lastDot = jarPath.lastIndexOf('.');

        if (lastSlash >= 0) {
            return jarPath.substring(lastSlash + 1, lastDot); //Adjust 1 past slash
        }
        return jarPath.substring(0, lastDot);
    }

    @Override
    public String getVersion() {
        return VIBotX.jpload().getPluginManifest(this).getMainAttributes().getValue("Plugin-Version");
    }

    @Override
    public VILogger getPluginLogger() {
        return logger == null ? logger = new VILogger(getName()) : logger;
    }

    public final void registerEventListener(EventListener listener) throws EventMethodSignatureException {
        VIBotX.eventHandler.registerListener(listener, this);
    }
    /* END Convenience Methods */

    /* Internal Plugin Handling Methods */
    final boolean isClosed() {
        return closed;
    }

    final void closePlugin() {
        try {
            ((URLClassLoader) getClass().getClassLoader()).close();
        }
        catch (IOException e) {
            //Oops
        }
        closed = true;
    }

    final void flagEnabled() {
        this.disabled = false;
    }

    final void flagDisabled() {
        this.disabled = true;
        disable();
    }
    /* END Internal Plugin Handling Methods */

    public final String toString() {
        return String.format("%s v%s", getName(), getVersion());
    }

    @Override
    public final boolean equals(Object obj) { //Locked out on purpose, should only ever be 1 instance anyways
        return super.equals(obj);
    }
}
