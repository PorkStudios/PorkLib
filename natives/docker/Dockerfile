# syntax=docker/dockerfile:1.4

# 1.2: RUN --mount
# 1.4: COPY --link

FROM debian:trixie AS base

# configure apt-get!
# prevent packages from blocking installation with interaction requests
# also, make apt keep downloaded package files locally so that they get kept in the build cache
ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get clean autoclean \
    && rm -rf /var/lib/apt/lists/* /var/lib/apt/* /var/log/apt/* \
    && rm -f /etc/apt/apt.conf.d/docker-clean \
    && echo 'Binary::apt::APT::Keep-Downloaded-Packages "true";' > /etc/apt/apt.conf.d/keep-cache

# eatmydata (speed up build times)
RUN --mount=type=cache,target=/var/cache/apt,sharing=locked --mount=type=cache,target=/var/lib/apt,sharing=locked --mount=type=tmpfs,target=/var/log/apt \
    apt-get update \
    && apt-get install --yes --no-install-recommends \
        eatmydata

# misc. build tools
# - build-essential: contains tools like 'as' which are needed during compilation
# - clang, lld: needed by the msvc cross-compiler
# - tar, wget: needed for downloading native library source
RUN --mount=type=cache,target=/var/cache/apt,sharing=locked --mount=type=cache,target=/var/lib/apt,sharing=locked --mount=type=tmpfs,target=/var/log/apt \
    eatmydata apt-get update \
    && eatmydata apt-get install --yes --no-install-recommends \
        build-essential \
        clang \
        lld \
        tar \
        wget

#
# MSVC SETUP
# https://github.com/mstorsjo/msvc-wine
#
FROM base AS msvc-wine

# msvc-wine deps
# also git because we'll need it to fetch the repo stuff
RUN --mount=type=cache,target=/var/cache/apt,sharing=locked --mount=type=cache,target=/var/lib/apt,sharing=locked --mount=type=tmpfs,target=/var/log/apt \
    eatmydata apt-get update \
    && eatmydata apt-get install --yes --no-install-recommends \
        git \
        wine64 \
        python3 \
        msitools \
        ca-certificates

# MSVC 17.14 is compatible with clang 19 (the version shipped in debian trixie)
# Found by searching for "clang" in https://github.com/microsoft/STL/commits/main/stl/inc/yvals_core.h
RUN --mount=type=cache,target=/var/cache/apt,sharing=locked --mount=type=cache,target=/var/lib/apt,sharing=locked --mount=type=tmpfs,target=/var/log/apt \
    eatmydata git clone https://github.com/mstorsjo/msvc-wine \
    && eatmydata ./msvc-wine/vsdownload.py \
        --accept-license \
        --msvc-version 17.14 \
        --dest /opt/msvc \
    && eatmydata ./msvc-wine/install.sh /opt/msvc \
    && cp -a ./msvc-wine/msvcenv-native.sh /opt/msvc \
    && rm -rf ./msvc-wine/

#
# FINAL OUTPUT IMAGE
#
FROM base AS build

# JDK (for jni.h)
RUN --mount=type=cache,target=/var/cache/apt,sharing=locked --mount=type=cache,target=/var/lib/apt,sharing=locked --mount=type=tmpfs,target=/var/log/apt \
    eatmydata apt-get update \
    && eatmydata apt-get install --yes --no-install-recommends \
        default-jdk-headless
ENV JAVA_HOME=/usr/lib/jvm/default-java

# all the GNU cross-compilers
RUN --mount=type=cache,target=/var/cache/apt,sharing=locked --mount=type=cache,target=/var/lib/apt,sharing=locked --mount=type=tmpfs,target=/var/log/apt \
    eatmydata apt-get update \
    && eatmydata apt-get install --yes --no-install-recommends \
        crossbuild-essential-arm64 \
        crossbuild-essential-amd64 \
        crossbuild-essential-armhf \
        crossbuild-essential-i386 \
        crossbuild-essential-ppc64el \
        crossbuild-essential-s390x

# add MSVC cross-compiler infrastructure
COPY --link --from=msvc-wine /opt/msvc /opt/msvc
COPY msvc-wine-scripts/bin/* /usr/local/bin
