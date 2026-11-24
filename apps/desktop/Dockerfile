FROM amd64/ubuntu:24.04

# Keep apt operations in a single layer with no recommended packages.
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        ca-certificates \
        curl \
        wget \
        gnupg \
        software-properties-common \
        xorg \
        libgtk-3-0 \
        xdg-utils \
        maven \
        locales \
    && install -d -m 0755 /etc/apt/keyrings \
    && wget -qO - https://packages.adoptium.net/artifactory/api/gpg/key/public \
        | gpg --dearmor -o /etc/apt/keyrings/adoptium.gpg \
    && echo "deb [signed-by=/etc/apt/keyrings/adoptium.gpg] https://packages.adoptium.net/artifactory/deb noble main" \
        > /etc/apt/sources.list.d/adoptium.list \
    && apt-get update \
    && apt-get install -y --no-install-recommends temurin-21-jdk \
    && locale-gen en_US.UTF-8 \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

ENV JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64
ENV PATH=$JAVA_HOME/bin:$PATH
ENV LANG=en_US.UTF-8
ENV LANGUAGE=en_US:en
ENV LC_ALL=en_US.UTF-8
ENV JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8"

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B || true

COPY src/ src/
COPY .env.example .env

RUN mvn clean compile -DskipTests \
    && useradd --create-home --shell /bin/bash rakcha \
    && chown -R rakcha:rakcha /app

USER rakcha

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/ || exit 1

CMD ["mvn", "jpro:run", "-Dfile.encoding=UTF-8", "-Dsun.jnu.encoding=UTF-8", "-Duser.language=en", "-Duser.country=US", "-Djpro.openURLOnStartup=false"]
