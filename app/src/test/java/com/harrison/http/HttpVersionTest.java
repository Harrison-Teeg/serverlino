package com.harrison.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS) // ??
class HttpVersionTest {
  private HttpParser httpParser;

  @BeforeAll
  private void beforeClass() {
    httpParser = new HttpParser();
  }

  @Test
  void parseInvalidHttpMajorVersionTooHigh() {
    try {
      httpParser.parseHttpRequest(
          generateInvalidHttpMajorVersionTooHigh());
      fail("The parser should throw an exception for a bad Http Version.");
    } catch (HttpParsingException e) {
      assertEquals(HttpStatusCode.SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED, e.getErrorCode());
    }
  }

  @Test
  void parseValidHttpVersion() {
    try {
      HttpRequest request = httpParser.parseHttpRequest(
          generateValidHttpVersion());
      assertEquals(HttpMethod.GET, request.getMethod());
    } catch (HttpParsingException e) {
      fail(e);
    }
  }

  private InputStream generateValidHttpVersion() {
    String rawData = "GET / HTTP/1.1\r\n" +
        "Host: localhost:8080\r\n" +
        "Connection: keep-alive\r\n" +
        "Upgrade-Insecure-Requests: 1\r\n" +
        "User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36\r\n"
        +
        "Sec-Fetch-User: ?1\r\n" +
        "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3\r\n"
        +
        "Sec-Fetch-Site: none\r\n" +
        "Sec-Fetch-Mode: navigate\r\n" +
        "Accept-Encoding: gzip, deflate, br\r\n" +
        "Accept-Language: en-US,en;q=0.9,es;q=0.8,pt;q=0.7,de-DE;q=0.6,de;q=0.5,la;q=0.4\r\n" +
        "\r\n";

    InputStream inputStream = new ByteArrayInputStream(
        rawData.getBytes(
            StandardCharsets.US_ASCII));

    return inputStream;
  }

  private InputStream generateInvalidHttpMajorVersionTooHigh() {
    String rawData = "GET / HTTP/2000.1\r\n" +
        "Host: localhost:8080\r\n" +
        "Connection: keep-alive\r\n" +
        "Upgrade-Insecure-Requests: 1\r\n" +
        "User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36\r\n"
        +
        "Sec-Fetch-User: ?1\r\n" +
        "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3\r\n"
        +
        "Sec-Fetch-Site: none\r\n" +
        "Sec-Fetch-Mode: navigate\r\n" +
        "Accept-Encoding: gzip, deflate, br\r\n" +
        "Accept-Language: en-US,en;q=0.9,es;q=0.8,pt;q=0.7,de-DE;q=0.6,de;q=0.5,la;q=0.4\r\n" +
        "\r\n";

    InputStream inputStream = new ByteArrayInputStream(
        rawData.getBytes(
            StandardCharsets.US_ASCII));

    return inputStream;
  }
}