package audio;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import javax.sound.sampled.*;
import java.io.*;

public class AudioPlayer extends Thread {
    /*
        播放语音的线程
     */
    private final File audioFile;
    private Player player;

    public AudioPlayer(File audioFile) {
        this.audioFile = audioFile;
    }

    @Override
    public void run() {
        /*
            使用jl1.0.jar来播放mp3文件
         */
        BufferedInputStream stream;
        try (FileInputStream fis = new FileInputStream(this.audioFile)) {
            stream = new BufferedInputStream(fis);
            // TODO：无法播放收到的文件，没有声音
            this.player = new Player(stream);
            player.play();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JavaLayerException e) {
            e.printStackTrace();
        }
    }

    public void stopAudio() {
        player.close();
    }
}
