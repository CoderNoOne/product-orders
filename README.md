# System of submitting and processing product orders - RESTful API
---
### 1. About

An application that allows you to implement an extensive system of submitting and processing product orders, as well as exchanging custom messages, product proposals, product complaints etc. within the system and via email between customers and their managers. It's not particularly a shop, it's rather an application which provides a communication between sales representatives and their clients. The clients can view the history of their orders. They also have access to functionalities that allow the user to filter product orders by the selected criteria. Managers are also responsible for the completion of products in the shop's stocks
Admin_shop takes care of adding, modifying shop and stocks associated with them. Admin_product on the othed hand, adds products to the database. Admin_actuator has access to actuator endpoints (such as shutdown etc.)
The application is developed in Domain Driven Domain (DDD) desing on top of Spring Boot utilizing jdk 14


![Alt text](https://i.imgur.com/fKkvDHD.png "EER DIAGRAM")
*Fig. 1: Database model*

### 2. Project build with

* [Maven](https://maven.apache.org/) - Dependency Management
* [Spring boot]() - starting point for building Spring-based applications


### 3. Main dependencies, libraries, frameworks, technologies, standards:
* Spring Data Jpa
* Spring Security with Json Web Token (JWT) 
* Spring Boot Mail Starter
* Spring-Data-Envers
* Spring-Boot-Actuator
* Lombok
* J2html
* openApi - swagger 3.0
