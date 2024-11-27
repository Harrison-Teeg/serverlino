package com.harrison.httpserver.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harrison.httpserver.core.io.WebRootHandler;
import com.harrison.httpserver.core.io.WebRootNotFoundException;

/**
 * This class implements a Thread object which will continuously listen
 * on the chosen port and for each accepted connection spawn a connection
 * processing thread, then return to listening.
 * <br>
 * <br>
 * If the socket is unbound or closed then this thread will close the Socket
 * and shut down.
 */
public class ServerListenerThread extends Thread {

  private final static Logger LOGGER = LoggerFactory.getLogger(ServerListenerThread.class);

  private int port;
  private WebRootHandler webRootHandler;
  private ServerSocket serverSocket;

  /**
   * Creates a thread which opens a serversocket and waits for a connection
   * attempt.
   *
   * On a successfully accepted connection, launches a HttpRequestProccessorThread
   * to serve the requested page/resource.
   *
   * @param port    - The port to open and listen for requests.
   * @param webRoot - The root directory of the webpage to be served.
   * @throws IOException
   * @throws WebRootNotFoundException
   */
  public ServerListenerThread(int port, String webRoot) throws IOException, WebRootNotFoundException {
    this.port = port;
    this.webRootHandler = new WebRootHandler(webRoot);
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
