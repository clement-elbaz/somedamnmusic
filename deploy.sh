java -DSTOP.PORT=8079 -DSTOP.KEY=somedamnmusic_rulezzzzz -jar start.jar --stop
cp target/somedamnmusic.war /home/jetty/webapps/root.war
java -DSTOP.PORT=8079 -DSTOP.KEY=somedamnmusic_rulezzzzz -jar start.jar &