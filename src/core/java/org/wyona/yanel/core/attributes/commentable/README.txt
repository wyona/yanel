JAXB requires:
- package-info.java: You set the namespace for all your data objects as well as the used prefix in the resulting XMLs
                     Verify that your target JAR or WAR file containing the data CLASS files also contains the package-info.class file!!
                     Make sure that the build process follows some workarounds described here:
                     http://stackoverflow.com/questions/4877097/jaxb-2-x-and-ant
                     
- jaxb.properties: This file must be available, otherwise you get a runtime exception by the JAXB engine
                   Also make sure that this properties file gets into the resulting JAR or WAR file.
                   The file contains one property which defines which JAXB Implementation should be used. Due to issues with the standard JRE6 engine, 
                   MOXy from the EclipseLink project is used.