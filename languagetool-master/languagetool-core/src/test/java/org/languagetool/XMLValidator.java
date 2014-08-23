/* LanguageTool, a natural language style checker 
 * Copyright (C) 2007 Daniel Naber (http://www.danielnaber.de)
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.languagetool.tools.StringTools;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Validate XML files with a given DTD.
 * 
 * @author Daniel Naber
 */
public final class XMLValidator {

  public XMLValidator() {
  }

  /**
   * Check some limits of our simplified XML output.
   */
  public void checkSimpleXMLString(String xml) throws IOException {
    final Pattern pattern = Pattern.compile("(<error.*?/>)", Pattern.DOTALL|Pattern.MULTILINE);
    final Matcher matcher = pattern.matcher(xml);
    int pos = 0;
    while (matcher.find(pos)) {
      final String errorElement = matcher.group();
      pos = matcher.end();
      if (errorElement.contains("\n") || errorElement.contains("\r")) {
        throw new IOException("<error ...> may not contain line breaks");
      }
      final char beforeError = xml.charAt(matcher.start()-1);
      if (beforeError != '\n' && beforeError != '\r') {
        throw new IOException("Each <error ...> must start on a new line");
      }
    }
  }

  /**
   * Validate XML with the given DTD. Throws exception on error. 
   */
  public void validateXMLString(String xml, String dtdFile, String docType) throws SAXException, IOException, ParserConfigurationException {
    validateInternal(xml, dtdFile, docType);
  }

  /**
   * Validate XML file in classpath with the given DTD. Throws exception on error.
   */
  public void validateWithDtd(String filename, String dtdPath, String docType) throws IOException {
    InputStream xmlStream = this.getClass().getResourceAsStream(filename);
    if (xmlStream == null) {
      throw new IOException("Not found in classpath: " + filename);
    }
    try {
      final String xml = StringTools.readFile(xmlStream, "utf-8");
      validateInternal(xml, dtdPath, docType);
    } catch (Exception e) {
      throw new IOException("Cannot load or parse '" + filename + "'", e);
    } finally {
      xmlStream.close();
    }
  }

  /**
   * Validate XML file using the given XSD. Throws an exception on error.
   * @param filename File in classpath to validate
   * @param xmlSchemaPath XML schema file in classpath
   */
  public void validateWithXmlSchema(String filename, String xmlSchemaPath) throws IOException {
    try {
      final InputStream xmlStream = this.getClass().getResourceAsStream(filename);
      if (xmlStream == null) {
        throw new IOException("File not found in classpath: " + filename);
      }
      try {
        final URL schemaUrl = this.getClass().getResource(xmlSchemaPath);
        if (schemaUrl == null) {
          throw new IOException("XML schema not found in classpath: " + xmlSchemaPath);
        }
        validateInternal(xmlStream, schemaUrl);
      } finally {
        xmlStream.close();
      }
    } catch (Exception e) {
      throw new IOException("Cannot load or parse '" + filename + "'", e);
    }
  }

  /**
   * Validate XML file using the given XSD. Throws an exception on error.
   * @param xml the XML string to be validated
   * @param xmlSchemaPath XML schema file in classpath
   * @since 2.3
   */
  public void validateStringWithXmlSchema(String xml, String xmlSchemaPath) throws IOException {
    try {
      final URL schemaUrl = this.getClass().getResource(xmlSchemaPath);
      if (schemaUrl == null) {
        throw new IOException("XML schema not found in classpath: " + xmlSchemaPath);
      }
      final ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes("utf-8"));
      validateInternal(stream, schemaUrl);
    } catch (SAXException e) {
      throw new RuntimeException(e);
    }
  }

  private void validateInternal(String xml, String dtdPath, String docType) throws SAXException, IOException, ParserConfigurationException {
    final SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setValidating(true);
    final SAXParser saxParser = factory.newSAXParser();
    //used for removing existing DOCTYPE from grammar.xml files
    final String cleanXml = xml.replaceAll("<!DOCTYPE.+>", "");
    final String decl = "<?xml version=\"1.0\"";
    final String endDecl = "?>";
    final URL dtdUrl = this.getClass().getResource(dtdPath);
    if (dtdUrl == null) {
      throw new RuntimeException("DTD not found in classpath: " + dtdPath);
    }
    final String dtd = "<!DOCTYPE " + docType + " PUBLIC \"-//W3C//DTD Rules 0.1//EN\" \"" + dtdUrl + "\">";
    final int pos = cleanXml.indexOf(decl);
    final int endPos = cleanXml.indexOf(endDecl);
    if (pos == -1) {
      throw new IOException("No XML declaration found in '" + cleanXml.substring(0, Math.min(100, cleanXml.length())) + "...'");
    }
    final String newXML = cleanXml.substring(0, endPos+endDecl.length()) + "\r\n" + dtd + cleanXml.substring(endPos+endDecl.length());
    final InputSource is = new InputSource(new StringReader(newXML));
    saxParser.parse(is, new ErrorHandler());
  }

  private void validateInternal(InputStream xml, URL xmlSchema) throws SAXException, IOException {
    final SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    final Schema schema = sf.newSchema(xmlSchema);
    final Validator validator = schema.newValidator();
    validator.setErrorHandler(new ErrorHandler());
    validator.validate(new StreamSource(xml));
  }

  /**
   * XML handler that throws exception on error and warning, does nothing otherwise.
   */
  static class ErrorHandler extends DefaultHandler {

    @Override
    public void warning (SAXParseException e) throws SAXException {
      System.err.println(e.getMessage()
              + " Problem found at line " + e.getLineNumber()
              + ", column " + e.getColumnNumber() + ".");
      throw e;
    }

    @Override
    public void error (SAXParseException e) throws SAXException {
      System.err.println(e.getMessage()
              + " Problem found at line " + e.getLineNumber()
              + ", column " + e.getColumnNumber() + ".");
      throw e;
    }

  }

}