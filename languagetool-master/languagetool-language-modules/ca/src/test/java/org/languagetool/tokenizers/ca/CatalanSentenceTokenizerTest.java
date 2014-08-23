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

package org.languagetool.tokenizers.ca;

import junit.framework.TestCase;
import org.languagetool.TestTools;
import org.languagetool.language.Catalan;
import org.languagetool.tokenizers.SRXSentenceTokenizer;
import org.languagetool.tokenizers.SentenceTokenizer;

public class CatalanSentenceTokenizerTest extends TestCase {

  private final SentenceTokenizer stokenizer = new SRXSentenceTokenizer(new Catalan());

  public final void testTokenize() {

    // Simple sentences
    testSplit("Això és una frase. ", "Això és una altra frase.");
    testSplit("Aquesta és l'egua. ", "Aquell és el cavall.");
    testSplit("Aquesta és l'egua? ", "Aquell és el cavall.");
    testSplit("Vols col·laborar? ", "Sí, i tant.");
    testSplit("Com vas d'il·lusió? ", "Bé, bé.");
    testSplit("Com vas d’il·lusió? ", "Bé, bé.");
    testSplit("És d’abans-d’ahir? ", "Bé, bé.");
    testSplit("És d’abans-d’ahir! ", "Bé, bé.");
    testSplit("Què vols dir? ", "Ja ho tinc!");
    testSplit("Ja ho tinc! ", "Què vols dir?");
    testSplit("Us explicaré com va anar: ",
            "»La Maria va engegar el cotxe");
    testSplit("diu que va dir. ",
            "A mi em feia estrany.");    

    // Initials
    testSplit("A l'atenció d'A. Comes.");
    testSplit("A l'atenció d'À. Comes.");
    testSplit("Núm. operació 220130000138.");

    // Ellipsis
    testSplit("el vi no és gens propi de monjos, amb tot...\" vetllant, això sí");
    testSplit("Desenganyeu-vos… ",
            "L’únic problema seriós de l'home en aquest món és el de subsistir.");
    testSplit("és clar… traduir és una feina endimoniada");
    testSplit("«El cordó del frare…» surt d'una manera desguitarrada");
    testSplit("convidar el seu heroi –del ram que sigui–… a prendre cafè.");

    // Abbreviations
    testSplit("Vegeu el cap. 24 del llibre.");
    testSplit("Vegeu el cap. IX del llibre.");
    testSplit("Viu al núm. 24 del carrer de l'Hort.");
    testSplit("El Dr. Joan no vindrà.");
    testSplit("Distingit Sr. Joan,");
    testSplit("Molt Hble. Sr. President");
    testSplit("de Sant Nicolau (del s. XII; cor gòtic del s. XIV) i de Sant ");

    // Exception to abbreviations
    testSplit("Ell és el número u. ", "Jo el dos.");
    testSplit("Té un trau al cap. ", "Cal portar-lo a l'hospital.");
    // Units
    testSplit("1 500 m/s. ", "Neix a");
    
    //Error: missing space. It is not split in order to trigger other errors. 
    testSplit("s'hi enfrontà quan G.Oueddei n'esdevingué líder");
    testSplit("el jesuïta alemany J.E. Nithard");
  }

  private void testSplit(final String... sentences) {
    TestTools.testSplit(sentences, stokenizer);
  }

}
