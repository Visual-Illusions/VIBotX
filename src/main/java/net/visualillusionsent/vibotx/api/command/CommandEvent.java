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
package net.visualillusionsent.vibotx.api.command;

import net.visualillusionsent.utils.StringUtils;
import net.visualillusionsent.vibotx.VIBotX;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

/**
 * @author Jason (darkdiplomat)
 */
public final class CommandEvent {
    private final User user;
    private final Channel channel;
    private final VIBotX bot;
    private final String[] args;
    private final String command;
    private final char prefix;

    public CommandEvent(MessageEvent<VIBotX> event) {
        this.channel = event.getChannel();
        this.user = event.getUser();
        this.bot = event.getBot();

        String[] tempArgs = event.getMessage().split(" ");
        prefix = tempArgs[0].charAt(0);
        command = tempArgs[0].substring(1);
        args = new String[tempArgs.length - 1];
        System.arraycopy(tempArgs, 1, args, 0, tempArgs.length - 1);
    }

    public CommandEvent(PrivateMessageEvent<VIBotX> event) {
        this.channel = null;
        this.user = event.getUser();
        this.bot = event.getBot();

        String[] tempArgs = event.getMessage().split(" ");
        prefix = tempArgs[0].charAt(0);
        command = tempArgs[0].substring(1);
        args = new String[tempArgs.length - 1];
        System.arraycopy(tempArgs, 1, args, 0, tempArgs.length - 1);
    }

    public char getPrefix() {
        return prefix;
    }

    public String getCommand() {
        return command;
    }

    public String[] getArguments() {
        return args;
    }

    public String getArgument(int index) {
        if (hasArguments() && index >= 0 && index < args.length) {
            return args[index];
        }
        return null;
    }

    public boolean hasArguments() {
        return args.length > 0;
    }

    public String getArgumentsAsString() {
        if (!hasArguments()) {
            return "{none}";
        }
        return StringUtils.joinString(args, " ", 0);
    }

    public User getUser() {
        return user;
    }

    public Channel getChannel() {
        return channel;
    }

    public VIBotX getBot() {
        return bot;
    }

    public void respondToUser(String msg) {
        getUser().send().message(msg);
    }

    public void respondToChannel(String msg) {
        if (channel == null) {
            return;
        }
        getChannel().send().message(msg);
    }

    public void respondNoticeToUser(String msg) {
        getUser().send().notice(msg);
    }

    public void repsondNoticeToChannel(String msg) {
        if (channel == null) {
            return;
        }
        getChannel().send().notice(msg);
    }

}
