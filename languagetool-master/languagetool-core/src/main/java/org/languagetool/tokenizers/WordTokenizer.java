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
package org.languagetool.tokenizers;

import org.languagetool.tools.StringTools;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tokenizes a sentence into words. Punctuation and whitespace gets their own tokens.
 * The tokenizer is a quite simple character-based one, though it knows
 * about urls and will put them in one token, if fully specified including
 * a protocol (like {@code http://foobar.org}).
 * 
 * @author Daniel Naber
 */
public class WordTokenizer implements Tokenizer {

  private static final List<String> PROTOCOLS = Collections.unmodifiableList(Arrays.asList("http", "https", "ftp"));
  private static final Pattern URL_CHARS = Pattern.compile("[a-zA-Z0-9/%$-_.+!*'(),\\?]+");

  /**
   * Get the protocols that the tokenizer knows about.
   * @return currently {@code http}, {@code https}, and {@code ftp}
   * @since 2.1
   */
  public static List<String> getProtocols() {
    return PROTOCOLS;
  }
  
  public WordTokenizer() {
  }

  @Override
  public List<String> tokenize(final String text) {
    final List<String> l = new ArrayList<>();
    final StringTokenizer st = new StringTokenizer(text, 
        "\u0020\u00A0\u115f\u1160\u1680" 
        + "\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007" 
        + "\u2008\u2009\u200A\u200B\u200c\u200d\u200e\u200f"
        + "\u2028\u2029\u202a\u202b\u202c\u202d\u202e\u202f"
        + "\u205F\u2060\u2061\u2062\u2063\u206A\u206b\u206c\u206d"
        + "\u206E\u206F\u3000\u3164\ufeff\uffa0\ufff9\ufffa\ufffb" 
        + ",.;()[]{}<>!?:/|\\\"'«»„”“`´‘’‛′…¿¡\t\n\r", true);
    while (st.hasMoreElements()) {
      l.add(st.nextToken());
    }
    return joinUrls(l);
  }

  // see rfc1738 and http://stackoverflow.com/questions/1856785/characters-allowed-in-a-url
  protected List<String> joinUrls(List<String> l) {
    final List<String> newList = new ArrayList<>();
    boolean inUrl = false;
    final StringBuilder url = new StringBuilder();
    for (int i = 0; i < l.size(); i++) {
      if (urlStartsAt(i, l)) {
        inUrl = true;
        url.append(l.get(i));
      } else if (inUrl && urlEndsAt(i, l)) {
        inUrl = false;
        newList.add(url.toString());
        url.setLength(0);
        newList.add(l.get(i));
      } else if (inUrl) {
        url.append(l.get(i));
      } else {
        newList.add(l.get(i));
      }
    }
    if (url.length() > 0) {
      newList.add(url.toString());
    }
    return newList;
  }

  private boolean urlStartsAt(int i, List<String> l) {
    final String token = l.get(i);
    if (isProtocol(token)) {
      if (l.size() > i + 3) {
        final String nToken = l.get(i + 1);
        final String nnToken = l.get(i + 2);
        final String nnnToken = l.get(i + 3);
        if (nToken.equals(":") && nnToken.equals("/") && nnnToken.equals("/")) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean isProtocol(String token) {
    for (String protocol : PROTOCOLS) {
      if (token.equals(protocol)) {
        return true;
      }
    }
    return false;
  }

  private boolean urlEndsAt(int i, List<String> l) {
    final String token = l.get(i);
    if (StringTools.isWhitespace(token)) {
      return true;
    } else if (token.equals(")")) {
      return true;
    } else if (l.size() > i + 1) {
      final String nToken = l.get(i + 1);
      if (StringTools.isWhitespace(nToken)) {
        if (token.equals(".") || token.equals(",") || token.equals(";") || token.equals(":") || token.equals("!") || token.equals("?")) {
          return true;
        }
      }
    } else {
      final Matcher matcher = URL_CHARS.matcher(token);
      if (!matcher.matches()) {
        return true;
      }
    }
    return false;
  }

}
