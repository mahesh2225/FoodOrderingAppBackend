# FoodOrderingAppBackend

Instruction to setup project

1. git clone https://github.com/dipeshsinghal/FoodOrderingAppBackend

2. open project in intelliji and chose folder FoodOrderingAppBackend

3. start postgress and greate db with name "restaurantdb"

4. on intellij terminal got to FoodOrderingAppBackend/FoodOrderingApp-db/ and run

	mvn clean install -Psetup -DskipTests

	it will create tables in db

5. make db user name as "postgress" and password as "password"

FoodOrderingAppBackend/FoodOrderingApp-api/src/main/resources/application.yaml should have

driverClassName: org.postgresql.Driver
url: jdbc:postgresql://localhost:5432/restaurantdb
username: postgres
password: password


FoodOrderingAppBackend/FoodOrderingApp-db/src/main/resources/config/localhost.properties should have
server.port=5432
server.host=localhost
database.name=restaurantdb
database.username=postgres
database.password=password

6. on intellij terminal got to FoodOrderingAppBackend and run 
	mvn clean install -DskipTests

7. FoodOrderingAppBackend/FoodOrderingApp-api/src/main/java/com/upgrad/FoodOrderingApp/api/FoodOrderingAppApiApplication.java
	Run Above Application

8. open url http://localhost:8080/api/swagger-ui.html
