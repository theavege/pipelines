import groovy.json.JsonOutput

properties([
    disableConcurrentBuilds(),
    parameters([
        choice(name: 'TARGET', choices: [
            '127.0.0.1',
            '192.168.25.202',
            '192.168.25.203',
            '192.168.25.204',
            '192.168.25.205',
            '192.168.25.206',
            '192.168.25.66',
            '192.168.25.67',
            '192.168.25.68',
            '192.168.25.40',
            '192.168.25.42'
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
                'Stop-ScheduledTask -TaskName "Restart RT Loader"'
                'Start-ScheduledTask -TaskName "Restart RT Loader"'
            ].join('; ')
            sh(
                returnStdout: false,
                script: '#!/usr/bin/env python' +
"""
from winrm import Session
from sys import stderr

s = Session('${params.TARGET}', auth=('${user}', '${pass}'), transport='ntlm')
stderr.write(str(s.run_ps(${SCRIPT}).std_out))
"""
            )
        }
        cleanWs()
    }
}
