#Project DevRate. Docker Container.

This Docker container is designed to run the DevRate application in a consistent environment.

Ensure Docker is installed on your system.

1. Build the Docker image:
- Open a terminal.
- Enter: "docker build -t [enter tag-name for your Container] ."
(For example: "docker build -t devrate-container .")


2. Running the Container
- docker run -p [free port local PC]:[free port in Container] [enter tag-name for your Container]
(For example: "docker run -p 8087:8088 devrate-container")

3. Stopping the Container
- Open a terminal.
- docker stop $(docker ps -a -q --filter ancestor=[enter tag-name for your Container])
(For example: "docker stop $(docker ps -a -q --filter ancestor=devrate-container)")






