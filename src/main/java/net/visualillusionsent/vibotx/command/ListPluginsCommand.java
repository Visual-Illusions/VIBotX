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

/**
 * List Plugins Command<br>
 * Gets the list of {@link net.visualillusionsent.vibotx.api.plugin.Plugin}s<br>
 * Enabled plugins show as <font color=green>green</font>; Disabled plugins show as <font color=red>red</font><br>
 * <b>Usage:</b> .listplugins<br>
 * <b>Minimum Params:</b> 0<br>
 * <b>Maximum Params:</b> 0<br>
 * <b>Requires:</b> Bot Operator<br/>
 *
 * @author Jason (darkdiplomat)
 */
@BotCommand(
        main = "listplugins",
        prefix = '.',
        usage = ".listplugins",
        desc = "Gives a list of plugins",
        maxParam = 0,
        botOp = true
)
public final class ListPluginsCommand extends BaseCommand {

    /**
     * Constructs a new {@code ListPluginsCommand}
     */
    public ListPluginsCommand(VIBotX viBotX) {
        super(viBotX);
    }

    @Override
    public final boolean execute(CommandEvent event) {
        event.getUser().send().notice(VIBotX.jpload().getReadablePluginList());
        return true;
    }

}
