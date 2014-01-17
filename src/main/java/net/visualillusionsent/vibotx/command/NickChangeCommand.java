/*
 * This file is part of VIBotX.
 *
 * Copyright © 2014 Visual Illusions Entertainment
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

import net.visualillusionsent.vibotx.VIBotX;
import net.visualillusionsent.vibotx.api.command.BaseCommand;
import net.visualillusionsent.vibotx.api.command.BotCommand;
import net.visualillusionsent.vibotx.api.command.CommandEvent;

/**
 * Nick Change Command<br>
 * Tells the {@link VIBotX} to change it's nick<br/>
 * <b>Usage:</b> .nick {@literal <newnick>}<br/>
 * <b>Minimum Params:</b> 1<br/>
 * <b>Maximum Params:</b> 1<br/>
 * <b>Requires:</b> Bot Operator<br/>
 *
 * @author Jason (darkdiplomat)
 */
@BotCommand(
        main = "nick",
        prefix = '.',
        usage = ".nick <newnick>",
        desc = "Changes the bot's nickname",
        minParam = 1,
        maxParam = 1,
        botOp = true
)
public final class NickChangeCommand extends BaseCommand {

    /**
     * Constructs a new {@code NickChangeCommand}
     */
    public NickChangeCommand(VIBotX viBotX) {
        super(viBotX);
    }

    @Override
    public final boolean execute(CommandEvent event) {
        event.getBot().sendIRC().changeNick(event.getArguments()[0]);
        return true;
    }
}
