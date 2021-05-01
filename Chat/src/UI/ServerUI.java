package UI;

import server.Admin;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

public class ServerUI {
    private static JFrame f;
    private static JTextPane history; // 历史记录
    private static JTextField inputCommand; // 输入的命令
    private static JButton but;

    public static void initUI() {
        initComponents();
        registerListeners();
    }

    /**
     * 获取自定义字体
     * @param fontSize 字体大小
     */
    public static Font getSelfDefinedFont(int fontSize) {
        Font font = null;
        File fontFile = new File("src/lib/Microsoft+YaHei+Mono.ttf");
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
            font = font.deriveFont(Font.PLAIN, fontSize);
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return font;
    }

    /**
     * 初始化组件
     */
    public static void initComponents() {

        f = new JFrame("JChat Server");
        f.setLayout(null);
        f.setBounds(400, 300, 520, 600);
        Border grayBorder = BorderFactory.createLineBorder(Color.GRAY);

        history = new JTextPane();
        history.setEditable(false);
        history.setBorder(grayBorder);
        history.setLocation(3, 3);
        history.setSize(490, 500);
        history.setFont(getSelfDefinedFont(14));
        JScrollPane jScrollPane = new JScrollPane(history);
        jScrollPane.setBounds(3, 3, 490, 500);
        f.add(jScrollPane);

        inputCommand = new JTextField();
        inputCommand.setLocation(3, 506);
        inputCommand.setSize(400, 50);
        inputCommand.setBorder(grayBorder);
        inputCommand.setFont(getSelfDefinedFont(16));
        f.add(inputCommand);

        but = new JButton("发送");
        but.setLocation(406, 506);
        but.setSize(88, 50);
        but.setFont(getSelfDefinedFont(18));
        f.add(but);

        f.setVisible(true);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    /**
     * 注册监听器
     */
    public static void registerListeners() {
        inputCommand.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    analyzeCommand();
                }
            }
        });
        but.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                analyzeCommand();
            }
        });
    }

    public static void appendText(String text) {
        if (text.endsWith("<br>")) {
            text = text.substring(0, text.length() - 4) + "\n";
        }
        Document doc = history.getStyledDocument();
        SimpleAttributeSet set = new SimpleAttributeSet();
        try {
            doc.insertString(doc.getLength(), text, set);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public static void analyzeCommand() {
        // do something...
        String input = inputCommand.getText();
        int pos = input.indexOf(" ");
        String command = "";
        try {
            command = input.substring(0, pos);
        } catch (StringIndexOutOfBoundsException e) {
            command = input;
        }
        input = input.replace(command + " ", "");
        if (command.equals("stop")) {
            Admin.shutdown();
        } else if (command.equals("silent")) {
            pos = input.indexOf(" ");
            String user = input.substring(0, pos);
            String duration = input.replace(user + " ", "");
            Admin.silent(user, duration);
        } else if (command.equals("kick")) {
            String user = input;
            Admin.kick(user);
        } else if (command.equals("tell")) {
            pos = input.indexOf(" ");
            String user = input.substring(0, pos);
            String content = input.replace(user + " ", "");
            if (content.length() == 0) {
                try {
                    history.getDocument().insertString(history.getDocument().getLength(),
                            "[Error] 发送的消息不得为空！", new SimpleAttributeSet());
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
            Admin.sendPrivateMessage(user, content);
        } else if (command.equals("say")) {
            String content = input;
            if (content.length() == 0) {
                try {
                    history.getDocument().insertString(history.getDocument().getLength(),
                            "[Error] 发送的消息不得为空！", new SimpleAttributeSet());
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
            Admin.broadcast(content);
        } else {
            try {
                history.getDocument().insertString(history.getDocument().getLength(),
                        "[Error] 未知的命令：" + command, new SimpleAttributeSet());
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
        inputCommand.setText("");
    }

    // getter()

    public static JFrame getF() {
        return f;
    }

    public static JTextPane getHistory() {
        return history;
    }

    public static JTextField getInputCommand() {
        return inputCommand;
    }

    public static JButton getBut() {
        return but;
    }
}
