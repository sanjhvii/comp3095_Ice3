#-----------------------
#Dockerfile for product-service
#-----------------------

#------
#BIULD stage
#------
#Base image
FROM gradle:8-jdk17-alpine AS builder
COPY --chown=gradle:gradle . /home/gradle/src

#Set the directory within the image, for executing future commands
WORKDIR /home/gradle/src

# Run gradle build within the container - NOT the host machine
RUN gradle build -x test

#------
#Package stage
#------

FROM  openjdk:17-alpine

RUN mkdir /app

COPY --from=builder /home/gradle/src/build/libs/*.jar /app/discovery-service.jar


EXPOSE 8761

ENTRYPOINT ["java", "-jar", "/app/discovery-service.jar"]

