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

  public HttpRequest parseHttpRequest(InputStream inputStream) throws HttpParsingException {
    InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.US_ASCII);

    HttpRequest httpRequest = new HttpRequest();

    parseRequestLine(reader, httpRequest);
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
          if (_byte == LF && parsedMethod && parsedTarget) { // Finished request line and filled all values
            LOGGER.info("HttpVersion received as: " + sb.toString());
            sb.delete(0, sb.length());
            return;
          } else {
            throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST); // too few fields in request ||
                                                                                         // CR without LF
          }
        } else if (_byte == SP) {
          if (!parsedMethod) {
            httpRequest.setMethod(sb.toString());
            sb.delete(0, sb.length());
            parsedMethod = true;
          } else if (!parsedTarget) {
            httpRequest.setRequestTarget(sb.toString());
            sb.delete(0, sb.length());
            parsedTarget = true;
          } else {
            throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST); // too few fields in request
          }
        } else {
          sb.append((char) _byte);

          if (!parsedMethod) {
            if (sb.length() > HttpMethod.MAX_LENGTH) {
              throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED);
            }
          }
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
