package UI;

import audio.AudioPlayer;
import audio.AudioRecorder;
import bean.Audio;
import bean.Message;
import client.ClientSender;
import server.ServerSender;
import util.AudioUtils;
import util.MySQLUtils;
import util.Utils;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class RecodeUI {
    /*
        录音
     */
    private final RoomUI roomUI;
    private JFrame f;
    private JButton but; // 开始录制/结束录制按钮
    private JButton play; // 播放按钮
    private JLabel state; // 录制状态
    private JButton sendBut; // 发送按钮
    private final String pathOfWAV; // 当前录制的语音对应的文件名(wav格式)
    private final String pathOfMP3; // 当前录制的语音对应的文件名(mp3格式)
    private final String receiver;
    private AudioRecorder audioRecorder;
    private AudioPlayer audioPlayer;

    public RecodeUI(RoomUI roomUI, String receiver) {
        this.roomUI = roomUI;
        this.receiver = receiver;
        this.pathOfWAV = Utils.getFilePath(roomUI.getClient().name, "AUDIO") + "\\" +
                AudioUtils.getAudioFileName(roomUI.getClient().name, receiver);
        this.pathOfMP3 = this.pathOfWAV.substring(0, this.pathOfWAV.length() - 3) + "mp3";
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
        this.but.setFont(new Font("Microsoft Yahei", Font.PLAIN, 20));
        this.state = new JLabel("录制状态："); // 录制状态
        this.state.setFont(new Font("Microsoft Yahei", Font.PLAIN, 20));
        this.sendBut = new JButton("发送"); // 发送按钮
        this.sendBut.setFont(new Font("Microsoft Yahei", Font.PLAIN, 20));
        this.play = new JButton("播放已经录制的音频");
        this.play.setFont(new Font("Microsoft Yahei", Font.PLAIN, 20));

        this.f.setBounds(400, 300, 350, 400);
        this.f.setLayout(new FlowLayout());

        this.f.add(new JLabel("                                                                                "));
        this.f.add(new JLabel("                                                                                "));

        this.f.add(this.but);
        this.f.add(new JLabel("                                                                                "));

        this.f.add(play);
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
                if (clickCnt % 2 == 1) {
                    AudioUtils.playRemindAudio();
                    but.setText("点击此处停止录制");
                    state.setText("录制状态：录制中");
                    AudioRecorder recorder = new AudioRecorder(clickCnt, pathOfWAV, pathOfMP3);
                    audioRecorder = recorder;
                    recorder.start();
                }
                if (clickCnt % 2 == 0) {
                    try {
                        audioRecorder.stopRecord();
                    } catch (LineUnavailableException lineUnavailableException) {
                        lineUnavailableException.printStackTrace();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    but.setText("点击此处开始录制");
                    state.setText("录制状态：录制成功");
                    // 录制完就转格式
                    AudioUtils.wavToMp3(pathOfWAV, pathOfMP3);
                    AudioUtils.playRemindAudio();
                }
            }
        });

        play.addMouseListener(new MouseAdapter() {
            private int clickCnt = 0;
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                clickCnt++;
                // 先判断有没有录音
                if (!Utils.fileExists(pathOfWAV)) {
                    Utils.geneTestDialog(f,"录制语音", "还未录制语音，无法播放！", f);
                    return;
                }


                if (clickCnt == 1) {
                    AudioUtils.playRemindAudio();
                    playAudio();
                    play.setText("点击此处暂停播放");
                } else {
                    audioPlayer.stopAudio();
                    play.setText("播放已经录制的音频");
                    AudioUtils.playRemindAudio();
                    clickCnt = 0;
                }
            }
        });

        // 发送按钮监听的事件
        sendBut.addActionListener(e -> {
            // 先判断有没有录音
            if (!Utils.fileExists(pathOfWAV)) {
                Utils.geneTestDialog(f,"录制语音", "还未录制语音，无法播放！", f);
                return;
            }

            String sendname = this.roomUI.getClient().name;
            // TODO:构造一个Audio类，然后发送
            String recName = this.roomUI.getSendType().getSelectedItem().toString();
            String recAddr = null;
            // 确定发送类型
            if (recName.equals("所有人")) {
                recAddr = "/" + Utils.getConfig("SERVER_IP");
            } else {
                recAddr = MySQLUtils.getIPByName(recName);
            }
            Message.SendType sendType = null;
            if (recAddr.equals("/" + Utils.getConfig("SERVER_IP"))) {
                sendType = Message.SendType.ALL;
            } else {
                sendType = Message.SendType.TELL;
            }

            // 发送的是mp3格式的文件
            Audio audio = new Audio(Message.MsgType.AUDIO, sendType, MySQLUtils.getIPByName(sendname), recAddr, this.pathOfMP3);
            new ClientSender(this.roomUI.getClient().oos, audio).start();
            Utils.deleteFile(this.pathOfWAV); // 删掉wav格式的文件

            // 将自己发的信息显示到聊天区
            String text = "你";
            if (recAddr.equals("/" + Utils.getConfig("SERVER_IP"))) {
                text = text + "对所有人说";
            } else {
                text = text + "悄悄地对" + recName;
            }
            String fullContent = text + " " + Utils.getCurrentTime() + "<br>" + audio.getContent() + "<br><br>";
            this.roomUI.appendText(this.roomUI.getClient().name, fullContent, audio);
        });
    }

    public void playAudio() {
        File file = new File(this.pathOfMP3);
        this.audioPlayer = new AudioPlayer(file);
        this.audioPlayer.start();
        new Thread(() -> {
            while (audioPlayer.isAlive()) {}
            play.setText("播放已经录制的音频");
        }).start();
    }
}
