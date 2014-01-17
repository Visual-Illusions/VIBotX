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
/*
 * Some source code derived and adapted from CanaryLib
 *
 * Copyright © 2012-2014, FallenMoonNetwork/CanaryMod Team
 * Licenced under the BSD 3-Clause License
 * https://github.com/CanaryModTeam/CanaryLib/
 */
package net.visualillusionsent.vibotx.api.events;

import net.visualillusionsent.vibotx.VIBotX;
import org.pircbotx.hooks.Event;

/**
 * Event Dispatch class
 * <p/>
 * Some source code derived and adapted from CanaryLib
 *
 * @author Jason (darkdiplomat)
 * @author Chris (damagefilter) [CanaryLib]
 */
abstract class EventDispatch {

    public abstract void execute(EventListener listener, Event<VIBotX> event) throws EventHandlingException;
}
