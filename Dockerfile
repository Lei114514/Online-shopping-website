# 使用Maven構建階段
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app

# 複製pom.xml和源代碼
COPY pom.xml .
COPY src ./src

# 構建應用程序
RUN mvn clean package -DskipTests

# 運行階段
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
