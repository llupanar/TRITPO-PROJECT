FROM openjdk:16
ADD build/libs/wclookup.jar wclookup.jar
ADD keystore/keystore.p12 keystore.p12
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=debug", "-Dkeystore.name=keystore.p12", "-Dkeystore.password=explosion204", "wclookup.jar"]