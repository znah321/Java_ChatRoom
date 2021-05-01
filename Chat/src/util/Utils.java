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
    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("config");
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
        testButton.setFont(new Font("Microsoft Yahei", Font.PLAIN, 20));
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
        JLabel label = new JLabel(msg);
        label.setFont(new Font("Microsoft Yahei", Font.PLAIN, 20));
        testDialog.add(label);
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
     * 获取当前时间
     * @return 当前时间（年-月-日 时：分：秒）
     */
    public static String getCurrentTime() {
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" );
        java.util.Date d= new Date();
        String time = sdf.format(d);
        return time;
    }

    /**
     * 建立系统目录
     * @param username 用户名
     */
    public static void createSystemDirectory(String username) {
        String path = System.getenv("USERPROFILE") + "\\Java_ChatRoom\\" + username;
        File file = new File(path);
        if (!file.exists() || file.isFile()) {
            file.mkdirs();
        }

        File recvFile = new File(path + "\\FileRecv");
        if (!recvFile.exists() || recvFile.isFile()) {
            recvFile.mkdirs();
        }

        File audioFile = new File(path + "\\AudioRecv");
        if (!audioFile.exists() || audioFile.isFile()) {
            audioFile.mkdirs();
        }
    }

    /**
     * 获取用户地址
     * @param username 用户名
     * @param type 种类
     * @return 地址
     */
    public static String getFilePath(String username, String type) {
        String path = System.getenv("USERPROFILE") + "\\Java_ChatRoom\\" + username;
        if (type.equals("FILE")) {
            path = path + "\\FileRecv";
        } else {
            path = path + "\\AudioRecv";
        }
        return path;
    }

    /**
     * 删除文件
     * @param filepath 文件路径
     */
    public static void deleteFile(String filepath) {
        File file = new File(filepath);
        file.delete();
    }

    /**
     * 判断文件是否存在
     * @param filepath 文件路径
     * @return true为存在，false为不存在
     */
    public static boolean fileExists(String filepath) {
        File file = new File(filepath);
        return file.exists() && file.isFile();
    }
}
