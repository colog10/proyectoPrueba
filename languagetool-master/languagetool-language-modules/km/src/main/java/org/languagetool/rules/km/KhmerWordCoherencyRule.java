/* LanguageTool, a natural language style checker 
 * Copyright (C) 2005 Daniel Naber (http://www.danielnaber.de)
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
package org.languagetool.rules.km;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Scanner;

import org.languagetool.AnalyzedSentence;
import org.languagetool.AnalyzedTokenReadings;
import org.languagetool.JLanguageTool;
import org.languagetool.rules.Category;
import org.languagetool.rules.RuleMatch;
import org.languagetool.tools.StringTools;

/**
 * A Khmer rule that matches words or phrases which should not be used and suggests
 * correct ones instead. Loads the relevant words from 
 * <code>rules/km/coherency.txt</code>, where km is a code of the language.
 * 
 * @author Andriy Rysin
 */
public abstract class KhmerWordCoherencyRule extends KhmerRule {

  private static final String FILE_NAME = "/km/coherency.txt";
  private static final String FILE_ENCODING = "utf-8";

  private final Map<String, String> wrongWords; // e.g. "вреѿті реѿт" -> "зреѿтою"

  public KhmerWordCoherencyRule(final ResourceBundle messages) throws IOException {
    if (messages != null) {
      super.setCategory(new Category(messages.getString("category_misc")));
    }
    wrongWords = loadWords(JLanguageTool.getDataBroker().getFromRulesDirAsStream(FILE_NAME));
  }

  @Override
  public String getId() {
    return "KM_WORD_COHERENCY";
  }

  @Override
  public String getDescription() {
    return "Checks for wrong words/phrases";
  }

  @Override
  public final RuleMatch[] match(final AnalyzedSentence text) {
    final List<RuleMatch> ruleMatches = new ArrayList<>();
    final AnalyzedTokenReadings[] tokens = text.getTokensWithoutWhitespace();

    for (int i = 1; i < tokens.length; i++) {
      final String token = tokens[i].getToken();

      final String origToken = token;
      final String replacement = isCaseSensitive()?wrongWords.get(token):wrongWords.get(token.toLowerCase(getLocale()));
      if (replacement != null) {
        final String msg = token + getSuggestion() + replacement;
        final int pos = tokens[i].getStartPos();
        final RuleMatch potentialRuleMatch = new RuleMatch(this, pos, pos
            + origToken.length(), msg, getShort());
        if (!isCaseSensitive() && StringTools.startsWithUppercase(token)) {
          potentialRuleMatch.setSuggestedReplacement(StringTools.uppercaseFirstChar(replacement));
        } else {
          potentialRuleMatch.setSuggestedReplacement(replacement);
        }
        ruleMatches.add(potentialRuleMatch);
      }
    }
    return toRuleMatchArray(ruleMatches);
  }

  /**
   * Indicates if the rule is case-sensitive. Default value is <code>true</code>.
   * @return true if the rule is case-sensitive, false otherwise.
   */
  private boolean isCaseSensitive() {
    return false;
  }

  /**
   * @return the locale used for case conversion when {@link #isCaseSensitive()} is set to <code>false</code>.
   */
  private Locale getLocale() {
    return Locale.getDefault();
  }  

  private String getShort() {
    return "Wrong word";
  }

  private String getSuggestion() {
    return " is not valid, use ";
  }

  private Map<String, String> loadWords(final InputStream file) throws IOException {
    final Map<String, String> map = new HashMap<>();
    try (Scanner scanner = new Scanner(file, FILE_ENCODING)) {
      while (scanner.hasNextLine()) {
        final String line = scanner.nextLine().trim();
        if (line.length() < 1) {
          continue;
        }
        if (line.charAt(0) == '#') { // ignore comments
          continue;
        }
        final String[] parts = line.split("=");
        if (parts.length < 2) {
          throw new IOException("Format error in file - Make sure you have removed the Unicode BOM as well as check for other errors: "
                  + JLanguageTool.getDataBroker().getFromRulesDirAsUrl(FILE_NAME) + ", line: " + line);
        }
        for (int i = 0; i < parts.length - 1; i++) {
          map.put(parts[i], parts[parts.length - 1]);
        }
      }
    }
    return map;
  }

  @Override
  public void reset() {
  }  

}
