FROM glassfish:latest

COPY target/*.war /
COPY start.sh /

EXPOSE 8080

ENTRYPOINT [ "/start.sh" ]