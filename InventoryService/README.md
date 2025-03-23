# This is a Java [Spring Boot](https://spring.io/projects/spring-boot) Inventory Service using H2 database, Junit5 unit tests, DataJpaTest, WebMvcTest, mockito library, SpringBootTest integration tests, docker and jenkins

- Author: Attila Gyori
- Email: gyoriattila@yahoo.com
- Website: https://attila-gyori.com

![Playwright](assets/spring.png)

> Inventory Service with spring boot



## 🚀 Execute the project

### Navigate to root directory

```bash
Execute commands from the root directory of the project
```

### Maven tests and artifact build:

```bash
mvn clean package verify
```

### Test results output:

```bash
unit tests & integration tests: target/site/surefire-report.html
coverage results: target/site/jacoco/index.html
```

### Create docker container:

```bash
docker build -t inventory-service:latest .
```

### Run docker image:

```bash
docker run -d -p 8095:8095 inventory-service:latest
```

or

### Run service from maven:
```bash
mvn spring-boot:run
(default port in application.yml server: port: 8095)
```

or

### Create a jenkins pipeline job and use projects Jenkinsfile

### Execute API tests using playwright
```bash
navigate to folder APITest/playwright-test
```

<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->

