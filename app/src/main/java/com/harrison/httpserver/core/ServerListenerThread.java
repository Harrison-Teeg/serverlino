package com.harrison.httpserver.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerListenerThread extends Thread {

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
    try {
      Socket socket = serverSocket.accept();
      InputStream inputStream = socket.getInputStream();
      OutputStream outputStream = socket.getOutputStream();

      String html = "<html><head><title>Test website</title></head><body><h1>Welcome to the simple java server</h1></body></html>";
      final String CRLF = "\n\r";
      String response = "HTTP/1.1 200 OK" + CRLF + // HTTP_Version Response_Code Response_Message
          "Content-Length: " + html.getBytes().length + CRLF + // Header
          CRLF +
          html +
          CRLF + CRLF;

      outputStream.write(response.getBytes());

      inputStream.close();
      outputStream.close();
      socket.close();
      serverSocket.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
