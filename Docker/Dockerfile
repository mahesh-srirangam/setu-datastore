FROM ubuntu:latest



RUN apt-get update && \
    apt-get install -y wget && \
    apt-get clean



# Make ports available to the world outside this container
EXPOSE 8080 443 8125 8126

RUN wget https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-21.3.0/graalvm-ce-java11-linux-amd64-21.3.0.tar.gz && \
    tar zxvf graalvm-ce-java11-linux-amd64-21.3.0.tar.gz -C /usr/local/ && \
    rm -f graalvm-ce-java11-linux-amd64-21.3.0.tar.gz && \
    mv /usr/local/graalvm-ce-java11-21.3.0 /usr/local/graalvm

ENV PATH="/usr/local/graalvm/bin:${PATH}"

WORKDIR /app

# Copy the current directory contents into the container at /app
ADD . /app
ENTRYPOINT ["sh","-c","exec java -Dquarkus.profile=prod -javaagent:/app/newrelic/newrelic.jar -jar /app/quarkus-app/quarkus-run.jar --server.tomcat.protocol-header=x-forwarded-proto"]