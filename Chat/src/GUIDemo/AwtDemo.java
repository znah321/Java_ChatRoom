package GUIDemo;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/*
    创建图形化界面：
        1、创建Frame框体
        2、对窗体进行基本设置：比如大小、位置、布局
        3、定义组件
        4、将组件通过窗体的add方法添加到窗体中
        5、让窗体显示：setVisible(true)
 */

public class AwtDemo {
    public static void main(String[] args) {
        Frame f = new Frame("my awt");
        f.setSize(500, 400);
        f.setLocation(300, 200);
        f.setVisible(true);
        f.setLayout(new FlowLayout()); // 设置为流式布局

        Button b = new Button("这是一个按钮");
        f.add(b);

        /*
            框架Frame --> 注册监听器：addWindowListener
         */
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosed(e);
                System.out.println("window closing... " + e.toString());
            }
        });

        System.out.println("Hello World!");
    }
}
