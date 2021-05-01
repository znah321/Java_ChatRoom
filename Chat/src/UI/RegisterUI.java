package UI;

import util.MySQLUtils;
import util.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.regex.Pattern;

public class RegisterUI {
    /*
        注册窗口
     */
    private final LoginUI loginUI;
    private JFrame f;
    private JLabel nameLab; // 用户名标签
    private JLabel pwLab; // 密码标签
    private JLabel confPwLab; // 确认密码标签
    private JTextField nameText; // 用户文本框
    private JPasswordField pwText; // 密码文本框
    private JPasswordField confPwText; // 确认密码文本框
    private JButton registerBut; // 注册按钮

    public RegisterUI(LoginUI loginUI) {
        this.loginUI = loginUI;
    }

    /**
     * 注册窗口运行
     */
    public void initUI() {
        initComponents();
        registerListeners();
    }

    /**
     * 初始化组件
     */
    public void initComponents() {
        this.f = new JFrame("注册用户");
        this.nameLab = new JLabel("    用户名："); // 用户名标签
        this.nameLab.setFont(new Font("Microsoft Yahei", Font.PLAIN, 20));
        this.pwLab = new JLabel("       密码："); // 密码标签
        this.pwLab.setFont(new Font("Microsoft Yahei", Font.PLAIN, 20));
        this.confPwLab = new JLabel("确认密码："); // 确认密码标签
        this.confPwLab.setFont(new Font("Microsoft Yahei", Font.PLAIN, 20));
        this.nameText = new JTextField(12); // 用户文本框
        this.nameLab.setFont(new Font("Microsoft Yahei", Font.PLAIN, 20));
        this.pwText = new JPasswordField(12); // 密码文本框
        this.pwText.setFont(new Font("Microsoft Yahei", Font.PLAIN, 20));
        this.confPwText = new JPasswordField(12); // 确认密码文本框
        this.confPwText.setFont(new Font("Microsoft Yahei", Font.PLAIN, 20));
        this.registerBut = new JButton("    注册    "); // 注册按钮
        this.registerBut.setFont(new Font("Microsoft Yahei", Font.PLAIN, 20));

        this.f.setBounds(400, 300, 400, 500);
        this.f.setLayout(new FlowLayout());

        this.f.add(new JLabel("                                                                                "));
        this.f.add(new JLabel("                                                                                "));

        // 用户名的Panel
        JPanel namePanel = new JPanel();
        namePanel.setLayout(new FlowLayout());
        namePanel.add(this.nameLab);
        namePanel.add(this.nameText);
        namePanel.setVisible(true);
        f.add(namePanel);

        // 密码的Panel
        JPanel pwPanel = new JPanel();
        pwPanel.setLayout(new FlowLayout());
        pwPanel.add(this.pwLab);
        pwPanel.add(this.pwText);
        pwPanel.setVisible(true);
        this.f.add(pwPanel);


        // 确认密码的Panel
        JPanel confPwPanel = new JPanel();
        confPwPanel.setLayout(new FlowLayout());
        confPwPanel.add(this.confPwLab);
        confPwPanel.add(this.confPwText);
        confPwPanel.setVisible(true);
        this.f.add(confPwPanel);

        this.f.add(new JLabel("                                                                                                               "));

        this.f.add(this.registerBut);


        this.f.setVisible(true);
    }

    /**
     * 注册监听器
     */
    public void registerListeners() {
        this.f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                // 关闭注册界面，重新显示登录界面
                f.setVisible(false);
                loginUI.f.setVisible(true);
            }
        });

        this.registerBut.addActionListener(e -> register());
    }

    public void register() {
        String pw = String.valueOf(this.pwText.getPassword());
        String confPw = String.valueOf(this.confPwText.getPassword());
        String nameRegex = "^[a-zA-Z\\u4E00-\\u9FA5][a-zA-Z0-9_\\u4E00-\\u9FA5]{4,15}$";
        String pwRegex = "^[a-zA-Z_0-9]{5,17}$";
        // 检查两次输入的密码是否一致
        if (!pw.equals(confPw)) {
            Utils.geneTestDialog(this.f, "注册", "两次输入的密码不一致！", this.f);
            return;
        }
        // 检查用户名是否已存在
        String name = this.nameText.getText();
        if (MySQLUtils.hasSameUser(name)) {
            Utils.geneTestDialog(this.f, "注册", "该用户名已存在！", this.f);
            return;
        }
        // 检查用户名是否合法（5-16位，只能含有汉字、字母、数字，不能以数字开头）
        if (!Pattern.matches(nameRegex, name)) {
            Utils.geneTestDialog(this.f, "注册", "用户名不合法！", this.f);
            return;
        }
        // 检查密码是否合法（长度在6~18之间，只能包含字母、数字和下划线）
        if (!Pattern.matches(pwRegex, pw)) {
            Utils.geneTestDialog(this.f, "注册", "密码不合法！", this.f);
            return;
        }

        // 注册
        MySQLUtils.register(name, pw);
        Utils.geneTestDialog(this.f, "注册用户", "注册成功！", loginUI.f);
        this.f.setVisible(false);
    }
}
