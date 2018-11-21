# Keyless-Server

## How to run

Make sure you have a MySQL user called `admin` with password `admin`.  
Then, setup an empty database called `keyless`, and execute `mvn clean spring-boot:run`.


## Testing

Running coverage test:  
`mvn clean`  
`mvn clover:instrument clover:clover`

Test report can be found at:  
`/target/site/clover/index.html`