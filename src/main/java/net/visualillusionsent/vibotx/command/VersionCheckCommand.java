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
package net.visualillusionsent.vibotx.command;

import net.visualillusionsent.vibotx.VIBotX;
import net.visualillusionsent.vibotx.api.command.BaseCommand;
import net.visualillusionsent.vibotx.api.command.BotCommand;
import net.visualillusionsent.vibotx.api.command.CommandCreationException;
import net.visualillusionsent.vibotx.api.command.CommandEvent;
import org.pircbotx.Colors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Version Command<br>
 * Tells the current version of the {@link VIBotX}<br>
 * <b>Usage:</b> !version <br>
 * <b>Minimum Params:</b> 0<br>
 * <b>Maximum Params:</b> 0<br>
 * <b>Requires:</b> op<br>
 *
 * @author Jason (darkdiplomat)
 * @version 1.0
 * @since 1.0
 */
@BotCommand(
        main = "version",
        usage = "!version",
        desc = "Checks the version of the VIBotX or specified plugin",
        maxParam = 0,
        op = true
)
public final class VersionCheckCommand extends BaseCommand {
    private final List<String> about;

    public VersionCheckCommand(VIBotX viBotX) throws CommandCreationException {
        super(viBotX);

        ArrayList<String> pre = new ArrayList<>();
        pre.add(
                new StringBuilder(Colors.CYAN)
                        .append("--- ")
                        .append(Colors.GREEN)
                        .append("VIBotX ")
                        .append(Colors.BROWN)
                        .append("v")
                        .append(viBotX.getVersion())
                        .append(Colors.CYAN)
                        .append(" ---")
                        .toString()
        );
        pre.add("$PROGRAM_STATUS_CHECK$");
        pre.add(
                new StringBuilder(Colors.CYAN)
                        .append("Jenkins Build: ")
                        .append(Colors.GREEN)
                        .append(VIBotX.getJenkinsBuild())
                        .toString()
        );
        pre.add(
                new StringBuilder(Colors.CYAN)
                        .append("Developer(s): ")
                        .append(Colors.GREEN)
                        .append(VIBotX.getDevelopers())
                        .toString()
        );
        pre.add(
                new StringBuilder(Colors.CYAN)
                        .append("Website: ")
                        .append(Colors.GREEN)
                        .append(VIBotX.getWebsiteURL())
                        .toString()
        );
        pre.add(
                new StringBuilder(Colors.CYAN)
                        .append("Issues: ")
                        .append(Colors.GREEN)
                        .append(VIBotX.getIssuesURL())
                        .toString()
        );

        /*
        pre.add("§BBuilt On: §A".concat(plugin.getBuildTime()));

        // Next line should always remain at the end of the About
        pre.add(center(String.format("§BCopyright © %s §AVisual §6I§9l§Bl§4u§As§2i§5o§En§7s §6Entertainment", plugin.getCopyYear())));
        */
        about = Collections.unmodifiableList(pre);
    }

    @Override
    public final boolean execute(CommandEvent event) {
        for (String msg : about) {
            if (msg.equals("$PROGRAM_STATUS_CHECK$")) {
                msg = VIBotX.getProgramStatusMessage();
            }
            if (event.getChannel() != null) {
                event.respondToChannel(msg);
            }
            else {
                event.respondToUser(msg);
            }
        }
        return true;
    }
}
