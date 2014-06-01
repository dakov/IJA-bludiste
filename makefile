
CP = `pwd`/src/
CLIENT = $(CP)/ija/client/Client.java
SERVER = $(CP)/ija/server/Server.java

CLS_CLIENT = `pwd`/build/client/
CLS_SERVER = `pwd`/build/server/

compile: 
	rm -rf build
	mkdir build
	mkdir build/client
	mkdir build/server
	javac -cp $(CP) -d $(CLS_CLIENT) $(CLIENT)
	javac -cp $(CP) -d $(CLS_SERVER) $(SERVER)

client:
	@java -cp $(CLS_CLIENT) ija.client.Client

server:
	@java -cp $(CLS_SERVER) ija.server.Server

pack:
	rm -rf grp107
	mkdir grp107
	cp -r examples grp107
	cp -r src grp107
	mkdir grp107/lib
	cp build.xml grp107
	cp readme.txt grp107
	zip -r grp107.zip	grp107
	rm -rf grp107
