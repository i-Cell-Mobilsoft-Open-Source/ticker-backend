#
# Mock server
#
# https://github.com/mock-server/mockserver alatt levo Dockerfile atdolgozasa
# https://www.mock-server.com
#
#

ARG OSS_DOCKER_REPOSITORY
ARG DOCKER_BASE_IMAGE_VERSION
ARG MOCK_SERVER_VERSION_RELEASE=5.15.0
ARG NETTY_TCNATIVE_VERSION=2.0.50.Final

################################################################
# step1 :: download mock-server 
################################################################
FROM ${OSS_DOCKER_REPOSITORY}/icellmobilsoft/builder-nexus-download:${DOCKER_BASE_IMAGE_VERSION} as download-mock-server
ARG MOCK_SERVER_VERSION_RELEASE

ARG MOCKSERVER_POM_GROUP_ID=org.mock-server
ARG MOCKSERVER_POM_ARTIFACT_ID=mockserver-netty
ARG MOCKSERVER_POM_VERSION=${MOCK_SERVER_VERSION_RELEASE}
ARG MOCKSERVER_POM_EXTENSION=jar
ARG MOCKSERVER_POM_CLASSIFIER=jar-with-dependencies

ENV NEXUS_OBJECT_GROUP_ID=$MOCKSERVER_POM_GROUP_ID
ENV NEXUS_OBJECT_ARTIFACT_ID=$MOCKSERVER_POM_ARTIFACT_ID
ENV NEXUS_OBJECT_VERSION=$MOCKSERVER_POM_VERSION
ENV NEXUS_OBJECT_EXTENSION=$MOCKSERVER_POM_EXTENSION
ENV NEXUS_OBJECT_CLASSIFIER=$MOCKSERVER_POM_CLASSIFIER
ENV NEXUS_DOWNLOAD_OUTPUT_FILE_NAME=mockserver-netty-jar-with-dependencies.jar

RUN $HOME/script/maven-search-download.sh

################################################################
# step2 :: download libnetty tcnative amd64 
################################################################
FROM ${OSS_DOCKER_REPOSITORY}/icellmobilsoft/builder-nexus-download:${DOCKER_BASE_IMAGE_VERSION} as download-tcnative-amd64
USER root
RUN apt update && \
    apt install unzip -y
USER ${SYSTEM_USER}

ARG NETTY_TCNATIVE_VERSION

ARG MOCKSERVER_POM_GROUP_ID=io.netty
ARG MOCKSERVER_POM_ARTIFACT_ID=netty-tcnative-boringssl-static
ARG MOCKSERVER_POM_VERSION=${NETTY_TCNATIVE_VERSION}
ARG MOCKSERVER_POM_EXTENSION=jar
ARG MOCKSERVER_POM_CLASSIFIER=linux-x86_64

ENV NEXUS_OBJECT_GROUP_ID=$MOCKSERVER_POM_GROUP_ID
ENV NEXUS_OBJECT_ARTIFACT_ID=$MOCKSERVER_POM_ARTIFACT_ID
ENV NEXUS_OBJECT_VERSION=$MOCKSERVER_POM_VERSION
ENV NEXUS_OBJECT_EXTENSION=$MOCKSERVER_POM_EXTENSION
ENV NEXUS_OBJECT_CLASSIFIER=$MOCKSERVER_POM_CLASSIFIER
ENV NEXUS_DOWNLOAD_OUTPUT_FILE_NAME=netty-tcnative-boringssl-static.jar

RUN $HOME/script/maven-search-download.sh
RUN unzip -o netty-tcnative-boringssl-static.jar META-INF/native/libnetty_tcnative_linux_x86_64.so -d /home/icellmobilsoft/tcnative

################################################################
# step3 :: download libnetty tcnative arm64 
################################################################
FROM ${OSS_DOCKER_REPOSITORY}/icellmobilsoft/builder-nexus-download:${DOCKER_BASE_IMAGE_VERSION} as download-tcnative-arm64
USER root
RUN apt update && \
    apt install unzip -y
USER ${SYSTEM_USER}

ARG NETTY_TCNATIVE_VERSION

ARG MOCKSERVER_POM_GROUP_ID=io.netty
ARG MOCKSERVER_POM_ARTIFACT_ID=netty-tcnative-boringssl-static
ARG MOCKSERVER_POM_VERSION=${NETTY_TCNATIVE_VERSION}
ARG MOCKSERVER_POM_EXTENSION=jar
ARG MOCKSERVER_POM_CLASSIFIER=linux-aarch_64

ENV NEXUS_OBJECT_GROUP_ID=$MOCKSERVER_POM_GROUP_ID
ENV NEXUS_OBJECT_ARTIFACT_ID=$MOCKSERVER_POM_ARTIFACT_ID
ENV NEXUS_OBJECT_VERSION=$MOCKSERVER_POM_VERSION
ENV NEXUS_OBJECT_EXTENSION=$MOCKSERVER_POM_EXTENSION
ENV NEXUS_OBJECT_CLASSIFIER=$MOCKSERVER_POM_CLASSIFIER
ENV NEXUS_DOWNLOAD_OUTPUT_FILE_NAME=netty-tcnative-boringssl-static-aarch64.jar

RUN $HOME/script/maven-search-download.sh
RUN unzip -o netty-tcnative-boringssl-static-aarch64.jar META-INF/native/libnetty_tcnative_linux_aarch_64.so -d /home/icellmobilsoft/tcnative

################################################################
# step4 :: create mock server from jre11 image 
#          (we need the nashorn built in)
################################################################
FROM ${OSS_DOCKER_REPOSITORY}/icellmobilsoft/base-java11jre:${DOCKER_BASE_IMAGE_VERSION}

##copy mock-server + dependencies
COPY --from=download-mock-server /home/icellmobilsoft/download/mockserver-netty-jar-with-dependencies.jar /libs/mockserver-netty-jar-with-dependencies.jar
COPY --from=download-tcnative-amd64 /home/icellmobilsoft/tcnative/META-INF/native/libnetty_tcnative_linux_x86_64.so /usr/lib/x86_64-linux-gnu/libnetty_tcnative_linux_x86_64.so
COPY --from=download-tcnative-arm64 /home/icellmobilsoft/tcnative/META-INF/native/libnetty_tcnative_linux_aarch_64.so /usr/lib/aarch64-linux-gnu/libnetty_tcnative_linux_aarch_64.so
# copy init.json + entrypoint
COPY /etc/config/mock-server/init.json /home/icellmobilsoft/init.json
COPY /etc/config/mock-server/entrypoint.sh /entrypoint.sh
##our entrypoint is a .sh file but we need CTRL-C to terminate the running as quick as possible:
STOPSIGNAL SIGKILL 
##set some environment for running mock server
ENV MOCKSERVER_INITIALIZATION_JSON_PATH=/home/icellmobilsoft/init.json
ENV MOCKSERVER_LIVENESS_HTTP_GET_PATH=/health
ENV SERVER_PORT 1080
#start with initiating the expectations in the .sh file
ENTRYPOINT ["/entrypoint.sh"]
CMD []
