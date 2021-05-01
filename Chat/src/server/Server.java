package server;

import UI.ServerUI;
import bean.Message;
import util.Utils;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final Map<String, Socket> onlineUserList = new HashMap<>(); // 用户列表
    private static final Map<String, ObjectOutputStream> oosUserMap = new HashMap<>(); // 用户对应的ObjectOutputStream的Map
    private static final ExecutorService clientPool = Executors.newFixedThreadPool(50); // 用户线程池
    public enum Status {
        UNSTART,
        RUNNING,
        SHUTDOWN
    }

    public static void main(String[] args) {
        // 初始化UI界面
        ServerUI.initUI();

        final int SERVER_PORT = Integer.parseInt(Utils.getConfig("SERVER_PORT"));
        final int STOP_PORT = Integer.parseInt(Utils.getConfig("STOP_PORT"));
        ServerUI.appendText("Server started...\n");

        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            while (true) {
                // 等待新的连接
                Socket clientSocket = serverSocket.accept();
                String connectionMsg = "[New Connection] Connected from " + clientSocket.getRemoteSocketAddress() + "\n";
                ServerUI.appendText(connectionMsg);

                // 检查是否为STOP_PORT
                if (clientSocket.getPort() == (STOP_PORT)) {
                    // do something...
                    clientSocket.close();
                    break;
                }
                String remoteSocketAddress = clientSocket.getRemoteSocketAddress().toString();

                // 添加到onlineUserList中，并设置一个线程处理
                onlineUserList.put(remoteSocketAddress, clientSocket);
                // 初始化ObjectOutputStream，添加到oosUserMap中
                ObjectOutputStream clientOOS = new ObjectOutputStream(clientSocket.getOutputStream());
                oosUserMap.put(remoteSocketAddress, clientOOS);

                ServerHandler clientHandler = new ServerHandler(clientSocket);
                clientPool.submit(clientHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        shutdown();
        clientPool.shutdown();
        ServerUI.getF().setVisible(false);
        while (!clientPool.isTerminated()) {

        }
        Iterator iterator = onlineUserList.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Socket socket = (Socket) entry.getValue();
            System.out.println(socket.isClosed());
        }
        System.exit(0);
    }

    /**
     * 关闭服务器
     */
    public static void shutdown() {
        Iterator iterator = oosUserMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String recAddress = (String) entry.getKey();
            Message msg = new Message(Message.MsgType.WORD, Message.SendType.SHUTDOWN,
                    "/" + Utils.getConfig("SERVER_IP"), recAddress,
                    "服务器已被管理员关闭...");
            new ServerSender(recAddress, msg).start();
        }
    }

    public static Map<String, Socket> getOnlineUserList() {
        return onlineUserList;
    }

    public static Map<String, ObjectOutputStream> getOosUserMap() {
        return oosUserMap;
    }

    public static ExecutorService getClientPool() {
        return clientPool;
    }
}
