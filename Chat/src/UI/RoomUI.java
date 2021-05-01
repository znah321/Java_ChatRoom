package UI;

import audio.AudioPlayer;
import bean.Audio;
import bean.FileBean;
import bean.Message;
import client.Client;
import client.ClientSender;
import server.Server;
import util.AudioUtils;
import util.MySQLUtils;
import util.Utils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.*;
import javax.swing.text.html.HTMLDocument;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RoomUI {
    /*
        聊天室界面
     */
    private final Client client;
    private JFrame f;
    private JTextPane chatHistory; // 聊天记录
    private Document historyDoc;
    private HTMLDocument chatHtmlDoc;
    private JTextPane inputArea; // 输入框
    private Document inputDoc;
    private JTextArea onlineCnt; // 在线人数统计
    private JComboBox sendType; // 发送类型
    private JButton sendAudioBut;
    private JButton sendFileBut;
    private JButton addEmojiBut; // 添加表情
    private ImageIcon[][] emojiList;
    private String[] name;
    private JButton sendMsg;

    public RoomUI(Client client) {
        this.client = client;
    }

    public  void initUI() {
        this.initComponents();
        this.registerListeners();
        /* 加载表情文件 */
        File filepath = new File("src/emoji");
        File[] emojis = filepath.listFiles();
        ImageIcon[][] emojiList = new ImageIcon[11][10];
        String[] name = new String[10];
        for(int i = 0; i < 11; i++) {
            for(int j = 0; j < 10; j++) {
                try {
                    String path = emojis[i*10+j].getAbsolutePath();
                    emojiList[i][j] = new ImageIcon(path);
                    name[j] = "第" + (j+1) + "列";
                } catch (ArrayIndexOutOfBoundsException e) {
                    break;
                }
            }
        }
        this.emojiList = emojiList;
        this.name = name;
    }

    private void initComponents() {
        this.f = new JFrame("JChat");
        this.f.setBounds(300, 200, 1000, 800);
        this.f.setLayout(null);

        Border grayBorder = BorderFactory.createLineBorder(Color.GRAY);

        this.chatHistory = new JTextPane();
        this.chatHistory.setBorder(grayBorder);
        this.chatHistory.setSize(750, 500);
        this.chatHistory.setLocation(3, 3);
        this.chatHistory.setContentType("text/html");
        this.chatHistory.setEditable(false); // 禁止编辑
        this.chatHtmlDoc = (HTMLDocument) this.chatHistory.getDocument();
        this.historyDoc = chatHistory.getStyledDocument();

        JScrollPane historyScrollPane = new JScrollPane(chatHistory);
        historyScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        historyScrollPane.setBounds(3, 3, 750, 500);

        this.f.add(historyScrollPane);

        /* 功能表 */
        JPanel functionPanel = new JPanel();
        functionPanel.setLocation(3, 503);
        functionPanel.setSize(750, 40);
        functionPanel.setLayout(null);

        this.sendType = new JComboBox();
        this.sendType.setFont(new Font("Microsoft Yahei", Font.PLAIN, 20));
        this.sendType.addItem("所有人");
        this.sendType.setSize(150, 40);
        this.sendType.setLocation(0, 0);

        this.sendAudioBut = new JButton("发送语音");
        this.sendAudioBut.setFont(new Font("Microsoft Yahei", Font.PLAIN, 20));
        this.sendAudioBut.setSize(150, 40);
        this.sendAudioBut.setLocation(150, 0);

        this.sendFileBut = new JButton("发送文件");
        this.sendFileBut.setFont(new Font("Microsoft Yahei", Font.PLAIN, 20));
        this.sendFileBut.setSize(150, 40);
        this.sendFileBut.setLocation(300, 0);

        this.addEmojiBut = new JButton("添加表情");
        this.addEmojiBut.setFont(new Font("Microsoft Yahei", Font.PLAIN, 20));
        this.addEmojiBut.setSize(150, 40);
        this.addEmojiBut.setLocation(450, 0);

        functionPanel.add(this.sendType);
        functionPanel.add(this.sendAudioBut);
        functionPanel.add(this.sendFileBut);
        functionPanel.add(this.addEmojiBut);

        functionPanel.setVisible(true);
        this.f.add(functionPanel);

        this.inputArea = new JTextPane();
        this.inputArea.setBorder(grayBorder);
        this.inputArea.setFont(new Font("Microsoft Yahei", Font.PLAIN, 14));
        this.inputArea.setSize(750, 200);
        this.inputArea.setLocation(3, 550);
        SimpleAttributeSet inputSet = new SimpleAttributeSet();
        inputArea.setCharacterAttributes(inputSet, true);
        this.inputDoc = inputArea.getStyledDocument();
        JScrollPane inputScrollPane = new JScrollPane(inputArea);
        inputScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        inputScrollPane.setBounds(3, 550, 750, 200);

        this.f.add(inputScrollPane);

        this.onlineCnt = new JTextArea();
        this.onlineCnt.setBorder(grayBorder);
        this.onlineCnt.setFont(new Font("Microsoft Yahei", Font.PLAIN, 16));
        this.onlineCnt.setSize(225, 700);
        this.onlineCnt.setLocation(755, 3);
        this.onlineCnt.append("在线用户：");
        this.onlineCnt.setEditable(false); // 禁止编辑
        this.f.add(this.onlineCnt);

        this.sendMsg = new JButton("发送");
        this.sendMsg.setFont(new Font("Microsoft Yahei", Font.PLAIN, 20));
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
                    Client.close(client);
                    System.exit(0);
                } else if (client.serverStatus == Server.Status.SHUTDOWN) {
                    System.exit(0);
                }
            }
        });

        this.sendMsg.addActionListener(e -> {
            sendMessage();
        });

        this.sendFileBut.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser(System.getenv("USERPROFILE"));
            int val = fileChooser.showOpenDialog(null);
            Message.SendType type = null;
            String recName = sendType.getSelectedItem().toString();
            String recAddr;
            if (recName.equals("所有人")) {
                type = Message.SendType.ALL;
                recAddr = "/" + Utils.getConfig("SERVER_IP");
            } else {
                type = Message.SendType.TELL;
                recAddr = MySQLUtils.getIPByName(recName);
            }

            if (val == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                FileBean fileBean = new FileBean(Message.MsgType.FILE, type,
                        MySQLUtils.getIPByName(client.name), recAddr,
                        file.getName(), file);
                new ClientSender(client.oos, fileBean).start();
                // 将自己发的信息显示到聊天区
                String text = "你";
                if (recAddr.equals("/" + Utils.getConfig("SERVER_IP"))) {
                    text = text + "对所有人说";
                } else {
                    text = text + "悄悄地对" + recName;
                }
                String fullContent = text + " " + Utils.getCurrentTime() + "<br>" + "发送了一个文件" + fileBean.getFilename() + "<br><br>";
                this.appendText(client.name, fullContent, fileBean);
            }
        });

        this.sendAudioBut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RecodeUI recodeUI = new RecodeUI(client.roomUI, sendType.getSelectedItem().toString());
                recodeUI.initUI();
            }
        });

        this.chatHistory.addHyperlinkListener(e -> {
            if (e.getEventType() != HyperlinkEvent.EventType.ACTIVATED)
                return;
            URL linkURL = e.getURL();
            if (linkURL != null) {
                try {
                    if (linkURL.toString().endsWith(".mp3")) {
                        String audioPath = linkURL.toString().substring(6);
                        new AudioPlayer(new File(audioPath)).start();
                    } else {
                        Desktop.getDesktop().browse(linkURL.toURI());
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                } catch (URISyntaxException uriSyntaxException) {
                    uriSyntaxException.printStackTrace();
                }
            }
        });

        this.addEmojiBut.addActionListener(e -> appendEmoji());
    }

    /**
     * 发送消息
     */
    public void sendMessage() {
        // 检查是否被管理员踢出聊天室
        if (!MySQLUtils.isOnlineByName(client.name)) {
            return;
        }
        String sendName = sendType.getSelectedItem().toString();
        String recAddr = null;
        if (sendName.equals("所有人")) {
            recAddr = "/" + Utils.getConfig("SERVER_IP");
        } else {
            recAddr = MySQLUtils.getIPByName(sendName);
        }
        String content = this.inputArea.getText();

        client.sendMsg(this, recAddr, content);
        inputArea.setText("");
    }

    /**
     * 添加文本
     * @param text 文本内容
     * @param sender 发送人
     * @param msg 信息类对象
     */
    public void appendText(String sender, String text, Message msg) {
        try {
            // 先处理时间、发送人（第一行）
            String time = text.substring(0, text.indexOf("<"));
            text = text.substring(time.length() + 4);
            if (sender.equals(client.name)) {
                time = "<font face='Microsoft Yahei' size=5 color='green'>" + time + "</font><br>";
            } else {
                time = "<font face='Microsoft Yahei' size=5 color='blue'>" + time + "</font><br>";
            }
            this.chatHtmlDoc.insertAfterEnd(this.chatHtmlDoc.getCharacterElement(this.chatHtmlDoc.getLength()), time);
            /*
            对发送的内容进行处理：
                筛选链接-->href     图片-->img
                */
            // 1-筛选URL
            String regEx_Url = "(((https?|ftp|file)://)|www)[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
            Pattern p = Pattern.compile(regEx_Url);
            Matcher m = p.matcher(text);
            while (m.find()) {
                String matchedContent = m.group();
                String hrefContent = "<a href='" + matchedContent + "'>" + matchedContent + "</a>";
                text = text.replace(matchedContent, hrefContent);
            }
            // 2-筛选图片
            List<ImageIcon> imageIconList = this.filterImageIcon(sender, text);
            for(ImageIcon icon : imageIconList) {
                String iconPath = icon.toString();
                String imgContent = "<img src='file:\\" + iconPath + "'/>";
                text = text.replace("\\" + new File(iconPath).getName(), imgContent);
            }
            /*
                3-筛选语音文件
                    包装成超链接
             */
            if (msg instanceof Audio) {
                Audio audio = (Audio) msg;
                String filepath = audio.getAudioName();
                String normalContent = "<a href='file:\\" + filepath + "'>" + "一条语音消息（"
                        + AudioUtils.getDuration(filepath) + "s）</a><br><br>";
                text = normalContent;
            }

            /*
                4-处理普通文本
                    除了<a>、<img>、<br>之外的文本，其余全部加上font标签
             */
            String regEx_A = "<a[^>]*href=(\"([^\"]*)\"|'([^']*)'|([^\\s>]*))[^>]*>(.*?)</a>";
            String regEx_Img = "<img.*src\\s*=\\s*(.*?)[^>]*?>";
            String regEx_NL = "<br>";
            String fullRegEx = "(" + regEx_A + ")|" + "(" + regEx_Img + ")|" + "(" + regEx_NL + ")";
            p = Pattern.compile(fullRegEx);
            m = p.matcher(text);
            List<String> temp = new ArrayList<>();
            String tempText = text;
            char spliter = '\0';
            while (m.find()) {
                temp.add(m.group());
                tempText = tempText.replace(m.group(), String.valueOf(spliter));
            }
            String[] tempTextList = tempText.split(String.valueOf(spliter));
            for(String content : tempTextList) {
                if (content.equals("")) {
                    continue;
                }
                String normalContent = null;
                if (client.name.equals(sender)) {
                    normalContent = "<font face='Microsoft Yahei' size=5 color='green'>" + content + "</font>";
                } else {
                    normalContent = "<font face='Microsoft Yahei' size=5 color='blue'>" + content + "</font>";
                }
                text = text.replace(content, normalContent);
            }
            this.chatHtmlDoc.insertAfterEnd(this.chatHtmlDoc.getCharacterElement(this.chatHtmlDoc.getLength()), text);
        } catch (BadLocationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加表情
     */
    public void appendEmoji() {
        /* Step-1 初始化表格 */
        DefaultTableModel model = new DefaultTableModel(this.emojiList, this.name) {
            private static final long serialVersionUID = 1L;

            @Override
            public Class getColumnClass(int column) {
                return getValueAt(0, column).getClass();
            }
        };
        JTable emojiTable=new JTable(model);
        emojiTable.setRowHeight(30);
        JFrame tableFrame = new JFrame("表情");
        tableFrame.setLocation(500, 500);
        tableFrame.setSize(450,340);
        tableFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        Container container = tableFrame.getContentPane();
        container.add(new JScrollPane(emojiTable));
        tableFrame.setVisible(true);

        /* Step-3 表格注册监听器 */
        emojiTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int r = emojiTable.getSelectedRow();
                int c = emojiTable.getSelectedColumn();
                ImageIcon selectedEmoji = (ImageIcon) emojiTable.getValueAt(r, c);
                if (selectedEmoji == null) {
                    e.consume();
                } else {
                    SimpleAttributeSet iconSet = new SimpleAttributeSet();
                    StyleConstants.setIcon(iconSet, selectedEmoji);
                    try {
                        inputDoc.insertString(inputDoc.getLength(), "\\" + new File(selectedEmoji.toString()).getName(), iconSet);
                    } catch (BadLocationException badLocationException) {
                        badLocationException.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 过滤待发送文本中的表情
     * @return
     */
    public List<ImageIcon> filterImageIcon(String name, String text) {
        List<ImageIcon> imageIconList = new ArrayList<>();
        if (name.equals(this.client.name)) {
            for (int i = 0; i < inputDoc.getRootElements()[0].getElementCount(); i++) {
                Element root = inputDoc.getRootElements()[0].getElement(i);
                for (int j = 0; j < root.getElementCount(); j++) {
                    ImageIcon icon = (ImageIcon) StyleConstants.getIcon(root.getElement(j).getAttributes());
                    if (icon != null) {
                        imageIconList.add(icon);
                    }
                }
            }
        } else {
            for(int i = 0; i < emojiList[0].length; i++) {
                for(int j = 0 ; j < emojiList[i].length; j++) {
                    String[] temp = emojiList[i][j].toString().split("\\\\");
                    String iconName = "\\" + temp[temp.length - 1];
                    if (text.contains(iconName)) {
                        imageIconList.add(emojiList[i][j]);
                    }
                }
            }
        }
        return imageIconList;
    }

    // getter()
    public JFrame getF() {
        return this.f;
    }

    public JTextPane getChatHistory() {
        return this.chatHistory;
    }

    public JTextPane getInputArea() {
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

    public JButton getSendFileBut() {
        return this.sendFileBut;
    }

    public JButton getaddEmojiBut() {
        return this.addEmojiBut;
    }

    public JButton getSendMsg() {
        return this.sendMsg;
    }

    public Client getClient() {
        return client;
    }
}
