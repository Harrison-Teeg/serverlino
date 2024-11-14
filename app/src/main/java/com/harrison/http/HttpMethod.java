package com.harrison.http;

public enum HttpMethod {
  GET, HEAD;

  public static final int MAX_LENGTH;

  static {
    int len = -1;
    for (HttpMethod method : HttpMethod.values()) {
      len = Math.max(len, method.name().length());
    }
    MAX_LENGTH = len;
  }
}
