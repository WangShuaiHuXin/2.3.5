FROM java:8
EXPOSE 8181

VOLUME /tmp
ADD target/nest-common.jar  /app.jar
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-jar","/app.jar"]