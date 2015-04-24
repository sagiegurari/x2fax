
"%JAVA_HOME%\bin\java.exe" -Dorg.fax4j.x2fax.launcher.class.name=@main.class@ -Dorg.fax4j.x2fax.launcher.jar.dir=@jar.dir@ -cp ./lib/fax4j-x2fax-@project.version@.jar;./lib/fax4j-@fax4j.version@.jar @jvm.args@ org.fax4j.x2fax.util.JavaLauncher %*
