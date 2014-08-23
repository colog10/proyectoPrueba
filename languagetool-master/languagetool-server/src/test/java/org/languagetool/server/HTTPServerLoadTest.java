/* LanguageTool, a natural language style checker
 * Copyright (C) 2011 Daniel Naber (http://www.danielnaber.de)
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

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test HTTP server access from multiple threads.
 */
public class HTTPServerLoadTest extends HTTPServerTest {

  // we keep these numbers low so the tests stay fast - increase them for serious testing:
  private static final int REPEAT_COUNT = 1;
  private static final int THREAD_COUNT = 2;

  @Test
  @Override
  public void testHTTPServer() throws Exception {
    final long startTime = System.currentTimeMillis();
    final HTTPServer server = new HTTPServer();
    assertFalse(server.isRunning());
    try {
      server.run();
      assertTrue(server.isRunning());
      final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
      final List<Future> futures = new ArrayList<>();
      for (int i = 0; i < THREAD_COUNT; i++) {
        final Future<?> future = executorService.submit(new TestRunnable());
        futures.add(future);
      }
      for (Future future : futures) {
        future.get();
      }
    } finally {
      server.stop();
      assertFalse(server.isRunning());
      final long runtime = System.currentTimeMillis() - startTime;
      System.out.println("Running with " + THREAD_COUNT + " threads in " + runtime + "ms");
    }
  }

  @Override
  public void testAccessDenied() throws Exception {
    // no need to test this here, tested in super class
  }
  
  private class TestRunnable implements Runnable {
    @Override
    public void run() {
      try {
        for (int i = 0; i < REPEAT_COUNT; i++) {
          runTests();
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
  
}
