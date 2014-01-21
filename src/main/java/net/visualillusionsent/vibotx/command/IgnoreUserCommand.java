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

import net.visualillusionsent.vibotx.MuteTracker;
import net.visualillusionsent.vibotx.VIBotX;
import net.visualillusionsent.vibotx.api.command.BaseCommand;
import net.visualillusionsent.vibotx.api.command.BotCommand;
import net.visualillusionsent.vibotx.api.command.CommandCreationException;
import net.visualillusionsent.vibotx.api.command.CommandEvent;
import net.visualillusionsent.vibotx.configuration.BotOpsManager;
import org.pircbotx.User;

/**
 * Ignore User Command<br>
 * Tells the {@link VIBotX} to ignore a {@link User} in a specified {@link org.pircbotx.Channel}<br>
 * <b>Usage:</b> !ignore {@literal <user>}<br>
 * <b>Minimum Params:</b> 1<br>
 * <b>Maximum Params:</b> 1<br>
 * <b>Requires:</b> Op Channel<br>
 *
 * @author Jason (darkdiplomat)
 */
@BotCommand(
        main = "ignore",
        usage = "!ignore <user>",
        desc = "Ignores a user",
        minParam = 1,
        maxParam = 1,
        op = true,
        privateAllowed = false
)
public final class IgnoreUserCommand extends BaseCommand {

    /**
     * Constructs a new {@code IgnoreUserCommand}
     */
    public IgnoreUserCommand(VIBotX viBotX) throws CommandCreationException {
        super(viBotX);
    }

    @Override
    public final synchronized boolean execute(CommandEvent event) {
        if (event.getBot().getUserChannelDao().userExists(event.getArgument(0))) {
            User ignore = event.getBot().getUserChannelDao().getUser(event.getArgument(0));
            if (!BotOpsManager.isBotOp(ignore) && event.getChannel().getUsers().contains(ignore)) {
                MuteTracker.muteUserIn(ignore, event.getChannel());
                event.respondNoticeToUser("Now ignoring " + event.getArgument(0));
            }
            else {
                event.respondNoticeToUser("User is not in the channel or is a Bot Operator");
            }
        }
        else {
            event.respondNoticeToUser("Could not find user");
        }
        return true;
    }
}
