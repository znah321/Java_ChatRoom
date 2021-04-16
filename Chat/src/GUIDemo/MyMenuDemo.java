package GUIDemo;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MyMenuDemo {
    private Frame f;
    private MenuBar mb;
    private Menu m;
    private MenuItem closeItem;

    MyMenuDemo() {
        init();
    }

    public void init() {
        f = new Frame("MyMenu");
        f.setBounds(300,100,500,600);
        f.setLayout(new FlowLayout());

        mb = new MenuBar();
        m = new Menu("文件");
        closeItem = new MenuItem("退出");

        m.add(closeItem);
        mb.add(m);
        f.setMenuBar(mb);

        myEvent();

        f.setVisible(true);
    }

    public void myEvent() {
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.exit(-1);
            }
        });

        closeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        new MyMenuDemo();
    }
}
