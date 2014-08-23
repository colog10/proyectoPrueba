/* LanguageTool, a natural language style checker 
 * Copyright (C) 2012 Jaume Ortolà
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
import org.languagetool.rules.RuleMatch;

import java.io.IOException;

/**
 * @author Jaume Ortolà
 */
public class ComplexAdjectiveConcordanceRuleTest extends TestCase {

	private ComplexAdjectiveConcordanceRule rule;
	private JLanguageTool langTool;

	@Override
	public void setUp() throws IOException {
		rule = new ComplexAdjectiveConcordanceRule(null);
		langTool = new JLanguageTool(new Catalan());
	}

	public void testRule() throws IOException { 
	  
		// correct sentences:
		//es van posar en camí proveïts de presents 
	  /*
	     d'una banda tossut i, de l'altra, del tot inepte
	   
	   principis mascle i femella de la foscor//els elements reproductors mascle i femella// les formigues mascle i femella
	   
	  de barba i cabellera blanques
	  */
	  /*multiwords: en aparença, en essència,per essència, amb excés,en repòs, amb rapidesa, en algun grau, per molt de temps
	    altres vegades estacionat,  en molts casos subordinada?, era al principi instintiva, de moment imperfectament conegudes
	    de llarg menys perfectes, és de totes passades exactament intermèdia,
	      és, en conjunt, gairebé intermèdia 
	      en cert grau paral·lela
	      en algun grau
	      en grau lleuger menys distintes
	      han estat de fet exterminades
	   */
	  // (en especial si hi ha un adverbi entremig: en algun grau més distintes
	  assertCorrect("no s'atorguen drets de visita tret que ho consenta el progenitor");
	  assertCorrect("La meua filla viu amb mi la major part del temps");
	  assertCorrect("que en l'actualitat viu a la ciutat de Santa Cruz");
	  assertCorrect("són submarines i la nostra gent viu al fons del mar.");
	  assertCorrect("la meitat mascles i la meitat femelles");
	  assertCorrect("És força amarg");
	  assertCorrect("Era poderós, força estrabul·lat");
	  assertCorrect("Són força desconegudes");
	  assertCorrect("Zeus, força cansat de tot");
	  assertCorrect("un caràcter fix, per molt extraordinària que sigui la manera");
	  assertCorrect("una quantitat copiosa de llavors olioses");
	  assertCorrect("que criï sense variació, per molt lleugers que fossin");
		assertCorrect("Bernabé i Saule, un cop acomplerta la seva missió a Jerusalem");
		assertCorrect("he passat una nit i un dia sencers a la deriva");
		assertCorrect("L'olor dels teus perfums, més agradable que tots els bàlsams.");
		assertCorrect("La part superior esquerra");
		assertCorrect("I sí, la crisi serà llarga, molt llarga, potser eterna.");
		assertCorrect("El rei ha trobat l'excusa i l'explicació adequada.");
		assertCorrect("té una manera de jugar aquestes gires tan femenina");
		assertCorrect("des de la tradicional divisió en dos regnes establida per Linnaeus");
		assertCorrect("aquestes activitats avui residuals donada ja la manca de territori");
		assertCorrect("instruments de càlcul basats en boles anomenats yupana.");
		assertCorrect("El rei ha trobat l'excusa i l'explicació adequades.");
		assertCorrect("Copa del món femenina.");   
		assertCorrect("Batalla entre asteques i espanyols coneguda com la Nit Trista.");
		assertCorrect("És un informe sobre la cultura japonesa realitzat per encàrrec de l'exèrcit d'Estats Units.");
		assertCorrect("Les perspectives de futur immediat.");
		assertCorrect("Les perspectives de futur immediates.");
		assertCorrect("la tècnica i l'art cinematogràfiques.");
		assertCorrect("la tècnica i l'art cinematogràfic.");
		assertCorrect("la tècnica i l'art cinematogràfics.");
		assertCorrect("la tècnica i l'art cinematogràfica.");
		assertCorrect("Les perspectives i el futur immediats.");
		assertCorrect("Un punt de densitat i gravetat infinites.");
		assertCorrect("De la literatura i la cultura catalanes.");
		assertCorrect("Es fa segons regles de lectura constants i regulars.");
		assertCorrect("Les meitats dreta i esquerra de la mandíbula.");
		assertCorrect("Els períodes clàssic i medieval.");
		//assertCorrect("Els costats superior i laterals.");
		assertCorrect("En una molècula de glucosa i una de fructosa unides.");
		// Should be Incorrect, but it is impossible to detect
		assertCorrect("Índex de desenvolupament humà i qualitat de vida elevat"); 
		assertCorrect("Índex de desenvolupament humà i qualitat de vida elevats");
		assertCorrect("Índex de desenvolupament humà i qualitat de vida elevada");
		assertCorrect("La massa, el radi i la lluminositat llistats per ell.");
		assertCorrect("La massa, el radi i la lluminositat llistada per ell.");
		assertCorrect("L'origen de l'àbac està literalment perdut en el temps.");
		assertCorrect("L'origen ha esdevingut literalment perdut en el temps.");
		assertCorrect("En efecte, hi ha consideracions racistes, llavors força comunes");
		assertCorrect("el personatge canvià físicament: més alt i prim que el seu germà");
		assertCorrect("un a baix i un altre a dalt identificat amb el símbol");
		assertCorrect("un a baix i un altre a dalt identificats amb el símbol");
		assertCorrect("El tabaquisme és l'addicció al tabac provocada per components.");
		assertCorrect("El \"treball\" en qüestió, normalment associat a un lloc de treball pagat");
		assertCorrect("una organització paramilitar de protecció civil típicament catalana");
		assertCorrect("un Do dues octaves més alt que l'anterior");
		assertCorrect("són pràcticament dos graus més baixes");
		assertCorrect("és unes vint vegades més gran que l'espermatozou.");
		assertCorrect("és unes 20 vegades més gran que l'espermatozou.");
		assertCorrect("eren quatre vegades més alts");
		assertCorrect("eren uns fets cada volta més inexplicables");
		//assertCorrect("El castell està totalment en ruïnes i completament cobert de vegetació.");
		assertCorrect("han estat tant elogiades per la crítica teatral, com polèmiques");
		assertCorrect("Del segle XVIII però reconstruïda recentment");
		//assertCorrect("vivien a la casa paterna, mig confosos entre els criats.");		
		assertCorrect("La indústria, tradicionalment dedicada al tèxtil i ara molt diversificada,");
		assertCorrect("oficialment la comarca[2] del Moianès, molt reivindicada");
		assertCorrect("En l'actualitat està del tot despoblada de residència permanent.");
		assertCorrect("amb la terra repartida entre diversos propietaris, bé que encara poc poblada");
		assertCorrect("al capdamunt de les Costes d'en Quintanes, sota mateix del Turó");
		assertCorrect("el Moviment per l’Autodeterminació cors");
		assertCorrect("amb una taula de logaritmes davant meu.");
		assertCorrect("la denominació valencià per a la llengua pròpia");
		assertCorrect("Com més petita és l'obertura de diafragma, més grans són la profunditat de camp i la profunditat");
		assertCorrect("es movien mitjançant filferros, tot projectant ombres");
		assertCorrect("sota les grans persianes de color verd recalcades");
		assertCorrect("que seria en pocs anys força hegemònica a Catalunya");
		assertCorrect("Era un home força misteriós");
		
		// errors:
		assertIncorrect("Son molt boniques");
		assertIncorrect("La casa destrossat");
		assertIncorrect("pantalons curt o llargs");
		assertIncorrect("sota les grans persianes de color verd recalcada");
		assertIncorrect("sota les grans persianes de color verd recalcat");
		assertIncorrect("sota les grans persianes de color verd recalcats");
		assertIncorrect("Són unes corbes de llum complexos.");
		assertIncorrect("fets moltes vegades inexplicable.");
		assertIncorrect("eren uns fets cada volta més inexplicable");
		assertIncorrect("Unes explotacions ramaderes porcina.");
		assertIncorrect("amb un rendiment del 5,62%, més alta que el 5,44%");
		//assertIncorrect("un a baix i un altre a dalt identificada amb el símbol");
		//assertIncorrect("un a baix i un altre a dalt identificades amb el símbol");
		assertIncorrect("En efecte, hi ha consideracions, llavors força comuns");
 		assertIncorrect("En efecte, hi ha consideracions racistes, llavors força comuns");
		assertIncorrect("amb una alineació impròpiament habituals");
		assertIncorrect("amb una alineació poc habituals");
		assertIncorrect("amb una alineació molt poc habituals");
		//assertIncorrect("Era un home força misteriosa"); -> permet "en pocs anys força hegemònica"
		assertIncorrect("Era un home força misteriosos");
		assertIncorrect("El rei ha trobat l'excusa perfecte.");
		assertIncorrect("El rei ha trobat l'excusa i l'explicació adequats.");
		assertIncorrect("El rei ha trobat l'excusa i l'explicació adequat.");
		assertIncorrect("Les perspectives de futur immediata.");
		assertIncorrect("Les perspectives de futur immediats.");
		assertIncorrect("la llengua i la cultura catalans.");
		assertIncorrect("En una molècula de glucosa i una de fructosa units.");
		assertIncorrect("Un punt de densitat i gravetat infinits.");
		assertIncorrect("Índex de desenvolupament humà i qualitat de vida elevades.");
		// Should be Incorrect, but it is impossible to detect
		// assertIncorrect("Índex de desenvolupament humà i qualitat de vida elevat");
		assertIncorrect("La massa, el radi i la lluminositat llistat per ell.");
		assertIncorrect("La massa, el radi i la lluminositat llistades per ell.");

		/*   final RuleMatch[] matches = rule.match(langTool.getAnalyzedSentence("Les circumstancies que ens envolten són circumstancies extraordinàries."));
    assertEquals(2, matches.length);*/
	}

	private void assertCorrect(String sentence) throws IOException {
		final RuleMatch[] matches = rule.match(langTool.getAnalyzedSentence(sentence));
		assertEquals(0, matches.length);
	}

	private void assertIncorrect(String sentence) throws IOException {
		final RuleMatch[] matches = rule.match(langTool.getAnalyzedSentence(sentence));
		assertEquals(1, matches.length);
	}

}
