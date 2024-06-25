class artParserCustomisation{ 
  public:
  int indentLevel;
  artParserCustomisation() { indentLevel = 0; }

  virtual const char* targetLanguageName() {return "";}

  /* names */
  virtual const char* booleanName() { return NULL; }
  virtual const char* trueName() { return NULL; }
  virtual const char* falseName() { return NULL; }
  virtual const char* integerName() {  return NULL; }
  virtual const char* stringName() {  return NULL; }
  virtual const char* nullName() {  return NULL; }
  virtual const char* inputAccessToken() {  return NULL; }
  virtual const char* inputAccessLeftExtent() {  return NULL; }
  virtual const char* inputAccessFirstSuccessorReference() {  return NULL; }

  /* text formatting */
  virtual void newLine() {}
  virtual void indent() {}
  void indentSet(int i) { indentLevel = i; }
  void indentUp() { indentLevel++; }
  void indentDown() { if (indentLevel > 0) indentLevel--; }
  virtual void comment(const char* s){}

  /* control flow */
  virtual void ifInSet(const char* set, const char* var) {}
  virtual void ifNotInSet(const char* set, const char* var) {}
  virtual void ifNot(const char* var) {}
  virtual void ifNull(const char* var) {}
  virtual void ifFunction(const char* var) {}
  virtual void ifTestRepeat(const char* var, const char* p1, const char* p2, const char* p3) {}
  virtual void whileTrue(const char* label) {}
  virtual void forSuccessorPair() {}
  virtual const char* inputSuccessorReference() {return "";}
  virtual const char* inputSuccessorReferenceToken() {return "";}
  virtual const char* inputSuccessorReferenceLeftExtent() {return "";}
  virtual void exception(const char* id) {}
  virtual void blockOpen() {}
  virtual void blockClose() {}

  virtual void caseOpen(const char* id) {}
  virtual void caseBranchOpen(const char* id, bool flowThrough) { }
  virtual void caseBranchClose(const char* id) {}
  virtual void caseDefault() {}
  virtual void caseClose(const char* id, bool flowThrough) {}

  virtual void jump(const char* id) {}
  virtual void jumpDynamic(const char* id) {}
  virtual void jumpState(const char* id) {}
  virtual void jumpFragment(const char* id) {}
  virtual void label(const char* label) {}
  virtual void ret() {}
  virtual void brk() {}

  /* file handling */
  virtual void fileOpen(const char* filename, const char* modulename) {}
  virtual void fileClose(const char* filename, const char* modulename) {}

  /* declarations and assignments */
  virtual void classOpen(const char* id) {}
  virtual void classOpen(const char* id, const char* super, const char* interface) {}
  virtual void classClose(const char* id) {}

  virtual void enumerationOpen(const char* id) {}
  virtual void enumerationElement(const char* id) {}
  virtual void enumerationClose(const char* id) {}

  virtual void constructorOpen(const char* id){}
  virtual void constructorOpen(const char* id, const char*p1Type, const char* p1){}
  virtual void constructorOpenRef(const char* id, const char*p1Type, const char* p1){}
  virtual void constructorOpen(const char* id, const char*p1Type, const char* p1, const char*p2Type, const char* p2){}
  virtual void constructorOpenRef(const char* id, const char*p1Type, const char* p1, const char*p2Type, const char* p2){}
  virtual void constructorClose(const char* id) {}

  virtual void functionVoidForward(const char* id){ }
  virtual void functionVoidOpen(const char* id){ }
  virtual void functionVoidOpen(const char* id, const char*p1Type, const char*p1){ }
  virtual void functionVoidOpenForward(const char* id, const char*p1Type, const char*p1){ }
  virtual void functionVoidOpen(const char* id, const char*p1Type, const char*p1, const char*p2Type, const char*p2){ }
  virtual void functionVoidOpenThrows(const char* id, const char*p1Type, const char*p1, const char*p2Type, const char*p2){ }
  virtual void functionVoidOpenRef(const char* id, const char*p1Type, const char*p1){ }

  virtual void functionClose(const char* id) {}

  virtual void functionCall(const char* id) {}
  virtual void functionCall(const char* id, const char* p1) {}
  virtual void functionCall(const char* id, const char* p1, const char* p2) {}
  virtual void functionCall(const char* id, const char* p1, const char* p2, const char* p3) {}
  virtual void functionCall(const char* id, const char* p1, const char* p2, const char* p3, const char* p4) {}

  virtual void functionAssignCall(const char* variable, const char* id, const char* p1) {}
  virtual void functionAssignCall(const char* variable, const char* id, const char* p1, const char* p2) {}
  virtual void functionAssignCall(const char* variable, const char* id, const char* p1, const char* p2, const char* p3) {}
  virtual void functionAssignCall(const char* variable, const char* id, const char* p1, const char* p2, const char* p3, const char* p4) {}

  virtual void assign(const char* id, const char* value) { }
  virtual void assignString(const char* id, const char* value) { }
  virtual void assignAppendNull(const char* id, const char* value) { }

  virtual void declareBoolean(const char* id) {}
  virtual void declareBoolean(const char* id, const char* value) {}
  virtual void declareBooleanArray(const char* id) {}
  virtual void allocateBooleanArray(const char* id, const char* extent) {}
  virtual void assignBooleanArrayElement(const char* id, const char* index, const char* value) {}

  virtual void declareInteger(const char* id) {}
  virtual void declareInteger(const char* id, const char* value) {}
  virtual void declareIntegerArray(const char* id) {}
  virtual void allocateIntegerArray(const char* id, const char* extent) {}
  virtual void assignIntegerArrayElement(const char* id, const char* index, const char* value) {}

  virtual void declareEnumeration(const char* enumId, const char* id) { }
  virtual void declareEnumeration(const char* enumId, const char* id, const char* value) { }
  virtual void declareEnumerationArray(const char* enumId, const char* id) { }
  virtual void allocateEnumerationArray(const char* enumId, const char* id, const char* extent) { }
  virtual void assignEnumerationArrayElement(const char* id, const char* index, const char* value) { }

  virtual void declareString(const char* id) {}
  virtual void declareString(const char* id, const char* value) {}
  virtual void declareStringArray(const char* id, const char* extent) {}
  virtual void allocateStringArray(const char* id, const char* extent) {}
  virtual void assignStringArrayElement(const char* id, const char* index, const char* value) {}

  /* Lexer builtin support */
  virtual void lexerBuiltInInstance(const char* builtinId, const char* terminalId) {}
  virtual void lexerWhitespaceBuiltinInstance(const char* id) {}
  virtual void lexerWhitespaceCharInstance(const char* id) {}
};

/** Java **********************************************************************/
class javaCustomisation: public artParserCustomisation {
  private:
  const char* className;

  const char* targetLanguageName() {return "Java";}

  const char* booleanName() { return "boolean"; }
  const char* trueName() { return "true"; }
  const char* falseName() { return "false"; }
  const char* integerName() { return "int";}
  const char* stringName() { return "String";}
  const char* nullName() { return "null"; }
  const char* inputAccessToken() {  return "artLexer.artInputPairBuffer[artLexer.artCurrentInputPairReference]"; }
  const char* inputAccessLeftExtent() {  return "artLexer.artInputPairBuffer[artLexer.artCurrentInputPairReference + 1]"; }
  const char* inputAccessFirstSuccessorReference() {  return "artLexer.artInputSuccessorIndex[artLexer.artInputPairBuffer[artLexer.artCurrentInputPairReference + 1]][artLexer.artInputPairBuffer[artLexer.artCurrentInputPairReference]]"; }

  void fileOpen(const char* filename, const char* modulename) {
    char *fullFilename = (char*) mem_calloc(1, 1 + strlen(filename) + strlen(".java"));
    strcat(fullFilename, filename);
    strcat(fullFilename, ".java");

    artRedirectToFile((char*) fullFilename);
    if (modulename != NULL)
      text_printf("package %s;\n\n", modulename);

    text_printf("import uk.ac.rhul.cs.csle.artvalue.*;\n"
                "import uk.ac.rhul.cs.csle.arttext.*;\n"
                "import uk.ac.rhul.cs.csle.artgraph.*;\n"
                "import uk.ac.rhul.cs.csle.artgll.*;\n\n"
                "import uk.ac.rhul.cs.csle.art.ARTException;\n\n"
                "/*******************************************************************************\n"
                "*\n"
                "* %s\n"
                "*\n"
                "*******************************************************************************/\n",
                fullFilename
                );

  }

  void fileClose(const char* filename, const char* modulename) {
    text_redirect(stdout);
  }

  void newLine() { text_printf("\n"); }
  void indent() { for (int i = 0; i < indentLevel; i++) text_printf("  "); }
  void comment(const char* s){ text_printf("/*%s*/\n", s);}

  void blockOpen() { text_printf("{ "); }
  void blockClose() { text_printf(" }\n"); }

  void ifInSet(const char* set, const char* var) { indent(); text_printf("if (%s[%s]) ", set, var); }
  void ifNotInSet(const char* set, const char* var) { indent(); text_printf("if (!%s[%s]) ", set, var); }
  void ifNot(const char* var) { indent(); text_printf("if (!%s) ", var); }
  void ifNull(const char* var) { indent(); text_printf("if (%s == null) ", var); }
  void ifFunction(const char* var) { indent(); text_printf("if (%s()) ", var); }
  void ifTestRepeat(const char* var, const char* p1, const char* p2, const char* p3) { indent(); text_printf("if (artTestRepeat(%s, %s, %s, %s)) ", var, p1, p2, p3); }

  void jump(const char* id) { indent(); text_printf("goto %s;\n", id); }
  void jumpDynamic(const char* id) { indent(); text_printf("goto *%s;\n", id); }
  void jumpState(const char* id) { text_printf("{ artCurrentRestartLabel = %s; break; }\n", id); }
  void jumpFragment(const char* id) { text_printf("{ artCurrentRestartLabel = %s; return; }\n", id); }

  void ret() { indent(); text_printf("return;\n"); }
  void brk() { indent(); text_printf("break;\n"); }
  void exception(const char* id) { indent(); text_printf("printf(\"\\nException: %s\\n\"); exit(1);\n", id); }
  void whileTrue(const char* label) {indent(); if (label != NULL) text_printf("%s: ", label); text_printf("while (true)\n"); indentUp(); }
  void forSuccessorPair() {indent(); text_printf("for (int artI = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference+1]][artInputPairBuffer[artCurrentInputPairReference]]; artInputSuccessorBuffer[artI] != -1; artI++) "); indentUp(); }
  const char* inputSuccessorReference() {return "artInputSuccessorBuffer[artI]";};
  const char* inputSuccessorReferenceToken() {return "artInputPairBuffer[artInputSuccessorBuffer[artI]]"; };
  const char* inputSuccessorReferenceLeftExtent() {return "artInputPairBuffer[artInputSuccessorBuffer[artI]+1]"; };

  void caseOpen(const char* id) { indent(); text_printf("switch (%s) {\n", id); indentUp(); }
  void caseBranchOpen(const char* id, bool flowThrough) { if (flowThrough) indentDown(); indent(); text_printf("case %s: \n", id); indentUp(); }
  void caseBranchClose(const char* id) { indent(); text_printf("continue artSelectState;\n"); indentDown(); }
  void caseDefault() { indent(); text_printf("default: "); indentUp(); }
  void caseClose(const char* id, bool flowThrough) { if (flowThrough) indentDown(); indentDown(); indent(); text_printf("}\n"); }

  void classOpen(const char* id) { className = id; text_printf("public class %s {\n", id); }
  void classOpen(const char* id, const char* super, const char* interface) {
    className = id;
    text_printf("public class %s extends uk.ac.rhul.cs.csle.artgll.%s ", id, super);
    if (interface != NULL) text_printf("implements uk.ac.rhul.cs.csle.artgll.%s {\n", interface);
    text_printf("{\n");
  }
  void classClose(const char* id) { text_printf("};\n"); }

  int elementNumber;
  void enumerationOpen(const char* id) { indent(); elementNumber = 0; text_printf("/* Start of %s enumeration */\n", id ); }
  void enumerationElement(const char* id) { indent(); text_printf("public static final int %s = %i;\n", id, elementNumber++); }
  void enumerationClose(const char* id) { indent(); text_printf("/* End of %s enumeration */\n", id); }

  void constructorOpen(const char* id){ indent(); text_printf("public %s() {\n", id); indentUp(); }
  void constructorOpen(const char* id, const char*p1Type, const char* p1){ indent(); text_printf("public %s(%s %s) {\n", id, p1Type, p1); indentUp(); }
  void constructorOpen(const char* id, const char*p1Type, const char* p1, const char* p2Type, const char* p2){ indent(); text_printf("public %s(%s %s, %s %s) {\n", id, p1Type, p1, p2Type, p2); indentUp(); }
  void constructorOpenRef(const char* id, const char*p1Type, const char* p1){ constructorOpen(id, p1Type, p1); }  // No difference in Java
  void constructorOpenRef(const char* id, const char*p1Type, const char* p1, const char*p2Type, const char* p2){ constructorOpen(id, p1Type, p1, p2Type, p2); }  // No difference in Java
  void constructorClose(const char* id) { indentDown(); indent(); text_printf("}\n"); }

  void functionVoidForward(const char* id) { }
  void functionVoidOpen(const char* id) { indent(); text_printf("public void %s() {\n", id); indentUp(); }
  void functionVoidOpen(const char* id, const char* p1Type, const char* p1){ indent(); text_printf("public void %s(%s %s) {\n", id, p1Type, p1); indentUp(); }
  void functionVoidOpen(const char* id, const char* p1Type, const char* p1, const char* p2Type, const char* p2){ indent(); text_printf("public void %s(%s %s, %s %s) {\n", id, p1Type, p1, p2Type, p2); indentUp(); }
  void functionVoidOpenThrows(const char* id, const char*p1Type, const char*p1, const char*p2Type, const char*p2){  indent(); text_printf("public void %s(%s %s, %s %s) throws ARTException {\n", id, p1Type, p1, p2Type, p2); indentUp(); }
  void functionVoidOpenRef(const char* id, const char* p1Type, const char* p1){ indent(); text_printf("public void %s(%s %s) {\n", id, p1Type, p1); indentUp(); }

  void functionClose(const char* id) { indentDown(); indent(); text_printf("}\n"); }

  void functionCall(const char* id) { indent(); text_printf("%s();\n", id);}
  void functionCall(const char* id, const char* p1) { indent(); text_printf("%s(%s);\n", id, p1);}
  void functionCall(const char* id, const char* p1, const char* p2) { indent(); text_printf("%s(%s, %s);\n", id, p1, p2);}
  void functionCall(const char* id, const char* p1, const char* p2, const char* p3) { indent(); text_printf("%s(%s, %s, %s);\n", id, p1, p2, p3);}
  void functionCall(const char* id, const char* p1, const char* p2, const char* p3, const char* p4) { indent(); text_printf("%s(%s, %s, %s, %s);\n", id, p1, p2, p3, p4);}

  void functionAssignCall(const char* var, const char* id, const char* p1) { indent(); text_printf("%s = %s(%s);\n", var, id, p1);}
  void functionAssignCall(const char* var, const char* id, const char* p1, const char* p2) { indent(); text_printf("%s = %s(%s, %s);\n", var, id, p1, p2);}
  void functionAssignCall(const char* var, const char* id, const char* p1, const char* p2, const char* p3) { indent(); text_printf("%s = %s(%s, %s, %s);\n", var, id, p1, p2, p3);}
  void functionAssignCall(const char* var, const char* id, const char* p1, const char* p2, const char* p3, const char* p4) { indent(); text_printf("%s = %s(%s, %s, %s, %s);\n", var, id, p1, p2, p3, p4);}

  void assign(const char* id, const char* value) { indent(); text_printf("%s = %s;\n", id, value); }
  void assignString(const char* id, const char* value) { indent(); text_printf("%s = \"%s\";\n", id, value); }
  void assignAppendNull(const char* id, const char* value) { indent(); text_printf("%s = %s + \"\\0\";\n", id, value); }

  void declareBoolean(const char* id) { indent(); text_printf("boolean %s;\n", id); }
  void declareBoolean(const char* id, const char* value) { indent(); text_printf("boolean %s = %s;\n", id, value); }
  void declareBooleanArray(const char* id) { indent(); text_printf("private static boolean[] %s;\n", id); }
  void allocateBooleanArray(const char* id, const char* extent) { indent(); text_printf("%s = new boolean[%s];\n", id, extent); }
  void assignBooleanArrayElement(const char* id, const char* index, const char* value) { indent(); text_printf("%s[%s] = %s;\n", id, index, value); }

  void declareInteger(const char* id) { indent(); text_printf("int %s;\n", id); }
  void declareInteger(const char* id, const char* value) { indent(); text_printf("int %s = %s;\n", id, value); }
  void declareIntegerArray(const char* id) { indent(); text_printf("int *%s;\n", id); }
  void allocateIntegerArray(const char* id, const char* extent) { indent(); text_printf("%s = new int[%s];\n", id, extent); }
  void assignIntegerArrayElement(const char* id, const char* index, const char* value) { indent(); text_printf("%s[%s] = %s;\n", id, index, value); }

  void declareEnumeration(const char* enumId, const char* id) { indent(); text_printf("int %s;\n", id); }
  void declareEnumeration(const char* enumId, const char* id, const char* value) { indent(); text_printf("int %s = %s;\n", id, value); }
  void declareEnumerationArray(const char* enumId, const char* id) { indent(); text_printf("int [] %s;\n", id); }
  void allocateEnumerationArray(const char* enumId, const char* id, const char* extent) { indent(); text_printf("%s = new int[%s];\n", id, extent); }
  void assignEnumerationArrayElement(const char* id, const char* index, const char* value) { indent(); text_printf("%s[%s] = %s;\n", id, index, value); }

  void declareString(const char* id) { indent(); text_printf("String %s;\n", id); }
  void declareString(const char* id, const char* value) { indent(); text_printf("String %s = \"%s\";\n", id, value); }
  void declareStringArray(const char* id, const char* extent) { indent(); text_printf("String [] %s;\n", id); }
  void allocateStringArray(const char* id, const char* extent) { indent(); text_printf("%s = new String[%s];\n", id, extent); }
  void assignStringArrayElement(const char* id, const char* index, const char* value) { indent(); text_printf("%s[%s] = \"%s\";\n", id, index, value); }

  void lexerBuiltInInstance(const char* builtinId, const char* terminalId) { indent(); text_printf("artUpdateLongestLength(artBuiltin_%s(artCharacterStringInputIndex), %s);\n", builtinId, terminalId); }
  void lexerWhitespaceBuiltinInstance(const char* id) { indent(); text_printf("artCharacterStringInputIndex += artBuiltin_%s(artCharacterStringInputIndex);\n", id); }
  void lexerWhitespaceCharInstance(const char* id) { indent(); text_printf("artCharacterStringInputIndex += artCharacterStringInputTest(\'%s\', artCharacterStringInputIndex);\n", id); }

  void label(const char*  label) { indent(); text_printf("Java error; goto state mode not allowed (%s)", label); }
};

/** CPP **********************************************************************/
class cppEnumCustomisation: public artParserCustomisation {
  private:
  void fileOpen(const char* filename, const char* modulename) {
    char *fullFilename = (char*) mem_calloc(1, 1 + strlen(filename) + strlen(".h"));
    strcat(fullFilename, filename);
    strcat(fullFilename, ".h");

    artRedirectToFile((char*) fullFilename);
    text_printf("/*******************************************************************************\n"
                "*\n"
                "* %s - enum declaration\n"
                "*\n"
                "*******************************************************************************/\n",
                filename
                );
    if (modulename != NULL)
      text_printf("namespace %s;\n\n", modulename);
  }

  void fileClose(const char* filename, const char* modulename) {
    text_redirect(stdout);
  }
  void enumerationOpen(const char* id) { indent(); text_printf("enum %s {\n", id); indentUp();}
  void enumerationElement(const char* id) { indent(); text_printf("%s,\n", id); }
  void enumerationClose(const char* id) { indentDown(); indent(); text_printf("};\n"); }
};

class cppCustomisation: public artParserCustomisation {
  private:
  const char* className;

  public:
  const char* booleanName() { return "bool"; }
  const char* trueName() { return "true"; }
  const char* falseName() { return "false"; }
  const char* integerName() { return "int";}
  const char* stringName() { return "const char*";}
  const char* nullName() { return "NULL"; }
  const char* inputAccessToken() {  return "inputSetBuffer[currentInputPairReference]"; }
  const char* inputAccessLeftExtent() {  return "inputSetBuffer[currentInputPairReference + 1]"; }
  const char* inputAccessFirstSuccessorReference() {  return "inputSetBuffer[currentInputPairReference + 3]"; }

  void newLine() { text_printf("\n"); }
  void indent() { for (int i = 0; i < indentLevel; i++) text_printf("  "); }
  void comment(const char* s){ text_printf("/*%s*/\n", s);}

  void blockOpen() { text_printf("{ "); }
  void blockClose() { text_printf(" }"); }

  void ifInSet(const char* set, const char* var) { indent(); text_printf("if (%s[%s]) ", set, var); }
  void ifNotInSet(const char* set, const char* var) { indent(); text_printf("if (!%s[%s]) ", set, var); }
  void ifNot(const char* var) { indent(); text_printf("if (!%s) ", var); }
  void ifNull(const char* var) { indent(); text_printf("if (%s == NULL) ", var); }
  void ifFunction(const char* var) { indent(); text_printf("if (%s()) ", var); }
  void ifTestRepeat(const char* var, const char* p1, const char* p2, const char* p3) { indent(); text_printf("if (testRepeat(%s, %s, %s, %s)) ", var, p1, p2, p3); }
  void jump(const char* id) { text_printf("goto %s;\n", id); }
  void jumpDynamic(const char* id) { text_printf("goto *%s;\n", id); }
  void jumpState(const char* id) { text_printf("{ currentRestartLabel = %s; break; }\n", id); }
  void jumpFragment(const char* id) { text_printf("{ currentRestartLabel = %s; return; }\n", id); }

  void label(const char* label) { indent(); text_printf("%s:\n", label); }

  void ret() { indent(); text_printf("return;"); }
  void brk() { indent(); text_printf("break;\n"); }
  void exception(const char* id) { indent(); text_printf("printf(\"\\nException: %s\\n\"); exit(1);\n", id); }
  void whileTrue(const char *label) { indent(); if (label != NULL) text_printf("%s: ", label); text_printf("while (true)\n"); }
  void forSuccessorPair() {indent(); text_printf("for (int i = 0; i < inputSetBuffer[currentInputPairReference + 2]; i++) "); indentUp(); }
  const char* inputSuccessorReference() {return "inputSetBuffer[currentInputPairReference + 3 + i]"; };
  const char* inputSuccessorReferenceToken() {return "inputSetBuffer[inputSetBuffer[currentInputPairReference + 3 + i]]"; };
  const char* inputSuccessorReferenceLeftExtent() {return "inputSetBuffer[inputSetBuffer[currentInputPairReference + 3 + i] + 1]"; };
  void caseOpen(const char* id) { indent(); text_printf("switch (%s) {\n", id); indentUp(); }
  void caseBranchOpen(const char* id, bool flowThrough) { if (flowThrough) indentDown(); indent(); text_printf("case %s: \n", id); indentUp();}
  void caseBranchClose(const char* id) { indent(); text_printf("break;\n"); indentDown(); indentDown(); }
  void caseDefault() { indent(); text_printf("default:\n "); indentUp(); }
  void caseClose(const char* id, bool flowThrough) { if (flowThrough) indentDown(); indent(); text_printf("}\n"); indentDown(); }

  void fileOpen(const char* filename, const char* modulename) {
    char *fullFilename = (char*) mem_calloc(1, 1 + strlen(filename) + strlen(".h"));
    strcat(fullFilename, filename);
    strcat(fullFilename, ".h");

    artRedirectToFile((char*) fullFilename);
    text_printf("/*******************************************************************************\n"
                "*\n"
                "* %s\n"
                "*\n"
                "*******************************************************************************/\n",
                filename);

    if (modulename != NULL)
      text_printf("namespace %s;\n\n", modulename);

    text_printf("#include \"bccgcc.h\"\n"
                "#include \"Text.h\"\n"
                "#include \"ARTGLLParserHashPool.h\"\n"
                );
  }

  void fileClose(const char* filename, const char* modulename) {
    text_redirect(stdout);
  }

  void classOpen(const char* id) { className = id; text_printf("class %s {\n", id); indentUp(); }
  void classOpen(const char* id, const char* super, const char *interface) { className = id; text_printf("class %s: public %s {\n", id, super); indentUp(); }
  void classClose(const char* id) { indentDown(); text_printf("}; "); }

  void constructorOpen(const char* id){ indent(); text_printf("public: %s() {\n", id); indentUp(); }
  void constructorOpen(const char* id, const char*p1Type, const char* p1){ indent(); text_printf("public: %s(%s %s) {\n", id, p1Type, p1); indentUp(); }
  void constructorOpen(const char* id, const char*p1Type, const char* p1, const char*p2Type, const char* p2){ indent(); text_printf("public: %s(%s %s, %s %s) {\n", id, p1Type, p1, p2Type, p2); indentUp(); }
  void constructorOpenRef(const char* id, const char* p1Type, const char* p1){ indent(); text_printf("public: %s(%s* %s) {\n", id, p1Type, p1); indentUp(); }
  void constructorOpenRef(const char* id, const char* p1Type, const char* p1, const char*p2Type, const char* p2){ indent(); text_printf("public: %s(%s* %s, %s* %s) {\n", id, p1Type, p1, p2Type, p2); indentUp(); }
  void constructorClose(const char* id) { indentDown(); indent(); text_printf("}\n"); }

  void functionVoidForward(const char* id) { indent(); text_printf("public: void %s();\n", id);}
  void functionVoidOpen(const char* id) { indent(); text_printf("public: void %s() {\n", id); indentUp(); }
  void functionVoidOpen(const char* id, const char* p1Type, const char* p1){ indent(); text_printf("public: void %s(%s %s) {\n", id, p1Type, p1);  indentUp(); }
  void functionVoidOpen(const char* id, const char* p1Type, const char* p1, const char* p2Type, const char* p2){ indent(); text_printf("public: void %s(%s %s, %s %s) {\n", id, p1Type, p1, p2Type, p2);  indentUp(); }
  void functionVoidOpenRef(const char* id, const char* p1Type, const char* p1){ indent(); text_printf("public: void %s(%s* %s) {\n", id, p1Type, p1); indentUp(); }

  void functionClose(const char* id) { indentDown(); indent(); text_printf("}\n"); }

  void functionCall(const char* id) { indent(); text_printf("%s();\n", id);}
  void functionCall(const char* id, const char* p1) { indent(); text_printf("%s(%s);\n", id, p1);}
  void functionCall(const char* id, const char* p1, const char* p2) { indent(); text_printf("%s(%s, %s);\n", id, p1, p2);}
  void functionCall(const char* id, const char* p1, const char* p2, const char* p3) { indent(); text_printf("%s(%s, %s, %s);\n", id, p1, p2, p3);}
  void functionCall(const char* id, const char* p1, const char* p2, const char* p3, const char* p4) { indent(); text_printf("%s(%s, %s, %s, %s);\n", id, p1, p2, p3, p4);}

  void functionAssignCall(const char* var, const char* id, const char* p1) { indent(); text_printf("%s = %s(%s);\n", var, id, p1);}
  void functionAssignCall(const char* var, const char* id, const char* p1, const char* p2) { indent(); text_printf("%s = %s(%s, %s);\n", var, id, p1, p2);}
  void functionAssignCall(const char* var, const char* id, const char* p1, const char* p2, const char* p3) { indent(); text_printf("%s = %s(%s, %s, %s);\n", var, id, p1, p2, p3);}
  void functionAssignCall(const char* var, const char* id, const char* p1, const char* p2, const char* p3, const char* p4) { indent(); text_printf("%s = %s(%s, %s, %s, %s);\n", var, id, p1, p2, p3, p4);}

  void assign(const char* id, const char* value) { indent(); text_printf("%s = %s;\n", id, value); }
  void assignString(const char* id, const char* value) { indent(); text_printf("%s = (const char*) \"%s\";\n", id, value); }
  void assignAppendNull(const char* id, const char* value) { indent(); text_printf("%s = (const char*) %s; /* C string already null terminated */\n", id, value); }

  void declareBoolean(const char* id) { indent(); text_printf("bool %s;\n", id); }
  void declareBoolean(const char* id, const char* value) { indent(); text_printf("bool %s = %s;\n", id, value); }
  void declareBooleanArray(const char* id) { indent(); text_printf("bool *%s;\n", id); }
  void allocateBooleanArray(const char* id, const char* extent) { indent(); text_printf("%s = new bool[%s];\n", id, extent); }
  void assignBooleanArrayElement(const char* id, const char* index, const char* value) { indent(); text_printf("%s[%s] = %s;\n", id, index, value); }

  void declareInteger(const char* id) { indent(); text_printf("int %s;\n", id); }
  void declareInteger(const char* id, const char* value) { indent(); text_printf("int %s = %s;\n", id, value); }
  void declareIntegerArray(const char* id) { indent(); text_printf("int *%s;\n", id); }
  void allocateIntegerArray(const char* id, const char* extent) { indent(); text_printf("%s = new int[%s];\n", id, extent); }
  void assignIntegerArrayElement(const char* id, const char* index, const char* value) { indent(); text_printf("%s[%s] = %s;\n", id, index, value); }

  void declareEnumeration(const char* enumId, const char* id) { indent(); text_printf("enum %s %s;\n", enumId, id); }
  void declareEnumeration(const char* enumId, const char* id, const char* value) { indent(); text_printf("enum %s %s = %s;\n", enumId, id, value); }
  void declareEnumerationArray(const char* enumId, const char* id) { indent(); text_printf("enum %s *%s;\n", enumId, id); }
  void allocateEnumerationArray(const char* enumId, const char* id, const char* extent) { indent(); text_printf("%s = new enum %s[%s];\n", id, enumId, extent); }
  void assignEnumerationArrayElement(const char* id, const char* index, const char* value) { indent(); text_printf("%s[%s] = %s;\n", id, index, value); }

  void declareString(const char* id) { indent(); text_printf("const char* %s;\n", id); }
  void declareString(const char* id, const char* value) { indent(); text_printf("const char* %s = \"%s\";\n", id, value); }
  void declareStringArray(const char* id, const char* extent) { indent(); text_printf("const char** %s;\n", id); }
  void allocateStringArray(const char* id, const char* extent) { indent(); text_printf("%s = new const char*[%s];\n", id, extent); }
  void assignStringArrayElement(const char* id, const char* index, const char* value) { indent(); text_printf("%s[%s] = \"%s\";\n", id, index, value); }

  void lexerBuiltInInstance(const char* builtinId, const char* terminalId) { indent(); text_printf("updateLongestLength(artBuiltin_%s(characterStringInputIndex), %s);\n", builtinId, terminalId); }
  void lexerWhitespaceBuiltinInstance(const char* id) { indent(); text_printf("characterStringInputIndex += artBuiltin_%s(characterStringInputIndex);\n", id); }
  void lexerWhitespaceCharInstance(const char* id) { indent(); text_printf("characterStringInputIndex += characterStringInputTest(\'%s\', characterStringInputIndex);\n", id); }
};

