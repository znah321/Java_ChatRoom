package audio;

import util.Utils;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioRecorder extends Thread {
    /*
        录音线程
     */
    private String audioFileName;
    private final int clickCnt;
    private final String pathOfWAV;
    private final String pathOfMP3;
    private TargetDataLine targetDataLine;

    public AudioRecorder(int clickCnt, String pathOfWAV, String pathOfMP3) {
        this.clickCnt = clickCnt;
        this.pathOfWAV = pathOfWAV;
        this.pathOfMP3 = pathOfMP3;
    }

    @Override
    public void run() {
        if (clickCnt > 0) {
            // 两种格式的都删掉
            Utils.deleteFile(this.pathOfWAV);
            Utils.deleteFile(this.pathOfMP3);
        }
        // TODO:录音
        try {
            this.startRecord();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public String getAudioFileName() {
        return this.audioFileName;
    }

    public void startRecord() throws IOException, LineUnavailableException {
        File outputFile = new File(this.pathOfWAV);
        AudioFormat audioFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED, 44100.0F, 16, 2, 4, 44100.0F,
                false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
        this.targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
        targetDataLine.open(audioFormat);
        targetDataLine.start();
        new Thread() {
            public void run() {
                AudioInputStream cin = new AudioInputStream(targetDataLine);
                try {
                    AudioSystem.write(cin, AudioFileFormat.Type.WAVE, outputFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void stopRecord() throws LineUnavailableException, IOException {
        this.targetDataLine.close();
    }
}
