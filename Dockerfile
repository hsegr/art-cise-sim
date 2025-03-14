FROM azul/zulu-openjdk-alpine:11-jre
LABEL maintainer="JRC-CISE-DEV@ec.europa.eu"

RUN apk update &&\
    apk upgrade &&\
    apk add bash

ARG VERSION=1.4.5

COPY target/cise-sim-"$VERSION""-bin.tar.gz" /srv/cise-sim.tar.gz
RUN mkdir -p /srv/cise-simulator && tar xvfz /srv/cise-sim.tar.gz -C /srv/cise-simulator --strip-components 1
RUN rm /srv/cise-sim.tar.gz

# Create a group and user. NB: this user group should exists in the host machine and the volume must be writeble by it
#RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# All future commands should run as the appuser user
#USER appuser

EXPOSE 8200 8201

WORKDIR /srv/cise-simulator

VOLUME /srv/cise-simulator/conf /srv/cise-simulator/logs /srv/cise-simulator/msghistory /srv/cise-simulator/templates

ENTRYPOINT ["/srv/cise-simulator/sim"]
CMD ["run"]
