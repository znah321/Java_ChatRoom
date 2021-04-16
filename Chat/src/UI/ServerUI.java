package UI;

import server.Admin;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

public class ServerUI {
    private static JFrame f;
    private static JTextArea history; // 历史记录
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

        history = new JTextArea();
        history.setEditable(false);
        history.setBorder(grayBorder);
        history.setLocation(3, 3);
        history.setSize(490, 500);
        history.setFont(getSelfDefinedFont(14));
        history.setLineWrap(true);
        history.setWrapStyleWord(true);
        JScrollPane jScrollPane = new JScrollPane(history);
        f.add(jScrollPane);
        f.add(history);

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
    }

    /**
     * 注册监听器
     */
    public static void registerListeners() {
        inputCommand.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    // do something...
                    String command = inputCommand.getText();
                    String[] commands = command.split(" ");
                    if (commands[0].equals("stop")) {
                        Admin.shutdown();
                    } else if (commands[0].equals("silent")) {
                        Admin.silent(command, commands[1], commands[2]);
                    }
                    inputCommand.setText("");
                }
            }
        });
        but.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println(e);
            }
        });
    }

    // getter()

    public static JFrame getF() {
        return f;
    }

    public static JTextArea getHistory() {
        return history;
    }

    public static JTextField getInputCommand() {
        return inputCommand;
    }

    public static JButton getBut() {
        return but;
    }
}
