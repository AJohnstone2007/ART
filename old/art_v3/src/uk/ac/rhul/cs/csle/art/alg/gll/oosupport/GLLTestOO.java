package uk.ac.rhul.cs.csle.art.alg.gll.oosupport;
/******************************************************************************
 * GLLtestOO.java
 * 
 * This is a test harness for ART generated Java parsers - OO project version
 *  
 * (c) Adrian Johnstone 2013
 *****************************************************************************/

import java.io.PrintStream;
import java.util.Scanner;
import java.io.File;
import java.util.Date;
import java.io.FileNotFoundException;

public class GLLTestOO {

	static void log(String inputFilename, GLLParser parser, Boolean initial, Boolean console) throws FileNotFoundException {
		String inputFiletype = inputFilename.substring(inputFilename.lastIndexOf('.') + 1, inputFilename.lastIndexOf('.') + 4);

		if (!(inputFiletype.equals("acc") || inputFiletype.equals("rej")) && initial)
			return;

		PrintStream output = console ? System.out : new PrintStream("accept_reject.csv"); 

		String status = "good";
		if ((inputFiletype.equals("acc") && !parser.inLanguage) || (inputFiletype.equals("rej") && parser.inLanguage) || initial) 
			status = "bad";

		int localInputLength = parser.inputLength - 1;
		if (localInputLength <= 0) localInputLength = 1;
		double localRunTime = parser.runTime / 1.0E9;
		if (localRunTime < 1.0E-9) localRunTime = 1;

		output.printf("%s,%s,%s,%s,%s,%s,%s,%f,%d,%f\n",
				parser.grammarName, parser.startSymbolName, ""/*inputFilename*/, parser.buildOptions,
				parser.inLanguage ? "accept": "reject",  initial ? "crash" : status, new Date(),
						localRunTime, localInputLength, localInputLength/localRunTime);

		if (!console)
			output.close();

		if (inputFilename.indexOf("deliberatelyCrash") != -1)
			System.exit(1);
	}

	public static void main(String[] args) throws FileNotFoundException {
		GLLParser parser = new GLLParser();
		String input = "";

		log(args[0], parser, true, false);

		try {
			Scanner inputScanner = new Scanner(new File(args[0]));
			if (inputScanner.hasNext())
				input = inputScanner.useDelimiter("\\A").next();
			inputScanner.close();
		} catch (FileNotFoundException ex) {
			System.err.printf("Unable to open input file %s\n", args[0]);
			System.exit(1);
		};

		System.out.printf("Input: %s\n", input);
		parser.parse(input);

		log(args[0], parser, false, true);
		log(args[0], parser, false, false);

//		for (int tmp = 0; !parser.labelIsEOS(parser.inputLabel(tmp)); tmp++)
//			System.out.printf("%d->%d:%d %s \n", tmp, parser.inputIndexToCharacterIndex(tmp), parser.inputCharacterExtent(tmp), parser.labelToString(parser.inputLabel(tmp)));

	}
}
	