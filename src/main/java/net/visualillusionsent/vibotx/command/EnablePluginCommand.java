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
import net.visualillusionsent.vibotx.api.command.CommandEvent;
import net.visualillusionsent.vibotx.api.plugin.JavaPluginLoader;

/**
 * Enable Plugin Command<br>
 * Enables a {@link net.visualillusionsent.vibotx.api.plugin.Plugin}<br>
 * <b>Usage:</b> .enableplugin {@literal <plugin>}<br>
 * <b>Minimum Params:</b> 1<br>
 * <b>Maximum Params:</b> 1<br>
 * <b>Requires:</b> Bot Operator<br/>
 *
 * @author Jason (darkdiplomat)
 */
@BotCommand(
        main = "enableplugin",
        prefix = '.',
        usage = "!enableplugin <plugin>",
        desc = "Enables a plugin",
        minParam = 1,
        maxParam = 1,
        botOp = true
)
public final class EnablePluginCommand extends BaseCommand {

    /**
     * Constructs a new {@code EnablePluginCommand}
     */
    public EnablePluginCommand(VIBotX viBotX) {
        super(viBotX);
    }

    @Override
    public final boolean execute(CommandEvent event) {
        JavaPluginLoader load = VIBotX.jpload();
        String msg = "An exception occurred while enabling plugin...";
        if (load.enablePlugin(event.getArgument(0))) {
            msg = load.getPlugin(event.getArgument(0)).toString().concat(" enabled successfully!");
        }
        event.respondNoticeToUser(msg);
        return true;
    }
}
