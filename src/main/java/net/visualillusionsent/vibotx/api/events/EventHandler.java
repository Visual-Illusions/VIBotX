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

import net.visualillusionsent.utils.ArrayUtils;
import net.visualillusionsent.vibotx.api.plugin.Plugin;
import org.pircbotx.hooks.Event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import static net.visualillusionsent.vibotx.VIBotX.log;

/**
 * Event Handler class
 * <p/>
 * Some source code derived and adapted from CanaryLib
 *
 * @author Jason (darkdiplomat)
 * @author Chris (damagefilter) [CanaryLib]
 * @author Jos Kuijpers [CanaryLib]
 * @author Yariv Livay [CanaryLib]
 */
public final class EventHandler {
    private final ConcurrentHashMap<Class<? extends Event>, ArrayList<RegisteredEventListener>> regListeners = new ConcurrentHashMap<>();
    private final String eherr = "Exception while passing Event: %s to EventListener: %s (Plugin: %s)";

    /**
     * Register a {@link EventListener} for a system hook
     */
    public void registerListener(EventListener listener, Plugin plugin) throws EventMethodSignatureException {
        Method[] methods = ArrayUtils.safeArrayMerge(listener.getClass().getMethods(), listener.getClass().getDeclaredMethods(), new Method[1]);
        for (final Method method : methods) {
            // Check if the method is a hook handling method
            final EventMethod eMethod = method.getAnnotation(EventMethod.class);

            if (eMethod == null) {
                continue; // Next, not one of our things
            }
            // Check the parameters for number and type and decide if it's one
            // that is really a handler method
            Class<?>[] parameters = method.getParameterTypes();
            if (parameters.length > 1 || parameters.length == 0) {
                throw new EventMethodSignatureException("Invalid number of parameters for Method: " + method.getName() + " in EventListener: " + listener.getClass().getName());
            }
            Class<?> eventClass = parameters[0];
            if (!Event.class.isAssignableFrom(eventClass)) {
                throw new EventMethodSignatureException("Event.class is not assignable from " + eventClass.getName());
            }
            if (!regListeners.containsKey(eventClass.asSubclass(Event.class))) {
                regListeners.put(eventClass.asSubclass(Event.class), new ArrayList<RegisteredEventListener>());
            }

            EventDispatch dispatcher = new EventDispatch() {
                private static final String err = "Failed to handle Event: '%s' for Listener: '%s'";

                @Override
                public void execute(EventListener listener, Event event) throws EventHandlingException {
                    try {
                        method.invoke(listener, event);
                    }
                    catch (Exception ex) {
                        throw new EventHandlingException(String.format(err, event.getClass().getSimpleName(), listener.getClass().getName()), ex.getCause());
                    }
                }
            };

            regListeners.get(eventClass.asSubclass(Event.class)).add(new RegisteredEventListener(plugin, listener, dispatcher));
        }
    }

    public void unregisterPluginListeners(Plugin plugin) {
        Iterator<ArrayList<RegisteredEventListener>> iter = regListeners.values().iterator();

        while (iter.hasNext()) {
            Iterator<RegisteredEventListener> regIterator = iter.next().iterator();

            while (regIterator.hasNext()) {
                RegisteredEventListener listener = regIterator.next();

                if (listener.getPlugin().equals(plugin)) {
                    regIterator.remove();
                }
            }
        }
    }

    /** Passes an event to the Plugin Event Listeners */
    public final void passEvent(Event event) {
        ArrayList<RegisteredEventListener> listeners = this.regListeners.get(event.getClass().asSubclass(Event.class));
        if (listeners != null) {
            for (RegisteredEventListener regEL : listeners) {
                try {
                    regEL.execute(event);
                }
                catch (EventHandlingException ehex) {
                    log.error(String.format(eherr, event.getClass().getSimpleName(), regEL.getClass().getSimpleName(), regEL.getPlugin().getName()), ehex.getCause());
                }
            }
        }
    }
}
