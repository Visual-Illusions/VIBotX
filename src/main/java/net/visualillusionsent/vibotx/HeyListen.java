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

import net.visualillusionsent.vibotx.api.command.CommandEvent;
import net.visualillusionsent.vibotx.api.command.ReturnStatus;
import net.visualillusionsent.vibotx.api.events.EventHandler;
import net.visualillusionsent.vibotx.configuration.ConfigurationManager;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.*;

import static net.visualillusionsent.vibotx.VIBotX.log;

/**
 * VIBotX internal listener
 *
 * @author Jason (darkdiplomat)
 */
final class HeyListen extends ListenerAdapter<VIBotX> {
    private final String logFormat(Channel chan, User user, String... args) {
        StringBuilder sBuild = new StringBuilder();
        if (chan != null) {
            sBuild.append('(').append(chan.getName()).append(") ");
        }

        if (user != null) {
            sBuild.append("<")
                    .append(user.getNick())
                    .append('!')
                    .append(user.getLogin())
                    .append('@')
                    .append(user.getHostmask())
                    .append("> ");
        }

        for (String arg : args) {
            sBuild.append(arg).append(' ');
        }
        return sBuild.toString();
    }

    private String logFormatStupidEvent(String chan, String user, String... args) {
        StringBuilder sBuild = new StringBuilder();
        if (chan != null) {
            sBuild.append('(').append(chan).append(") ");
        }

        if (user != null) {
            sBuild.append("<")
                    .append(user)
                    .append("> ");
        }

        for (String arg : args) {
            sBuild.append(arg).append(' ');
        }
        return sBuild.toString();
    }

    @Override
    public void onAction(ActionEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
        log.action(logFormat(event.getChannel(), event.getUser(), event.getAction()));
    }

    @Override
    public void onChannelInfo(ChannelInfoEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
    }

    @Override
    public void onConnect(ConnectEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
        log.connect(String.valueOf(event.getTimestamp()));
    }

    @Override
    public void onDisconnect(DisconnectEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
        log.disconnect(String.valueOf(event.getTimestamp()));
    }

    @Override
    public void onFinger(FingerEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
        log.finger(logFormat(event.getChannel(), event.getUser(), String.valueOf(event.getTimestamp())));
    }

    @Override
    public void onHalfOp(HalfOpEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
    }

    @Override
    public void onIncomingChatRequest(IncomingChatRequestEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
    }

    @Override
    public void onIncomingFileTransfer(IncomingFileTransferEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
    }

    @Override
    public void onInvite(InviteEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
        log.invite(logFormatStupidEvent(event.getChannel(), event.getUser(), String.valueOf(event.getTimestamp())));
    }

    @Override
    public void onJoin(JoinEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
        log.join(logFormat(event.getChannel(), event.getUser()));
    }

    @Override
    public void onKick(KickEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
        log.kick(logFormat(event.getChannel(), event.getUser(), event.getRecipient().getNick(), event.getReason()));
    }

    @Override
    public void onMessage(MessageEvent<VIBotX> event) throws Exception {
        CommandEvent cmdEvent = new CommandEvent(event);
        ReturnStatus status = CommandParser.parseBotCommand(cmdEvent);
        switch (status) {
            case NOTCOMMAND:
                EventHandler.getInstance().passEvent(event);
                break;
            default:
                log.command(logFormat(event.getChannel(), event.getUser(), cmdEvent.getCommand(), cmdEvent.getArgumentsAsString()));
                return;
        }
        log.message(logFormat(event.getChannel(), event.getUser(), event.getMessage()));
    }

    @Override
    public void onMode(ModeEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
    }

    @Override
    public void onMotd(MotdEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
    }

    @Override
    public void onNickAlreadyInUse(NickAlreadyInUseEvent<VIBotX> event) throws Exception {
        if (ConfigurationManager.shouldGhostNick()) {
            VIBotX bot = event.getBot();
            bot.msgNickServ("GHOST " + bot.getNick());
            bot.sendIRC().changeNick(bot.getNick());
        }
        EventHandler.getInstance().passEvent(event);
        //TODO: Should event get passed?
    }

    @Override
    public void onNickChange(NickChangeEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
    }

    @Override
    public void onNotice(NoticeEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
        log.notice(logFormat(event.getChannel(), event.getUser(), event.getNotice()));
    }

    @Override
    public void onOp(OpEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
    }

    @Override
    public void onOwner(OwnerEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
    }

    @Override
    public void onPart(PartEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
        log.part(logFormat(event.getChannel(), event.getUser(), event.getReason()));
    }

    @Override
    public void onPing(PingEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
        log.ping(logFormat(event.getChannel(), event.getUser(), event.getPingValue()));
    }

    @Override
    public void onPrivateMessage(PrivateMessageEvent<VIBotX> event) throws Exception {
        CommandEvent cmdEvent = new CommandEvent(event);
        ReturnStatus status = CommandParser.parseBotCommand(cmdEvent);
        switch (status) {
            case NOTCOMMAND:
                EventHandler.getInstance().passEvent(event);
                break;
            default:
                log.command(logFormat(null, event.getUser(), cmdEvent.getCommand(), cmdEvent.getArgumentsAsString()));
                return;
        }
        EventHandler.getInstance().passEvent(event);
        log.privatemessage(logFormat(null, event.getUser(), event.getMessage()));
    }

    @Override
    public void onQuit(QuitEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
    }

    @Override
    public void onRemoveChannelBan(RemoveChannelBanEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
    }

    @Override
    public void onRemoveChannelKey(RemoveChannelKeyEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
    }

    @Override
    public void onRemoveChannelLimit(RemoveChannelLimitEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
    }

    @Override
    public void onRemoveInviteOnly(RemoveInviteOnlyEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
    }

    @Override
    public void onRemoveModerated(RemoveModeratedEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
    }

    @Override
    public void onRemoveNoExternalMessages(RemoveNoExternalMessagesEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
    }

    @Override
    public void onRemovePrivate(RemovePrivateEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
    }

    @Override
    public void onRemoveSecret(RemoveSecretEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
    }

    @Override
    public void onRemoveTopicProtection(RemoveTopicProtectionEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
    }

    @Override
    public void onServerPing(ServerPingEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
        log.ping("SERVER: " + event.getResponse());
    }

    @Override
    public void onServerResponse(ServerResponseEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
    }

    @Override
    public void onSetChannelBan(SetChannelBanEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
    }

    @Override
    public void onSetChannelKey(SetChannelKeyEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
    }

    @Override
    public void onSetChannelLimit(SetChannelLimitEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
    }

    @Override
    public void onSetInviteOnly(SetInviteOnlyEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
    }

    @Override
    public void onSetModerated(SetModeratedEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
    }

    @Override
    public void onSetNoExternalMessages(SetNoExternalMessagesEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
    }

    @Override
    public void onSetPrivate(SetPrivateEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
    }

    @Override
    public void onSetSecret(SetSecretEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
    }

    @Override
    public void onSetTopicProtection(SetTopicProtectionEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
    }

    @Override
    public void onSocketConnect(SocketConnectEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
    }

    @Override
    public void onSuperOp(SuperOpEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
    }

    @Override
    public void onTime(TimeEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
    }

    @Override
    public void onTopic(TopicEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
    }

    @Override
    public void onUnknown(UnknownEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
    }

    @Override
    public void onUserList(UserListEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
    }

    @Override
    public void onUserMode(UserModeEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
    }

    @Override
    public void onVersion(VersionEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
    }

    @Override
    public void onVoice(VoiceEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
    }

    @Override
    public void onWhois(WhoisEvent<VIBotX> event) throws Exception {
        EventHandler.getInstance().passEvent(event);
    }
}
