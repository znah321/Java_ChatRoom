package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RecodeUI {
    /*
        录音
     */
    private RoomUI roomUI;
    private JFrame f;
    private JButton but; // 开始录制/结束录制按钮
    private JLabel volumeLab; // 音量标签
    private JProgressBar volumeBar; // 音量进度条
    private JLabel state; // 录制状态
    private JButton sendBut; // 发送按钮

    public RecodeUI(RoomUI roomUI) {
        this.roomUI = roomUI;
    }

    public void initUI() {
        initComponents();
        registerListeners();
    }

    /**
     * 初始化组件
     */
    public void initComponents() {
        this.f = new JFrame("录制语音");
        this.but = new JButton("点击此处开始录制"); // 开始录制/结束录制按钮
        this.volumeLab = new JLabel("音量："); // 音量标签
        this.volumeBar = new JProgressBar(); // 音量进度条
        this.state = new JLabel("录制状态："); // 录制状态
        this.sendBut = new JButton("发送"); // 发送按钮

        this.f.setBounds(400, 300, 350, 400);
        this.f.setLayout(new FlowLayout());

        this.f.add(new JLabel("                                                                                "));
        this.f.add(new JLabel("                                                                                "));

        this.f.add(this.but);
        this.f.add(new JLabel("                                                                                "));

        // 音量进度条初始化
        this.f.add(this.volumeLab);
        this.volumeBar.setMinimum(0);
        this.volumeBar.setMaximum(10);
        this.volumeBar.setValue(0);
        this.f.add(this.volumeBar);

        this.f.add(new JLabel("                                                                  "));

        this.state.setText("录制状态：未开始录制");
        this.f.add(this.state);
        this.f.add(new JLabel("                                                                  "));
        this.f.add(new JLabel("                       "));
        this.f.add(this.sendBut);

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
                f.setVisible(false);
                // 返回聊天界面
            }
        });

        this.but.addMouseListener(new MouseAdapter() {
            private int clickCnt = 0;
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                clickCnt++;
                if (clickCnt == 1) {
                    but.setText("点击此处停止录制");
                    state.setText("录制状态：录制中");
                }
                if (clickCnt == 2) {
                    but.setText("点击此处开始录制");
                    clickCnt = 0;
                }
            }
        });

        // 发送按钮监听的事件
    }

    /**
     * 录制音频
     */
    public void recodeAudio() {

    }
}
