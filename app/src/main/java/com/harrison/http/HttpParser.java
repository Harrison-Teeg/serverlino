package com.harrison.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpParser {

  private final static Logger LOGGER = LoggerFactory.getLogger(HttpParser.class);

  private static final int SP = 0x20;
  private static final int CR = 0x0D;
  private static final int LF = 0x0A;

  public HttpRequest parseHttpRequest(InputStream inputStream) {
    InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.US_ASCII);

    HttpRequest httpRequest = new HttpRequest();

    try {
      parseRequestLine(reader, httpRequest);
    } catch (HttpParsingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    parseHeaders(reader, httpRequest);
    parseBody(reader, httpRequest);

    return httpRequest;
  }

  private void parseRequestLine(InputStreamReader reader, HttpRequest httpRequest) throws HttpParsingException {
    int _byte;
    StringBuffer sb = new StringBuffer();
    boolean parsedMethod = false;
    boolean parsedTarget = false;

    try {
      while ((_byte = reader.read()) >= 0) {
        if (_byte == CR) {
          _byte = reader.read();
          if (_byte == LF) {
            // finished with request line
            if (parsedMethod && parsedTarget) {
              LOGGER.info("HttpVersion received as: " + sb.toString());
              sb.delete(0, sb.length());
            } else {
              throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
            }
          }
        } else if (_byte == SP) {
          if (!parsedMethod) {
            LOGGER.info("Method received as: " + sb.toString());
            httpRequest.setMethod(sb.toString());
            sb.delete(0, sb.length());
            parsedMethod = true;
          } else if (!parsedTarget) {
            LOGGER.info("Target received as: " + sb.toString());
            sb.delete(0, sb.length());
            parsedTarget = true;
          }
        } else {
          sb.append((char) _byte);
        }
      }
    } catch (IOException e) {
      throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
    }
  }

  private void parseHeaders(InputStreamReader reader, HttpRequest httpRequest) {
    // TODO Auto-generated method stub
  }

  private void parseBody(InputStreamReader reader, HttpRequest httpRequest) {
    // TODO Auto-generated method stub
  }
}
