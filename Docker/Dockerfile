FROM ghcr.io/graalvm/graalvm-ce:ol7-java11-22.3.1

# Set the working directory to /app
WORKDIR /app

# Copy the current directory contents into the container at /app
ADD . /app

# Make ports available to the world outside this container
EXPOSE 8080 443 8125 8126

#RUN   apk update \
#   &&   apk add ca-certificates wget \
#   &&   update-ca-certificates
#RUN wget -N https://download.newrelic.com/newrelic/java-agent/newrelic-agent/current/newrelic-java.zip

#RUN apk add --no-cache fontconfig ttf-dejavu

#RUN apk add --update nodejs npm
#RUN unzip newrelic-java.zip -d /app/
#RUN mkdir /app/config
#RUN touch /app/config/newrelic.yml
#RUN ln -sf  /app/config/newrelic.yml /app/newrelic/newrelic.yml
ENTRYPOINT ["sh","-c","exec java -javaagent:/app/newrelic/newrelic.jar -jar /app/ROOT.jar --server.tomcat.protocol-header=x-forwarded-proto"]
CMD [--spring.profiles.active=prod]

