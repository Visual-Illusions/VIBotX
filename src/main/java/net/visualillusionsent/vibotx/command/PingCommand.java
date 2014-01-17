package net.visualillusionsent.vibotx.command;

import net.visualillusionsent.utils.DateUtils;
import net.visualillusionsent.utils.UtilityException;
import net.visualillusionsent.vibotx.VIBotX;
import net.visualillusionsent.vibotx.api.command.BaseCommand;
import net.visualillusionsent.vibotx.api.command.BotCommand;
import net.visualillusionsent.vibotx.api.command.CommandEvent;
import org.pircbotx.Channel;
import org.pircbotx.User;

/**
 * Ping Command<br>
 * Sends a Pong<br>
 * <b>Usage:</b> !ping<br>
 * <b>Minimum Params:</b> 0<br>
 * <b>Maximum Params:</b> 0<br>
 * <b>Requires:</b> Owner<br>
 *
 * @author Jason (darkdiplomat)
 */
@BotCommand(
        main = "ping",
        usage = "!ping",
        maxParam = 0,
        desc = "Sends a Pong"
)
public final class PingCommand extends BaseCommand {

    public PingCommand(VIBotX viBotX) {
        super(viBotX);
    }

    @Override
    public final boolean execute(CommandEvent event) {
        Channel channel = event.getChannel();
        User user = event.getUser();
        try {
            if (channel != null) {
                channel.send().message("PONG: ".concat(DateUtils.longToDate(System.currentTimeMillis())));
            }
            else {
                user.send().message("PONG: ".concat(DateUtils.longToDate(System.currentTimeMillis())));
            }
        }
        catch (UtilityException ue) {
            //Shouldn't happen but just in case
            if (channel != null) {
                channel.send().message("PONG: ".concat(ue.getLocalizedMessage()));
            }
            else {
                user.send().message("PONG: ".concat(ue.getLocalizedMessage()));
            }
        }
        return true;
    }

}
