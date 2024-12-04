package com.harrison.httpserver.core;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harrison.httpserver.config.Configuration;
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

  private InetAddress hostname;
  private int backlog;
  private int port;
  private WebRootHandler webRootHandler;
  private ServerSocket serverSocket;
  private final ExecutorService threadPool;

  /**
   * Creates a thread which opens a serversocket and waits for a connection
   * attempt.
   *
   * On a successfully accepted connection, launches a HttpRequestProccessorThread
   * to serve the requested page/resource.
   *
   * @param conf - Server configuration,
   *             {@link com.harrison.httpserver.config.Configuration}
   * @throws IOException
   * @throws WebRootNotFoundException
   */
  public ServerListenerThread(Configuration conf)
      throws IOException, WebRootNotFoundException {
    this.hostname = conf.getHostname();
    this.backlog = conf.getBacklog();
    this.port = conf.getPort();
    this.webRootHandler = new WebRootHandler(conf.getWebroot());
    this.serverSocket = new ServerSocket(port, backlog, hostname);
    this.threadPool = Executors.newFixedThreadPool(conf.getThreadpoolCount());
  }

  @Override
  public void run() {
    try {
      while (serverSocket.isBound() && !serverSocket.isClosed()) {
        try {
          Socket socket = serverSocket.accept();
          LOGGER.info(" * Connection on port: " + port
              + ", serving: " + webRootHandler.getWebrootName()
              + " is accepted from: " + socket.getInetAddress());
          threadPool.submit(new HttpRequestProccessor(socket, webRootHandler));
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
