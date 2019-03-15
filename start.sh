#!/bin/sh

# start script for docker
# see https://www.jujens.eu/posts/en/2015/May/24/deploy-glassfish-docker/

# waiting for mysql to start up
sleep 5

/usr/local/glassfish4/bin/asadmin start-domain
/usr/local/glassfish4/bin/asadmin -u admin deploy `ls /*.war`
/usr/local/glassfish4/bin/asadmin stop-domain
/usr/local/glassfish4/bin/asadmin start-domain --verbose