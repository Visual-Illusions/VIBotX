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
package net.visualillusionsent.vibotx.configuration;

import net.visualillusionsent.utils.FileUtils;
import net.visualillusionsent.utils.JarUtils;
import net.visualillusionsent.vibotx.VIBotX;
import org.pircbotx.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import static net.visualillusionsent.vibotx.VIBotX.log;

/**
 * @author Jason (darkdiplomat)
 */
public final class BotOpsManager {
    private static final ArrayList<BotOperator> operators = new ArrayList<>();

    private static void loadOps(File universe) {
        File opsFile = new File(universe, "bot-ops.txt");
        if (!opsFile.exists()) {
            try {
                FileUtils.cloneFileFromJar(JarUtils.getJarPath(VIBotX.class), "resources/example.ops", opsFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Scanner scan;
        try {
            scan = new Scanner(opsFile);
        } catch (FileNotFoundException e) {
            return; // Well...
        }

        int linenum = 0;
        while (scan.hasNext()) {
            linenum++;
            String line = scan.nextLine();
            if (line.startsWith("#")) {
                continue;
            }
            try {
                operators.add(new BotOperator(line));
            } catch (RuntimeException rex) {
                log.warning("Invalid HostMask in bot-ops.txt @ line: " + linenum);
            }
        }
    }

    static void touch(File universe) {
        loadOps(universe);
    }

    public static boolean isBotOp(User user) {
        return operators.contains(new BotOperator(user.getNick(), user.getLogin(), user.getHostmask()));
    }
}
