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

import net.visualillusionsent.vibotx.CommandParser;
import net.visualillusionsent.vibotx.VIBotX;
import net.visualillusionsent.vibotx.api.command.BaseCommand;
import net.visualillusionsent.vibotx.api.command.BotCommand;
import net.visualillusionsent.vibotx.api.command.CommandCreationException;
import net.visualillusionsent.vibotx.api.command.CommandEvent;

/**
 * Help Command<br>
 * Displays a list of commands and their usage<br>
 * <b>Usage:</b> .help<br>
 * <b>Minimum Params:</b> 0<br>
 * <b>Maximum Params:</b> 1<br>
 * <b>Requires:</b> Channel<br>
 *
 * @author Jason (darkdiplomat)
 */
@BotCommand(
        main = "help",
        prefix = '.',
        usage = ".help",
        maxParam = 1,
        desc = "Displays a list of commands and their usage",
        privateAllowed = false
)
public final class HelpCommand extends BaseCommand {

    /**
     * Constructs a new {@code HelpCommand}
     */
    public HelpCommand(VIBotX viBotX) throws CommandCreationException {
        super(viBotX);
    }

    @Override
    public final boolean execute(CommandEvent event) {
        int pageStart = 1;
        if (event.hasArguments()) {
            try {
                pageStart = Integer.parseInt(event.getArgument(0));
            }
            catch (NumberFormatException nfex) {

            }
        }
        CommandParser.printHelp(event.getChannel(), event.getUser(), pageStart);
        return true;
    }
}
