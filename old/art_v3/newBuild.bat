pushd \adrian\eclipse\art\bin
java uk.ac.rhul.cs.csle.arttg.ARTVersionUpdate %1
move artversion.h.new \csle\dev\art\art_v2\artversion.h
move ARTVersion.java.new \adrian\eclipse\art\src\uk\ac\rhul\cs\csle\art\ARTVersion.java
move manifest.local.new \csle\dev\art\art_v3\manifest.local
popd
