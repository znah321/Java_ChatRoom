package bean;

import util.AudioUtils;
import util.MySQLUtils;
import util.Utils;

import java.io.*;

public class Audio extends Message implements Serializable {
    /*
        语音文件类
     */
    private static final long serialVersionUID = -5533531453227359949L;
    private File file;
    private String audioName;
    private byte[] buffer;

    public Audio(MsgType msgType, SendType sendType, String sendAdr, String recAdr, String audioName) {
        super(msgType, sendType, sendAdr, recAdr);
        this.file = new File(audioName);
        this.audioName = audioName;
        this.audioToBytes();
    }

    public Audio(File file, byte[] buffer) {
        this.file = file;
        this.buffer = buffer;
    }

    public void bytesToAudio() {
        try (FileOutputStream fos = new FileOutputStream(new File(this.audioName))){
            fos.write(this.buffer, 0, this.buffer.length);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void audioToBytes() {
        byte[] data = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(this.file);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int numBytesRead = 0;
            while ((numBytesRead = fis.read(buf)) != -1) {
                output.write(buf, 0, numBytesRead);
            }
            data = output.toByteArray();
            output.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.buffer = data;
    }

    public static File getAudioFile(String username, String filename) {
        String path = Utils.getFilePath(username, "AUDIO") + "\\" + filename;
        File file = new File(path);
        return file;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public void setBuffer(byte[] buffer) {
        this.buffer = buffer;
    }

    public String getAudioName() {
        return audioName;
    }

    public void setAudioName(String audioName) {
        this.audioName = audioName;
    }
}
