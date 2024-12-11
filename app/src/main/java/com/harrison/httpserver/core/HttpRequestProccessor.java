package com.harrison.httpserver.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harrison.http.HttpHeaderName;
import com.harrison.http.HttpParser;
import com.harrison.http.HttpParsingException;
import com.harrison.http.HttpRequest;
import com.harrison.http.HttpResponse;
import com.harrison.http.HttpStatusCode;
import com.harrison.http.HttpVersion;
import com.harrison.httpserver.config.Configuration;
import com.harrison.httpserver.core.io.ReadFileException;
import com.harrison.httpserver.core.io.WebRootHandler;

public class HttpRequestProccessor implements Runnable {

  private final static Logger LOGGER = LoggerFactory.getLogger(HttpRequestProccessor.class);

  private Socket socket;
  private WebRootHandler webRootHandler;
  private HttpParser httpParser = new HttpParser();
  private String cacheControl;

  /**
   * Creates a thread which will read the InputStream from the connected Socket
   * and write the response (webpage or appropriate error page) from the webRoot
   * into the OutputStream.
   *
   * @param socket  - Successfully accepted ServerSocket.
   * @param webRoot - Instantiated root directory handler for website.
   */
  public HttpRequestProccessor(Socket socket, WebRootHandler webRoot, String cacheControl) {
    this.socket = socket;
    this.webRootHandler = webRoot;
    this.cacheControl = cacheControl;

  }

  @Override
  public void run() {
    InputStream inputStream = null;
    OutputStream outputStream = null;
    try {
      inputStream = socket.getInputStream();
      outputStream = socket.getOutputStream();

      HttpRequest httpRequest = null;
      HttpResponse httpResponse = null;
      try {
        httpRequest = httpParser.parseHttpRequest(inputStream);
        LOGGER.info("Request accepted as: " + httpRequest.getMethod().toString());
        httpResponse = handleRequest(httpRequest);
      } catch (HttpParsingException e) {
        httpResponse = handleServiceError(e.getErrorCode());
      }

      outputStream.write(httpResponse.getResponseBytes());

      LOGGER.info(" * Finished serving page to: " + socket.getInetAddress());
    } catch (IOException e) {
      LOGGER.error("Failed to serve page.", e);
    } finally {
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException e) {
        }
      }
      if (outputStream != null) {
        try {
          outputStream.close();
        } catch (IOException e) {
        }
      }
      if (socket != null) {
        try {
          socket.close();
        } catch (IOException e) {
        }
      }
    }
  }

  private HttpResponse handleRequest(HttpRequest request) throws HttpParsingException {
    switch (request.getMethod()) {
      case GET:
        LOGGER.info(" * GET request.");
        return handleGetRequest(request, true);
      case HEAD:
        LOGGER.info(" * HEAD request.");
        return handleGetRequest(request, false);
      default:
        return new HttpResponse.Builder()
            .httpVersion(request.getHttpVersion().LITERAL)
            .statusCode(HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED)
            .build();
    }

  }

  private HttpResponse handleGetRequest(HttpRequest request, boolean includeBody) throws HttpParsingException {
    try {
      HttpResponse.Builder responseBuilder = new HttpResponse.Builder();
      String target = request.getRequestTarget();
      String mimeType = webRootHandler.getFileMimeType(target);
      responseBuilder
          .httpVersion(request.getHttpVersion().LITERAL)
          .statusCode(HttpStatusCode.OK)
          .addHeader(HttpHeaderName.CONTENT_TYPE.headerName, mimeType);
      if (includeBody) {
        byte[] body = webRootHandler.getFileAsByteArray(target);
        responseBuilder
            .addHeader(HttpHeaderName.CONTENT_LENGTH.headerName, String.valueOf(body.length))
            .messageBody(body);
        if (!cacheControl.equals("")) {
          responseBuilder.addHeader(HttpHeaderName.CACHE_CONTROL.headerName, cacheControl);
        }
      }
      return responseBuilder.build();
    } catch (FileNotFoundException e) {
      throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_404_BAD_REQUEST);
    } catch (ReadFileException e) {
      throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
    }
  }

  private HttpResponse handleServiceError(HttpStatusCode errorCode) {
    HttpResponse.Builder errorResponseBuilder = new HttpResponse.Builder();
    errorResponseBuilder
        .httpVersion(HttpVersion.HTTP_1_1.LITERAL)
        .statusCode(errorCode);

    try {
      String target = "/error/" + errorCode.STATUS_CODE + "_page";
      String mimeType = webRootHandler.getFileMimeType(target);
      byte[] body = webRootHandler.getFileAsByteArray(target);
      errorResponseBuilder
          .addHeader(HttpHeaderName.CONTENT_TYPE.headerName, mimeType)
          .addHeader(HttpHeaderName.CONTENT_LENGTH.headerName, String.valueOf(body.length))
          .messageBody(body);
    } catch (ReadFileException e) {
    } catch (FileNotFoundException e) {
    }
    return errorResponseBuilder.build();
  }
}
