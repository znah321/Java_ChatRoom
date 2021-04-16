package GUIDemo;

import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class MyWindowDemo {
    private Frame f;
    private Button b;
    private TextField tf;
    private TextArea ta;
    private Dialog d;
    private Label lab;
    private Button OKButton;

    MyWindowDemo() {
        init();
    }

    public void init() {
        f = new Frame("MyWindow");
        f.setBounds(300,100,600,500);
        f.setLayout(new FlowLayout());

        tf = new TextField(60);
        b = new Button("转到");
        ta = new TextArea(25,70);

        d = new Dialog(f, "提示信息-self", true);
        d.setBounds(400, 200, 240, 150);
        d.setLayout(new FlowLayout());

        lab = new Label();
        OKButton = new Button("确定");
        d.add(lab);
        d.add(OKButton);

        f.add(tf);
        f.add(b);
        f.add(ta);

        myEvent();

        f.setVisible(true);
    }

    private void myEvent() {
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.exit(0);
            }
        });

        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                solve();
            }
        });

        ta.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                e.consume();
            }
        });

        d.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                d.setVisible(false);
            }
        });
        OKButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                d.setVisible(false);
            }
        });

        tf.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    solve();
            }
        });
    }

    public void solve() {
        String path = tf.getText();
        File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            ta.setText("");
            File[] files = file.listFiles();
            for (File aFile : files)
                ta.append(aFile.toString() + "\r\n");
        } else {
            String info = "该目录不存在！";
            lab.setText(info);
            d.setVisible(true);
        }
    }

    public static void main(String[] args) {
        MyWindowDemo myWindowDemo = new MyWindowDemo();
    }
}
