# docker-compose adja
ARG ICELL_JAVA_JRE_BASE_IMAGE
################################################################################
# Default image customization
################################################################################
FROM ${ICELL_JAVA_JRE_BASE_IMAGE:-icellmobilsoft/base-java21jre:1.5.0} AS base

ENV LANG='en_US.UTF-8' LANGUAGE='en_US:en'

################################################################################
# Download .jar
################################################################################
FROM icellmobilsoft/builder-nexus-download:1.5.0 AS download

ARG POM_GROUP_ID
ARG POM_ARTIFACT_ID
ARG POM_VERSION
ARG POM_EXTENSION

ENV NEXUS_OBJECT_GROUP_ID=$POM_GROUP_ID
ENV NEXUS_OBJECT_ARTIFACT_ID=$POM_ARTIFACT_ID
ENV NEXUS_OBJECT_EXTENSION=$POM_EXTENSION
ENV NEXUS_OBJECT_VERSION=$POM_VERSION

ENV NEXUS_DOWNLOAD_OUTPUT_FILE_NAME=fastjar.tar.gz

RUN $HOME/script/sonatype-download.sh

################################################################################
# Create production image
################################################################################
FROM base AS prod

ARG POM_ARTIFACT_ID
ARG POM_VERSION

ENV TICKER_LOGSTASH_MODULE_ID=$POM_ARTIFACT_ID
ENV TICKER_LOGSTASH_MODULE_VERSION=$POM_VERSION
ENV TICKER_JSON_MODULE_ID=$POM_ARTIFACT_ID
ENV TICKER_JSON_MODULE_VERSION=$POM_VERSION

LABEL moduleName="$POM_ARTIFACT_ID"
LABEL moduleVersion="$POM_VERSION"

# create directories
RUN mkdir -p $HOME/tmp && mkdir $HOME/deployments
# copy downlaoded file
COPY --from=download /home/icellmobilsoft/download/fastjar.tar.gz $HOME/tmp

# unzip
RUN tar --strip-components=1 -xvzf /home/icellmobilsoft/tmp/fastjar.tar.gz -C $HOME/deployments/
RUN rm -rf /$HOME/tmp

ENV JAVA_OPTS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"

EXPOSE 8080
EXPOSE 5005

CMD exec java $JAVA_OPTS -jar $HOME/deployments/quarkus-run.jar
