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
package net.visualillusionsent.vibotx.command;

import net.visualillusionsent.utils.DateUtils;
import net.visualillusionsent.utils.UtilityException;
import net.visualillusionsent.vibotx.VIBotX;
import net.visualillusionsent.vibotx.api.command.BaseCommand;
import net.visualillusionsent.vibotx.api.command.BotCommand;
import net.visualillusionsent.vibotx.api.command.CommandCreationException;
import net.visualillusionsent.vibotx.api.command.CommandEvent;
import org.pircbotx.Channel;
import org.pircbotx.User;

/**
 * Ping Command<br>
 * Sends a Pong<br>
 * <b>Usage:</b> !ping<br>
 * <b>Minimum Params:</b> 0<br>
 * <b>Maximum Params:</b> 0<br>
 * <b>Requires:</b> n/a<br>
 *
 * @author Jason (darkdiplomat)
 */
@BotCommand(
        main = "ping",
        usage = "!ping",
        maxParam = 0,
        desc = "Sends a Pong"
)
public final class PingCommand extends BaseCommand {

    public PingCommand(VIBotX viBotX) throws CommandCreationException {
        super(viBotX);
    }

    @Override
    public final boolean execute(CommandEvent event) {
        Channel channel = event.getChannel();
        User user = event.getUser();
        try {
            if (channel != null) {
                channel.send().message("PONG: ".concat(DateUtils.longToTimeDate(System.currentTimeMillis())));
            }
            else {
                user.send().message("PONG: ".concat(DateUtils.longToTimeDate(System.currentTimeMillis())));
            }
        }
        catch (UtilityException ue) {
            //Shouldn't happen but just in case
            if (channel != null) {
                channel.send().message("PONG: ".concat(ue.getLocalizedMessage()));
            }
            else {
                user.send().message("PONG: ".concat(ue.getLocalizedMessage()));
            }
        }
        return true;
    }

}
