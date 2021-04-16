package UI;

import client.Client;
import server.Server;
import test_Package.Client01;
import util.MySQLUtils;
import util.Utils;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class RoomUI {
    /*
        聊天室界面
     */
    private Client client;
    private JFrame f;
    private JTextArea chatHistory; // 聊天记录
    private JTextArea inputArea; // 输入框
    private JTextArea onlineCnt; // 在线人数统计
    private JComboBox sendType; // 发送类型
    private JButton sendAudioBut;
    private JButton listenAudioBut;
    private JButton sendFileBut;
    private JButton seeFileBut;
    private JButton sendMsg;

    public RoomUI(Client client) {
        this.client = client;
    }

    public  void initUI() {
        this.initComponents();
        this.registerListeners();
    }

    private void initComponents() {
        this.f = new JFrame("JChat");
        this.f.setBounds(300, 200, 1000, 800);
        this.f.setLayout(null);

        Border grayBorder = BorderFactory.createLineBorder(Color.GRAY);

        this.chatHistory = new JTextArea();
        this.chatHistory.setBorder(grayBorder);
        this.chatHistory.setFont(new Font("Microsoft Yahei", Font.PLAIN, 14));
        this.chatHistory.setSize(750, 500);
        this.chatHistory.setLocation(3, 3);
        this.chatHistory.setEditable(false); // 禁止编辑
        this.chatHistory.setLineWrap(true);
        this.chatHistory.setWrapStyleWord(true);
        this.f.add(this.chatHistory);

        /* 功能表 */
        JPanel functionPanel = new JPanel();
        functionPanel.setLocation(3, 503);
        functionPanel.setSize(750, 40);
        functionPanel.setLayout(null);

        this.sendType = new JComboBox();
        this.sendType.addItem("所有人");
        this.sendType.setSize(150, 40);
        this.sendType.setLocation(0, 0);

        this.sendAudioBut = new JButton("发送语音");
        this.sendAudioBut.setSize(150, 40);
        this.sendAudioBut.setLocation(150, 0);

        this.listenAudioBut = new JButton("收听语音");
        this.listenAudioBut.setSize(150, 40);
        this.listenAudioBut.setLocation(300, 0);

        this.sendFileBut = new JButton("发送文件");
        this.sendFileBut.setSize(150, 40);
        this.sendFileBut.setLocation(450, 0);

        this.seeFileBut = new JButton("查看文件");
        this.seeFileBut.setSize(150, 40);
        this.seeFileBut.setLocation(600, 0);

        functionPanel.add(this.sendType);
        functionPanel.add(this.sendAudioBut);
        functionPanel.add(this.listenAudioBut);
        functionPanel.add(this.sendFileBut);
        functionPanel.add(this.seeFileBut);

        functionPanel.setVisible(true);
        this.f.add(functionPanel);

        this.inputArea = new JTextArea();
        this.inputArea.setBorder(grayBorder);
        this.inputArea.setFont(new Font("Microsoft Yahei", Font.PLAIN, 14));
        this.inputArea.setSize(750, 200);
        this.inputArea.setLocation(3, 550);
        this.inputArea.setLineWrap(true);
        this.inputArea.setWrapStyleWord(true);
        this.f.add(this.inputArea);

        this.onlineCnt = new JTextArea();
        this.onlineCnt.setBorder(grayBorder);
        this.onlineCnt.setFont(new Font("Microsoft Yahei", Font.PLAIN, 16));
        this.onlineCnt.setSize(225, 700);
        this.onlineCnt.setLocation(755, 3);
        this.onlineCnt.append("在线用户：");
        this.onlineCnt.setEditable(false); // 禁止编辑
        this.f.add(this.onlineCnt);

        this.sendMsg = new JButton("发送");
        this.sendMsg.setSize(80, 40);
        this.sendMsg.setLocation(755, 710);
        this.f.add(this.sendMsg);

        this.f.setVisible(true);
        this.f.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    }

    private void registerListeners() {
        this.f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                f.setVisible(false);
                /*
                    如果状态是SHUTDOWN，说明是从服务器端执行的断开连接，不需要发送下线信息
                 */
                if (client.serverStatus == Server.Status.RUNNING) {
                    client.close(client);
                    System.exit(0);
                } else if (client.serverStatus == Server.Status.SHUTDOWN) {
                    System.exit(0);
                }
            }
        });

        this.sendMsg.addActionListener(e -> {
            String sendName = sendType.getSelectedItem().toString();
            String recAddr = null;
            if (sendName.equals("所有人")) {
                recAddr = "/" + Utils.getConfig("SERVER_IP");
            } else {
                recAddr = MySQLUtils.getIPByName(sendName);
            }
            String content = inputArea.getText();
            client.sendMsg(this, recAddr, content);
            inputArea.setText("");
        });
    }

    // getter()
    public JFrame getF() {
        return this.f;
    }

    public JTextArea getChatHistory() {
        return this.chatHistory;
    }

    public JTextArea getInputArea() {
        return this.inputArea;
    }

    public JTextArea getOnlineCnt() {
        return this.onlineCnt;
    }

    public JComboBox getSendType() {
        return this.sendType;
    }

    public JButton getSendAudioBut() {
        return this.sendAudioBut;
    }

    public  JButton getListenAudioBut() {
        return this.listenAudioBut;
    }

    public JButton getSendFileBut() {
        return this.sendFileBut;
    }

    public JButton getSeeFileBut() {
        return this.seeFileBut;
    }

    public JButton getSendMsg() {
        return this.sendMsg;
    }
}
