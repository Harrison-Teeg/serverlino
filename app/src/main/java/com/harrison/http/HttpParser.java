package com.harrison.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            httpRequest.setHttpVersion(sb.toString());
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
            LOGGER.info("Requested URI: " + sb.toString());
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
          } else if (!parsedTarget) {
            if (sb.length() > 100) { // TODO update this with target max length
              throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_414_BAD_REQUEST);
            }
          } else {
            if (sb.length() > HttpVersion.LITERAL_MAX_LENGTH) {
              throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
            }
          }

        }
      }
    } catch (IOException e) {
      throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
    }
  }

  private void parseHeaders(InputStreamReader reader, HttpRequest httpRequest) throws HttpParsingException {
    int _byte;
    StringBuffer sb = new StringBuffer();
    boolean lastWasCRLF = false;

    try {
      while ((_byte = reader.read()) >= 0) {
        if (_byte == CR) {
          _byte = reader.read();
          if (_byte == LF) {
            if (lastWasCRLF) { // finished processing header block (CRLF + CRLF)
              return;
            } else { // finished processing a single header
              lastWasCRLF = true;
              parseSingleHeader(sb.toString(), httpRequest);
              sb.delete(0, sb.length());
            }
          } else {
            throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST); // CR without LF
          }
        } else {
          lastWasCRLF = false;
          sb.append((char) _byte);
          // TODO set a maximum header length
        }
      }
    } catch (IOException e) {
      throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
    }
  }

  private void parseSingleHeader(String rawHeaderText, HttpRequest httpRequest) throws HttpParsingException {
    Pattern headerRegex = Pattern.compile(
        "^(?<headerName>[!#$%&’*+\\-./^_‘|˜\\dA-Za-z]+):\\s?(?<headerValue>[!#$%&’*+\\-./^_‘|˜(),:;<=>?@[\\\\]{}\" \\dA-Za-z]+)\\s?$");

    Matcher regexMatcher = headerRegex.matcher(rawHeaderText);
    if (!regexMatcher.matches()) {
      throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
    }

    String headerName = regexMatcher.group("headerName");
    String headerValue = regexMatcher.group("headerValue");
    httpRequest.addHeader(headerName, headerValue);
  }

  private void parseBody(InputStreamReader reader, HttpRequest httpRequest) {
    // TODO Auto-generated method stub
  }
}
