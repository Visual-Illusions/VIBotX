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

import net.visualillusionsent.vibotx.VIBotX;
import net.visualillusionsent.vibotx.api.command.BaseCommand;
import net.visualillusionsent.vibotx.api.command.BotCommand;
import net.visualillusionsent.vibotx.api.command.CommandCreationException;
import net.visualillusionsent.vibotx.api.command.CommandEvent;

/**
 * Disable Plugin Command<br>
 * Disables a {@link net.visualillusionsent.vibotx.api.plugin.Plugin}<br>
 * <b>Usage:</b> .disableplugin {@literal <plugin>}<br>
 * <b>Minimum Params:</b> 1<br>
 * <b>Maximum Params:</b> 1<br>
 * <b>Requires:</b> Bot Operator<br/>
 *
 * @author Jason (darkdiplomat)
 */
@BotCommand(
        main = "disableplugin",
        prefix = '.',
        desc = "Disables a plugin",
        usage = ".disableplugin <plugin>",
        minParam = 1,
        maxParam = 1,
        botOp = true
)
public final class DisablePluginCommand extends BaseCommand {

    /**
     * Constructs a new {@code DisablePluginCommand} object
     */
    public DisablePluginCommand(VIBotX viBotX) throws CommandCreationException {
        super(viBotX);
    }

    @Override
    public final boolean execute(CommandEvent event) {
        String pluginName = VIBotX.jpload().getPlugin(event.getArgument(0)).getName();
        if (VIBotX.jpload().disablePlugin(pluginName)) {
            event.respondNoticeToUser(pluginName.concat(" disabled!"));
        }
        return true;
    }
}
