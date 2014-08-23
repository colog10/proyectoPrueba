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
package org.languagetool.rules.patterns.bitext;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.languagetool.Language;
import org.languagetool.bitext.StringPair;
import org.languagetool.rules.IncorrectExample;
import org.languagetool.rules.bitext.IncorrectBitextExample;
import org.languagetool.rules.patterns.Element;
import org.languagetool.rules.patterns.Match;
import org.languagetool.rules.patterns.PatternRule;
import org.languagetool.rules.patterns.PatternRuleHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Loads {@link PatternRule}s from an XML file.
 * 
 * @author Marcin Miłkowski
 */
public class BitextPatternRuleLoader extends DefaultHandler {

  public final List<BitextPatternRule> getRules(final InputStream is,
      final String filename) throws IOException {
    final List<BitextPatternRule> rules;
    try {
      final BitextPatternRuleHandler handler = new BitextPatternRuleHandler();
      final SAXParserFactory factory = SAXParserFactory.newInstance();
      final SAXParser saxParser = factory.newSAXParser();
      saxParser.getXMLReader().setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
      saxParser.parse(is, handler);
      rules = handler.getBitextRules();
      return rules;
    } catch (final Exception e) {
      throw new IOException("Cannot load or parse '" + filename + "'", e);
    }
  }

}

class BitextPatternRuleHandler extends PatternRuleHandler {

  private PatternRule srcRule;
  private PatternRule trgRule;

  private IncorrectExample trgExample;
  private IncorrectExample srcExample;

  private Language srcLang;

  private List<StringPair> correctExamples = new ArrayList<>();
  private List<IncorrectBitextExample> incorrectExamples = new ArrayList<>();
  private final List<BitextPatternRule> rules = new ArrayList<>();

  List<BitextPatternRule> getBitextRules() {
    return rules;
  }

  // ===========================================================
  // SAX DocumentHandler methods
  // ===========================================================

  @Override
  public void startElement(final String namespaceURI, final String lName,
      final String qName, final Attributes attrs) throws SAXException {
    if (qName.equals("rules")) {
      final String languageStr = attrs.getValue("targetLang");
      language = Language.getLanguageForShortName(languageStr);
    } else if (qName.equals("rule")) {
      super.startElement(namespaceURI, lName, qName, attrs);
      correctExamples = new ArrayList<>();
      incorrectExamples = new ArrayList<>();
    } else if (qName.equals("target")) {
      startPattern(attrs);
    } else if (qName.equals("source")) {
      srcLang = Language.getLanguageForShortName(attrs.getValue("lang"));
    } else {
      super.startElement(namespaceURI, lName, qName, attrs);
    }
  }

  @Override
  public void endElement(final String namespaceURI, final String sName,
      final String qName) throws SAXException {
    if (qName.equals("rule")) {
      trgRule.setMessage(message.toString());
      if (suggestionMatches != null) {
        for (final Match m : suggestionMatches) {
          trgRule.addSuggestionMatch(m);
        }
        if (phraseElementList.size() <= 1) {
          suggestionMatches.clear();
        }
      }
      final BitextPatternRule bRule = new BitextPatternRule(srcRule, trgRule);
      bRule.setCorrectBitextExamples(correctExamples);
      bRule.setIncorrectBitextExamples(incorrectExamples);
      bRule.setSourceLang(srcLang);
      rules.add(bRule);
    } else if (qName.equals("trgExample")) {
      trgExample = setExample();
    } else if (qName.equals("srcExample")) {
      srcExample = setExample();
    } else if (qName.equals("source")) {
      srcRule = finalizeRule();
    } else if (qName.equals("target")) {
      trgRule = finalizeRule();
    } else if (qName.equals("example")) {
      if (inCorrectExample) {
        correctExamples.add(new StringPair(srcExample.getExample(), trgExample.getExample()));
      } else if (inIncorrectExample) {
        final StringPair examplePair = new StringPair(srcExample.getExample(), trgExample.getExample());
        if (trgExample.getCorrections() == null) {
          incorrectExamples.add(new IncorrectBitextExample(examplePair));
        } else {
          final List<String> corrections = trgExample.getCorrections();
          final String[] correctionArray = corrections.toArray(new String[corrections.size()]);
          incorrectExamples.add(new IncorrectBitextExample(examplePair, correctionArray));
        }
      }
      inCorrectExample = false;
      inIncorrectExample = false;
    } else {
      super.endElement(namespaceURI, sName, qName);
    }

  }

  private IncorrectExample setExample() {
    IncorrectExample example = null;
    if (inCorrectExample) {
      example = new IncorrectExample(correctExample.toString());
    } else if (inIncorrectExample) {
      final String[] corrections = exampleCorrection.toString().split("\\|");
      if (corrections.length > 0 && corrections[0].length() > 0) {
        example = new IncorrectExample(incorrectExample.toString(), corrections);
      } else {
        example = new IncorrectExample(incorrectExample.toString());
      }
    }      
    correctExample = new StringBuilder();
    incorrectExample = new StringBuilder();
    exampleCorrection = new StringBuilder();
    return example;
  }

  private PatternRule finalizeRule() {
    PatternRule rule = null;
    phraseElementInit();
    if (phraseElementList.isEmpty()) {
      rule = new PatternRule(id, language, elementList,
          name, "", shortMessage.toString());
      prepareRule(rule);              
    } else {
      if (!elementList.isEmpty()) {
        for (final ArrayList<Element> ph : phraseElementList) {
          ph.addAll(new ArrayList<>(elementList));
        }
      }
      for (final ArrayList<Element> phraseElement : phraseElementList) {
        processElement(phraseElement);
        rule = new PatternRule(id, language, phraseElement,
            name, message.toString(), shortMessage.toString(), "",
            phraseElementList.size() > 1);
        prepareRule(rule);       
      }
    }
    elementList.clear();
    if (phraseElementList != null) {
      phraseElementList.clear();
    }
    startPositionCorrection = 0;
    endPositionCorrection = 0;    
    return rule;
  }

}
