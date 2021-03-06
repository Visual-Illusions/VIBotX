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
package net.visualillusionsent.vibotx;

import net.visualillusionsent.utils.JarUtils;
import net.visualillusionsent.vibotx.api.command.BaseCommand;
import net.visualillusionsent.vibotx.api.command.BotCommand;
import net.visualillusionsent.vibotx.api.command.CommandCreationException;
import net.visualillusionsent.vibotx.api.command.CommandEvent;
import net.visualillusionsent.vibotx.api.command.ReturnStatus;
import net.visualillusionsent.vibotx.api.plugin.Plugin;
import net.visualillusionsent.vibotx.command.OkThanksCommand;
import net.visualillusionsent.vibotx.configuration.BotOpsManager;
import org.pircbotx.Channel;
import org.pircbotx.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static net.visualillusionsent.vibotx.VIBotX.log;
import static net.visualillusionsent.vibotx.api.command.ReturnStatus.ERROR;
import static net.visualillusionsent.vibotx.api.command.ReturnStatus.FAILURE;
import static net.visualillusionsent.vibotx.api.command.ReturnStatus.NOTCOMMAND;
import static net.visualillusionsent.vibotx.api.command.ReturnStatus.SUCCESS;

/**
 * Command parsing class
 * <p/>
 * Handle parsing of {@link net.visualillusionsent.vibotx.api.command.BaseCommand}s supplied to the {@link VIBotX}<br>
 *
 * @author Jason (darkdiplomat)
 * @version 1.0
 * @see net.visualillusionsent.vibotx.command
 * @since 1.0
 */
public final class CommandParser {

    /**
     * Synchronization lock object
     */
    private static final Object lock = new Object();

    /**
     * HashMap of command names to their {@link net.visualillusionsent.vibotx.api.command.BaseCommand} counterpart
     */
    private final HashMap<String, BaseCommand> commands;

    /**
     * Constructs a new {@code CommandParser}<br>
     * Should not be constructed externally
     */
    private CommandParser() {
        commands = new HashMap<>();
    }

    /* Initialization on Demand Holder idiom */
    // Private constructor prevents instantiation from other classes
    private static class CommandParserHolder {
        public static final CommandParser INSTANCE;

        static {
            INSTANCE = new CommandParser();
            try {
                Class<? extends BaseCommand>[] cmdClasses = JarUtils.getClassesInPackageExtending(JarUtils.getJarForClass(VIBotX.class), "net.visualillusionsent.vibotx.command", BaseCommand.class);
                for (Class<? extends BaseCommand> cls : cmdClasses) {
                    log.debug("Found Internal Command Class: " + cls.getSimpleName());
                    cls.getConstructor(VIBotX.class).newInstance(VIBotX.bot);
                }
            } catch (Exception ex) {
                log.error("Failed to register internal commands...", ex);
            }
        }
    }

    /**
     * Gets the {@code CommandParser} instance<br>
     * If the instance is null, the method will create a new instance and initialize the internal {@link BaseCommand}s
     *
     * @return {@code CommandParser} instance
     * @see net.visualillusionsent.vibotx.command
     */
    public static CommandParser getInstance() {

        return CommandParserHolder.INSTANCE;
    }
    /* END */

    /**
     * Adds a {@link BaseCommand} to the server list.
     *
     * @param cmd the {@link BaseCommand} to add
     */
    public final void add(BaseCommand cmd) throws CommandCreationException {
        if (cmd != null) {
            if (!commands.containsKey(cmd.getName())) {
                commands.put(cmd.getName(), cmd);
            } else {
                throw new CommandCreationException("Command: '".concat(cmd.getName()).concat("' is already registered!"));
            }
            if (!cmd.getAliases()[0].equals(BotCommand.NULL)) {
                for (String alias : cmd.getAliases()) {
                    if (!commands.containsKey(alias)) {
                        commands.put(alias, cmd);
                    } else {
                        log.warning("Command: '".concat(alias).concat("' is already registered!"));
                    }
                }
            }
        }
    }

    /**
     * Performs a lookup for a command of the given name and executes it if
     * found. Returns false if command not found.
     *
     * @param event the {@link CommandEvent}
     * @return {@code true} if is parsed successfully
     */
    public static ReturnStatus parseBotCommand(CommandEvent event) {
        synchronized (lock) {
            BaseCommand cmd = getInstance().getCommand(event.getCommand());

            User user = event.getUser();
            Channel channel = event.getChannel();
            if (cmd != null) {
                if (cmd.getAllowedPrefix() != event.getPrefix()) {
                    return NOTCOMMAND;
                }
                try {
                    if (event.getChannel() != null && MuteTracker.botMuteIn(event.getChannel())) {
                        if (!BotOpsManager.isBotOp(user) && !channel.isOp(user) && cmd.getClass() != OkThanksCommand.class) {
                            return FAILURE;
                        }
                    }
                    if (event.getChannel() != null && MuteTracker.userMuteIn(event.getUser(), event.getChannel())) {
                        if (!BotOpsManager.isBotOp(user) && !channel.isOp(user)) {
                            return FAILURE;
                        }
                    }
                    if (!cmd.privateMessageAllowed() && channel == null) {
                        return FAILURE;
                    }
                    if (cmd.requiresVoice() && (channel == null || !channel.hasVoice(user) || !channel.isOp(user) || !BotOpsManager.isBotOp(user))) {
                        return FAILURE;
                    }
                    if (cmd.requiresOp() && (channel == null || !channel.isOp(user) || !BotOpsManager.isBotOp(user))) {
                        return FAILURE;
                    }
                    if (cmd.requiresOwner() && !BotOpsManager.isBotOp(user)) {
                        return FAILURE;
                    }
                    if (!cmd.argumentsInRange(event.getArguments().length)) {
                        cmd.onBadSyntax(event.getUser());
                        return FAILURE;
                    }
                    if (cmd.execute(event)) {
                        return SUCCESS;
                    }
                    return FAILURE;
                } catch (Throwable thrown) {
                    log.error("Exception occurred while parsing Command: ".concat(event.getCommand()), thrown);
                    return ERROR;
                }
            }
        }
        return NOTCOMMAND;
    }

    /**
     * Gets the {@link BaseCommand} by name from the commands map
     *
     * @param command the name of the {@link BaseCommand} to get
     * @return the {@link BaseCommand} if found; {@code null} otherwise
     */
    private BaseCommand getCommand(String command) {
        return commands.get(command);
    }

    /**
     * Prints out the help list to the {@link User} based on their status in the channel
     *
     * @param channel the {@link Channel} help is being called from
     * @param user    the {@link User} calling for help
     */
    public static void printHelp(Channel channel, User user, int pageStart) {
        synchronized (lock) {
            user.send().notice("-- Help List for you in Channel: ".concat(channel.getName()).concat(" --"));
            int pageMax = getInstance().commands.values().size() / 10;
            pageMax = pageMax < 1 ? 1 : pageMax;
            if (pageMax < pageStart) {
                pageStart = 1;
            }
            int nPS = (pageStart - 1) * 10;
            int nPSten = nPS + 10;
            user.send().notice(String.format("-- Page %d of %d --", pageStart, pageMax));
            List<BaseCommand> toDisplay = new ArrayList<>();
            toDisplay.addAll(getInstance().commands.values());
            for (BaseCommand cmd : toDisplay.subList(nPS, nPSten)) {
                if (cmd.requiresVoice() && !channel.hasVoice(user)) {
                    continue;
                }
                if (cmd.requiresOp() && !channel.isOp(user)) {
                    continue;
                }
                if (cmd.requiresOwner() && !(BotOpsManager.isBotOp(user))) {
                    continue;
                }

                user.send().notice(cmd.getUsage().concat(" - ").concat(cmd.getDescription()));
                /*
                if (cmd.getAliases().length > 1) {
                    StringBuilder builder = new StringBuilder();
                    for (String alias : cmd.getAliases()) {
                        builder.append(alias);
                        builder.append(" ");
                    }
                    user.send().notice("Aliases for ".concat(cmd.getName()).concat(": ").concat(builder.toString()));
                }
                */
            }
        }
    }

    /**
     * Removes all {@link BaseCommand}s associated with the {@link Plugin}
     *
     * @param plugin the {@link Plugin} to remove {@link BaseCommand}s for
     */
    public final void removePluginCommands(Plugin plugin) {
        synchronized (lock) {
            List<String> toRemove = new ArrayList<>();
            for (String cmdName : commands.keySet()) {
                BaseCommand cmd = commands.get(cmdName);
                if (cmd.getPlugin() != null && cmd.getPlugin().equals(plugin)) {
                    toRemove.add(cmdName);
                }
            }
            for (String toRem : toRemove) {
                commands.remove(toRem);
            }
        }
    }
}
