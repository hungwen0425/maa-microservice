FROM openjdk:8-jdk
LABEL maintainer=TsengHungWen


#启动自行加载   服务名-prod.yml配置
ENV PARAMS="--server.port=8080 --spring.profiles.active=prod --spring.cloud.nacos.server-addr=his-nacos.his:8848 --spring.cloud.nacos.config.file-extension=yml"
RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone

COPY target/*.jar /app.jar
EXPOSE 8080

#
ENTRYPOINT ["/bin/sh","-c","java -Dfile.encoding=utf8  -Djava.security.egd=file:/dev/./urandom -jar /app.jar ${PARAMS}"]