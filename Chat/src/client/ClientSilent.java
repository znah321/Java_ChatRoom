package client;

import UI.RoomUI;
import bean.Message;
import server.Server;
import server.ServerSender;
import util.MySQLUtils;
import util.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;

/*
    禁言线程
 */
public class ClientSilent extends Thread {
    private long currentTimeMillis; // 被禁言时的时间戳
    private int duration; // 禁言时间
    private Client client; // 被禁言的用户
    private RoomUI roomUI;

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
        String fullMsg = "[Server] " + Utils.getCurrentTime() + "\n你已被服务器解除禁言\n\n";
        this.roomUI.getChatHistory().append(fullMsg);
//        Message msg = new Message(Message.MsgType.WORD, Message.SendType.ALL,
//                "/" + Utils.getConfig("SERVER_IP"), null,
//                this.client.name + "已被服务器解除禁言");
//        ResultSet set = MySQLUtils.getAllOnlineUser();
//        try {
//            while (set.next()) {
//                String socketAddress = set.getString("ip");
//                if (socketAddress.equals(this.client.socket.getLocalSocketAddress().toString())) {
//                    msg.setContent("你已被服务器解除禁言");
//                }
//                msg.setRecAdr(socketAddress);
//                new ServerSender(socketAddress, msg);
//            }
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
    }
}
