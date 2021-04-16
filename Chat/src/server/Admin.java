package server;

import UI.ServerUI;
import bean.Message;
import util.MySQLUtils;
import util.Utils;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Map;

public class Admin {
    /*
        管理员工具
     */
    private Admin() {

    }

    /**
     * 通过服务器向指定用户发送消息
     * @param message 消息内容
     * @param recAddr 收件人地址
     */
    public static void sendMessage(Message message, String recAddr) {

    }

    /**
     * 关闭服务器
     */
    public static void shutdown() {
        Socket socket = null;
        final int SERVER_PORT = Integer.valueOf(Utils.getConfig("SERVER_PORT"));
        final int STOP_PORT = Integer.valueOf(Utils.getConfig("STOP_PORT"));
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            socket = new Socket(localhost, SERVER_PORT, localhost, STOP_PORT);
            Thread.sleep(3000);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 禁言用户
     * @param command 指令
     * @param ipOrName 用户ip地址/用户名
     * @param min 禁言时间
     */
    public static void silent(String command, String ipOrName, String min) {
        if (!MySQLUtils.hasUserByIP(ipOrName) && !MySQLUtils.hasUserByName(ipOrName)) {
            ServerUI.getHistory().append("[Error] 用户" + ipOrName + "不存在！请检查输入!\n");
            return;
        }
        String ip = null, name = null;
        if (MySQLUtils.hasUserByName(ipOrName)) {
            ip = MySQLUtils.getIPByName(ipOrName);
            name = ipOrName;
            MySQLUtils.changeStatusByName(ipOrName, "Silent");
        } else {
            ip = ipOrName;
            name = MySQLUtils.getNameByIP(ipOrName);
            MySQLUtils.changeStatusByIP(ipOrName, "Silent");
        }
        ServerUI.getHistory().append("用户" + ipOrName + "被禁言" + min + "分钟\n");
        Message msg = new Message(Message.MsgType.WORD, Message.SendType.SILENT,
                "/" + Utils.getConfig("SERVER_IP"), ip,
                "你被服务器禁言" + min + "分钟\n");
        new ServerSender(ip, msg).start();

        Map<String, ObjectOutputStream> oosUserMap = Server.getOosUserMap();
        Iterator iterator = oosUserMap.keySet().iterator();
        Message silentMsg = new Message(Message.MsgType.WORD, Message.SendType.ALL,
                "/" + Utils.getConfig("SERVER_IP"), null,
                name + "被服务器禁言" + min + "分钟\n");
        while (iterator.hasNext()) {
            String recAddr = (String) iterator.next();
            silentMsg.setRecAdr(recAddr);
            if (!recAddr.equals(ip)) {
                new ServerSender(recAddr, silentMsg).start();
            }
        }
    }
}
