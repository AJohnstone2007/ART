echo on
cls
pushd \adrian\eclipse\art\src\uk\ac\rhul\cs\csle\art\manager\parser
java -jar \csle\dev\art\art_v3\art.jar +namespace:'uk.ac.rhul.cs.csle.art.manager.parser' +parserName:'ARTParser' +lexerName:'ARTLexer' ARTParser.art
popd

pushd \adrian\eclipse\art\src\uk\ac\rhul\cs\csle\art\tg\
java -jar \csle\dev\art\art_v3\art.jar +namespace:'uk.ac.rhul.cs.csle.art.tg' +parserName:'ARTTGParser' +lexerName:'ARTTGLexer' ARTTGParser.art
popd

pushd \adrian\eclipse\art\src\uk\ac\rhul\cs\csle\art\esos\
java -jar \csle\dev\art\art_v3\art.jar +namespace:'uk.ac.rhul.cs.csle.art.esos' +parserName:'ARTeSOSParser' +lexerName:'ARTeSOSLexer' ARTeSOSParser.art
popd

pushd \adrian\eclipse\art\src\uk\ac\rhul\cs\csle\art\cava\
java -jar \csle\dev\art\art_v3\art.jar +namespace:'uk.ac.rhul.cs.csle.art.cava' +parserName:'ARTCavaParser' +lexerName:'ARTCavaLexer' ARTCavaParser.art
popd
