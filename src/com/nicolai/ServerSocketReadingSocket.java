package com.nicolai;

import com.nicolai.SocketUtil;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class ServerSocketReadingSocket extends Thread {
    Socket socket;
    List<Socket> socketList;

    public ServerSocketReadingSocket(Socket socket, List<Socket> socketList) {
        this.socket = socket;
        this.socketList = socketList;
    }

    public void send(Socket socketInn, String msg, String username) {
        for (Socket socket : socketList) {
            if (socketInn != socket) {
                SocketUtil.send(socket, msg, username);
            }
        }
    }


    @Override
    public synchronized void run() {
        Socket socketRead = SocketUtil.read(socket, this);
        try {
            socketList.remove(socketRead);
            socketRead.close();
            this.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
