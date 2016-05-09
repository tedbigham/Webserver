Notes
-----
* Requires JDK 1.7 or higher

* Build with maven 3.0 or higher.  Command

mvn package

* Run from webserver folder.  Command:

java -jar target/webserver.jar

* Sample pages are
	/
	/stats
	/echo
	/hello


* The Maven build will generate some exceptions.  They are
  part of the junit tests for failure. The test should still
  be passing.

* Default port is 8080

* Default root folder is ./files

* The configuration file is ./conf/webserver.properties

* I included generated files in the archive (eclipse project and target folder)

* I time-boxed my work to 8 hours. I didn't try for any optional 
  features, but NIO would have been interesting to try with more 
  time.


Issues (I'm sure there's more)
------------------------------
* Logging is hard coded to System.out and System.err, and can't be 
  turned off.

* The file servlet probably has tons of security holes.

* Configurable servlets need to be aware of naming convention used 
  in the property file and have access to other servlet's configs.

* Headers are not parsed to HTTP spec, specifically around duplicates.

* Request parameters are not parsed to HTTP spec, specifically around 
  duplicates.

* Error messages are not localized.

* Character encoding is not adjustable.

* No remote port for stopping server.

* Thread pool is not adjustable.


Thanks
-Ted Bigham