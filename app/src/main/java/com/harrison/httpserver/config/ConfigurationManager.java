package com.harrison.httpserver.config;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.harrison.httpserver.util.Json;

/**
 * Singleton class use: {@link #ConfigurationManager.getInstance()} implementing
 * methods to read in server configuration from a .json file.
 */
public class ConfigurationManager {

  private static ConfigurationManager myConfigurationManager;
  private static Configuration myCurrentConfiguration;

  private ConfigurationManager() {
  }

  public static ConfigurationManager getInstance() {
    if (myConfigurationManager == null) {
      myConfigurationManager = new ConfigurationManager();
    }
    return myConfigurationManager;
  }

  /**
   * Load the server configuration .json file located at filePath
   *
   * @param filePath
   * @throws HttpConfigurationException
   */
  public void loadConfigurationFile(String filePath) throws HttpConfigurationException {
    FileReader fileReader = null;
    try {
      fileReader = new FileReader(filePath);
    } catch (FileNotFoundException e) {
      throw new HttpConfigurationException(e);
    }
    StringBuffer sb = new StringBuffer();
    int i;
    try {
      while ((i = fileReader.read()) != -1) {
        sb.append((char) i);
      }
    } catch (IOException e) {
      throw new HttpConfigurationException(e);
    }
    try {
      fileReader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    JsonNode conf;
    try {
      conf = Json.parse(sb.toString());
    } catch (IOException e) {
      throw new HttpConfigurationException("Error parsing the configuration file.", e);
    }
    try {
      myCurrentConfiguration = Json.fromJson(conf, Configuration.class);
    } catch (JsonProcessingException e) {
      throw new HttpConfigurationException("Error internally processing the configuration.", e);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    }
  }

  /**
   * Returns the currently loaded configuration.
   */
  public Configuration getCurrentConfiguration() {
    if (myCurrentConfiguration == null) {
      throw new HttpConfigurationException("No current configuration set.");
    }
    return myCurrentConfiguration;
  }
}
