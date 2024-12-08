package com.harrison.httpserver.config;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Configuration {
  private InetAddress hostname;
  private int backlog = 50;
  private int port;
  private int securePort = -1;
  private int threadpoolCount;
  private String webroot;
  private String keystorePath;
  private String keystorePassword;

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
    if (name == null || name == "" || name.toLowerCase().equals("localhost")) {
      this.hostname = null;
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

  public int getSecurePort() {
    return securePort;
  }

  public void setSecurePort(int securePort) {
    this.securePort = securePort;
  }

  public String getKeystorePath() {
    return keystorePath;
  }

  public void setKeystorePath(String keystorePath) {
    this.keystorePath = keystorePath;
  }

  public String getKeystorePassword() {
    return keystorePassword;
  }

  public void setKeystorePassword(String keystorePassword) {
    this.keystorePassword = keystorePassword;
  }

}
