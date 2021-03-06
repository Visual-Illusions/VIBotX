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

import java.util.jar.Attributes;

/**
 * @author Jason (darkdiplomat)
 */
public enum PluginManifestAttributes {
    DEVELOPERS("Developers"),
    ISSUESURL("Issues-URL"),
    JENKINSBUILD("Jenkins-Build"),
    PLUGINCLASS("Plugin-Class"),
    PLUGINNAME("Plugin-Name"),
    STATUSURL("Status-URL"),
    VERSION("Version"),
    WEBSITEURL("Website-URL"),;

    private final Attributes.Name attName;

    private PluginManifestAttributes(String name) {
        attName = new Attributes.Name(name);
    }

    public Attributes.Name getValue() {
        return attName;
    }
}
