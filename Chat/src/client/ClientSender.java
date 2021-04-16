package client;

import bean.Message;

import java.io.*;

public class ClientSender extends Thread {
    private ObjectOutputStream oos;
    private Message msg;

    public ClientSender(ObjectOutputStream oos, Message msg) {
        this.oos = oos;
        this.msg = msg;
    }

    @Override
    public void run() {
        try {
            oos.reset(); //////////////////////
            oos.writeObject(msg);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
