package com.harrison.httpserver.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harrison.httpserver.core.io.WebRootHandler;
import com.harrison.httpserver.core.io.WebRootNotFoundException;

public class ServerListenerThread extends Thread {

  private final static Logger LOGGER = LoggerFactory.getLogger(ServerListenerThread.class);

  private int port;
  private WebRootHandler webRootHandler;
  private ServerSocket serverSocket;

  public ServerListenerThread(int port, String webroot) throws IOException, WebRootNotFoundException {
    this.port = port;
    this.webRootHandler = new WebRootHandler(webroot);
    this.serverSocket = new ServerSocket(port);
  }

  @Override
  public void run() {
    // TODO use thread-pool instead of infinitely generating threads
    try {
      while (serverSocket.isBound() && !serverSocket.isClosed()) {
        try {
          Socket socket = serverSocket.accept();
          LOGGER.info(" * Connection on port: " + port
              + ", serving: " + webRootHandler.getWebrootName()
              + " is accepted from: " + socket.getInetAddress());
          Thread pageServer = new HttpRequestProccessorThread(socket, webRootHandler);
          pageServer.start();
        } catch (IOException e) {
          LOGGER.error("Failed to bind socket and generate processing thread.", e);
        }
      }
    } finally {
      try {
        serverSocket.close();
      } catch (IOException e) {
      }
    }
  }

}
