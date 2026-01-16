FROM debian:trixie AS base

#configure apt-get
ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get clean autoclean \
    && rm -rf /var/lib/apt/lists/* /var/lib/apt/* /var/log/apt/* \
    && rm -f /etc/apt/apt.conf.d/docker-clean \
    && echo 'Binary::apt::APT::Keep-Downloaded-Packages "true";' > /etc/apt/apt.conf.d/keep-cache

#make apt-get go through eatmydata for Speed:tm:
RUN --mount=type=cache,target=/var/cache/apt,sharing=locked --mount=type=cache,target=/var/lib/apt,sharing=locked --mount=type=tmpfs,target=/var/log/apt \
    apt-get update \
    && apt-get install -y eatmydata \
    && ln -s /usr/bin/eatmydata /usr/local/bin/apt-get \
    && ln -s /usr/bin/eatmydata /usr/local/bin/dpkg

# misc. build tools
RUN --mount=type=cache,target=/var/cache/apt,sharing=locked --mount=type=cache,target=/var/lib/apt,sharing=locked --mount=type=tmpfs,target=/var/log/apt \
    apt-get update \
    && apt-get install -y build-essential tar wget

# JDK (for jni.h)
RUN --mount=type=cache,target=/var/cache/apt,sharing=locked --mount=type=cache,target=/var/lib/apt,sharing=locked --mount=type=tmpfs,target=/var/log/apt \
    apt-get update \
    && apt-get install -y default-jdk-headless
ENV JAVA_HOME=/usr/lib/jvm/default-java

# all the GNU cross-compilers
RUN --mount=type=cache,target=/var/cache/apt,sharing=locked --mount=type=cache,target=/var/lib/apt,sharing=locked --mount=type=tmpfs,target=/var/log/apt \
    apt-get update \
    && apt-get install -y crossbuild-essential-arm64 crossbuild-essential-amd64 crossbuild-essential-armhf crossbuild-essential-i386 crossbuild-essential-ppc64el crossbuild-essential-s390x
