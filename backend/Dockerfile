# 构建阶段
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# 复制Maven配置和pom.xml
COPY settings.xml .
COPY pom.xml .

# 预下载依赖 (利用Docker层缓存)
RUN mvn -s settings.xml dependency:go-offline -B

# 复制源码并打包
COPY src ./src
RUN mvn -s settings.xml package -DskipTests

# 运行阶段
FROM eclipse-temurin:17-jre
WORKDIR /app

# 复制打包好的jar
COPY --from=build /app/target/*.jar app.jar

# 设置时区
ENV TZ=Asia/Shanghai

EXPOSE 8000

ENTRYPOINT ["java", "-jar", "app.jar"]
