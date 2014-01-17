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
package net.visualillusionsent.vibotx.api.command;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Jason (darkdiplomat)
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface BotCommand {

    /**
     * Special {@code null} value for aliases - DO NOT USE
     */
    static final String NULL = "null.NULL#NULL throws null.NilException (WAT)";

    /**
     * The prefix allowed for the command, default: !
     */
    char prefix() default '!';

    /**
     * Main name for the {@link BaseCommand}
     */
    String main();

    /**
     * {@link String} array of aliases for the {@link BaseCommand}
     */
    String[] aliases() default NULL;

    /**
     * The usage for the {@link BaseCommand}
     */
    String usage();

    /**
     * A description of the {@link BaseCommand}
     */
    String desc();

    /**
     * The minimum number of required parameters
     */
    int minParam() default 0;

    /**
     * The maximum number of required parameteres
     */
    int maxParam() default Integer.MAX_VALUE;

    /**
     * Whether the {@link BaseCommand} is for {@link org.pircbotx.User}s with {@code Voice} and above
     */
    boolean voice() default false;

    /**
     * Whether the {@link BaseCommand} is for {@link org.pircbotx.User}s with {@code Op} and above
     */
    boolean op() default false;

    /**
     * Whether the {@link BaseCommand} is for {@link org.pircbotx.User}s that are {@code BotOperator}
     */
    boolean botOp() default false;

    /**
     * Whether the {@link BaseCommand} can be sent via PrivateMessage
     */
    boolean privateAllowed() default true;
}
