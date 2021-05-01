package util;

import audio.AudioPlayer;
import audio.AudioRecorder;
import it.sauronsoftware.jave.*;

import java.io.File;

public class AudioUtils {
    private AudioUtils() {}

    /**
     * 将wav文件转成mp3文件
     * @param inPath 源路径
     * @param outPath 输出路径
     */
    public static void wavToMp3(String inPath, String outPath) {
        File source = new File(inPath);
        File target = new File(outPath);
        AudioAttributes audioAttributes = new AudioAttributes();
        audioAttributes.setCodec("libmp3lame");
        audioAttributes.setBitRate(new Integer(44100));
        audioAttributes.setChannels(new Integer(2));
        audioAttributes.setSamplingRate(new Integer(44100));
        EncodingAttributes encodingAttributes = new EncodingAttributes();
        encodingAttributes.setFormat("mp3");
        encodingAttributes.setAudioAttributes(audioAttributes);
        Encoder encoder = new Encoder();
        try {
            encoder.encode(source, target, encodingAttributes);
        } catch (EncoderException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取语音文件持续时间
     * @param audioPath 文件路径
     * @return 持续时间(s)
     */
    public static int getDuration(String audioPath) {
        File source = new File(audioPath);
        int second = 0;
        Encoder encoder = new Encoder();
        try {
            MultimediaInfo m = encoder.getInfo(source);
            long ls = m.getDuration();
            second = (int) ls / 1000;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return second;
    }

    /**
     * 给语音文件命名
     * @param sender 发送人的名字
     * @param receiver 接收人的名字
     * @return 文件名
     */
    public static String getAudioFileName(String sender, String receiver) {
        String time = Utils.getCurrentTime();
        time = time.replace(" ", "_").replace(":", "_");
        String audioName = time + "_from_" + sender + "_to_" + receiver +".wav";
        return audioName;
    }

    /**
     * 播放录音/播放语音的提示音
     */
    public static void playRemindAudio() {
        File file = new File("src/sounds/audio.mp3");
        AudioPlayer player = new AudioPlayer(file);
        player.start();
        while (player.isAlive()) {}
    }

    /**
     * 播放上线提示音
     */
    public static void playOnlineAudio() {
        File file = new File("src/sounds/Online.mp3");
        AudioPlayer player = new AudioPlayer(file);
        player.start();
    }

    /**
     * 播放新消息提示音
     */
    public static void playNewMessageAudio() {
        File file = new File("src/sounds/msg.mp3");
        AudioPlayer player = new AudioPlayer(file);
        player.start();
    }
}
