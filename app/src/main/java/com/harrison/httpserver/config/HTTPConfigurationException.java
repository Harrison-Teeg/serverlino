package com.harrison.httpserver.config;

public class HTTPConfigurationException extends RuntimeException {

  public HTTPConfigurationException() {
  }

  public HTTPConfigurationException(String message) {
    super(message);
  }

  public HTTPConfigurationException(String message, Throwable cause) {
    super(message, cause);
  }

  public HTTPConfigurationException(Throwable cause) {
    super(cause);
  }
}
