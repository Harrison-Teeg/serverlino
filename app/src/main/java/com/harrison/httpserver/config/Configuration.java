package com.harrison.httpserver.config;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Configuration {
  private InetAddress hostname;
  private int backlog = 50;
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

  public InetAddress getHostname() {
    return hostname;
  }

  public void setHostname(String name) throws UnknownHostException {
    if (name == null || name == "") {
      this.hostname = InetAddress.getLocalHost();
    } else {
      this.hostname = InetAddress.getByName(name);
    }
  }

  public int getBacklog() {
    return backlog;
  }

  public void setBacklog(int backlog) {
    this.backlog = backlog;
  }

}
