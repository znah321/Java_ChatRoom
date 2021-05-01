package bean;

import util.MySQLUtils;
import util.Utils;

import java.io.*;

public class FileBean extends Message implements Serializable {
    private static final long serialVersionUID = 1863330785113532789L;
    private final String filename;
    private final File file;
    private byte[] buffer;

    public FileBean(MsgType msgType, SendType sendType, String sendAdr, String recAdr, String filename, File file) {
        super(msgType, sendType, sendAdr, recAdr);
        this.content = this.getContent() + "：" + filename;
        this.filename = filename;
        this.file = file;
        // 转成byte数组
        this.fileToBytes();
    }

    public void fileToBytes() {
        byte[] data = null;
        FileInputStream input = null;
        try {
            input = new FileInputStream(this.file);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int numBytesRead = 0;
            while ((numBytesRead = input.read(buf)) != -1) {
                output.write(buf, 0, numBytesRead);
            }
            data = output.toByteArray();
            output.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.buffer = data;
    }

    public void bytesToFile() {
        if (this.buffer.length < 3) {
            return;
        }
        String path = Utils.getFilePath(MySQLUtils.getNameByIP(this.recAdr), "FILE");
        File recvFile = new File(path + "\\" +this.filename);
        try {
            FileOutputStream output = new FileOutputStream(recvFile);
            output.write(this.buffer, 0, this.buffer.length);
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFilename() {
        return filename;
    }

    public File getFile() {
        return file;
    }

    public byte[] getBuffer() {
        return buffer;
    }
}
