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

import org.pircbotx.Channel;
import org.pircbotx.User;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Jason (darkdiplomat)
 */
public final class MuteTracker {
    private static final ArrayList<Channel> botMutedChans = new ArrayList<>();
    private static final HashMap<Channel, ArrayList<User>> muted_users = new HashMap<>();

    public static boolean botMuteIn(Channel channel) {
        return botMutedChans.contains(channel);
    }

    public static void muteBotIn(Channel channel) {
        botMutedChans.add(channel);
    }

    public static void unmuteBotIn(Channel channel) {
        botMutedChans.remove(channel);
    }

    public static boolean userMuteIn(User user, Channel channel) {
        if (!muted_users.containsKey(channel)) {
            muted_users.put(channel, new ArrayList<User>());
            return false;
        }
        return muted_users.get(channel).contains(user);
    }

    public static void muteUserIn(User user, Channel channel) {
        if (!muted_users.containsKey(channel)) {
            muted_users.put(channel, new ArrayList<User>());
        }
        muted_users.get(channel).add(user);
    }

    public static void unmuteUserIn(User user, Channel channel) {
        if (!muted_users.containsKey(channel)) {
            return;
        }
        muted_users.get(channel).remove(user);
    }
}
