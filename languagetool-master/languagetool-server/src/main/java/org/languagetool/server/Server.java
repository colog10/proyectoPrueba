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

import com.sun.net.httpserver.HttpServer;
import org.languagetool.JLanguageTool;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.languagetool.server.HTTPServerConfig.DEFAULT_PORT;

/**
 * Super class for HTTP and HTTPS server.
 *
 * @since 2.0
 */
abstract class Server {

  protected abstract String getProtocol();

  protected static final Set<String> DEFAULT_ALLOWED_IPS = new HashSet<>(Arrays.asList(
            "0:0:0:0:0:0:0:1",     // Suse Linux IPv6 stuff
            "0:0:0:0:0:0:0:1%0",   // some(?) Mac OS X
            "127.0.0.1"
    ));
  static final int THREAD_POOL_SIZE = 10;

  protected int port;
  protected String host;
  protected HttpServer server;

  private boolean isRunning;

  /**
   * Start the server.
   */
  public void run() {
    final String hostName = host != null ? host : "localhost";
    System.out.println("Starting LanguageTool " + JLanguageTool.VERSION +
            " (build date: " + JLanguageTool.BUILD_DATE + ") server on " + getProtocol() + "://" + hostName + ":" + port  + "...");
    server.start();
    isRunning = true;
    System.out.println("Server started");
  }

  /**
   * Stop the server. Once stopped, a server cannot be used again.
   */
  public void stop() {
    if (server != null) {
      System.out.println("Stopping server");
      server.stop(0);
      isRunning = false;
      System.out.println("Server stopped");
    }
  }

  /**
   * @return whether the server is running
   * @since 2.0
   */
  public boolean isRunning() {
    return isRunning;
  }

  protected static boolean usageRequested(String[] args) {
    return args.length == 1 && (args[0].equals("-h") || args[0].equals("--help"));
  }

  protected static void printCommonOptions() {
    System.out.println("  --port, -p     port to bind to, defaults to " + DEFAULT_PORT + " if not specified");
    System.out.println("  --public       allow this server process to be connected from anywhere; if not set,");
    System.out.println("                 it can only be connected from the computer it was started on");
    System.out.println("  --allow-origin ORIGIN  set the Access-Control-Allow-Origin header in the HTTP response,");
    System.out.println("                         used for direct (non-proxy) JavaScript-based access from browsers;");
    System.out.println("                         example: --allow-origin \"*\"");
  }

}
