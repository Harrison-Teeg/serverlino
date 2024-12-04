package com.harrison.httpserver.config;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Configuration {
  private InetAddress address;
  private int backlog = 10;
  private int port;
  private int threadpoolCount;
  private String webroot;

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getWebroot() {
    return webroot;
  }

  public void setWebroot(String webroot) {
    this.webroot = webroot;
  }

  public int getThreadpoolCount() {
    return threadpoolCount;
  }

  public void setThreadpoolCount(int threadpoolCount) {
    this.threadpoolCount = threadpoolCount;
  }

  public InetAddress getAddress() {
    return address;
  }

  public void setAddress(String address) throws UnknownHostException {
    if (address == null || address == "") {
      this.address = InetAddress.getLocalHost();
    } else {
      this.address = InetAddress.getByName(address);
    }
  }

  public int getBacklog() {
    return backlog;
  }

  public void setBacklog(int backlog) {
    this.backlog = backlog;
  }

}
