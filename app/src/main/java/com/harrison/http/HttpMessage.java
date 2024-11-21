package com.harrison.http;

import java.util.HashMap;
import java.util.Set;

public abstract class HttpMessage {

  private HashMap<String, String> headers = new HashMap<>();
  private byte[] messageBody = new byte[0];

  public Set<String> getHeaderNames() {
    return headers.keySet();
  }

  public String getHeader(String headerName) {
    return headers.get(headerName.toLowerCase());
  }

  void addHeader(String headerName, String headerValue) { // only allowed in http package (not public)
    headers.put(headerName.toLowerCase(), headerValue);
  }

  public byte[] getMessageBody() {
    return messageBody;
  }

  public void setMessageBody(byte[] messageBody) {
    this.messageBody = messageBody;
  }
}
