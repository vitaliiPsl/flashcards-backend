FROM openjdk:11
WORKDIR /app
ADD  build/libs/*.jar ./app.jar
CMD java -jar ./app.jar