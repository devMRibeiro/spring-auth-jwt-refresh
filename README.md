# Spring Boot Login with Spring Security, PostgreSQL and JWT

User can signup new account (registration), or login with username & password.
By User’s role (admin, moderator, user), we authorize the User to access resources.
These are APIs that we need to provide:

#### Methods	Urls	Actions
POST	/api/auth/signup	signup new account
POST	/api/auth/signin	login an account
POST	/api/auth/signout	logout the account
GET	/api/test/all	retrieve public content
GET	/api/test/user	access User’s content
GET	/api/test/mod	access Moderator’s content
GET	/api/test/admin	access Admin’s content

#### Dependency
– If you want to use PostgreSQL:

```
<dependency>
  <groupId>org.postgresql</groupId>
  <artifactId>postgresql</artifactId>
  <scope>runtime</scope>
</dependency>
– or MySQL:

<dependency>
  <groupId>com.mysql</groupId>
  <artifactId>mysql-connector-j</artifactId>
  <scope>runtime</scope>
</dependency>
```
#### Configure Spring Datasource, JPA, App properties
Open src/main/resources/application.properties

#### For PostgreSQL:
```
spring.datasource.url= jdbc:postgresql://localhost:5432/testdb
spring.datasource.username= postgres
spring.datasource.password= root

spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation= true
spring.jpa.properties.hibernate.dialect= org.hibernate.dialect.PostgreSQLDialect

# Hibernate ddl auto (create, create-drop, validate, update)
spring.jpa.hibernate.ddl-auto= update

# App Properties
bezkoder.app.jwtCookieName= COOKIE_NAME
bezkoder.app.jwtSecret= YOUR_SECRET_KEY
bezkoder.app.jwtExpirationMs= 86400000
```

#### For MySQL
```
spring.datasource.url= jdbc:mysql://localhost:3306/testdb?useSSL=false
spring.datasource.username= root
spring.datasource.password= 123456

spring.jpa.properties.hibernate.dialect= org.hibernate.dialect.MySQL5InnoDBDialect
spring.jpa.hibernate.ddl-auto= update

# App Properties
bezkoder.app.jwtCookieName= COOKIE_NAME
bezkoder.app.jwtSecret= YOUR_SECRET_KEY
bezkoder.app.jwtExpirationMs= 86400000
```

#### Run Spring Boot application
```
mvn spring-boot:run
```

#### Run following SQL insert statements:
```
INSERT INTO roles(name) VALUES('ROLE_USER');
INSERT INTO roles(name) VALUES('ROLE_MODERATOR');
INSERT INTO roles(name) VALUES('ROLE_ADMIN');
```
