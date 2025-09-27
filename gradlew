#!/bin/sh

#
# Copyright © 2015-2021 the original authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# SPDX-License-Identifier: Apache-2.0
#

set -e

APP_HOME=$(cd "$(dirname "$0")"; pwd -P)
WRAPPER_PROPERTIES="$APP_HOME/gradle/wrapper/gradle-wrapper.properties"
if [ ! -r "$WRAPPER_PROPERTIES" ]; then
    echo "ERROR: Cannot read $WRAPPER_PROPERTIES" >&2
    exit 1
fi

# shellcheck disable=SC2034
APP_BASE_NAME=$(basename "$0")

parse_property() {
    key=$1
    grep "^$key=" "$WRAPPER_PROPERTIES" | head -n 1 | cut -d= -f2- | sed 's#\\##g'
}

distributionUrl=$(parse_property distributionUrl)
if [ -z "$distributionUrl" ]; then
    echo "ERROR: distributionUrl is not set in gradle-wrapper.properties" >&2
    exit 1
fi

distributionBase=$(parse_property distributionBase)
[ -n "$distributionBase" ] || distributionBase=PROJECT

distributionPath=$(parse_property distributionPath)
[ -n "$distributionPath" ] || distributionPath=wrapper/dists

zipStoreBase=$(parse_property zipStoreBase)
[ -n "$zipStoreBase" ] || zipStoreBase=PROJECT

zipStorePath=$(parse_property zipStorePath)
[ -n "$zipStorePath" ] || zipStorePath=wrapper/dists

get_base_dir() {
    case "$1" in
        GRADLE_USER_HOME)
            printf '%s' "${GRADLE_USER_HOME:-$HOME/.gradle}"
            ;;
        PROJECT)
            printf '%s' "$APP_HOME"
            ;;
        *)
            printf '%s' "$APP_HOME"
            ;;
    esac
}

baseDir=$(get_base_dir "$distributionBase")
zipBaseDir=$(get_base_dir "$zipStoreBase")

distributionFileName=$(basename "$distributionUrl")
distributionName=${distributionFileName%.zip}

compute_hash() {
    if command -v sha256sum >/dev/null 2>&1; then
        printf '%s' "$1" | sha256sum | awk '{print $1}'
    elif command -v shasum >/dev/null 2>&1; then
        printf '%s' "$1" | shasum -a 256 | awk '{print $1}'
    elif command -v md5 >/dev/null 2>&1; then
        printf '%s' "$1" | md5 | awk '{print $1}'
    else
        printf '%s' "$1" | md5sum | awk '{print $1}'
    fi
}

distributionHash=$(compute_hash "$distributionUrl")

distDir="$baseDir/$distributionPath/$distributionName/$distributionHash"
zipDir="$zipBaseDir/$zipStorePath/$distributionName"
zipFile="$zipDir/$distributionHash.zip"

mkdir -p "$zipDir" "$distDir"

download() {
    url=$1
    target=$2
    tmpFile="$target.part"
    if command -v curl >/dev/null 2>&1; then
        if ! curl -fL "$url" -o "$tmpFile"; then
            rm -f "$tmpFile"
            return 1
        fi
    elif command -v wget >/dev/null 2>&1; then
        if ! wget -O "$tmpFile" "$url"; then
            rm -f "$tmpFile"
            return 1
        fi
    else
        echo "ERROR: Neither curl nor wget is available to download Gradle" >&2
        return 1
    fi
    mv "$tmpFile" "$target"
}

if [ ! -f "$zipFile" ]; then
    echo "Downloading Gradle distribution from $distributionUrl" >&2
    if ! download "$distributionUrl" "$zipFile"; then
        echo "ERROR: Failed to download Gradle distribution" >&2
        exit 1
    fi
fi

extract_distribution() {
    archive=$1
    destination=$2
    rm -rf "$destination"
    mkdir -p "$destination"
    if command -v unzip >/dev/null 2>&1; then
        unzip -q "$archive" -d "$destination"
    elif command -v jar >/dev/null 2>&1; then
        (cd "$destination" && jar xf "$archive")
    else
        echo "ERROR: Neither unzip nor jar commands are available to extract Gradle" >&2
        return 1
    fi
}

if ! find "$distDir" -maxdepth 1 -mindepth 1 -type d -name 'gradle-*' | grep -q .; then
    echo "Extracting Gradle distribution" >&2
    if ! extract_distribution "$zipFile" "$distDir"; then
        exit 1
    fi
fi

gradleHome=$(find "$distDir" -maxdepth 1 -mindepth 1 -type d -name 'gradle-*' | head -n 1)
if [ -z "$gradleHome" ]; then
    echo "ERROR: Unable to locate extracted Gradle distribution" >&2
    exit 1
fi

gradleCmd="$gradleHome/bin/gradle"
if [ ! -x "$gradleCmd" ]; then
    chmod +x "$gradleCmd" 2>/dev/null || true
fi

exec "$gradleCmd" "$@"
