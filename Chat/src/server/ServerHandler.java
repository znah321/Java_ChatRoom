package server;

import UI.ServerUI;
import util.MySQLUtils;
import util.Utils;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketAddress;

public class ServerHandler extends Thread {
    private Socket clientSocket; // 客户端的Socket

    public ServerHandler(Socket socket) {
        this.clientSocket = socket;
    }

    public void run() {
        System.out.println("ServerHandler for " + clientSocket.getRemoteSocketAddress() + " started...");
        ServerReceiver receiver = new ServerReceiver(this.clientSocket);
        receiver.start();
        while (receiver.isAlive()) {

        }
        /*
            用户退出了聊天室
                1、Server类中的onlineUserList删除对应的Socket
                2、表onlineUser删除对应的记录
                3、ServerUI中打印信息
                4、向全体用户广播用户下线消息
         */
        System.out.println("ServerHandler for " + clientSocket.getRemoteSocketAddress() + " ended...");
        String socketAddress = clientSocket.getRemoteSocketAddress().toString();
        ServerUI.getHistory().append("[Disconnect] " + socketAddress + "\n");

        // 从Server类中的onlineUserList和oosUserMap删除对应的键值对，并从数据库中删除
        Server.getOnlineUserList().remove(socketAddress);
        MySQLUtils.deleteClientByIP(socketAddress);
        Server.getOosUserMap().remove(socketAddress);

        // 关闭Socket
        if (clientSocket != null) {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
