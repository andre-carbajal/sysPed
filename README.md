# sysPed

## Technologies Used
- Spring Boot
- Maven
- Spring Data JPA
- MySQL
- Spring Boot Docker Compose(for database)
- Thymeleaf
- Spring Security
- Lombok
- Spring Web

## Requirements
- Java 21
- Docker and Docker Compose installed

## Recommendations
- Use an IDE like IntelliJ IDEA for better development experience.

## Installation
1. Clone the repository:
   ```bash
    git clone https://github.com/andre-carbajal/sysPed.git
    cd sysPed
    ```
2. Build the project using Maven:
    ```bash
    mvn clean install
    ```
3. Run the aplication
    ```bash
    mvn spring-boot:run
    ```
4. Run the sql Script in `data.sql` to insert initial data into the database.
5. Access the application at `http://localhost:8080`
