package uk.ac.rhul.cs.csle.art.alg.gll.oosupport;
/*******************************************************************************
*
* GLLParser.java
*
*******************************************************************************/
public class GLLParser extends GLLObjectOriented {
	private static boolean[] ARTSet1;
	private static boolean[] ARTSet2;
	private static boolean[] ARTSet3;
	private static boolean[] ARTSet4;
	private static boolean[] ARTSet5;
	static final int ART_X__EOS = 0;
	static final int ART_TC__a = 1;
	static final int ART_TC__b = 2;
	static final int ART_TC__c = 3;
	static final int ART_TC__d = 4;
	static final int ART_X__EPSILON = 5;
	static final int ART__ART____S_2 = 6;
	static final int ART__ART____S_4 = 7;
	static final int ART__ART____S_5 = 8;
	static final int ART__ART____S_6 = 9;
	static final int ART__ART____S_10 = 10;
	static final int ART__ART____S_11 = 11;
	static final int ART__ART____S_12 = 12;
	static final int ART__ART____S_13 = 13;
	static final int ART__ART____S_14 = 14;
	static final int ART__ART____S_17 = 15;
	static final int ART__ART____S_18 = 16;
	static final int ART__DESPATCH = 17;
	static final int ART_X__DUMMY = 18;
	static final int ART__EXTENT = 19;

	void parse(String characterStringP) {
		characterString = characterStringP;
		inLanguage = false;
		input = null;
		lexLongestMatch();
		if (input == null) return;
		dummySPPFNode = findSPPFInitial(ART__DUMMY, 0, 0);
		currentSPPFNode = dummySPPFNode;
		rootGSSNode = findGSS(ART__EOS, null, 0, null);
		currentGSSNode = rootGSSNode;
		inputIndex = 0;
		startClock();
		findDescriptor(ART__DESPATCH, rootGSSNode, inputIndex, dummySPPFNode);
		currentRestartLabel = ART__ART____S_2;
		selectState: while (true)
			switch (currentRestartLabel) {

			case ART__ART____S_2: // Nonterminal S production descriptor loads
				if (ARTSet1[input[inputIndex]]) findDescriptor(ART__ART____S_4, currentGSSNode, inputIndex, dummySPPFNode);
				currentRestartLabel = ART__DESPATCH;
				break;

			case ART__ART____S_4: // Nonterminal S: match production
				/* Terminal */
				currentSPPFRightChildNode = findSPPFTerminal(ART_TC__a, inputIndex);
				inputIndex++;
				currentSPPFNode = findSPPF(ART__ART____S_6, currentSPPFNode, currentSPPFRightChildNode);
				if (!ARTSet2[input[inputIndex]]) {
					currentRestartLabel = ART__DESPATCH;
					continue selectState;
				}
				/* Start of do-first body */
				/* Terminal */
				currentSPPFRightChildNode = findSPPFTerminal(ART_TC__b, inputIndex);
				inputIndex++;
				currentSPPFNode = findSPPF(ART__ART____S_11, currentSPPFNode, currentSPPFRightChildNode);
				if (!ARTSet3[input[inputIndex]]) {
					currentRestartLabel = ART__DESPATCH;
					continue selectState;
				}
				/* Terminal */
				currentSPPFRightChildNode = findSPPFTerminal(ART_TC__c, inputIndex);
				inputIndex++;
				currentSPPFNode = findSPPF(ART__ART____S_13, currentSPPFNode, currentSPPFRightChildNode);
				/* End of do-first body */
				if (!ARTSet4[input[inputIndex]]) {
					currentRestartLabel = ART__DESPATCH;
					continue selectState;
				}
				/* Terminal */
				currentSPPFRightChildNode = findSPPFTerminal(ART_TC__d, inputIndex);
				inputIndex++;
				currentSPPFNode = findSPPF(ART__ART____S_18, currentSPPFNode, currentSPPFRightChildNode);
				if (!ARTSet5[input[inputIndex]]) {
					currentRestartLabel = ART__DESPATCH;
					continue selectState;
				}
				pop(currentGSSNode, inputIndex, currentSPPFNode);
				currentRestartLabel = ART__DESPATCH;
				break;

			case ART__DESPATCH:
				if (noDescriptors()) {
					checkAcceptance(ART__ART____S_2);
					stopClock();
					return;
				}
				unloadDescriptor();
			}
	}

	void setInitialise() {
		ARTSet1 = new boolean[5];
		ARTSet1[1] = true;
		ARTSet2 = new boolean[5];
		ARTSet2[2] = true;
		ARTSet3 = new boolean[5];
		ARTSet3[3] = true;
		ARTSet4 = new boolean[5];
		ARTSet4[4] = true;
		ARTSet5 = new boolean[5];
		ARTSet5[0] = true;
	}

	void artLabelStringsInitialise() {
	}

	void artRDTStringsInitialise() {
	}

	void artTerminalRequiresWhiteSpaceInitialise() {
	}

	void artTerminalCaseInsensitiveInitialise() {
	}

	void foldLInitialise() {
	}

	void pLInitialise() {
	}

	void aLInitialise() {
	}

	void fiRLInitialise() {
	}

	void eoOPLInitialise() {
	}

	void eoR_pLInitialise() {
	}

	GLLParser() {
		ART__EOS = ART_X__EOS;
		ART__EPSILON = ART_X__EPSILON;
		ART__DUMMY = ART_X__DUMMY;
		grammarName = "anntest";
		startSymbolName = "ART__.S";
		buildOptions = "no,no,no,no,no,no,preparse,state,ObjectOriented,Java";
		artLabelStrings = new String[ART__EXTENT + 1];
		artLabelStrings[ART__EOS] = "ART__$";
		artRDTStrings = new String[ART__EXTENT + 1];
		artRDTStrings[ART__EOS] = "EOS $";
		artTerminalRequiresWhiteSpace = new boolean[ART__EPSILON];
		artTerminalCaseInsensitive = new boolean[ART__EPSILON];
		artLabelStrings[ART_TC__a] = "a";
		artRDTStrings[ART_TC__a] = "a";
		artLabelStrings[ART_TC__b] = "b";
		artRDTStrings[ART_TC__b] = "b";
		artLabelStrings[ART_TC__c] = "c";
		artRDTStrings[ART_TC__c] = "c";
		artLabelStrings[ART_TC__d] = "d";
		artRDTStrings[ART_TC__d] = "d";
		artLabelStrings[ART__EPSILON] = "#";
		artRDTStrings[ART__EPSILON] = "#";
		artLabelStrings[ART__ART____S_2] = "ART____S_2";
		artRDTStrings[ART__ART____S_2] = "S";
		artLabelStrings[ART__ART____S_4] = "ART____S_4";
		artRDTStrings[ART__ART____S_4] = "";
		artLabelStrings[ART__ART____S_5] = "ART____S_5";
		artRDTStrings[ART__ART____S_5] = "";
		artLabelStrings[ART__ART____S_6] = "ART____S_6";
		artRDTStrings[ART__ART____S_6] = "";
		artLabelStrings[ART__ART____S_10] = "ART____S_10";
		artRDTStrings[ART__ART____S_10] = "";
		artLabelStrings[ART__ART____S_11] = "ART____S_11";
		artRDTStrings[ART__ART____S_11] = "";
		artLabelStrings[ART__ART____S_12] = "ART____S_12";
		artRDTStrings[ART__ART____S_12] = "";
		artLabelStrings[ART__ART____S_13] = "ART____S_13";
		artRDTStrings[ART__ART____S_13] = "";
		artLabelStrings[ART__ART____S_14] = "ART____S_14";
		artRDTStrings[ART__ART____S_14] = "";
		artLabelStrings[ART__ART____S_17] = "ART____S_17";
		artRDTStrings[ART__ART____S_17] = "";
		artLabelStrings[ART__ART____S_18] = "ART____S_18";
		artRDTStrings[ART__ART____S_18] = "";
		artLabelStrings[ART__DESPATCH] = "ART__DESPATCH";
		artLabelStrings[ART__DUMMY] = "ART__DUMMY";
		artLabelStrings[ART__EXTENT] = "!!ILLEGAL!!";
		foldL = new int[ART__EXTENT];
		lhsL = new int[ART__EXTENT];
		pL = new int[ART__EXTENT];
		aL = new int[ART__EXTENT];
		fiRL = new boolean[ART__EXTENT];
		eoOPL = new boolean[ART__EXTENT];
		eoRL = new boolean[ART__EXTENT];
		eoR_pL = new boolean[ART__EXTENT];
		lhsL[ART__ART____S_2] = ART__ART____S_2;
		pL[ART__ART____S_2] = ART__ART____S_2;
		aL[ART__ART____S_2] = ART__ART____S_2;
		eoOPL[ART__ART____S_2] = false;
		fiRL[ART__ART____S_2] = false;
		eoRL[ART__ART____S_2] = false;
		eoR_pL[ART__ART____S_2] = false;
		lhsL[ART__ART____S_4] = ART__ART____S_2;
		pL[ART__ART____S_4] = ART__ART____S_4;
		aL[ART__ART____S_4] = ART__ART____S_4;
		eoOPL[ART__ART____S_4] = false;
		fiRL[ART__ART____S_4] = false;
		eoRL[ART__ART____S_4] = false;
		eoR_pL[ART__ART____S_4] = false;
		lhsL[ART__ART____S_5] = ART__ART____S_2;
		pL[ART__ART____S_5] = ART__ART____S_5;
		aL[ART__ART____S_5] = ART__ART____S_5;
		eoOPL[ART__ART____S_5] = false;
		fiRL[ART__ART____S_5] = false;
		eoRL[ART__ART____S_5] = false;
		eoR_pL[ART__ART____S_5] = false;
		lhsL[ART__ART____S_6] = ART__ART____S_2;
		pL[ART__ART____S_6] = ART__ART____S_6;
		aL[ART__ART____S_6] = ART__ART____S_6;
		eoOPL[ART__ART____S_6] = false;
		fiRL[ART__ART____S_6] = true;
		eoRL[ART__ART____S_6] = false;
		eoR_pL[ART__ART____S_6] = false;
		lhsL[ART__ART____S_10] = ART__ART____S_2;
		pL[ART__ART____S_10] = ART__ART____S_10;
		aL[ART__ART____S_10] = ART__ART____S_10;
		eoOPL[ART__ART____S_10] = false;
		fiRL[ART__ART____S_10] = false;
		eoRL[ART__ART____S_10] = false;
		eoR_pL[ART__ART____S_10] = false;
		lhsL[ART__ART____S_11] = ART__ART____S_2;
		pL[ART__ART____S_11] = ART__ART____S_11;
		aL[ART__ART____S_11] = ART__ART____S_11;
		eoOPL[ART__ART____S_11] = false;
		fiRL[ART__ART____S_11] = false;
		eoRL[ART__ART____S_11] = false;
		eoR_pL[ART__ART____S_11] = false;
		lhsL[ART__ART____S_12] = ART__ART____S_2;
		pL[ART__ART____S_12] = ART__ART____S_12;
		aL[ART__ART____S_12] = ART__ART____S_12;
		eoOPL[ART__ART____S_12] = false;
		fiRL[ART__ART____S_12] = false;
		eoRL[ART__ART____S_12] = false;
		eoR_pL[ART__ART____S_12] = false;
		lhsL[ART__ART____S_13] = ART__ART____S_2;
		pL[ART__ART____S_13] = ART__ART____S_14;
		aL[ART__ART____S_13] = ART__ART____S_13;
		eoOPL[ART__ART____S_13] = true;
		fiRL[ART__ART____S_13] = false;
		eoRL[ART__ART____S_13] = false;
		eoR_pL[ART__ART____S_13] = false;
		lhsL[ART__ART____S_14] = ART__ART____S_2;
		pL[ART__ART____S_14] = ART__ART____S_14;
		aL[ART__ART____S_14] = ART__ART____S_14;
		eoOPL[ART__ART____S_14] = false;
		fiRL[ART__ART____S_14] = false;
		eoRL[ART__ART____S_14] = false;
		eoR_pL[ART__ART____S_14] = false;
		lhsL[ART__ART____S_17] = ART__ART____S_2;
		pL[ART__ART____S_17] = ART__ART____S_17;
		aL[ART__ART____S_17] = ART__ART____S_17;
		eoOPL[ART__ART____S_17] = false;
		fiRL[ART__ART____S_17] = false;
		eoRL[ART__ART____S_17] = false;
		eoR_pL[ART__ART____S_17] = false;
		lhsL[ART__ART____S_18] = ART__ART____S_2;
		pL[ART__ART____S_18] = ART__ART____S_18;
		aL[ART__ART____S_18] = ART__ART____S_18;
		eoOPL[ART__ART____S_18] = false;
		fiRL[ART__ART____S_18] = false;
		eoRL[ART__ART____S_18] = true;
		eoR_pL[ART__ART____S_18] = true;
		setInitialise();
		artLabelStringsInitialise();
		artRDTStringsInitialise();
		artTerminalRequiresWhiteSpaceInitialise();
		artTerminalCaseInsensitiveInitialise();
		foldLInitialise();
		pLInitialise();
		aLInitialise();
		fiRLInitialise();
		eoOPLInitialise();
		eoR_pLInitialise();
	}
};
