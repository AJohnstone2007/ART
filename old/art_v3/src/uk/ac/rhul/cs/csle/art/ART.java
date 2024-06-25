package uk.ac.rhul.cs.csle.art;

import java.io.FileNotFoundException;

import uk.ac.rhul.cs.csle.art.alg.ARTParserBase;
import uk.ac.rhul.cs.csle.art.alg.cnp.generatedpool.ARTCNPGenerator;
import uk.ac.rhul.cs.csle.art.alg.cnp.indexedapi.ARTCNPIndexedAPI;
import uk.ac.rhul.cs.csle.art.alg.cnp.indexedpool.ARTCNPIndexedPool;
import uk.ac.rhul.cs.csle.art.alg.cnp.linkedapi.ARTCNPLinkedAPI;
import uk.ac.rhul.cs.csle.art.alg.earley.indexedapi.ARTEarleyIndexedAPI;
import uk.ac.rhul.cs.csle.art.alg.earley.indexedpool.ARTEarleyIndexedPool;
import uk.ac.rhul.cs.csle.art.alg.earley.linkedapi.ARTEarley2007LinkedAPI;
import uk.ac.rhul.cs.csle.art.alg.earley.linkedapi.ARTEarleyLinkedAPI;
import uk.ac.rhul.cs.csle.art.alg.earleytable.indexedapi.ARTEarleyTableIndexedAPI;
import uk.ac.rhul.cs.csle.art.alg.earleytable.indexedpool.ARTEarleyTableIndexedPool;
import uk.ac.rhul.cs.csle.art.alg.earleytable.linkedapi.ARTEarleyTableLinkedAPI;
import uk.ac.rhul.cs.csle.art.alg.earleytable.support.ARTEarleyTableDataIndexed;
import uk.ac.rhul.cs.csle.art.alg.gll.ARTGLLGenerator;
import uk.ac.rhul.cs.csle.art.alg.lcnp.linkedapi.ARTLCNPLinkedAPI;
import uk.ac.rhul.cs.csle.art.alg.osbrd.generator.ARTOSBRDGenerator;
import uk.ac.rhul.cs.csle.art.lex.ARTLexerBase;
import uk.ac.rhul.cs.csle.art.manager.ARTManager;
import uk.ac.rhul.cs.csle.art.manager.ARTOptionBlock;
import uk.ac.rhul.cs.csle.art.manager.grammar.ARTGrammar;
import uk.ac.rhul.cs.csle.art.util.ARTException;
import uk.ac.rhul.cs.csle.art.util.slotarray.ARTSlotArray;
import uk.ac.rhul.cs.csle.art.util.text.ARTText;
import uk.ac.rhul.cs.csle.art.util.text.ARTTextHandlerFile;
import uk.ac.rhul.cs.csle.art.util.text.ARTTextHandlerString;

public class ART {
  private static void runInterpreter(ARTManager artManager, ARTOptionBlock optionBlock, ARTParserBase interpreter) throws ARTException, FileNotFoundException {
    // System.out.println("runInterpreter with option block " + optionBlock);
    if (optionBlock.inputs.size() == 0)
      throw new ARTException("No input specified\n\n");
    else {
      String inputFilename = "???";
      if (!optionBlock.inputFilenames.isEmpty()) inputFilename = optionBlock.inputFilenames.get(0);
      interpreter.ARTTRACE = optionBlock.trace;
      interpreter.artParse(optionBlock.inputs.get(0), null);
      interpreter.artLog(inputFilename, false);
    }
  }

  public static void main(final String[] args) throws FileNotFoundException, ARTException {
    try {
      ARTManager artManager = new ARTManager();

      // Process command line
      if (args.length == 0) throw new ARTException(ARTOptionBlock.helpMessage);
      for (String arg : args)
        if (arg.charAt(0) == '-')
          throw new ARTException("unknown command line option " + arg + "\n\n" + ARTOptionBlock.helpMessage);
        else if (arg.charAt(0) == '+') {
          if (arg.length() == 1) throw new ARTException("argument introducer + must not be followed by a space");
          if (arg.charAt(1) == '+') {
            if (arg.length() == 2) throw new ARTException("argument introducer ++ must not be followed by a space");
            artManager.parseARTSpecification(arg.substring(2)); // ++ => direct input
          } else
            artManager.parseARTSpecification("option " + arg.substring(1)); // + => option input
        } else
          artManager.parseARTSpecification(ARTText.readFile(arg)); // no prefix => file input

      // System.out.println("** ART option block\n" + artManager.getOptionBlock());
      //
      // System.out.println("** ART manager\n" + artManager);

      switch (artManager.getOptionBlock().algorithmMode) {
      case osbrdGenerator:
        ARTOSBRDGenerator artOSBRDGenerator = new ARTOSBRDGenerator(
            artManager.addGrammar("Parser grammar", artManager.getDefaultMainModule(), false, artManager.getOptionBlock()), artManager.getOptionBlock());
        ARTTextHandlerString stringHandler = new ARTTextHandlerString();
        ARTText text = new ARTText(stringHandler);
        artOSBRDGenerator.generateParser(text);
        ARTText.writeFile(artManager.getOptionBlock().outputDirectory, artManager.getOptionBlock().parserName + ".java", stringHandler.getText());
        break;

      case gllTWEGeneratorPool:
      case gllGeneratorPool:

        // Make a new grammar and generator using the default start nonterminal in the default module
        ARTGrammar artParserGrammar = artManager.addGrammar("Parser grammar", artManager.getDefaultMainModule(), false, artManager.getOptionBlock());
        // ARTGrammar artLexerGrammar = artManager.addLexicalGrammar("Lexer grammar", artManager.getDefaultMainModule());

        ARTGLLGenerator artParserGenerator = new ARTGLLGenerator(artParserGrammar, artManager.getOptionBlock());

        // Generate parser and lexer as strings, and then write them out
        ARTTextHandlerString artStringHandler = new ARTTextHandlerString();
        ARTText artStringText = new ARTText(artStringHandler);

        artParserGenerator.generateParser(artStringText);
        ARTText.writeFile(artManager.getOptionBlock().outputDirectory, artManager.getOptionBlock().parserName + ".java",
            artStringHandler.getText()/* + slotArray.toJavaString() */);

        artStringHandler.clear();
        artParserGenerator.generateLexer(artStringText);
        ARTText.writeFile(artManager.getOptionBlock().outputDirectory, artManager.getOptionBlock().lexerName + ".java", artStringHandler.getText());
        break;

      case lexerData:
        ARTLexerBase experiment = new ARTLexerBase(
            artManager.addGrammar("Lexer data grammar", artManager.getDefaultMainModule(), true, artManager.getOptionBlock()));
        // experiment.lexicaliseIndexedTWESet(ARTText.readFile(artManager.getOptionBlock().parserInputFilename));
        if (artManager.getOptionBlock().inputs.isEmpty()) throw new ARTException("No input specified");
        experiment.lexicaliseToLinkedTWESet(artManager.getOptionBlock().inputs.get(0));
        experiment.computeLexerData();
        experiment.outputTokenFile(artManager.getOptionBlock().inputFilenames.get(0));
        experiment.outputCommentStrippedFile(artManager.getOptionBlock().inputFilenames.get(0));
        break;

      case earley2007LinkedAPI:
        runInterpreter(artManager, artManager.getOptionBlock(),
            new ARTEarley2007LinkedAPI(artManager.addGrammar("Parser grammar", artManager.getDefaultMainModule(), true, artManager.getOptionBlock())));
        break;

      case earleyLinkedAPI:
        runInterpreter(artManager, artManager.getOptionBlock(),
            new ARTEarleyLinkedAPI(artManager.addGrammar("Parser grammar", artManager.getDefaultMainModule(), true, artManager.getOptionBlock())));
        break;

      case earleyIndexedData:
        ARTText t = new ARTText(new ARTTextHandlerFile("ARTStaticSlotArray.h"));
        new ARTSlotArray(artManager.addGrammar("Parser grammar", artManager.getDefaultMainModule(), true, artManager.getOptionBlock())).toCString(t);
        t.close();
        break;

      case earleyIndexedAPI:
        runInterpreter(artManager, artManager.getOptionBlock(),
            new ARTEarleyIndexedAPI(artManager.addGrammar("Parser grammar", artManager.getDefaultMainModule(), true, artManager.getOptionBlock())));
        break;

      case earleyIndexedPool:
        runInterpreter(artManager, artManager.getOptionBlock(), new ARTEarleyIndexedPool(
            new ARTSlotArray(artManager.addGrammar("Parser grammar", artManager.getDefaultMainModule(), true, artManager.getOptionBlock()))));
        break;

      case earleyTableLinkedAPI:
        runInterpreter(artManager, artManager.getOptionBlock(),
            new ARTEarleyTableLinkedAPI(artManager.addGrammar("Parser grammar", artManager.getDefaultMainModule(), true, artManager.getOptionBlock())));
        break;

      case earleyTableIndexedData:
        ARTGrammar artGrammar = artManager.addGrammar("Parser grammar", artManager.getDefaultMainModule(), true, artManager.getOptionBlock());
        ARTText t1 = new ARTText(new ARTTextHandlerFile("ARTStaticEarleyTable.h"));
        new ARTEarleyTableDataIndexed(artGrammar).toCString(t1);
        t1.close();
        break;

      case earleyTableIndexedAPI:
        runInterpreter(artManager, artManager.getOptionBlock(),
            new ARTEarleyTableIndexedAPI(artManager.addGrammar("Parser grammar", artManager.getDefaultMainModule(), true, artManager.getOptionBlock())));
        break;

      case earleyTableIndexedPool:
        runInterpreter(artManager, artManager.getOptionBlock(),
            new ARTEarleyTableIndexedPool(artManager.addGrammar("Parser grammar", artManager.getDefaultMainModule(), true, artManager.getOptionBlock())));
        break;

      case cnpGeneratorPool:

        // Make a new grammar and generator using the default start nonterminal in the default module
        ARTGrammar artCNPGrammar = artManager.addGrammar("Parser grammar", artManager.getDefaultMainModule(), false, artManager.getOptionBlock());

        ARTCNPGenerator artCNPGenerator = new ARTCNPGenerator(artCNPGrammar, artManager.getOptionBlock());

        // Generate parser and lexer as strings, and then write them out
        artStringHandler = new ARTTextHandlerString();
        artStringText = new ARTText(artStringHandler);

        artCNPGenerator.generateParser(artStringText);
        ARTText.writeFile(artManager.getOptionBlock().outputDirectory, artManager.getOptionBlock().parserName + ".java",
            artStringHandler.getText()/* + slotArray.toJavaString() */);

        artStringHandler.clear();
        ARTText.writeFile(artManager.getOptionBlock().outputDirectory, artManager.getOptionBlock().lexerName + ".java",
            "class " + artManager.getOptionBlock().lexerName + "{}\n");

        break;

      case cnpLinkedAPI:
        runInterpreter(artManager, artManager.getOptionBlock(),
            new ARTCNPLinkedAPI(artManager.addGrammar("Parser grammar", artManager.getDefaultMainModule(), true, artManager.getOptionBlock())));
        break;

      case cnpIndexedAPI:
        runInterpreter(artManager, artManager.getOptionBlock(), new ARTCNPIndexedAPI(
            new ARTSlotArray(artManager.addGrammar("Parser grammar", artManager.getDefaultMainModule(), true, artManager.getOptionBlock()))));
        break;

      case cnpIndexedPool:
        runInterpreter(artManager, artManager.getOptionBlock(), new ARTCNPIndexedPool(
            new ARTSlotArray(artManager.addGrammar("Parser grammar", artManager.getDefaultMainModule(), true, artManager.getOptionBlock()))));
        break;

      case lcnpLinkedAPI:
        runInterpreter(artManager, artManager.getOptionBlock(),
            new ARTLCNPLinkedAPI(artManager.addGrammar("Parser grammar", artManager.getDefaultMainModule(), true, artManager.getOptionBlock())));
        break;

      default:
        throw new ARTException("no implementation for algorithm mode " + artManager.getOptionBlock().algorithmMode);
      }
    } catch (ARTException e) {
      ARTText.printFatal(e.getMessage());
    }
  }
}
