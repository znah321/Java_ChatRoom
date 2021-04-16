package client;

import UI.RoomUI;
import bean.Message;
import server.Server;
import util.MySQLUtils;
import util.Utils;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientReceiver extends Thread {
    private Client client;
    private RoomUI roomUI;

    public ClientReceiver(Client client, RoomUI roomUI) {
        this.client = client;
        this.roomUI = roomUI;
    }

    @Override
    public void run() {
        Socket socket = client.socket;
        System.out.println(socket.getLocalSocketAddress() + " receiver started...");
        try {
            InputStream input = socket.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(input);
            while (true) {
                Message msg = (Message) ois.readObject();
                /*
                    服务器被管理员关闭
                 */
                if (msg.getSendType() == Message.SendType.SHUTDOWN) {
                    client.serverStatus = Server.Status.SHUTDOWN;
                    Message leaveMsg = new Message(Message.MsgType.WORD, Message.SendType.LEAVE,
                            socket.getLocalSocketAddress().toString(), "/" + Utils.getConfig("SERVER_IP"),
                            client.name + "退出了聊天室");
                    ClientSender leaveThread = new ClientSender(client.oos, leaveMsg); // 发送下线消息
                    leaveThread.start();
                    // 等待发送完...
                    while (leaveThread.isAlive()) {}
                    this.showMessage(msg);
                    break;
                }
                this.showMessage(msg);
            }
        } catch (IOException e) {
            // 从客户端一端退出，会抛出EOFException
            if (e.getClass().equals(EOFException.class)) {
                // do nothing
            } else if (e.getClass().equals(SocketException.class)){
                // do nothing
            } else {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println(socket.getLocalSocketAddress() + " receiver ended...");
    }

    /**
     * 根据发送人地址打印信息
     * @param msg 信息
     */
    public void showMessage(Message msg) {
        String time = Utils.getCurrentTime();
        String sender = null;
        if (msg.getSendAdr().equals("/" + Utils.getConfig( "SERVER_IP"))) {
            sender = "[Server]";
        } else {
            sender = MySQLUtils.getNameByIP(msg.getSendAdr());
            if (msg.getSendType() == Message.SendType.TELL) {
                sender = sender + "悄悄地对你说";
            } else {
                sender = sender + "对所有人说";
            }
        }
        String fullMsg = sender + " " + time + "\n" + msg.getContent() + "\n";
        this.roomUI.getChatHistory().append(fullMsg);
        /*
            收到被禁言消息：
                启动一个SilentThread线程，传入一个时间戳和禁言时间，当时间达到时间戳+禁言时间时结束禁言
                对Client的sendMsg方法进行修改，发送前先检查状态，如果是Online就正常发送，如果是Silent就不发送，并且弹出一个窗口
                提示已被禁言，还有xx分钟才能发送消息
         */
        if (msg.getSendAdr().equals("/" + Utils.getConfig( "SERVER_IP")) && msg.getSendType() == Message.SendType.SILENT) {
            long currentTimeMillis = System.currentTimeMillis();
            int duration = 0;
            Pattern p = Pattern.compile("(0|[1-9][0-9]*)");
            Matcher m = p.matcher(msg.getContent());
            if (m.find()) {
                duration = Integer.parseInt(m.group(0));
            }
            new ClientSilent(currentTimeMillis, duration, this.client, this.roomUI).start();
        }
        /*
            更新sendType、onlineCnt控件
         */
        if (msg.getSendType() == Message.SendType.ONLINE || msg.getSendType() == Message.SendType.LEAVE) {
            String content = msg.getContent();
            String suffix = content.substring(content.length() - 7);
            //如果收到的是某用户的上线通知，需要在UI的sendType、onlineCnt控件更新
            if (msg.getSendType() == Message.SendType.ONLINE && suffix.equals("进入了聊天室\n")) {
                String name = content.substring(0, content.length() - 7);
                this.roomUI.getSendType().addItem(name);
                // onlineCnt控件
                this.roomUI.getOnlineCnt().append(name + "\n");
            }
            //如果收到的是某用户的下线通知，同理
            if (msg.getSendType() == Message.SendType.LEAVE && suffix.equals("退出了聊天室\n")) {
                String name = content.substring(0, content.length() - 7);
                roomUI.getSendType().removeItem(name);
                // onlineCnt控件
                String text = this.roomUI.getOnlineCnt().getText();
                text = text.replace(name + "\n", "");
                this.roomUI.getOnlineCnt().setText(text);
            }
        }
    }
}
