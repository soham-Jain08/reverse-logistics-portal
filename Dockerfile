# Dockerfile for Reverse Logistics Management Portal
# Base image: Eclipse Temurin OpenJDK 17 JRE on Alpine Linux (Lightweight, Secure, Minimal footprint)
FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="Reverse Logistics Team <dev@reverselogistics.com>" \
      version="1.0.0" \
      description="Automated Reverse Logistics Management Portal - Enterprise Container Edition"

# Configure production runtime environment
ENV APP_HOME=/app \
    SERVER_PORT=8080 \
    JAVA_OPTS="-Xms256m -Xmx512m -Djava.security.egd=file:/dev/./urandom"

# Create dedicated non-root application system user and group (Least Privilege Security)
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Set working directory
WORKDIR ${APP_HOME}

# Pre-create data directory for H2 persistent database with non-root permissions
RUN mkdir -p ${APP_HOME}/data && chown -R appuser:appgroup ${APP_HOME}

# Copy pre-built executable WAR artifact from target directory
ARG WAR_FILE=target/reverse-logistics-portal-0.0.1-SNAPSHOT.war
COPY ${WAR_FILE} ${APP_HOME}/app.war

# Set correct ownership for application artifact
RUN chown appuser:appgroup ${APP_HOME}/app.war

# Enforce non-root execution
USER appuser

# Expose container HTTP port
EXPOSE 8080

# Health check using Spring Boot Actuator endpoint
HEALTHCHECK --interval=20s --timeout=5s --start-period=30s --retries=3 \
  CMD wget -q --spider http://localhost:8080/actuator/health || exit 1

# Launch application with exec format to allow proper Unix signal handling (SIGTERM/SIGINT)
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -Dserver.port=$SERVER_PORT -jar app.war"]
