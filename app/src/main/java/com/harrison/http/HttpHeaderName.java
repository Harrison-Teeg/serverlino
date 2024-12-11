package com.harrison.http;

public enum HttpHeaderName {
  CONTENT_TYPE("Content-Type"),
  CONTENT_LENGTH("Content-Length"),
  CACHE_CONTROL("Cache-Control");

  public final String headerName;

  HttpHeaderName(String headerName) {
    this.headerName = headerName;
  }
}
