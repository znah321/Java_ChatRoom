package UI;

import util.MySQLUtils;
import util.Utils;
import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.*;
import java.util.Enumeration;

public class LoginUI {
    public JFrame f;
    public JButton loginBut; // 登录按钮
    public JButton registerBut; // 注册按钮
    public JLabel accLab; // 帐号标签
    public JTextField accText; // 帐号文本框
    public JLabel passwordLab; // 密码标签
    public JPasswordField passwordText; // 密码文本框
    public String state = null;
    public static Font font = new Font("Microsoft Yahei", Font.PLAIN, 20);

    public void run() {
        this.initComponents();
        this.registerListener();
    }

    /**
     * 初始化组件
     */
    public void initComponents() {
        this.f  = new JFrame("JChat");
        f.setFont(font);
        this.loginBut = new JButton("    登录    "); // 登录按钮
        loginBut.setFont(font);
        this.registerBut = new JButton("    注册    "); // 注册按钮
        registerBut.setFont(font);
        this.accLab = new JLabel("账号："); // 帐号标签
        accLab.setFont(font);
        this.accText = new JTextField(8); // 帐号文本框
        accText.setFont(font);
        this.passwordLab = new JLabel("密码："); // 密码标签
        passwordLab.setFont(font);
        this.passwordText = new JPasswordField(8); // 密码文本框
        passwordText.setFont(font);

        this.f.setBounds(400, 300, 400, 500);
        this.f.setLayout(new FlowLayout());

        this.f.add(new JLabel("                        "));
        this.f.add(new JLabel("                        "));
        this.f.add(new JLabel("                        "));

        // 帐号部分的Panel
        JPanel accPanel = new JPanel();
        accPanel.setLayout(new FlowLayout());
        accPanel.add(this.accLab);
        accPanel.add(this.accText);
        accPanel.setVisible(true);
        f.add(accPanel);

        // 密码部分的Panel
        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new FlowLayout());
        passwordPanel.add(this.passwordLab);
        passwordPanel.add(this.passwordText);
        passwordPanel.setVisible(true);
        this.f.add(passwordPanel);

        // 两个按钮的Panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout());
        buttonsPanel.add(this.loginBut);
        buttonsPanel.add(this.registerBut);
        buttonsPanel.setVisible(true);
        this.f.add(buttonsPanel);

        this.f.add(new JLabel("                                                       "));
        this.f.add(new JLabel("                                                       "));

        this.f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.f.setVisible(true);
    }

    /**
     * 注册监听器
     */
    public void registerListener() {
        // 登录按钮
        this.loginBut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                f.setVisible(false);
                String account  = accText.getText();
                String password = String.valueOf(passwordText.getPassword());
                login(account, password);
            }
        });

        // 注册按钮
        this.registerBut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                f.setVisible(false);
                register();
            }
        });

        // 密码框
        this.passwordText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    f.setVisible(false);
                    login(accText.getText(), String.valueOf(passwordText.getPassword()));
                }
            }
        });
    }

    /**
     * 登录
     */
    public void login(String account, String password) {
        if (MySQLUtils.matches(account, password)) {
            // do something...
            state = "true";
        }
        else {
            Utils.geneTestDialog(f, "登录", "登录失败，密码错误！", f);
            state = "false";
        }
    }

    /**
     * 注册
     */
    public void register() {
        RegisterUI registerUI = new RegisterUI(this);
        registerUI.initUI();
    }

}
