package com.harrison.httpserver.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerListenerThread extends Thread {

  private final static Logger LOGGER = LoggerFactory.getLogger(ServerListenerThread.class);

  private int port;
  private String webroot;
  private ServerSocket serverSocket;

  public ServerListenerThread(int port, String webroot) throws IOException {
    this.port = port;
    this.webroot = webroot;
    this.serverSocket = new ServerSocket(port);
  }

  @Override
  public void run() {
    // TODO use thread-pool instead of infinitely generating threads
    while (serverSocket.isBound() && !serverSocket.isClosed()) {
      try {
        Socket socket = serverSocket.accept();
        LOGGER.info(" * Connection on port: " + port
            + ", serving: " + webroot
            + " is accepted from: " + socket.getInetAddress());
        Thread pageServer = new HTTPRequestProccessorThread(socket);
        pageServer.start();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    try {
      serverSocket.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
