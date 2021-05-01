package bean;

import java.io.Serializable;

public class Message implements Serializable {
    /*
        信息类
     */
    protected static final long serialVersionUID = 2196110968332072987L;
    protected MsgType msgType;
    protected SendType sendType;
    protected String sendAdr; // 发送人地址
    protected String recAdr; // 收件人地址
    protected String content; // 消息内容
    protected String fullMessage;

    public enum MsgType { // 消息类型
        WORD,
        AUDIO,
        FILE
    }
    public enum SendType { // 发送类型
        ONLINE,
        ALL,
        TELL,
        LEAVE,
        SILENT,
        SHUTDOWN
    }

    public Message() {

    }

    // 一般是文字类
    public Message(MsgType msgType, SendType sendType, String sendAdr, String recAdr, String content) {
        this.msgType = msgType;
        this.sendType = sendType;
        this.sendAdr = sendAdr;
        this.recAdr = recAdr;
        this.content = content;
        if (!this.content.endsWith("<br>")) {
            this.content = this.content + "<br>";
        }
        this.jointMessage();
    }

    public Message(MsgType msgType, SendType sendType, String sendAdr, String recAdr) {
        this.msgType = msgType;
        this.sendType = sendType;
        this.sendAdr = sendAdr;
        this.recAdr = recAdr;
        if (msgType == MsgType.AUDIO) {
            this.content = "发送了一条语音消息";
        }
        if (msgType == MsgType.FILE) {
            this.content = "发送了一个文件";
        }
        if (!this.content.endsWith("<br>")) {
            this.content = this.content + "<br>";
        }
        this.jointMessage();
    }

    public MsgType getMsgType() {
        return msgType;
    }

    public void setMsgType(MsgType msgType) {
        this.msgType = msgType;
    }

    public SendType getSendType() {
        return sendType;
    }

    public void setSendType(SendType sendType) {
        this.sendType = sendType;
    }

    public String getSendAdr() {
        return sendAdr;
    }

    public void setSendAdr(String sendAdr) {
        this.sendAdr = sendAdr;
    }

    public String getRecAdr() {
        return recAdr;
    }

    public void setRecAdr(String recAdr) {
        this.recAdr = recAdr;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFullMessage() {
        return fullMessage;
    }

    public void setFullMessage(String fullMessage) {
        this.fullMessage = fullMessage;
    }

    public void jointMessage() {
        this.fullMessage = this.msgType.toString();
        this.fullMessage += "@" + this.sendType.toString();
        this.fullMessage += "@" + this.sendAdr;
        this.fullMessage += "@" + this.recAdr;
        this.fullMessage += "@" + this.content;
    }
}
