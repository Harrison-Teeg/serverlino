package com.harrison.httpserver.config;

public class Configuration {
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

}
