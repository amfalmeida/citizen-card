
echo %3 >> %1\launch4j.log

cd %2
launch4jc.exe %3 >> %1\launch4j.log
