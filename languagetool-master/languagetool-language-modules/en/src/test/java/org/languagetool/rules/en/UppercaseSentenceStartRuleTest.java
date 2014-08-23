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
package org.languagetool.rules.en;

import junit.framework.TestCase;
import org.languagetool.JLanguageTool;
import org.languagetool.language.English;

import java.io.IOException;

public class UppercaseSentenceStartRuleTest extends TestCase {

  public void testNonSentences() throws IOException {
    // In OO/LO we get text per paragraph, and list items are a paragraph.
    // Make sure the items that don't look like a sentence generate no error.
    final JLanguageTool lt = new JLanguageTool(new English());
    
    /*assertEquals(0, lt.check("a list item").size());
    assertEquals(0, lt.check("a list item,").size());
    assertEquals(0, lt.check("with trailing whitespace, ").size());
    assertEquals(0, lt.check("a list item;").size());
    assertEquals(0, lt.check("A sentence.").size());
    assertEquals(0, lt.check("A sentence!").size());

    assertEquals(1, lt.check("a sentence.").size());
    assertEquals(1, lt.check("a sentence!").size());*/
  }
  
  public void testRule() throws IOException {
    final JLanguageTool lt = new JLanguageTool(new English());
    assertEquals(0, lt.check("In Nov. next year.").size());
  }

}
