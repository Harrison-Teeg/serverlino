package com.harrison.httpserver.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTTPRequestProccessorThread extends Thread {

  private final static Logger LOGGER = LoggerFactory.getLogger(HTTPRequestProccessorThread.class);

  private Socket socket;

  public HTTPRequestProccessorThread(Socket socket) {
    this.socket = socket;
  }

  @Override
  public void run() {
    try {

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

      LOGGER.info(" * Finished serving page to: " + socket.getInetAddress());
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
