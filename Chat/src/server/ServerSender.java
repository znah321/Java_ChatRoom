package server;

import bean.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.Map;

public class ServerSender extends Thread {
    private final String receiverAddress;
    private final Message msg;

    public ServerSender(String receiverAddress, Message msg) {
        this.receiverAddress = receiverAddress;
        this.msg = msg;
    }

    @Override
    public void run() {
        ObjectOutputStream oos = getOOS();
        try {
//            oos.reset(); //////////////////////
            oos.writeObject(msg);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取输出流
     * @return 输出流
     */
    public ObjectOutputStream getOOS() {
        Map<String, ObjectOutputStream> oosUserMap = Server.getOosUserMap();
        ObjectOutputStream targetOOS = null;

        Iterator iterator = oosUserMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            if (entry.getKey().toString().equals(this.receiverAddress)) {
                targetOOS = (ObjectOutputStream) entry.getValue();
                break;
            }
        }

        return targetOOS;
    }
}
