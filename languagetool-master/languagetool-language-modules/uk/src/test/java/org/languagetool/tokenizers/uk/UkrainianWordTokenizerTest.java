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

package org.languagetool.tokenizers.uk;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

public class UkrainianWordTokenizerTest extends TestCase {
  private final UkrainianWordTokenizer w = new UkrainianWordTokenizer();

  public void testTokenize() {
    List<String> testList = w.tokenize("Вони прийшли додому.");
    assertEquals(Arrays.asList("Вони", " ", "прийшли", " ", "додому", "."), testList);

    testList = w.tokenize("Вони\u0301 при\u00ADйшли пʼятими зів’ялими.");
    assertEquals(Arrays.asList("Вони", " ", "прийшли", " ", "п'ятими", " ", "зів'ялими", "."), testList);

    testList = w.tokenize("Засідав І.Єрмолюк.");
    assertEquals(Arrays.asList("Засідав", " ", "І", ".", "Єрмолюк", "."), testList);

    testList = w.tokenize("Засідав І.П.Єрмолюк.");
    assertEquals(Arrays.asList("Засідав", " ", "І", ".", "П", ".", "Єрмолюк", "."), testList);

    testList = w.tokenize("надійшло 2,2 мільйона");
    assertEquals(Arrays.asList("надійшло", " ", "2,2", " ", "мільйона"), testList);

    testList = w.tokenize("надійшло 84,46 мільйона");
    assertEquals(Arrays.asList("надійшло", " ", "84,46", " ", "мільйона"), testList);
  }

}
