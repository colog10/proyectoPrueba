/* LanguageTool, a natural language style checker 
 * Copyright (C) 2010 Daniel Naber (http://www.languagetool.org)
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

package org.languagetool.dev.conversion;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

import org.languagetool.Language;

public class RuleConverterMain {
    
    private String grammarFile;
    private String specificFiletype;
    private String discardFile;
    private String disambigFile;
    private RuleConverter rc;
    
    private static String[] supportedGeneralFiletypes = {"atd","cg"};
    private static String[] supportedSpecificFiletypes = {"avoid","default"};
    
    private static void exitWithUsageMessage() {
        System.out
        .println("Usage: java org.languagetool.tools.RuleConverterMain "
            + "[-h|--help] [-g|--generalFiletype] [-s|--specificFiletype] [-i|--inputFile] [-a|--disambigFile] " +
            "[-d|--discardFile] [-o|--outputFile]");
        System.exit(1);
      }
    
    private RuleConverterMain(String inFilename, String grammarFile, String discardFile, String disambigFile, String generalFileType, String specificFileType) {
        this.grammarFile = grammarFile;
        this.specificFiletype = specificFileType;
        this.disambigFile = disambigFile;
        this.discardFile = discardFile;
        if (generalFileType.equals("atd")) {
            rc = new AtdRuleConverter(inFilename, grammarFile, specificFileType);
        } else if (generalFileType.equals("cg")) {
          rc = new CgRuleConverter(inFilename, grammarFile, specificFileType);
        }
    }
    
    private void run() throws IOException {
        rc.parseRuleFile();

        // write out the grammar rules to grammarFile
        PrintWriter w = new PrintWriter(new OutputStreamWriter(new FileOutputStream(grammarFile),"UTF-8"));
        
        w.write("<rules>\n");
        w.write("<category name=\"Auto-generated rules\">\n");
        for (List<String> ltRule : rc.ltRules) {
            for (String line : ltRule) {
                w.write(line + '\n');
            }
        }
        w.write("</category>\n");
        w.write("</rules>");
        w.close();
        
        /*
         * this will have to be added back in once I have the general format figured out
        // evaluate the rules to see which are already covered
        RuleCoverage checker = new RuleCoverage(Language.ENGLISH);
        checker.splitOutCoveredRules(grammarFile,discardFile);
        */
        // for now, write the disambiguation rules to a default file
        if (rc.disambiguationRules.size() > 0) {
          w = new PrintWriter(new OutputStreamWriter(new FileOutputStream(disambigFile), "UTF-8"));
            for (List<String> killedRule : rc.disambiguationRules) {
              for (String line : killedRule) {
                w.write(line + '\n');
              }
            }
            w.close();
            System.out.println(Integer.toString(rc.disambiguationRules.size()) + " disambiguation rules written to " + disambigFile);
        }
        
        
    }
    
    public static void main(String[] args) throws IOException {
        String grammarFile = null;
        String ruleFile = null;
        String specificFileType = null; // type of rule, specific to filetype (e.g. "avoid")
        String generalFiletType= null; // type of file, like the syntax of the other system (e.g. "atd") - i know this is confusing and needs to be fixed
        String discardFile = null;
        String disambigFile = null;
        
        if (args.length < 4) {
          exitWithUsageMessage();
        }

        // this doesn't work because the output rules file is not a legit rules file
        //TODO: fix this so we can have this functionality
        try {
          if (args[0].equals("--check")) {
            RuleCoverage checker = new RuleCoverage(Language.ENGLISH);
            String inFile = args[1];
            checker.evaluateRules(inFile);
            System.exit(1);
          }
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
        
        for (int i=0;i<args.length;i++) {
            if (args[i].equals("-h") || args[i].equals("--help") ||
                    args[i].equals("--?") || args[i].equals("-help")) {
                exitWithUsageMessage();
            }
            else if (args[i].equals("-s") || args[i].equals("--specificFiletype")) {
                specificFileType = getSpecificFiletypeOrExit(args[++i]);
            }
            else if (args[i].equals("-g") || args[i].equals("--generalFiletype")) {
                generalFiletType = getGeneralFiletypeOrExit(args[++i]);
            }
            else if (args[i].equals("--outputFile") || args[i].equals("-o")) {
              grammarFile = args[++i];
            }
            else if (args[i].equals("--discardFile") || args[i].equals("-d")) {
              discardFile = args[++i];
            }
            else if (args[i].equals("--disambigFile") || args[i].equals("-a")) {
              disambigFile = args[++i];
            }
            else if (args[i].equals("--inputFile") || args[i].equals("-i")) {
              ruleFile = args[++i];
            }
            else {
                System.err.println("Unknown option: " + args[i]);
                exitWithUsageMessage();
            }       
        }
        if (specificFileType == null) {
            specificFileType = "default";
        }
        if (generalFiletType == null) {
            generalFiletType = "atd";
        }
        if (grammarFile == null) {
          System.err.println("Need to specify a grammar file");
          exitWithUsageMessage();
        }
        if (ruleFile == null) {
          System.err.println("Need to specify a rule file");
          exitWithUsageMessage();
        }
        if (disambigFile == null) {
          disambigFile = "disambig.xml";
        }
        if (discardFile == null) {
          discardFile = "discard.xml";
        }
        RuleConverterMain prg = new RuleConverterMain(ruleFile, grammarFile, discardFile, disambigFile, generalFiletType, specificFileType);
        prg.run();       
        
    }
    
    public static String getSpecificFiletypeOrExit(String arg) {
        String type = null;
        boolean foundType = false;
        for (String s : supportedSpecificFiletypes) {
            if (arg.equals(s)) {
                type = s;
                foundType = true;
                break;
            }
        }
        if (!foundType) {
            System.out.println("Unknown specific filetype " + arg);
            System.out.print("Supported filetypes are");
            for (String s : supportedSpecificFiletypes) {
                System.out.print(" " + s);
            }
            System.out.println();
            exitWithUsageMessage();
        }
        return type;
    }
    
    public static String getGeneralFiletypeOrExit(String arg) {
        String type = null;
        boolean foundType = false;
        for (String s : supportedGeneralFiletypes) {
            if (arg.equals(s)) {
                type = s;
                foundType = true;
                break;
            }
        }
        if (!foundType) {
            System.out.println("Unknown general filetype " + arg);
            System.out.print("Supported filetypes are");
            for (String s : supportedGeneralFiletypes) {
                System.out.print(" " + s);
            }
            System.out.println();
            exitWithUsageMessage();
        }
        return type;
    }
    

  
    
}
