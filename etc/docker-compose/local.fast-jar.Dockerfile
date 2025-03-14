ARG ICELL_JAVA_JRE_BASE_IMAGE

FROM ${ICELL_JAVA_JRE_BASE_IMAGE}

ENV JAVA_OPTS="-XshowSettings:vm -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=*:5005 -Djava.util.logging.manager=org.jboss.logmanager.LogManager -Dquarkus.http.host=0.0.0.0"

ARG SERVICE
ARG POM_ARTIFACT_ID
ARG POM_VERSION

ENV SERVICE=${SERVICE}
ENV TICKER_JSON_MODULE_ID=$POM_ARTIFACT_ID
ENV TICKER_JSON_MODULE_VERSION=$POM_VERSION

COPY --chown=$SYSTEM_USER:$SYSTEM_USER_GROUP ticker-services/$SERVICE/target/quarkus-app/lib $HOME/deployments/lib/
COPY --chown=$SYSTEM_USER:$SYSTEM_USER_GROUP ticker-services/$SERVICE/target/quarkus-app/*.jar $HOME/deployments/
COPY --chown=$SYSTEM_USER:$SYSTEM_USER_GROUP ticker-services/$SERVICE/target/quarkus-app/app $HOME/deployments/app/
COPY --chown=$SYSTEM_USER:$SYSTEM_USER_GROUP ticker-services/$SERVICE/target/quarkus-app/quarkus $HOME/deployments/quarkus/

EXPOSE 8080
EXPOSE 5005

CMD exec java $JAVA_OPTS -jar $HOME/deployments/quarkus-run.jar
