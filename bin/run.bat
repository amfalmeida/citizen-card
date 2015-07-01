@echo off
setLocal EnableDelayedExpansion
for %%F IN (lib/*.jar) do (
  set cp=!cp!;lib/%%F%
)
java -cp "./classes/;%cp%" com.aalmeida.EmbeddedHTTPServer
pause.
