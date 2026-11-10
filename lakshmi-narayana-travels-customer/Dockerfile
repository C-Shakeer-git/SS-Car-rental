# ============================================================
#  Lakshmi Narayana Travels — Customer Portal
#  Multi-stage Docker build  |  Java 21 + Spring Boot 3.3.4
# ============================================================

# ── Stage 1: Build ───────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

# Copy Maven wrapper first (better layer caching)
COPY .mvn/  .mvn/
COPY mvnw   pom.xml ./
RUN chmod +x ./mvnw

# Download all dependencies (cached layer unless pom.xml changes)
RUN ./mvnw dependency:go-offline -B --no-transfer-progress

# Copy source and build the fat JAR
COPY src ./src
RUN ./mvnw clean package -DskipTests -B --no-transfer-progress

# ── Stage 2: Runtime ─────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="Lakshmi Narayana Travels"
LABEL description="Customer Portal – Car Rental Booking"

WORKDIR /app

# Create the shared-data directory structure inside the container.
# On Render: mount a Persistent Disk at /app/shared-data
# On local docker-compose: use a named volume or bind-mount
RUN mkdir -p /app/shared-data/uploads

# Copy the seed data files (used when no external volume is mounted)
COPY shared-data/ /app/shared-data/

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# ── Environment variables (all overridable at runtime) ───────
# PORT is injected by Render automatically; fallback = 8081
ENV PORT=8081
ENV DATA_SHARED_DIR=/app/shared-data
ENV GOOGLE_SHEETS_ENABLED=false
ENV GOOGLE_SHEETS_SPREADSHEET_ID=
ENV GOOGLE_SHEETS_CREDENTIALS_PATH=/app/credentials.json
ENV JAVA_OPTS="-Xms256m -Xmx512m"

EXPOSE 8081

# Use exec form + shell expansion for env vars
ENTRYPOINT ["sh", "-c", \
  "java $JAVA_OPTS \
   -Dserver.port=$PORT \
   -Ddata.shared.dir=$DATA_SHARED_DIR \
   -Dgoogle.sheets.enabled=$GOOGLE_SHEETS_ENABLED \
   -Dgoogle.sheets.spreadsheetId=$GOOGLE_SHEETS_SPREADSHEET_ID \
   -Dgoogle.sheets.credentialsPath=$GOOGLE_SHEETS_CREDENTIALS_PATH \
   -jar /app/app.jar"]

