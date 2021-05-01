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
     * 获取IP地址
     * @param ipOrName IP地址或用户名
     * @param type 1为姓名，2位ip
     * @return 用户名+IP地址
     */
    private static String getIPorName(String ipOrName, int type) {
        if (!MySQLUtils.hasUserByIP(ipOrName) && !MySQLUtils.hasUserByName(ipOrName)) {
            ServerUI.appendText("[Error] 用户" + ipOrName + "不存在！请检查输入!<br>");
            return null;
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
        if (type == 1) {
            return name;
        } else {
            return ip;
        }
    }

    /**
     * 通过服务器向指定用户发送消息
     * @param ipOrName ip或用户名
     * @param content 消息内容
     */
    public static void sendPrivateMessage(String ipOrName, String content) {
        if (content.startsWith("\"") && content.endsWith("\"")) {
            content = content.substring(1, content.length() - 1);
        }
        String recAddr = null;
        recAddr = Admin.getIPorName(ipOrName, 2);
        if (recAddr == null) {
            return;
        }
        Message msg = new Message(Message.MsgType.WORD, Message.SendType.TELL,
                "/" + Utils.getConfig("SERVER_IP"), recAddr,
                content);
        new ServerSender(recAddr, msg).start();
    }

    /**
     * 服务器群发消息
     * @param content 消息内容
     */
    public static void broadcast(String content) {
        if (content.startsWith("\"") && content.endsWith("\"")) {
            content = content.substring(1, content.length() - 1);
        }
        Message msg = new Message(Message.MsgType.WORD, Message.SendType.ALL,
                "/" + Utils.getConfig("SERVER_IP"), null,
                content);
        Iterator iterator = Server.getOosUserMap().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String recAddress = (String) entry.getKey();
            msg.setRecAdr(recAddress);
            new ServerSender(recAddress, msg).start();
        }
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
     * @param ipOrName 用户ip地址/用户名
     * @param min 禁言时间
     */
    public static void silent(String ipOrName, String min) {
        String name = null, ip = null;
        name = Admin.getIPorName(ipOrName, 1);
        ip = Admin.getIPorName(ipOrName, 2);
        if (name == null || ip == null) {
            return;
        }
        ServerUI.appendText("用户" + ipOrName + "被禁言" + min + "分钟<br>");
        Message msg = new Message(Message.MsgType.WORD, Message.SendType.SILENT,
                "/" + Utils.getConfig("SERVER_IP"), ip,
                "你被服务器禁言" + min + "分钟<br>");
        new ServerSender(ip, msg).start();

        Map<String, ObjectOutputStream> oosUserMap = Server.getOosUserMap();
        Iterator iterator = oosUserMap.keySet().iterator();
        Message silentMsg = new Message(Message.MsgType.WORD, Message.SendType.ALL,
                "/" + Utils.getConfig("SERVER_IP"), null,
                name + "被服务器禁言" + min + "分钟<br>");
        while (iterator.hasNext()) {
            String recAddr = (String) iterator.next();
            silentMsg.setRecAdr(recAddr);
            if (!recAddr.equals(ip)) {
                new ServerSender(recAddr, silentMsg).start();
            }
        }
    }

    /**
     * 踢出用户
     * @param ipOrName ip地址或用户名
     */
    public static void kick(String ipOrName) {
        if (!MySQLUtils.hasUserByIP(ipOrName) && !MySQLUtils.hasUserByName(ipOrName)) {
            ServerUI.appendText("[Error] 用户" + ipOrName + "不存在！请检查输入!<br>");
            return;
        }
        String ip = null;
        ip = Admin.getIPorName(ipOrName, 2);
        if (ip == null) {
            return;
        }

        Iterator iterator = Server.getOosUserMap().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String recAddress = (String) entry.getKey();
            if (recAddress.equals(ip)) {
                Message msg = new Message(Message.MsgType.WORD, Message.SendType.SHUTDOWN,
                        "/" + Utils.getConfig("SERVER_IP"), recAddress,
                        "您已被管理员踢出聊天室...");
                new ServerSender(recAddress, msg).start();
                break;
            }
        }
    }
}
