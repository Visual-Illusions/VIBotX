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
package net.visualillusionsent.vibotx.configuration;

/**
 * @author Jason (darkdiplomat)
 */
final class BotOperator {
    final String nick, login, host;

    BotOperator(String hostMask) {
        int bang = hostMask.indexOf('!');
        int at = hostMask.indexOf('@');

        if (bang <= 0 || at <= 0) {
            throw new RuntimeException();
        }

        nick = hostMask.substring(0, bang);
        login = hostMask.substring(bang + 1, at);
        host = hostMask.substring(at + 1);
    }

    BotOperator(String nick, String login, String host) {
        this.nick = nick;
        this.login = login;
        this.host = host;
    }

    public final boolean equals(Object obj) {
        if (obj instanceof BotOperator) {
            BotOperator other = (BotOperator) obj;

            if (!nick.equals(other.nick) && !hasStar(nick, other.nick)) {
                return false;
            }
            if (!login.equals(other.login) && !hasStar(login, other.login)) {
                return false;
            }
            if (!host.equals(other.host) && !hasStar(host, other.host)) {
                return false;
            }
            return true;
        }
        return false;
    }

    private final boolean hasStar(String a, String b) {
        return a.equals("*") || b.equals("*");
    }
}
