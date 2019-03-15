FROM glassfish:latest

COPY target/*.war /
COPY start.sh /

# make directory for uploading files
RUN mkdir -p /srv/slutprojekt/uploaded

EXPOSE 8080

ENTRYPOINT [ "/start.sh" ]