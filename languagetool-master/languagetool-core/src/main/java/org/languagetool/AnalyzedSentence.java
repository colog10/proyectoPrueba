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
package org.languagetool;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * A sentence that has been tokenized and analyzed.
 * 
 * @author Daniel Naber
 */
public class AnalyzedSentence {

  private final AnalyzedTokenReadings[] tokens;

  private AnalyzedTokenReadings[] nonBlankTokens;

  /**
   * Array mapping positions of tokens as returned with
   * getTokensWithoutWhitespace() to the internal tokens array.
   */
  private int[] whPositions;

  /**
   * Sets {@link AnalyzedTokenReadings}. Whitespace is also a token.
   */
  public AnalyzedSentence(final AnalyzedTokenReadings[] tokens) {
    this.tokens = tokens;
  }

  public AnalyzedSentence(final AnalyzedTokenReadings[] tokens, final
      int[] whPositions) {
    this.tokens = tokens;
    this.setWhPositions(whPositions);
    getTokensWithoutWhitespace();
  }

  /**
   * Returns the {@link AnalyzedTokenReadings} of the analyzed text. Whitespace
   * is also a token.
   */
  public final AnalyzedTokenReadings[] getTokens() {
    return tokens;
  }

  /**
   * Returns the {@link AnalyzedTokenReadings} of the analyzed text, with
   * whitespace tokens removed but with the artificial <code>SENT_START</code>
   * token included.
   */
  public final AnalyzedTokenReadings[] getTokensWithoutWhitespace() {
    if (nonBlankTokens == null) {
      int whCounter = 0;
      int nonWhCounter = 0;
      final int[] mapping = new int[tokens.length + 1];
      final List<AnalyzedTokenReadings> l = new ArrayList<>();
      for (final AnalyzedTokenReadings token : tokens) {
        if (!token.isWhitespace() || token.isSentenceStart() || token.isSentenceEnd()
            || token.isParagraphEnd()) {
          l.add(token);
          mapping[nonWhCounter] = whCounter;
          nonWhCounter++;
        }
        whCounter++;
      }
      setNonBlankTokens(l.toArray(new AnalyzedTokenReadings[l.size()]));
      setWhPositions(mapping.clone());
    }
    return nonBlankTokens.clone();
  }

  /**
   * Get a position of a non-whitespace token in the original sentence with
   * whitespace.
   *
   * @param nonWhPosition position of a non-whitespace token
   * @return position in the original sentence.
   */
  public final int getOriginalPosition(final int nonWhPosition) {
    if (nonBlankTokens == null) {
      getTokensWithoutWhitespace();
    }
    return getWhPositions()[nonWhPosition];
  }

  @Override
  public final String toString() {
    return toString(",");
  }

  /**
   * Return string representation without chunk information.
   * @since 2.3
   */
  public final String toShortString(String readingDelimiter) {
    return toString(readingDelimiter, false);
  }

  /**
   * Return string representation with chunk information.
   */
  public final String toString(String readingDelimiter) {
    return toString(readingDelimiter, true);
  }

  private String toString(String readingDelimiter, boolean includeChunks) {
    final StringBuilder sb = new StringBuilder();
    for (final AnalyzedTokenReadings element : tokens) {
      if (!element.isWhitespace()) {
        sb.append(element.getToken());
        sb.append('[');
      }
      Iterator<AnalyzedToken> iterator = element.iterator();
      while (iterator.hasNext()) {
        final AnalyzedToken token = iterator.next();
        final String posTag = token.getPOSTag();
        if (element.isSentenceStart()) {
          sb.append("<S>");
        } else if (JLanguageTool.SENTENCE_END_TAGNAME.equals(token.getPOSTag())) {
          sb.append("</S>");
        } else if (JLanguageTool.PARAGRAPH_END_TAGNAME.equals(token.getPOSTag())) {
          sb.append("<P/>");
        } else if (posTag == null && !includeChunks) {
          sb.append(token.getToken());
        } else {
          if (!element.isWhitespace()) {
            sb.append(token.toString());
            if (iterator.hasNext()) {
              sb.append(readingDelimiter);
            }
          }
        }
      }
      if (!element.isWhitespace()) {
        if (includeChunks) {
          sb.append(",");
          sb.append(StringUtils.join(element.getChunkTags(), "|"));
        }
        sb.append(']');
      } else {
        sb.append(' ');
      }

    }
    return sb.toString();
  }

  /**
   * Get disambiguator actions log.
   */
  public final String getAnnotations() {
    final StringBuilder sb = new StringBuilder();
    sb.append("Disambiguator log: \n");
    for (final AnalyzedTokenReadings element : tokens) {
      if (!element.isWhitespace() &&
              !"".equals(element.getHistoricalAnnotations())) {
        sb.append(element.getHistoricalAnnotations());
        sb.append("\n");
      }
    }
    return sb.toString();
  }

  /**
   * @param whPositions the whPositions to set
   */
  public void setWhPositions(int[] whPositions) {
    this.whPositions = whPositions;
  }

  /**
   * @return the whPositions
   */
  public int[] getWhPositions() {
    return whPositions;
  }

  /**
   * @param nonBlankTokens the nonBlankTokens to set
   */
  public void setNonBlankTokens(AnalyzedTokenReadings[] nonBlankTokens) {
    this.nonBlankTokens = nonBlankTokens;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final AnalyzedSentence other = (AnalyzedSentence) obj;
    if (!Arrays.equals(nonBlankTokens, other.nonBlankTokens))
      return false;
    if (!Arrays.equals(tokens, other.tokens))
      return false;
    if (!Arrays.equals(whPositions, other.whPositions))
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(nonBlankTokens);
    result = prime * result + Arrays.hashCode(tokens);
    result = prime * result + Arrays.hashCode(whPositions);
    return result;
  }

}
