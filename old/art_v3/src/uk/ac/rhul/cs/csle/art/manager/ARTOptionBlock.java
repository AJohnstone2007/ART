package uk.ac.rhul.cs.csle.art.manager;

import java.util.ArrayList;

import uk.ac.rhul.cs.csle.art.manager.mode.ARTModeAlgorithm;
import uk.ac.rhul.cs.csle.art.manager.mode.ARTModeDespatch;
import uk.ac.rhul.cs.csle.art.manager.mode.ARTModeSupport;
import uk.ac.rhul.cs.csle.art.manager.mode.ARTModeTargetLanguage;

public class ARTOptionBlock {
  // Manager level options
  public int verbosityLevel = 0;
  public boolean statistics = false;
  public boolean trace = false;

  public ArrayList<String> inputs = new ArrayList<>();
  public ArrayList<String> inputFilenames = new ArrayList<>();

  // Phase selection options
  public boolean phaseModule = true;
  public boolean phaseLex = true;
  public boolean phasePreChoose = true;
  public boolean phaseParse = true;
  public boolean phasePostChoose = true;
  public boolean phaseDerivationSelect = true;
  public boolean phaseGIFT = true;
  public boolean phaseAG = true;
  public boolean phaseTR = true;
  public boolean phaseSOS = true;

  // Artefact display options
  public boolean showTWE = false;
  public boolean showBSR = false;
  public boolean showSPPFFull = false;
  public boolean showSPPFCore = false;
  public boolean showDT = false;
  public boolean showGIFT = false;
  public boolean showAG = false;
  public boolean showTR = false;
  public boolean showSOS = false;

  // Grammar rework options
  public boolean ebnfClosureLeft = false;
  public boolean ebnfClosureRight = false;
  public boolean ebnfMultiplyOut = false;
  public boolean ebnfLeftFactor = false;
  public boolean ebnfBracketToNonterminal = false;

  // TWESet lexer control
  public boolean lexWSSuffix = false;
  public boolean lexExtents = false;
  public boolean lexSegments = false;
  public boolean lexRecursive = false;
  public boolean lexPrintTWESet = false;
  public boolean lexDFA = false;
  public boolean lexCFRecognise = false;
  public boolean lexCFParse = true;

  public boolean lexDead = false;
  public boolean lexLongestWithin = false;
  public boolean lexLongestAcross = false;
  public boolean lexPriority = false;

  public boolean postUseTerminals = false;
  public boolean postLongestWithin = false;
  public boolean postLongestAcross = false;
  public boolean postPriority = false;

  // Generated parser options
  public String outputDirectory = ".";
  public String parserName = "ARTGeneratedParser";
  public String lexerName = "ARTGeneratedLexer";
  public String namespace = null;

  public ARTModeDespatch despatchMode = ARTModeDespatch.fragment;
  public ARTModeSupport supportMode = ARTModeSupport.HashPool;
  public ARTModeTargetLanguage targetLanguageMode = ARTModeTargetLanguage.Java;

  public boolean predictivePops = false;
  public boolean FIFODescriptors = false;
  public boolean suppressPopGuard = false;
  public boolean suppressProductionGuard = false;
  public boolean suppressTestRepeat = false;
  public boolean suppressSemantics = false;

  // Algorithm selection options
  public boolean clusteredGSS = false;
  public boolean mGLL = false;
  public ARTModeAlgorithm algorithmMode = ARTModeAlgorithm.gllGeneratorPool;

  // Tree display levels
  public int treeLevel = 3; // This controls the kind of tree written by the generated attribute evaluator - it will be obsolete when the new displays are in
                            // use

  public static String helpMessage = "ART " + ARTVersion.version() + " usage: java -jar art.jar argument1, argument2, ...\n\n" +

      "An argument may be one of the following\n" + "    1. A filename which does not begin with a - or + character\n"
      + "    2. An ART option preceded by a single + character\n" + "    3. A piece of literal ART specification preceded by two + characters\n\n"
      + "    For details of ART options, see the documentation at https://art.csle.cs.rhul.ac.uk";

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("ARTOptionBlock [verbosityLevel=");
    builder.append(verbosityLevel);
    builder.append(", statistics=");
    builder.append(statistics);
    builder.append(", trace=");
    builder.append(trace);
    builder.append(", inputFilenames=");
    builder.append(inputFilenames);
    builder.append(", phaseModule=");
    builder.append(phaseModule);
    builder.append(", phaseLex=");
    builder.append(phaseLex);
    builder.append(", phasePreChoose=");
    builder.append(phasePreChoose);
    builder.append(", phaseParse=");
    builder.append(phaseParse);
    builder.append(", phasePostChoose=");
    builder.append(phasePostChoose);
    builder.append(", phaseDerivationSelect=");
    builder.append(phaseDerivationSelect);
    builder.append(", phaseGIFT=");
    builder.append(phaseGIFT);
    builder.append(", phaseAG=");
    builder.append(phaseAG);
    builder.append(", phaseTR=");
    builder.append(phaseTR);
    builder.append(", phaseSOS=");
    builder.append(phaseSOS);
    builder.append(", showTWE=");
    builder.append(showTWE);
    builder.append(", showBSR=");
    builder.append(showBSR);
    builder.append(", showSPPFFull=");
    builder.append(showSPPFFull);
    builder.append(", showSPPFCore=");
    builder.append(showSPPFCore);
    builder.append(", showDT=");
    builder.append(showDT);
    builder.append(", showGIFT=");
    builder.append(showGIFT);
    builder.append(", showAG=");
    builder.append(showAG);
    builder.append(", showTR=");
    builder.append(showTR);
    builder.append(", showSOS=");
    builder.append(showSOS);
    builder.append(", ebnfClosureLeft=");
    builder.append(ebnfClosureLeft);
    builder.append(", ebnfClosureRight=");
    builder.append(ebnfClosureRight);
    builder.append(", ebnfMultiplyOut=");
    builder.append(ebnfMultiplyOut);
    builder.append(", ebnfLeftFactor=");
    builder.append(ebnfLeftFactor);
    builder.append(", ebnfBracketToNonterminal=");
    builder.append(ebnfBracketToNonterminal);
    builder.append(", lexWSSuffix=");
    builder.append(lexWSSuffix);
    builder.append(", lexExtents=");
    builder.append(lexExtents);
    builder.append(", lexSegments=");
    builder.append(lexSegments);
    builder.append(", lexRecursive=");
    builder.append(lexRecursive);
    builder.append(", lexPrintTWESet=");
    builder.append(lexPrintTWESet);
    builder.append(", lexDFA=");
    builder.append(lexDFA);
    builder.append(", lexCFRecognise=");
    builder.append(lexCFRecognise);
    builder.append(", lexCFParse=");
    builder.append(lexCFParse);
    builder.append(", lexDead=");
    builder.append(lexDead);
    builder.append(", lexLongestWithin=");
    builder.append(lexLongestWithin);
    builder.append(", lexLongestAcross=");
    builder.append(lexLongestAcross);
    builder.append(", lexPriority=");
    builder.append(lexPriority);
    builder.append(", postLongestWithin=");
    builder.append(postLongestWithin);
    builder.append(", postLongestAcross=");
    builder.append(postLongestAcross);
    builder.append(", postPriority=");
    builder.append(postPriority);
    builder.append(", outputDirectory=");
    builder.append(outputDirectory);
    builder.append(", parserName=");
    builder.append(parserName);
    builder.append(", lexerName=");
    builder.append(lexerName);
    builder.append(", namespace=");
    builder.append(namespace);
    builder.append(", despatchMode=");
    builder.append(despatchMode);
    builder.append(", supportMode=");
    builder.append(supportMode);
    builder.append(", targetLanguageMode=");
    builder.append(targetLanguageMode);
    builder.append(", predictivePops=");
    builder.append(predictivePops);
    builder.append(", FIFODescriptors=");
    builder.append(FIFODescriptors);
    builder.append(", suppressPopGuard=");
    builder.append(suppressPopGuard);
    builder.append(", suppressProductionGuard=");
    builder.append(suppressProductionGuard);
    builder.append(", suppressTestRepeat=");
    builder.append(suppressTestRepeat);
    builder.append(", suppressSemantics=");
    builder.append(suppressSemantics);
    builder.append(", clusteredGSS=");
    builder.append(clusteredGSS);
    builder.append(", mGLL=");
    builder.append(mGLL);
    builder.append(", algorithmMode=");
    builder.append(algorithmMode);
    builder.append(", treeLevel=");
    builder.append(treeLevel);
    builder.append("]");
    return builder.toString();
  }

}