/* LanguageTool, a natural language style checker
 * Copyright (C) 2012 Daniel Naber (http://www.danielnaber.de)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301
 * USA
 */
package org.languagetool.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @since 2.0
 */
public class HTTPSServerConfig extends HTTPServerConfig {

  private final File keystore;
  private final String keyStorePassword;
  
  private int requestLimit;
  private int requestLimitPeriodInSeconds;
  private int maxTextLength = Integer.MAX_VALUE;

  /**
   * @param keystore a Java keystore file as created with the <tt>keytool</tt> command
   * @param keyStorePassword the password for the keystore
   */
  public HTTPSServerConfig(File keystore, String keyStorePassword) {
    super(DEFAULT_PORT, false);
    this.keystore = keystore;
    this.keyStorePassword = keyStorePassword;
  }

  /**
   * @param serverPort the port to bind to
   * @param verbose when set to <tt>true</tt>, the input text will be logged in case there is an exception
   * @param keystore a Java keystore file as created with the <tt>keytool</tt> command
   * @param keyStorePassword the password for the keystore
   */
  public HTTPSServerConfig(int serverPort, boolean verbose, File keystore, String keyStorePassword) {
    super(serverPort, verbose);
    this.keystore = keystore;
    this.keyStorePassword = keyStorePassword;
  }

  /**
   * @param serverPort the port to bind to
   * @param verbose when set to <tt>true</tt>, the input text will be logged in case there is an exception
   * @param keystore a Java keystore file as created with the <tt>keytool</tt> command
   * @param keyStorePassword the password for the keystore
   * @since 2.1
   */
  HTTPSServerConfig(int serverPort, boolean verbose, File keystore, String keyStorePassword, int requestLimit, int requestLimitPeriodInSeconds) {
    super(serverPort, verbose);
    this.keystore = keystore;
    this.keyStorePassword = keyStorePassword;
    this.requestLimit = requestLimit;
    this.requestLimitPeriodInSeconds = requestLimitPeriodInSeconds;
  }

  /**
   * Parse command line options and load settings from property file.
   */
  HTTPSServerConfig(String[] args) {
    super(args);
    File config = null;
    for (int i = 0; i < args.length; i++) {
      if ("--config".equals(args[i])) {
        config = new File(args[++i]);
      }
    }
    if (config == null) {
      throw new IllegalConfigurationException("Parameter --config must be set and point to a property file");
    }
    try {
      final Properties props = new Properties();
      try (FileInputStream fis = new FileInputStream(config)) {
        props.load(fis);
        keystore = new File(getProperty(props, "keystore", config));
        keyStorePassword = getProperty(props, "password", config);
        requestLimit = Integer.parseInt(getOptionalProperty(props, "requestLimit", "0"));
        requestLimitPeriodInSeconds = Integer.parseInt(getOptionalProperty(props, "requestLimitPeriodInSeconds", "0"));
        maxTextLength = Integer.parseInt(getOptionalProperty(props, "maxTextLength", Integer.toString(Integer.MAX_VALUE)));
      }
    } catch (IOException e) {
      throw new RuntimeException("Could not load properties from '" + config + "'", e);
    }
  }

  /**
   * @param maxTextLength the maximum text length allowed (in number of characters), texts that are longer
   *                      will cause an exception when being checked
   */
  public void setMaxTextLength(int maxTextLength) {
    this.maxTextLength = maxTextLength;
  }

  int getMaxTextLength() {
    return maxTextLength;
  }

  File getKeystore() {
    return keystore;
  }

  String getKeyStorePassword() {
    return keyStorePassword;
  }

  int getRequestLimit() {
    return requestLimit;
  }

  int getRequestLimitPeriodInSeconds() {
    return requestLimitPeriodInSeconds;
  }

  private String getProperty(Properties props, String propertyName, File config) {
    final String propertyValue = (String)props.get(propertyName);
    if (propertyValue == null || propertyValue.trim().isEmpty()) {
      throw new IllegalConfigurationException("Property '" + propertyName + "' must be set in " + config);
    }
    return propertyValue;
  }

  private String getOptionalProperty(Properties props, String propertyName, String defaultValue) {
    final String propertyValue = (String)props.get(propertyName);
    if (propertyValue == null) {
      return defaultValue;
    }
    return propertyValue;
  }
}
