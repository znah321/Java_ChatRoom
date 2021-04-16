package util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import bean.Message;
import server.Server;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class Utils {
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle("config");
    private Utils() {
    }

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 清空文件内容
     *
     * @param path 文件路径
     */
    public static void cleanFileContents(String path) {
        File file = new File(path);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成一个用于测试的对话框
     * @param msg 测试信息
     * @param name 对话框名
     * @param sourceF 源Frame
     * @param backF 返回的backF
     */
    public static void geneTestDialog(JFrame sourceF, String name, String msg, JFrame backF) {
        JDialog testDialog = new JDialog(sourceF, name, true);
        testDialog.setBounds(400, 200, 240, 150);
        testDialog.setLayout(new FlowLayout());
        JButton testButton = new JButton("确定");
        testButton.addActionListener(e -> {
            testDialog.setVisible(false);
            backF.setVisible(true);
        });
        testButton.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                testDialog.setVisible(false);
                backF.setVisible(true);
            }
        });
        testDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                testDialog.setVisible(false);
                backF.setVisible(true);
            }
        });
        testDialog.add(new JLabel(msg));
        testDialog.add(new JLabel("                            "));
        testDialog.add(testButton);
        testDialog.setVisible(true);
    }

    /**
     * 获取配置文件中的某个属性
     * @param key 关键词
     * @return 属性值(Object类型)
     */
    public static String getConfig(String key) {
        return resourceBundle.getString(key);
    }

    /**
     * 分配一个端口号
     * 生成原则：10000 + 用户数 + 增量
     * @return 端口号
     */
    public static int allocatePort() {
        int port, temp = 0;
        while (true) {
            port = Integer.valueOf(Server.getOnlineUserList().size()) + 10000 + temp;
            if (isPortUsing(port)) {
                temp++;
                continue;
            } else {
                break;
            }
        }
        return port;
    }

    /**
     * 判断端口是否被占用
     * @param port 端口号
     * @return true为被占用，false为未被占用
     */
    public static boolean isPortUsing(int port) {
        Socket socket = null;
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            socket = new Socket(localHost, port, localHost, port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            return true;
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 根据字节数据编译信息
     * @param bytes 字节数组
     * @return Message类的信息
     */
    public static Message parseMessage(byte[] bytes) {
        Message msg = new Message();
        Message.MsgType msgType = null;
        Message.SendType sendType = null;
        int flagPos = 0;
        String rawMsg = new String(bytes);

        flagPos = rawMsg.indexOf("@");
        if (rawMsg.substring(0, flagPos).equals("WORD")) {
            msgType = Message.MsgType.WORD;
        } else if (rawMsg.substring(0, flagPos).equals("AUDIO")) {
            msgType = Message.MsgType.AUDIO;
        } else {
            msgType = Message.MsgType.FILE;
        }
        rawMsg = rawMsg.substring(flagPos+1);
        flagPos = 0;
        msg.setMsgType(msgType);

        flagPos = rawMsg.indexOf("@");
        if (rawMsg.substring(0, flagPos).equals("ONLINE")) {
            sendType = Message.SendType.ONLINE;
        } else if (rawMsg.substring(0, flagPos).equals("ALL")) {
            sendType = Message.SendType.ALL;
        } else if (rawMsg.substring(0, flagPos).equals("TELL")){
            sendType = Message.SendType.TELL;
        } else {
            sendType = Message.SendType.LEAVE;
        }
        rawMsg = rawMsg.substring(flagPos+1);
        flagPos = 0;
        msg.setSendType(sendType);

        flagPos = rawMsg.indexOf("@");
        String sendAdr = rawMsg.substring(0, flagPos);
        rawMsg = rawMsg.substring(flagPos+1);
        msg.setSendAdr(sendAdr);
        flagPos = 0;

        flagPos = rawMsg.indexOf("@");
        String recAdr = rawMsg.substring(0, flagPos);
        rawMsg = rawMsg.substring(flagPos+1);
        msg.setRecAdr(recAdr);
        flagPos = 0;

        String content = rawMsg;
        msg.setContent(content);

        return msg;
    }

    /**
     * 获取当前时间
     * @return 当前时间（年-月-日 时：分：秒）
     */
    public static String getCurrentTime() {
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" );
        java.util.Date d= new Date();
        String time = sdf.format(d);
        return time;
    }
}
