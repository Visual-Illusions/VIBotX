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
import net.visualillusionsent.vibotx.api.command.CommandEvent;

/**
 * Shut The Fuck Up Command<br>
 * Quiets the {@link VIBotX} in a {@link org.pircbotx.Channel}<br>
 * <b>Usage:</b> .stfu<br>
 * <b>Minimum Params:</b> 0<br>
 * <b>Maximum Params:</b> 0<br>
 * <b>Requires:</b> Op Channel<br>
 *
 * @author Jason (darkdiplomat)
 */
@BotCommand(
        main = "stfu",
        prefix = '.',
        usage = ".stfu",
        desc = "Quiets the Bot in a channel",
        maxParam = 0,
        op = true,
        privateAllowed = false
)
public final class ShutTheFuckUpCommand extends BaseCommand {

    /**
     * Constructs a new {@code ShutTheFuckUpCommand}
     */
    public ShutTheFuckUpCommand(VIBotX viBotX) {
        super(viBotX);
    }

    @Override
    public final synchronized boolean execute(CommandEvent event) {
        MuteTracker.muteBotIn(event.getChannel());
        return true;
    }
}
