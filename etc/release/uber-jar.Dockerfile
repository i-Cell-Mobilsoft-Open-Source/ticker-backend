ARG ICELL_JAVA_JRE_BASE_IMAGE
################################################################################
# Default image customization
################################################################################
FROM ${ICELL_JAVA_JRE_BASE_IMAGE} AS base

ENV LANG='en_US.UTF-8' LANGUAGE='en_US:en'

################################################################################
# Download .jar
################################################################################
FROM icellmobilsoft/builder-nexus-download:1.7.0-SNAPSHOT as download

ARG POM_GROUP_ID
ARG POM_ARTIFACT_ID
ARG POM_VERSION
ARG POM_EXTENSION

ENV NEXUS_OBJECT_GROUP_ID=$POM_GROUP_ID
ENV NEXUS_OBJECT_ARTIFACT_ID=$POM_ARTIFACT_ID
ENV NEXUS_OBJECT_EXTENSION=$POM_EXTENSION
ENV NEXUS_OBJECT_VERSION=$POM_VERSION
ENV NEXUS_REPOSITORY_TYPE=central

RUN $HOME/script/sonatype-download.sh

################################################################################
# Create production image
################################################################################
FROM base AS prod

ARG POM_ARTIFACT_ID
ARG POM_VERSION
ARG POM_EXTENSION

ENV JAR_FILE=$POM_ARTIFACT_ID-$POM_VERSION.$POM_EXTENSION
ENV APP_DIR='quarkus-app'
ENV JAVA_OPTS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"

ENV TICKER_LOGSTASH_MODULE_ID=$POM_ARTIFACT_ID
ENV TICKER_LOGSTASH_MODULE_VERSION=$POM_VERSION
ENV TICKER_JSON_MODULE_ID=$POM_ARTIFACT_ID
ENV TICKER_JSON_MODULE_VERSION=$POM_VERSION

LABEL moduleName="$POM_ARTIFACT_ID"
LABEL moduleVersion="$POM_VERSION"

RUN mkdir $APP_DIR

COPY --from=download --chown=$SYSTEM_USER $HOME/download/$JAR_FILE $HOME/$APP_DIR/$JAR_FILE

EXPOSE 8080

CMD exec java $JAVA_OPTS -jar $HOME/$APP_DIR/$JAR_FILE