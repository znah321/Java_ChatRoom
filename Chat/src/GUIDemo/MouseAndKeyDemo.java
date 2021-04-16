package GUIDemo;

import java.awt.*;
import java.awt.event.*;

public class MouseAndKeyDemo {

    // 1、定义该图形中所需的引用
    private Frame f;
    private Button b;

    /* 使用静态代码块初始化
    static {

    }

     */

    public MouseAndKeyDemo() {
        this.init();
    }

    public void init() {
        f = new Frame("my frame");

        f.setBounds(300, 100, 600, 500);
        f.setLayout(new FlowLayout());

        b = new Button("my button");

        f.add(b);

        this.myEvent();

        f.setVisible(true);
    }

    public static void main(String[] args) {
        MouseAndKeyDemo mouseAndKeyDemo = new MouseAndKeyDemo();
    }

    public void myEvent() {
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.exit(0);
            }
        });

        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int cnt = e.getClickCount();
                if (cnt == 1)
                    System.out.println("单击");
                if (cnt == 2)
                    System.out.println("双击");
            }
        });

        b.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                e.consume();
                System.out.println(e.getKeyCode() + "被按下了");
            }
        });
    }
}
