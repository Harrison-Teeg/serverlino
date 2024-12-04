/*
 * This source file was generated by the Gradle 'init' task
 */
package com.harrison.httpserver;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harrison.httpserver.config.Configuration;
import com.harrison.httpserver.config.ConfigurationManager;
import com.harrison.httpserver.core.ServerListenerThread;
import com.harrison.httpserver.core.io.WebRootNotFoundException;

/**
 * A very basic Http server for practicing good coding practices and serving
 * my personal profile website.
 * 
 * @author Harrison Martin
 * @version 1.0
 */
public class App {
  private final static Logger LOGGER = LoggerFactory.getLogger(App.class);

  public static void main(String[] args) {
    LOGGER.info("Server starting...");

    ConfigurationManager.getInstance().loadConfigurationFile("src/main/resources/server_config.json");
    Configuration conf = ConfigurationManager.getInstance().getCurrentConfiguration();

    LOGGER.info("Address: " + conf.getAddress().toString());
    LOGGER.info("Backlog: " + conf.getBacklog());
    LOGGER.info("Port: " + conf.getPort());
    LOGGER.info("Request processing threadpool count: " + conf.getThreadpoolCount());
    LOGGER.info("Webroot: " + conf.getWebroot());

    try {
      ServerListenerThread listener = new ServerListenerThread(conf);
      listener.start();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (WebRootNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
