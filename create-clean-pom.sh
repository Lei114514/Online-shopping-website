#!/bin/bash

# 創建乾淨的pom.xml文件

echo '<?xml version="1.0" encoding="UTF-8"?>' > pom.xml.clean
echo '<project xmlns="http://maven.apache.org/POM/4.0.0"' >> pom.xml.clean
echo '         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"' >> pom.xml.clean
echo '         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 ' >> pom.xml.clean
echo '         https://maven.apache.org/xsd/maven-4.0.0.xsd">' >> pom.xml.clean
echo '    <modelVersion>4.0.0</modelVersion>' >> pom.xml.clean
echo '' >> pom.xml.clean
echo '    <parent>' >> pom.xml.clean
echo '        <groupId>org.springframework.boot</groupId>' >> pom.xml.clean
echo '        <artifactId>spring-boot-starter-parent</artifactId>' >> pom.xml.clean
echo '        <version>3.2.0</version>' >> pom.xml.clean
echo '        <relativePath/>' >> pom.xml.clean
echo '    </parent>' >> pom.xml.clean
echo '' >> pom.xml.clean
echo '    <groupId>com.onlineshop</groupId>' >> pom.xml.clean
echo '    <artifactId>online-shop</artifactId>' >> pom.xml.clean
echo '    <version>1.0.0</version>' >> pom.xml.clean
echo '    <name>online-shop</name>' >> pom.xml.clean
echo '    <description>Online Shopping Web Application</description>' >> pom.xml.clean
echo '    <packaging>jar</packaging>' >> pom.xml.clean
echo '' >> pom.xml.clean
echo '    <properties>' >> pom.xml.clean
echo '        <java.version>17</java.version>' >> pom.xml.clean
echo '        <maven.compiler.source>17</maven.compiler.source>' >> pom.xml.clean
echo '        <maven.compiler.target>17</maven.compiler.target>' >> pom.xml.clean
echo '    </properties>' >> pom.xml.clean
echo '' >> pom.xml.clean
echo '    <dependencies>' >> pom.xml.clean
echo '        <!-- Spring Boot Starters -->' >> pom.xml.clean
echo '        <dependency>' >> pom.xml.clean
echo '            <groupId>org.springframework.boot</groupId>' >> pom.xml.clean
echo '            <artifactId>spring-boot-starter-web</artifactId>' >> pom.xml.clean
echo '        </dependency>' >> pom.xml.clean
echo '        <dependency>' >> pom.xml.clean
echo '            <groupId>org.springframework.boot</groupId>' >> pom.xml.clean
echo '            <artifactId>spring-boot-starter-data-jpa</artifactId>' >> pom.xml.clean
echo '        </dependency>' >> pom.xml.clean
echo '        <dependency>' >> pom.xml.clean
echo '            <groupId>org.springframework.boot</groupId>' >> pom.xml.clean
echo '            <artifactId>spring-boot-starter-security</artifactId>' >> pom.xml.clean
echo '        </dependency>' >> pom.xml.clean
echo '        <dependency>' >> pom.xml.clean
echo '            <groupId>org.springframework.boot</groupId>' >> pom.xml.clean
echo '            <artifactId>spring-boot-starter-thymeleaf</artifactId>' >> pom.xml.clean
echo '        </dependency>' >> pom.xml.clean
echo '        <dependency>' >> pom.xml.clean
echo '            <groupId>org.springframework.boot</groupId>' >> pom.xml.clean
echo '            <artifactId>spring-boot-starter-mail</artifactId>' >> pom.xml.clean
echo '        </dependency>' >> pom.xml.clean
echo '        <dependency>' >> pom.xml.clean
echo '            <groupId>org.springframework.boot</groupId>' >> pom.xml.clean
echo '            <artifactId>spring-boot-starter-validation</artifactId>' >> pom.xml.clean
echo '        </dependency>' >> pom.xml.clean
echo '' >> pom.xml.clean
echo '        <!-- Thymeleaf Extras for Security -->' >> pom.xml.clean
echo '        <dependency>' >> pom.xml.clean
echo '            <groupId>org.thymeleaf.extras</groupId>' >> pom.xml.clean
echo '            <artifactId>thymeleaf-extras-springsecurity6</artifactId>' >> pom.xml.clean
echo '        </dependency>' >> pom.xml.clean
echo '' >> pom.xml.clean
echo '        <!-- Database -->' >> pom.xml.clean
echo '        <dependency>' >> pom.xml.clean
echo '            <groupId>mysql</groupId>' >> pom.xml.clean
echo '            <artifactId>mysql-connector-java</artifactId>' >> pom.xml.clean
echo '            <version>8.0.33</version>' >> pom.xml.clean
echo '            <scope>runtime</scope>' >> pom.xml.clean
echo '        </dependency>' >> pom.xml.clean
echo '' >> pom.xml.clean
echo '        <!-- Lombok for reducing boilerplate code -->' >> pom.xml.clean
echo '        <dependency>' >> pom.xml.clean
echo '            <groupId>org.projectlombok</groupId>' >> pom.xml.clean
echo '            <artifactId>lombok</artifactId>' >> pom.xml.clean
echo '            <optional>true</optional>' >> pom.xml.clean
echo '        </dependency>' >> pom.xml.clean
echo '' >> pom.xml.clean
echo '        <!-- Testing -->' >> pom.xml.clean
echo '        <dependency>' >> pom.xml.clean
echo '            <groupId>org.springframework.boot</groupId>' >> pom.xml.clean
echo '            <artifactId>spring-boot-starter-test</artifactId>' >> pom.xml.clean
echo '            <scope>test</scope>' >> pom.xml.clean
echo '        </dependency>' >> pom.xml.clean
echo '        <dependency>' >> pom.xml.clean
echo '            <groupId>org.springframework.security</groupId>' >> pom.xml.clean
echo '            <artifactId>spring-security-test</artifactId>' >> pom.xml.clean
echo '            <scope>test</scope>' >> pom.xml.clean
echo '        </dependency>' >> pom.xml.clean
echo '        <dependency>' >> pom.xml.clean
echo '            <groupId>com.h2database</groupId>' >> pom.xml.clean
echo '            <artifactId>h2</artifactId>' >> pom.xml.clean
echo '            <scope>test</scope>' >> pom.xml.clean
echo '        </dependency>' >> pom.xml.clean
echo '' >> pom.xml.clean
echo '        <!-- Development Tools -->' >> pom.xml.clean
echo '        <dependency>' >> pom.xml.clean
echo '            <groupId>org.springframework.boot</groupId>' >> pom.xml.clean
echo '            <artifactId>spring-boot-devtools</artifactId>' >> pom.xml.clean
echo '            <scope>runtime</scope>' >> pom.xml.clean
echo '            <optional>true</optional>' >> pom.xml.clean
echo '        </dependency>' >> pom.xml.clean
echo '    </dependencies>' >> pom.xml.clean
echo '' >> pom.xml.clean
echo '    <build>' >> pom.xml.clean
echo '        <plugins>' >> pom.xml.clean
echo '            <plugin>' >> pom.xml.clean
echo '                <groupId>org.springframework.boot</groupId>' >> pom.xml.clean
echo '                <artifactId>spring-boot-maven-plugin</artifactId>' >> pom.xml.clean
echo '                <configuration>' >> pom.xml.clean
echo '                    <excludes>' >> pom.xml.clean
echo '                        <exclude>' >> pom.xml.clean
echo '                            <groupId>org.projectlombok</groupId>' >> pom.xml.clean
echo '                            <artifactId>lombok</artifactId>' >> pom.xml.clean
echo '                        </exclude>' >> pom.xml.clean
echo '                    </excludes>' >> pom.xml.clean
echo '                </configuration>' >> pom.xml.clean
echo '            </plugin>' >> pom.xml.clean
echo '        </plugins>' >> pom.xml.clean
echo '    </build>' >> pom.xml.clean
echo '</project>' >> pom.xml.clean

echo "已創建乾淨的pom.xml.clean文件"
echo ""
echo "檢查文件:"
head -c 100 pom.xml.clean
echo ""
echo ""
echo "現在運行:"
echo "cp pom.xml.clean pom.xml"
echo "然後再次運行: ./docker-run.sh"
