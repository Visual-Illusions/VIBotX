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
 * Some source code derived and adapted from CanaryLib
 *
 * Copyright © 2012-2014, FallenMoonNetwork/CanaryMod Team
 * Licenced under the BSD 3-Clause License
 * https://github.com/CanaryModTeam/CanaryLib/
 */
package net.visualillusionsent.vibotx.api.events;

import net.visualillusionsent.vibotx.api.plugin.Plugin;
import org.pircbotx.hooks.Event;

/**
 * Registered Event Listener class
 *
 * @author Jason (darkdiplomat)
 */
final class RegisteredEventListener {
    private final EventListener listener;
    private final Plugin plugin;
    private final EventDispatch dispatch;

    RegisteredEventListener(Plugin plugin, EventListener listener, EventDispatch dispatch) {
        this.plugin = plugin;
        this.listener = listener;
        this.dispatch = dispatch;
    }

    public final Plugin getPlugin() {
        return plugin;
    }

    public final EventListener getListener() {
        return listener;
    }

    public final EventDispatch getDispatch() {
        return dispatch;
    }

    public final void execute(Event event) throws EventHandlingException {
        dispatch.execute(listener, event);
    }
}
