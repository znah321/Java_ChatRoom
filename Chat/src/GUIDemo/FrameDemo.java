package GUIDemo;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class FrameDemo {

    // 1、定义该图形中所需的引用
    private Frame f;
    private Button b;

    /* 使用静态代码块初始化
    static {

    }

     */

    public FrameDemo() {
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
        FrameDemo frameDemo = new FrameDemo();
    }

    public void myEvent() {
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.exit(0);
            }
        });

        /*
            让按钮具备退出程序的功能：
                按钮就是事件源，选择ActionListener
         */
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }
}
