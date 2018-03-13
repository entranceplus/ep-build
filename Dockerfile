FROM clojure:boot
MAINTAINER Akash Shakdwipeea <ashakdwipeea@gmail.com>

RUN mkdir /usr/src/entrance-plus
WORKDIR /usr/src/entrance-plus

# prod
COPY target/entrance-plus-0.1.0-SNAPSHOT-standalone.jar .

EXPOSE 7001 7000 8001 8000

CMD ["java", "-jar", "entrance-plus-0.1.0-SNAPSHOT-standalone.jar"]