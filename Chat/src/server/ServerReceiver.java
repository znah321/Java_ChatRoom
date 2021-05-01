package server;

import UI.ServerUI;
import bean.Message;
import util.MySQLUtils;
import util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Iterator;

public class ServerReceiver extends Thread {
    private final Socket socket;

    public ServerReceiver(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        InputStream input;
        ObjectInputStream ois;
        try {
            input = socket.getInputStream(); // 获取输入流
            ois = new ObjectInputStream(input);
            while (true) {
                Message message = (Message) ois.readObject();
                ServerUI.appendText(message.getContent());

                if (message.getSendType() == Message.SendType.ONLINE) {
                    this.sendOnlineNotice(message);
                }
                if (message.getSendType() == Message.SendType.ALL) {
                    this.sendBroadCast(message);
                }
                if (message.getSendType() == Message.SendType.TELL) {
                    this.sendMessage(message, message.getRecAdr());
                }
                if (message.getSendType() == Message.SendType.LEAVE) {
                    this.sendLeaveMessage(message);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送一条消息
     * @param msg 消息
     * @param socketAddress 发送者的地址
     */
    private void sendMessage(Message msg, String socketAddress) {
        ServerSender sender = new ServerSender(socketAddress, msg);
        sender.start();
    }

    /**
     * 发送上线消息
     * @param message 收到的上线通知
     */
    private void sendOnlineNotice(Message message) {
        String onlineUserName =  MySQLUtils.getNameByIP(socket.getRemoteSocketAddress().toString());
        Message helloMsg = new Message(Message.MsgType.WORD, Message.SendType.ONLINE,
                "/" + Utils.getConfig("SERVER_IP"), socket.getRemoteSocketAddress().toString(),
                "Hello " + onlineUserName +"!");
        this.sendMessage(helloMsg, helloMsg.getRecAdr());
        Message onlineMsg = null;
        /*
            遍历onlineUserList，对其他用户发送该用户的上线通知
         */
        Iterator iterator = Server.getOnlineUserList().keySet().iterator();
        while (iterator.hasNext()) {
            String socketAddress = (String) iterator.next();
            if (!message.getSendAdr().equals(socketAddress)) {
                onlineMsg = new Message(Message.MsgType.WORD, Message.SendType.ONLINE,
                        "/" + Utils.getConfig("SERVER_IP"), socketAddress,
                        onlineUserName + "进入了聊天室");
                sendMessage(onlineMsg, socketAddress);
            }
        }
    }

    /**
     * 发送广播消息
     * @param message 消息内容
     */
    private void sendBroadCast(Message message) {
        String sendAddr = message.getSendAdr();
        Iterator iterator = Server.getOnlineUserList().keySet().iterator();
        while (iterator.hasNext()) {
            String socketAddress = (String) iterator.next();
            if (!socketAddress.equals(sendAddr)) {
                sendMessage(message, socketAddress);
            }
        }
    }

    /**
     * 当有用户下线时，向全体用户广播
     * @param message 下线通知
     */
    private void sendLeaveMessage(Message message) {
        String leaveUserName = MySQLUtils.getNameByIP(message.getSendAdr());
        Iterator iterator =  Server.getOnlineUserList().keySet().iterator();
        while (iterator.hasNext()) {
            String socketAddress = (String) iterator.next();
            if (!message.getSendAdr().equals(socketAddress)) {
                Message leaveMsg = new Message(Message.MsgType.WORD, Message.SendType.LEAVE,
                        "/" + Utils.getConfig("SERVER_IP"), socketAddress,
                        leaveUserName + "退出了聊天室");
                sendMessage(leaveMsg, socketAddress);
            }
        }
    }
}
