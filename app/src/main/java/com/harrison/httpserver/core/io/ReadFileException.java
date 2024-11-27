package com.harrison.httpserver.core.io;

import java.io.IOException;

public class ReadFileException extends Exception {

  public ReadFileException(IOException errorMsg) {
    super(errorMsg);
  }
}
