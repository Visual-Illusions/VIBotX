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
package net.visualillusionsent.vibotx.api.plugin;

import net.visualillusionsent.vibotx.logging.VILogger;

/**
 * @author Jason (darkdiplomat)
 */
public interface Plugin {

    public String getName();

    /**
     * Gets the name of the {@code Plugin}'s jar file
     *
     * @return the name of the {@code Plugin}'s jar file; {@code null} if an exception occurred
     */
    public String getJarName();

    public String getJarPath();

    public String getVersion();

    public VILogger getPluginLogger();
}
