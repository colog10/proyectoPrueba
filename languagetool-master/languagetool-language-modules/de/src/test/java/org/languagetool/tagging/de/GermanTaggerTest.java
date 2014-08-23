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
package org.languagetool.tagging.de;

import junit.framework.TestCase;
import morfologik.stemming.Dictionary;
import morfologik.stemming.DictionaryLookup;
import morfologik.stemming.WordData;
import org.languagetool.AnalyzedToken;
import org.languagetool.AnalyzedTokenReadings;
import org.languagetool.JLanguageTool;
import org.languagetool.tools.StringTools;

import java.io.IOException;
import java.util.*;

public class GermanTaggerTest extends TestCase {

  public void testTagger() throws IOException {
    final GermanTagger tagger = new GermanTagger();
    
    AnalyzedTokenReadings aToken = tagger.lookup("Haus");
    assertEquals("Haus[Haus/SUB:AKK:SIN:NEU, Haus/SUB:DAT:SIN:NEU, Haus/SUB:NOM:SIN:NEU]", toSortedString(aToken));
    assertEquals("Haus", aToken.getReadings().get(0).getLemma());
    assertEquals("Haus", aToken.getReadings().get(1).getLemma());
    assertEquals("Haus", aToken.getReadings().get(2).getLemma());
    
    aToken = tagger.lookup("Hauses");
    assertEquals("Hauses[Haus/SUB:GEN:SIN:NEU]", toSortedString(aToken));
    assertEquals("Haus", aToken.getReadings().get(0).getLemma());
    
    aToken = tagger.lookup("hauses");
    assertNull(aToken);
    
    aToken = tagger.lookup("Groß");
    assertNull(aToken);

    aToken = tagger.lookup("Lieblingsbuchstabe");
    assertEquals("Lieblingsbuchstabe[Lieblingsbuchstabe/SUB:NOM:SIN:MAS]", toSortedString(aToken));

    aToken = tagger.lookup("großer");
    assertEquals("großer[groß/ADJ:DAT:SIN:FEM:GRU:SOL, groß/ADJ:GEN:PLU:FEM:GRU:SOL, groß/ADJ:GEN:PLU:MAS:GRU:SOL, " +
            "groß/ADJ:GEN:PLU:NEU:GRU:SOL, groß/ADJ:GEN:SIN:FEM:GRU:SOL, groß/ADJ:NOM:SIN:MAS:GRU:IND, " +
            "groß/ADJ:NOM:SIN:MAS:GRU:SOL]", toSortedString(aToken));
    assertEquals("groß", aToken.getReadings().get(0).getLemma());
    
    // from both german.dict and added.txt:
    aToken = tagger.lookup("Interessen");
    assertEquals("Interessen[Interesse/SUB:AKK:PLU:NEU, Interesse/SUB:DAT:PLU:NEU, " +
            "Interesse/SUB:GEN:PLU:NEU, Interesse/SUB:NOM:PLU:NEU]",
        toSortedString(aToken));
    assertEquals("Interesse", aToken.getReadings().get(0).getLemma());
    assertEquals("Interesse", aToken.getReadings().get(1).getLemma());
    assertEquals("Interesse", aToken.getReadings().get(2).getLemma());
    assertEquals("Interesse", aToken.getReadings().get(3).getLemma());
    
    // words that are not in the dictionary but that are recognized thanks to noun splitting:
    aToken = tagger.lookup("Donaudampfschiff");
    assertEquals("Donaudampfschiff[Donaudampfschiff/SUB:AKK:SIN:NEU, Donaudampfschiff/SUB:DAT:SIN:NEU, " +
            "Donaudampfschiff/SUB:NOM:SIN:NEU]", toSortedString(aToken));
    assertEquals("Donaudampfschiff", aToken.getReadings().get(0).getLemma());
    assertEquals("Donaudampfschiff", aToken.getReadings().get(1).getLemma());
    
    aToken = tagger.lookup("Häuserkämpfe");
    assertEquals("Häuserkämpfe[Häuserkampf/SUB:AKK:PLU:MAS, Häuserkampf/SUB:GEN:PLU:MAS, Häuserkampf/SUB:NOM:PLU:MAS]",
        toSortedString(aToken));
    assertEquals("Häuserkampf", aToken.getReadings().get(0).getLemma());
    assertEquals("Häuserkampf", aToken.getReadings().get(1).getLemma());
    assertEquals("Häuserkampf", aToken.getReadings().get(2).getLemma());
    
    aToken = tagger.lookup("Häuserkampfes");
    assertEquals("Häuserkampfes[Häuserkampf/SUB:GEN:SIN:MAS]", toSortedString(aToken));
    assertEquals("Häuserkampf", aToken.getReadings().get(0).getLemma());
    
    aToken = tagger.lookup("Häuserkampfs");
    assertEquals("Häuserkampfs[Häuserkampf/SUB:GEN:SIN:MAS]", toSortedString(aToken));
    assertEquals("Häuserkampf", aToken.getReadings().get(0).getLemma());

    aToken = tagger.lookup("Lieblingsfarben");
    assertEquals("Lieblingsfarben[Lieblingsfarbe/SUB:AKK:PLU:FEM, Lieblingsfarbe/SUB:DAT:PLU:FEM, " +
            "Lieblingsfarbe/SUB:GEN:PLU:FEM, Lieblingsfarbe/SUB:NOM:PLU:FEM]", toSortedString(aToken));
    assertEquals("Lieblingsfarbe", aToken.getReadings().get(0).getLemma());

    aToken = tagger.lookup("Autolieblingsfarben");
    assertEquals("Autolieblingsfarben[Autolieblingsfarbe/SUB:AKK:PLU:FEM, Autolieblingsfarbe/SUB:DAT:PLU:FEM, " +
            "Autolieblingsfarbe/SUB:GEN:PLU:FEM, Autolieblingsfarbe/SUB:NOM:PLU:FEM]", toSortedString(aToken));
    assertEquals("Autolieblingsfarbe", aToken.getReadings().get(0).getLemma());

    aToken = tagger.lookup("übrigbleibst");
    assertEquals("übrigbleibst[übrigbleiben/VER:2:SIN:PRÄ:NON:NEB]", toSortedString(aToken));
    assertEquals("übrigbleiben", aToken.getReadings().get(0).getLemma());
  }

  // make sure we use the version of the POS data that was extended with post spelling reform data
  public void testExtendedTagger() throws IOException {
    final GermanTagger tagger = new GermanTagger();

    assertEquals("Kuß[Kuß/SUB:AKK:SIN:MAS, Kuß/SUB:DAT:SIN:MAS, Kuß/SUB:NOM:SIN:MAS]", toSortedString(tagger.lookup("Kuß")));
    assertEquals("Kuss[Kuss/SUB:AKK:SIN:MAS, Kuss/SUB:DAT:SIN:MAS, Kuss/SUB:NOM:SIN:MAS]", toSortedString(tagger.lookup("Kuss")));

    assertEquals("Haß[Haß/SUB:AKK:SIN:MAS, Haß/SUB:DAT:SIN:MAS, Haß/SUB:NOM:SIN:MAS]", toSortedString(tagger.lookup("Haß")));
    assertEquals("Hass[Hass/SUB:AKK:SIN:MAS, Hass/SUB:DAT:SIN:MAS, Hass/SUB:NOM:SIN:MAS]", toSortedString(tagger.lookup("Hass")));

    assertEquals("muß[müssen/VER:MOD:1:SIN:PRÄ, müssen/VER:MOD:3:SIN:PRÄ]", toSortedString(tagger.lookup("muß")));
    assertEquals("muss[müssen/VER:MOD:1:SIN:PRÄ, müssen/VER:MOD:3:SIN:PRÄ]", toSortedString(tagger.lookup("muss")));
  }

  public void testTaggerBaseforms() throws IOException {
    final GermanTagger tagger = new GermanTagger();
    
    List<AnalyzedToken> readings = tagger.lookup("übrigbleibst").getReadings();
    assertEquals(1, readings.size());
    assertEquals("übrigbleiben", readings.get(0).getLemma());

    readings = tagger.lookup("Haus").getReadings();
    assertEquals(3, readings.size());
    assertEquals("Haus", readings.get(0).getLemma());
    assertEquals("Haus", readings.get(1).getLemma());
    assertEquals("Haus", readings.get(2).getLemma());

    readings = tagger.lookup("Häuser").getReadings();
    assertEquals(3, readings.size());
    assertEquals("Haus", readings.get(0).getLemma());
    assertEquals("Haus", readings.get(1).getLemma());
    assertEquals("Haus", readings.get(2).getLemma());
  }

  public void testTag() throws IOException {
    final GermanTagger tagger = new GermanTagger();

    final List<String> upperCaseWord = new ArrayList<>();
    upperCaseWord.add("Das");

    List<AnalyzedTokenReadings> readings = tagger.tag(upperCaseWord);
    assertEquals("[Das[der/ART:DEF:AKK:SIN:NEU*,der/ART:DEF:NOM:SIN:NEU*," +
        "der/PRO:DEM:AKK:SIN:NEU*,der/PRO:DEM:NOM:SIN:NEU*,der/PRO:PER:AKK:SIN:NEU*,der/PRO:PER:NOM:SIN:NEU*]]", readings.toString());
    
    readings = tagger.tag(upperCaseWord, false);
    assertEquals("[Das[null/null*]]", readings.toString());
  }

  public void testTagWithManualDictExtension() throws IOException {
    // words not originally in Morphy but added in LT 1.8 (moved from added.txt to german.dict)
    final GermanTagger tagger = new GermanTagger();
    final List<AnalyzedTokenReadings> readings = tagger.tag(Collections.singletonList("Wichtigtuerinnen"));
    assertEquals("[Wichtigtuerinnen[Wichtigtuerin/SUB:AKK:PLU:FEM*," +
        "Wichtigtuerin/SUB:DAT:PLU:FEM*,Wichtigtuerin/SUB:GEN:PLU:FEM*,Wichtigtuerin/SUB:NOM:PLU:FEM*]]", readings.toString());
  }

  public void testDictionary() throws IOException {
    final Dictionary dictionary = Dictionary.read(
        JLanguageTool.getDataBroker().getFromResourceDirAsUrl("/de/german.dict"));
    final DictionaryLookup dl = new DictionaryLookup(dictionary);
    for (WordData wd : dl) {
      if (wd.getTag() == null || wd.getTag().length() == 0) {
        System.err.println("**** Warning: the word " + wd.getWord() + "/" + wd.getStem()
                + " lacks a POS tag in the dictionary.");
      }
    }    
  }

  /**
   * Returns a string representation like {@code toString()}, but sorts
   * the elements alphabetically.
   */
  private String toSortedString(AnalyzedTokenReadings tokenReadings) {
    final StringBuilder sb = new StringBuilder(tokenReadings.getToken());
    final Set<String> elements = new TreeSet<>();
    sb.append('[');
    for (AnalyzedToken reading : tokenReadings) {
      if (!elements.contains(reading.toString())) {
        elements.add(reading.toString());
      }
    }
    sb.append(StringTools.listToString(elements, ", "));
    sb.append(']');
    return sb.toString();
  }

}
