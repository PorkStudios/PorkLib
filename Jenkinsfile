String getDiscordMessage() {
    def msg = "**Status:** " + currentBuild.currentResult.toLowerCase() + "\n**Branch:** ${BRANCH_NAME}\n**Changes:**"
    if (!currentBuild.changeSets.isEmpty()) {
        currentBuild.changeSets.first().getLogs().any {
            def line = "\n- `" + it.getCommitId().substring(0, 8) + "` *" + it.getComment().split("\n")[0].replaceAll('(?<!\\\\)([_*~`])', '\\\\$1') + "*"
            if (msg.length() + line.length() <= 2000)   {
                msg += line
                return
            } else {
                return true
            }
        }
    } else {
        msg += "\n- no changes"
    }

    return msg
}

/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

pipeline {
    agent any
    tools {
        git "Default"
        jdk "jdk8"
    }
    options {
        buildDiscarder(logRotator(artifactNumToKeepStr: '5'))
    }
    stages {
        stage("Natives") {
            steps {
                sh "bash gradlew compileNatives"
            }
        }
        stage("Build") {
            steps {
                sh "bash gradlew build -x test -x publish"
            }
        }
        stage("Test") {
            when {
                anyOf {
                    branch "master"
                    branch "development"
                }
            }
            steps {
                sh "bash gradlew test"
            }
            post {
                success {
                    junit "**/build/test-results/**/*.xml"
                }
            }
        }
        stage("Publish") {
            when {
                anyOf {
                    branch "master"
                    branch "development"
                }
            }
            steps {
                sh "bash gradlew publish -x test -x publishToMavenLocal"
            }
        }
    }

    post {
        always {
            sh "bash gradlew --stop"
            deleteDir()

            withCredentials([string(credentialsId: "daporkchop_discord_webhook", variable: "discordWebhook")]) {
                discordSend thumbnail: "https://cloud.daporkchop.net/static/img/logo/128/minecraft-porkchop_raw.png",
                        result: currentBuild.currentResult,
                        description: getDiscordMessage(),
                        link: env.BUILD_URL,
                        title: "PorkLib/${BRANCH_NAME} #${BUILD_NUMBER}",
                        webhookURL: "${discordWebhook}"
            }
        }
    }
}
