copy target\CayoPericoHeistAssistant.jar CayoPericoHeistAssistant
rmdir /q /s target\minijre
jlink --compress 1 --output target\minijre --strip-debug --no-header-files --no-man-pages --strip-native-commands --add-modules java.base,java.compiler,java.datatransfer,java.desktop,java.logging,java.management,java.naming,java.rmi,java.scripting,java.sql,java.xml
E:\Tools\jdk15\jdk-15.0.1\bin\jpackage.exe --app-version 0.11 --runtime-image target\minijre --dest target --input CayoPericoHeistAssistant --name CayoPericoHeistAssistant --main-jar CayoPericoHeistAssistant.jar --type msi --win-menu --win-shortcut --win-dir-chooser --win-per-user-install
