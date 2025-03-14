# Report Module 2.4.2

## Project Title & Description
- This module handles all patient, HTS, HIV, PMTCT Reports
  
## System Requirements

### Prerequisites to Install
- IDE of choice (IntelliJ, Eclipse, etc.)
- Java 8+
- PostgreSQL 14+
- React Js

## Run in Development Environment

### How to Install Dependencies
1. Install Java 8+
2. Install PostgreSQL 14+
3. Download and replace the system `.m2` folder.
4. Clone the git repository:
    ```bash
    git clone https://github.com/lamisplus/Report-Module.git
    ```
5. Open the project in your IDE of choice.
6. install node 16+.
7. Install React Js

### Update Configuration File
1. Update database access details in `db-config.yml` file.
2. Update other Maven application properties as required.

### Run Build and Install Commands
1. Run Frontend Build Command:
    ```bash
    npm run build
    ```
2. Run Maven clean install:
    ```bash
    mvn clean install
    ```

## How to Package for Production Environment
1. Run Maven package command:
    ```bash
    mvn clean package
    ```

## Launch Packaged JAR File
1. Launch the Core (LAMISPlus) JAR file:
    ```bash
    java -jar <path-to-jar-file>
    ```
2. Optionally, run with memory allocation:
    ```bash
    java -jar -Xms4096M -Xmx6144M <path-to-jar-file>
    ```
3. Install package Report Jar
4. Restart the Core (LAMISPlus)


## Visit the Application
- Visit the application on a browser at the configured port:
    ```
    http://localhost:8080
    ```

## Access Swagger Documentation
- Visit the application at:
    ```
    http://localhost:8080/swagger-ui.html#/
    ```

## Access Application Logs
- Application logs can be accessed in the `application-debug` folder.

# Authors & Acknowledgments
## Main Contributor
- Victor Ajor https://github.com/AJ-DataFI
- Mathew Adegbite https://github.com/mathewade
- Emeka https://github.com/drjavanew
- Joshua Gabriel https://github.com/joshuagabriel-datafi
- Ugo-Basil https://github.com/Ugo-Basil
- Abisayo Peter Abiodun https://github.com/Asquarep
- Okafor Chukwuma https://github.com/baronearl

## Special mentions
- Aniwange Tertese Amos https://github.com/aniwange33
- https://github.com/stomzy
- Anana Aristotle https://github.com/legendaryA3
- Adebisi Timothy https://github.com/SundayT29
- Emeka madubuko https://github.com/emadubuko
- Hafiz Mohammad Danmanu https://github.com/Danmanu44
