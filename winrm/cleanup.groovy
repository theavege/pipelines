import groovy.json.JsonOutput

properties([
    disableConcurrentBuilds(),
    parameters([
        choice(name: 'TARGET', choices: [
            '127.0.0.1',
            '192.168.25.209',
            '192.168.25.222'
        ].sort(), description: 'target')
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
            final String SCRIPT = [
                'Remove-Item C:\\Users\\ivolrt\\AppData\\Local\\Microsoft\\Windows\\INetCache\\IE\\*.js'
            ].join(';')
            sh(
                returnStdout: false,
                script: '#!/usr/bin/env python' +
"""
from sys import stderr
from winrm import Session

s = Session('${params.TARGET}', auth=('${user}', '${pass}'), transport='ntlm')
stderr.write(str(s.run__cmd('powershell', ['-command', ${SCRIPT}]).std_out))
"""
            )
        }
        cleanWs()
    }
}
