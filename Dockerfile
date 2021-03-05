FROM adoptopenjdk:11-jdk-hotspot

WORKDIR /app

COPY build/libs/wiremock-async-uber.jar /app
COPY src/callbackTest/resources/mappings /app/mappings

CMD [ "java", "-cp", "/app/wiremock-async-uber.jar", \
    "com.github.tomakehurst.wiremock.standalone.WireMockServerRunner", \
    "--extensions", "mjs.wiremock.DelayedCallback" ]

EXPOSE 8080
