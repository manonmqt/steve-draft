# STEVE USE CASE REPOSITORY


## Context 
This github repository contains the code used to create the following talk : "Comment j'ai convaincue mes données de voyager en classe éco" / 
"How I got my data to travel economy class."  

Feel free to use it to test various technologies and their impact on the basics of data export.

## How To

### How to start the app 
1. Run the docker environment, it contains some API mock-server.
```bash 
$ docker compose up -d
```

2. Run "import mock" request in Postman/Bruno/...  
3. Run the app in you IDE then call various endpoints availlable on LocationController.class

> **_For Kafka test :_** stats returned by API call correspond to the last message sent on steve-topic

### How shut down the app

4. Stop the app in your IDE then shut down docker environment.
```bash 
$ docker compose down -v
```