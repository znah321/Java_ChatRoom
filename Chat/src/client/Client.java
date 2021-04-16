package client;

import UI.LoginUI;
import UI.RoomUI;
import bean.Message;
import server.Server;
import util.*;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Client {
    /*
        客户类
     */
    public String name;
    public Socket socket = null;
    public Server.Status serverStatus;
    public ObjectOutputStream oos;
    public ClientReceiver receiver;

    public static void main(String[] args) {
        Client client = new Client();
        LoginUI loginUI = new LoginUI();
        loginUI.run();
        InetAddress localhost = null;
        try {
            localhost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        int SERVER_PORT = Integer.parseInt(Utils.getConfig("SERVER_PORT"));

        /* 等待用户点击登录按钮 */
        while (loginUI.state == null) {}
        if (loginUI.state.equals("false")) {
            // 登录失败
            // do something...
        } else {
            // 登录成功
            RoomUI roomUI = new RoomUI(client);
            roomUI.initUI();
            /*
                1、建立Socket连接服务器
                2、启动一个ClientReceiver线程接收服务器的消息
                3、主线程接收消息输入框里的消息
             */
            try {
                client.socket = new Socket(localhost, SERVER_PORT, localhost, Utils.allocatePort());
                client.serverStatus = Server.Status.RUNNING;
                client.name = loginUI.accText.getText();
                MySQLUtils.login(client.name, client.socket.getLocalSocketAddress().toString(), client.socket.getLocalPort());
                // 初始化sendType组件和onlineCnt组件
                ResultSet set = MySQLUtils.getAllOnlineUser();
                roomUI.getOnlineCnt().append("\n");
                try {
                    while (set.next()) {
                        String name = set.getString("name");
                        roomUI.getOnlineCnt().append(name + "\n");
                        if (!name.equals(client.name)) {
                            roomUI.getSendType().addItem(name);
                        }
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                /*
                    ObjectOutputStream存在锁机制，不能同时new多个ObjectOutputStream
                 */
                // 发送上线消息
                client.oos = new ObjectOutputStream(client.socket.getOutputStream());
                client.sendOnlineMsg();

                // 收信息线程启动
                client.receiver = new ClientReceiver(client, roomUI);
                client.receiver.start();

                while (!client.socket.isClosed()) {}
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 关闭客户端
     * @param client 客户端对象
     */
    public static void close(Client client) {
        Message msg = new Message(Message.MsgType.WORD, Message.SendType.LEAVE,
                client.socket.getLocalSocketAddress().toString(), "/" + Utils.getConfig("SERVER_IP"),
                client.name + "退出了聊天室");
        ClientSender leaveThread = new ClientSender(client.oos, msg); // 发送下线消息
        leaveThread.start();
        // 等待发送完...
        while (leaveThread.isAlive()) {}
        // 关闭ClientReceiver
        // do something...
        client.receiver.interrupt();
    }

    /**
     * 发送上线消息
     */
    public void sendOnlineMsg() {
        Message msg = new Message(Message.MsgType.WORD, Message.SendType.ONLINE,
                this.socket.getLocalSocketAddress().toString(), "/" + Utils.getConfig("SERVER_IP"),
                this.name + "进入了聊天室");
        new ClientSender(this.oos, msg).start(); // 发送上线消息
    }

    /**
     * 发送一条消息
     * @param recAddr 收件人地址
     * @param content 消息内容
     * @param roomUI 聊天界面
     */
    public void sendMsg(RoomUI roomUI, String recAddr, String content) {
        // 如果被禁言就弹出提示窗口，且不发送
        if (MySQLUtils.getStatusByName(this.name).equals("Silent")) {
            Utils.geneTestDialog(roomUI.getF(), "提示", "您已被管理员禁言", roomUI.getF());
            return;
        }
        // 消息为空或服务器没在运行就不发送
        if (content.equals("") || this.serverStatus != Server.Status.RUNNING) {
            return;
        }
        // 确定发送类型
        Message.SendType sendType = null;
        if (recAddr.equals("/" + Utils.getConfig("SERVER_IP"))) {
            sendType = Message.SendType.ALL;
        } else {
            sendType = Message.SendType.TELL;
        }
        Message msg = new Message(Message.MsgType.WORD, sendType,
                this.socket.getLocalSocketAddress().toString(), recAddr,
                content);
        new ClientSender(this.oos, msg).start();
        // 将自己发的信息显示到聊天区
        String text = "你";
        if (recAddr.equals("/" + Utils.getConfig("SERVER_IP"))) {
            text = text + "对所有人说";
        } else {
            text = text + "悄悄地对" + MySQLUtils.getNameByIP(recAddr);
        }
        String fullContent = text + " " + Utils.getCurrentTime() + "\n" + content + "\n\n";
        roomUI.getChatHistory().append(fullContent);
    }
}
