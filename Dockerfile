# 使用Maven構建階段
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# 配置Maven使用阿里雲鏡像源
RUN mkdir -p /root/.m2 && \
    echo '<?xml version="1.0" encoding="UTF-8"?>' > /root/.m2/settings.xml && \
    echo '<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"' >> /root/.m2/settings.xml && \
    echo 'xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"' >> /root/.m2/settings.xml && \
    echo '          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">' >> /root/.m2/settings.xml && \
    echo '<mirrors>' >> /root/.m2/settings.xml && \
    echo '    <mirror>' >> /root/.m2/settings.xml && \
    echo '      <id>aliyunmaven</id>' >> /root/.m2/settings.xml && \
    echo '      <mirrorOf>*</mirrorOf>' >> /root/.m2/settings.xml && \
    echo '      <name>阿里雲公共倉庫</name>' >> /root/.m2/settings.xml && \
    echo '      <url>https://maven.aliyun.com/repository/public</url>' >> /root/.m2/settings.xml && \
    echo '    </mirror>' >> /root/.m2/settings.xml && \
    echo '  </mirrors>' >> /root/.m2/settings.xml && \
    echo '</settings>' >> /root/.m2/settings.xml

# 複製pom.xml並下載依賴（利用Docker緩存）
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 複製源代碼
COPY src ./src

# 構建應用程序
RUN mvn clean package -DskipTests

# 運行階段 - 使用 OpenJDK 官方鏡像
FROM openjdk:17-jdk-slim
WORKDIR /app

# 複製構建的JAR文件
COPY --from=build /app/target/*.jar app.jar

# 創建上傳目錄
RUN mkdir -p /app/uploads/products

# 暴露端口
EXPOSE 8080

# 運行應用程序
ENTRYPOINT ["java", "-jar", "app.jar"]
