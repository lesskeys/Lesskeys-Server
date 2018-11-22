# Lesskeys-Server

This part of the Lesskeys softwarepackage is the backend server for the system.
This software is built using [Maven](https://maven.apache.org/). <br>
It uses the Spring Framework, is base on the Spring MVC pattern and is implemented as a RESTful service. <br>
The server is running on an HTTPS-secured Tomcat server, with a self-signed certificate that one would have to supply himeselfe to run the software. 

## How to run

Install the [Maven](https://maven.apache.org/) build tool.

Make sure you have a MySQL user called `admin` with password `admin`.  
Then, setup an empty database called `keyless`. <br>

If you cannot setup a db like that, then you would have to configure that in the file `src/main/ressources/application.properties`.

Navigate to the root directory and run:
```sh
mvn clean
mvn spring-boot:run
```

This will start the RESTful server on Port 8080.

## Testing

Running coverage test:  
`mvn clean`  
`mvn clover:instrument clover:clover`

Test report can be found at:  
`/target/site/clover/index.html`