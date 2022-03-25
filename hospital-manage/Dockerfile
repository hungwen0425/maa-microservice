FROM openjdk:8-jdk
LABEL maintainer=leifengyang


#启动自行加载   服务名-prod.yml配置
ENV PARAMS="--server.port=8080 --spring.profiles.active=prod"
RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone

COPY target/*.jar /app.jar
EXPOSE 8080

# hospital-manage
ENTRYPOINT ["/bin/sh","-c","java -Dfile.encoding=utf8  -Djava.security.egd=file:/dev/./urandom -jar /app.jar ${PARAMS}"]