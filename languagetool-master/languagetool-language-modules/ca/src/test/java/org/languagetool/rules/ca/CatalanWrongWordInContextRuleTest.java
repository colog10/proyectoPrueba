/* LanguageTool, a natural language style checker 
 * Copyright (C) 2012 Markus Brenneis
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
package org.languagetool.rules.ca;

import junit.framework.TestCase;
import org.languagetool.JLanguageTool;
import org.languagetool.language.Catalan;

import java.io.IOException;

/**
 * @author Jaume Ortolà
 */
public class CatalanWrongWordInContextRuleTest extends TestCase {

  public void testRule() throws IOException {
    CatalanWrongWordInContextRule rule = new CatalanWrongWordInContextRule(null);
    JLanguageTool langTool = new JLanguageTool(new Catalan());
    
    // rendible/rentable
    assertEquals(0, rule.match(langTool.getAnalyzedSentence("Una empresa molt rendible.")).length);
    assertEquals(1, rule.match(langTool.getAnalyzedSentence("Una empresa molt rentable.")).length);
    assertEquals(1, rule.match(langTool.getAnalyzedSentence("Uns cultius rentables.")).length);
    assertEquals(1, rule.match(langTool.getAnalyzedSentence("Es venen bé i són rentables.")).length);
    assertEquals("rendibles", rule.match(langTool.getAnalyzedSentence("Uns projectes molt rentables."))[0].getSuggestedReplacements().get(0));
    //assertEquals("rentable", rule.match(langTool.getAnalyzedSentence("Un teixit rendible."))[0].getSuggestedReplacements().get(0));
    
    assertEquals(1, rule.match(langTool.getAnalyzedSentence("Van escoltar el detingut fins al calabós.")).length);
    assertEquals(1, rule.match(langTool.getAnalyzedSentence("Li va infringir un mal terrible.")).length);
    assertEquals(1, rule.match(langTool.getAnalyzedSentence("És un terreny abonat per als problemes.")).length);
    assertEquals(1, rule.match(langTool.getAnalyzedSentence("No li va cosir bé les betes.")).length);
    assertEquals(1, rule.match(langTool.getAnalyzedSentence("Sempre li seguia la beta.")).length);
  }
  
}
