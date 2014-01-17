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
import net.visualillusionsent.utils.UtilityException;
import net.visualillusionsent.vibotx.CommandParser;
import net.visualillusionsent.vibotx.api.plugin.Plugin;
import org.pircbotx.User;

/**
 * Base Command form
 * <p/>
 * Commands are set up to auto register themselves,<br/>
 * all that needs to be done is the class to be initialized in the {@link Plugin}<br/>
 * Example:
 * <p/>
 * <pre>
 * public void initialize() {
 *     new BaseCommandImpl(this);
 * }
 * </pre>
 * <p/>
 * BaseCommand implementations also require to be annotated with the {@link BotCommand} annotation<br>
 * Example:
 * <p/>
 * <pre>
 * {@literal @BotCommand}(main="test", usage="!test", desc="Example Command Annotation")
 * public class BaseCommandImpl extends BaseCommand{
 * </pre>
 *
 * @author Jason (darkdiplomat)
 * @see BotCommand
 */
public abstract class BaseCommand {

    /**
     * The {@link BotCommand} annotation for the {@code BaseCommand}
     */
    private final BotCommand cmd;

    /**
     * The {@link Plugin} associated with the {@code BaseCommand}
     */
    private final Plugin plugin;

    /**
     * Constructs a new {@code BaseCommand} object
     * <p/>
     * Requires the {@code BaseCommand} to have the {@link BotCommand} annotation
     *
     * @param plugin
     *         the Plugin creating this command
     */
    public BaseCommand(Plugin plugin) {
        if (!getClass().isAnnotationPresent(BotCommand.class)) {
            throw new CommandCreationException("BotCommand annotation not found!");
        }
        else {
            cmd = getClass().getAnnotation(BotCommand.class);
        }
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
        }
        this.plugin = plugin;
        CommandParser.getInstance().add(this);
    }

    /**
     * Gets the name of the {@code BaseCommand}
     *
     * @return the name of the {@code BaseCommand}
     */
    public final String getName() {
        return cmd.main();
    }

    public final char getAllowedPrefix() {
        return cmd.prefix();
    }

    /**
     * Gets all the aliases for the {@code BaseCommand}
     *
     * @return the aliases for the {@code BaseCommand}
     */
    public final String[] getAliases() {
        return cmd.aliases().clone();
    }

    /**
     * Gets the usage for the {@code BaseCommand}
     *
     * @return the usage for the {@code BaseCommand}
     */
    public final String getUsage() {
        return cmd.usage();
    }

    /**
     * Gets the description for the {@code BaseCommand}
     *
     * @return the description for the {@code BaseCommand}
     */
    public final String getDescription() {
        return cmd.desc();
    }

    /**
     * Gets whether the {@code BaseCommand} requires a {@link User} to have {@code Voice} or above
     *
     * @return {@code true} if requires {@code Voice}
     */
    public final boolean requiresVoice() {
        return cmd.voice();
    }

    /**
     * Gets whether the {@code BaseCommand} requires a {@link User} to have {@code Op} or above
     *
     * @return {@code true} if requires {@code Op}
     */
    public final boolean requiresOp() {
        return cmd.op();
    }

    /**
     * Gets whether the {@code BaseCommand} requires a {@link User} to be a {@code BotOwner}
     *
     * @return {@code true} if requires {@code BotOwner}
     */
    public final boolean requiresOwner() {
        return cmd.botOp();
    }

    /**
     * Gets whether the {@code BaseCommand} can only be used within a Channel
     *
     * @return {@code true} if channel only; {@code false} otherwise
     */
    public final boolean privateMessageAllowed() {
        return cmd.privateAllowed();
    }

    /**
     * Checks if the number of passed arguments is within the min/max range
     *
     * @param paramCount
     *         the number of passed arguments
     *
     * @return {@code true} if in range; {@code false} if not
     */
    public boolean argumentsInRange(int paramCount) {
        return cmd.minParam() <= paramCount && cmd.maxParam() >= paramCount;
    }

    /**
     * Gets the {@link Plugin} associated with the {@code BaseCommand}
     *
     * @return the {@link Plugin} associated with the {@code BaseCommand}
     */
    public final Plugin getPlugin() {
        return plugin;
    }

    /**
     * Sends {@link User} a message about bad syntax
     *
     * @param user
     *         {@link User} using the command
     */
    public void onBadSyntax(User user) {
        user.send().notice(cmd.usage());
    }

    /**
     * Executes a {@link BaseCommand}. Note: should not be called directly.<br>
     *
     * @param event
     *         the CommandEvent taking place
     */
    abstract public boolean execute(CommandEvent event);

    /**
     * String representation as BaseCommand[ClassName=%s Aliases=%s Usage=%s ErrorMessage=%s MinParams=%d MaxParams=%d RequireVoice=%b RequireOp=%b RequireBotOwner=%b] format
     *
     * @return formatted string
     *
     * @see Object#toString()
     */
    @Override
    public final String toString() {
        try {
            return String.format("BaseCommand[ClassName=%s Aliases=%s Usage=%s ErrorMessage=%s MinParams=%d MaxParams=%d RequireVoice=%b RequireOp=%b RequireBotOwner=%b]", this.getClass().getSimpleName(), StringUtils.joinString(cmd.aliases(), ",", 0), cmd.usage(), cmd.desc(), Integer.valueOf(cmd.minParam()), Integer.valueOf(cmd.maxParam()), Boolean.valueOf(cmd.voice()), Boolean.valueOf(cmd.op()), Boolean.valueOf(cmd.botOp()));
        }
        catch (UtilityException e) {
        }
        return null;
    }

    /**
     * Checks is an {@link Object} is equal to the {@code BaseCommand}
     *
     * @return {@code true} if equal; {@code false} otherwise
     *
     * @see Object#equals(Object)
     */
    @Override
    public final boolean equals(Object other) {
        if (!(other instanceof BaseCommand)) {
            return false;
        }
        BaseCommand that = (BaseCommand) other;
        if (cmd != that.cmd) {
            return false;
        }
        if (plugin != null && !plugin.equals(that.getPlugin())) {
            return false;
        }
        return true;
    }

    /**
     * Returns a hash code value for the {@code BaseCommand}.
     *
     * @see Object#hashCode()
     */
    @Override
    public final int hashCode() {
        int hash = 5;
        hash = 53 * hash + cmd.hashCode();
        hash = 53 * hash + plugin.hashCode();
        hash = 53 * hash + super.hashCode();
        return hash;
    }
}
