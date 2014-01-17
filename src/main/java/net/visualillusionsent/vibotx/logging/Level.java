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
package net.visualillusionsent.vibotx.logging;

/**
 * @author Jason (darkdiplomat)
 */
public class Level extends java.util.logging.Level {
    private static int intVal = 800;

    /* VIBotX custom logging levels */
    public static final Level DEBUG = new Level("DEBUG", -1);
    public static final Level ERROR = new Level("ERROR", 1001);
    /* Levels below have a intVal auto assigned */
    public static final Level ACTION = new Level("ACTION");
    public static final Level CHANNELINFO = new Level("CHANNELINFO");
    public static final Level COMMAND = new Level("COMMAND");
    public static final Level CONNECT = new Level("CONNECT");
    public static final Level DISCONNECT = new Level("DISCONNECT");
    public static final Level FINGER = new Level("FINGER");
    public static final Level INVITE = new Level("INVITE");
    public static final Level JOIN = new Level("JOIN");
    public static final Level KICK = new Level("KICK");
    public static final Level MESSAGE = new Level("MESSAGE");
    public static final Level NOTICE = new Level("NOTICE");
    public static final Level PART = new Level("PART");
    public static final Level PING = new Level("PING");
    public static final Level PRIVATEMESSAGE = new Level("PRIVATEMESSAGE");

    protected Level(String name) {
        super(name, ++intVal);
    }

    protected Level(String name, int value) {
        super(name, value);
    }
}
