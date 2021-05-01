package client;

import UI.RoomUI;
import bean.Message;
import util.MySQLUtils;
import util.Utils;

/*
    禁言线程
 */
public class ClientSilent extends Thread {
    private final long currentTimeMillis; // 被禁言时的时间戳
    private final int duration; // 禁言时间
    private final Client client; // 被禁言的用户
    private final RoomUI roomUI;

    public ClientSilent(long currentTimeMillis, int duration, Client client, RoomUI roomUI) {
        this.currentTimeMillis = currentTimeMillis;
        this.duration = duration;
        this.client = client;
        this.roomUI = roomUI;
    }

    @Override
    public void run() {
        long endTimeMills = this.currentTimeMillis + duration * 1000 * 60;
        while (System.currentTimeMillis() < endTimeMills) {
            // waiting...
        }
        MySQLUtils.changeStatusByName(this.client.name, "Online");
        // 通过服务端向指定用户发送消息：被解除禁言
        String fullMsg = "你已被服务器解除禁言<br><br>";
        Message msg = new Message(Message.MsgType.WORD, Message.SendType.ALL,
                "/" + Utils.getConfig("SERVER_IP"), MySQLUtils.getIPByName(this.client.name),
                fullMsg);
        this.roomUI.appendText(this.client.name, "[Server] " + Utils.getCurrentTime() + "<br>" + fullMsg, msg);
    }
}
