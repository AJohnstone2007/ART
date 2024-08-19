rd/s/q bin
rd/s/q doc
rd/s/q gll
rd/s/q src

md bin
md doc
md gll
md src

xcopy  /Q /S \csle\dev\gtb\bin\*.* bin
xcopy  /Q /S \csle\dev\gtb\doc\*.* doc
xcopy  /Q /S \csle\dev\gtb\gll\*.* gll
xcopy  /Q /S \csle\dev\gtb\src\*.* src

pushd bin
call buildgpp
popd
