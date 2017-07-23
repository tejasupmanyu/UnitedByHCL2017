Redpin is an open source indoor positioning system that was developed with the goal of providing at least room-level accuracy. 
Moreover, it avoids the time-consuming training and setup phase known from other systems and instead relies on the user community.

SERVER

Launch the server from command line using java -jar redpin.jar [port].
Install MySQL Community Server
Run the 'redpin_mysql.sql' script from the src/resource directory within the RedpinServer project to configure the database.
Set the database access credentials by setting the 'db.type' and 'db.location' in the file 'redpin.properties':
db.type=mysql
db.location=//localhost:3306/dbname?user=username&password=pwd
For better performance, install LIBSVM
Configure the svm.libdir to point to your installation path in the file 'redpin.properties':
svm.libdir = libsvm-2.9
Optional: Configure the training rate of the svm locator to fit your needs, e.g. train every 10 minutes:
svm.trainrate = 10
Save map image as png, gif or jpg.
Upload image file to your web-server.
Use netcat or telnet to send the following request to your server:
{"action":"setMap","data":{"mapName":"YOUR MAP NAME","mapURL":"http://your-url.org/..."}}

ANDROID

Save map image as png, gif or jpg.
Upload image file to your web-server.
Use netcat or telnet to send the following request to your server:
{"action":"setMap","data":{"mapName":"YOUR MAP NAME","mapURL":"http://your-url.org/..."}}