/* LanguageTool, a natural language style checker 
 * Copyright (C) 2013 Daniel Naber (http://www.danielnaber.de)
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
package org.languagetool.dev.wikipedia;

import org.languagetool.JLanguageTool;
import org.languagetool.dev.index.Indexer;
import org.languagetool.dev.index.Searcher;
import org.languagetool.tools.JnaTools;

import java.util.Arrays;

/**
 * A class to be called from command line - dispatches to the actual implementations
 * by calling their main method.
 */
public class Main {

  public static void main(String[] args) throws Exception {
    JnaTools.setBugWorkaroundProperty();
    if (args.length == 0) {
      printUsageAndExit();
    } else {
      final String[] remainingArgs = Arrays.copyOfRange(args, 1, args.length);
      final String command = args[0];
      if (command.equals("check-dump")) {
        CheckWikipediaDump.main(remainingArgs);
      } else if (command.equals("wiki-index")) {
        WikipediaIndexHandler.main(remainingArgs);
      } else if (command.equals("wiki-check")) {
        WikipediaQuickCheck.main(remainingArgs);
      } else if (command.equals("index")) {
        Indexer.main(remainingArgs);
      } else if (command.equals("search")) {
        Searcher.main(remainingArgs);
      } else if (command.equals("version")) {
        System.out.println(JLanguageTool.VERSION + " (" + JLanguageTool.BUILD_DATE + ")");
      } else {
        System.out.println("Error: unknown command '" + command + "'");
        printUsageAndExit();
      }
    }
  }

  private static void printUsageAndExit() {
    System.out.println("Usage: " + Main.class.getName() + " <command> <command-specific-arguments>");
    System.out.println("Where <command> is one of:");
    System.out.println("   check-dump - check a Wikipedia XML dump,");
    System.out.println("                like those available from http://dumps.wikimedia.org/backup-index.html");
    System.out.println("   wiki-index - index a Wikipedia XML dump");
    System.out.println("   wiki-check - check a single Wikipedia page, fetched via the Mediawiki API");
    System.out.println("   index      - index a plain text file, putting the analysis in a Lucene index for faster rule match search");
    System.out.println("   search     - search for rule matches in an index created with 'index' or 'wiki-index'");
    System.out.println("   version    - print LanguageTool version number and build date");
    System.out.println("All commands have different usages. Call them without arguments to get help.");
    System.out.println("Example for a call with valid arguments:");
    System.out.println("   java -jar languagetool-wikipedia.jar wiki-check http://de.wikipedia.org/wiki/Bielefeld");
    System.exit(1);
  }

}
