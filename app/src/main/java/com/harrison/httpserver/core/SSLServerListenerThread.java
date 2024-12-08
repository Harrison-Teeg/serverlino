package com.harrison.httpserver.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

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
public class SSLServerListenerThread extends Thread {

  private final static Logger LOGGER = LoggerFactory.getLogger(SSLServerListenerThread.class);

  private InetAddress hostname;
  private int backlog;
  private int port;
  private WebRootHandler webRootHandler;
  private SSLServerSocket serverSocket;
  private final ExecutorService threadPool;

  /**
   * Creates a thread which opens a SSL Serversocket and waits for a connection
   * attempt.
   *
   * On a successfully accepted connection + encryption handshake, launches a
   * HttpRequestProccessorThread to serve the requested page/resource.
   *
   * @param conf - Server configuration,
   *             {@link com.harrison.httpserver.config.Configuration}
   * @throws IOException
   * @throws WebRootNotFoundException
   */
  public SSLServerListenerThread(Configuration conf)
      throws IOException, WebRootNotFoundException {
    this.hostname = conf.getHostname();
    this.backlog = conf.getBacklog();
    this.port = conf.getSecurePort();
    this.webRootHandler = new WebRootHandler(conf.getWebroot());
    SSLServerSocketFactory factory = null;
    try {
      SSLContext sslContext = this.createSSLContext(conf.getKeystorePath(), conf.getKeystorePassword());
      factory = (SSLServerSocketFactory) sslContext.getServerSocketFactory();
    } catch (Exception e) {
      LOGGER.error("sslContext loading failed", e);
      factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
    }
    this.serverSocket = (SSLServerSocket) factory.createServerSocket(port, backlog, hostname);
    this.threadPool = Executors.newFixedThreadPool(conf.getThreadpoolCount());
  }

  @Override
  public void run() {
    try {
      while (serverSocket.isBound() && !serverSocket.isClosed()) {
        try {
          // serverSocket.setNeedClientAuth(false);
          // serverSocket.setEnabledCipherSuites(serverSocket.getSupportedCipherSuites());
          SSLSocket socket = (SSLSocket) serverSocket.accept();
          socket.startHandshake();
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

  // Create the and initialize the SSLContext
  private SSLContext createSSLContext(String keystorePath, String keystorePassword) {
    try {
      FileInputStream keystoreInputStream = new FileInputStream(keystorePath);
      char[] password = keystorePassword.toCharArray();
      KeyStore keyStore = KeyStore.getInstance("PKCS12"); // TODO: should I make this settable in cfg?
      keyStore.load(keystoreInputStream, password);

      // Create key manager
      KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509"); // TODO: should I make this
                                                                                      // settable in cfg?
      keyManagerFactory.init(keyStore, password);
      KeyManager[] km = keyManagerFactory.getKeyManagers();

      // Create trust manager
      TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
      trustManagerFactory.init(keyStore);
      TrustManager[] tm = trustManagerFactory.getTrustManagers();

      // Initialize SSLContext
      SSLContext sslContext = SSLContext.getInstance("TLS"); // TODO: should I make this settable in cfg?
      sslContext.init(km, tm, null);

      return sslContext;
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    return null;
  }

}
