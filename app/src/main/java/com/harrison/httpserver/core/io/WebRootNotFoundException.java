package com.harrison.httpserver.core.io;

public class WebRootNotFoundException extends Exception {

  public WebRootNotFoundException(String errorMsg) {
    super(errorMsg);
  }

}
