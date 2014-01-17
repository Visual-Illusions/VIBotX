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
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 *  
 * This file is part of VIBot.
 *
 * VIBot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * VIBot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with VIBot.
 * If not, see http://www.gnu.org/licenses/lgpl.html
 */
package net.visualillusionsent.vibotx.command;

import net.visualillusionsent.utils.DateUtils;
import net.visualillusionsent.vibotx.VIBotX;
import net.visualillusionsent.vibotx.api.command.BaseCommand;
import net.visualillusionsent.vibotx.api.command.BotCommand;
import net.visualillusionsent.vibotx.api.command.CommandEvent;
import org.pircbotx.Channel;
import org.pircbotx.User;

import java.util.TimeZone;

/**
 * Time Command<br>
 * Gives the current time at the {@link net.visualillusionsent.vibotx.VIBotX} location, or time in the specified TimeZone<br/>
 * <b>Usage:</b> !time [TimeZone]<br/>
 * <b>Minimum Params:</b> 0<br/>
 * <b>Maximum Params:</b> 1<br/>
 * <b>Requires:</b> n/a<br/>
 *
 * @author Jason (darkdiplomat)
 */
@BotCommand(
        main = "time",
        usage = "!time [TimeZone]",
        desc = "Shows the current time",
        maxParam = 1
)
public final class TimeCommand extends BaseCommand {
    private final String print = "The current time in TimeZone: '%s' is %s";

    public TimeCommand(VIBotX viBotX) {
        super(viBotX);
    }

    @Override
    public final synchronized boolean execute(CommandEvent event) {
        long current = System.currentTimeMillis();
        Channel channel = event.getChannel();
        User user = event.getUser();
        if (event.hasArguments()) {
            TimeZone zone = TimeZone.getTimeZone(event.getArguments()[0]);
            if (channel != null) {
                event.respondToChannel(String.format(print, zone.getID(), DateUtils.longToTimeDate(current, zone), ""));
            }
            else {
                event.respondToUser(String.format(print, zone.getID(), DateUtils.longToTimeDate(current, zone), ""));
            }
        }
        else {
            if (channel != null) {
                event.respondToChannel("My system time is: " + DateUtils.longToTimeDate(current));
            }
            else {
                event.respondToUser("My system time is: " + DateUtils.longToTimeDate(current));
            }
        }
        return true;
    }
}
