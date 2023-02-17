import groovy.json.JsonOutput

properties([
    disableConcurrentBuilds(),
    parameters([
        choice(name: 'TARGET', choices: ['127.0.0.1'], description: 'target')
    ])
])


node('worker') {
    stage('Action') {
        cleanWs()
        withCredentials([usernamePassword(
            credentialsId: '9354ac3f628b97c899667da482e04e9a',
            passwordVariable: 'pass',
            usernameVariable: 'user'
        )]) {
            sh(
                returnStdout: false,
                script: '#!/usr/bin/env python' +
"""
from winrm import Session
from sys import stderr

s = Session('${params.TARGET}', auth=('${user}', '${pass}'), transport='ntlm')
stderr.write(str(s.run_ps('hostname').std_out))
"""
            )
        }
        cleanWs()
    }
}
