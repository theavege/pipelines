import groovy.json.JsonOutput

properties([
    disableConcurrentBuilds(),
    parameters([
        choice(name: 'PROTO', choices: ['http'], description: 'Протокол')
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
            withEnv(['SSHPASS=' + pass]) {
                sh(
                    returnStdout: false,
                    script: '#!/usr/bin/env python' +
"""
from winrm import Session
from sys import stderr

s = Session("192.168.25.206", auth=("${user}", "${pass}"), transport='ntlm')
stderr.write('{}\n'.format(str(s.run_ps("hostname").std_out)))
"""
                )
            }
        }
        cleanWs()
    }
}
