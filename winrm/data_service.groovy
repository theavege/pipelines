import groovy.json.JsonOutput

properties([
    disableConcurrentBuilds(),
    parameters([
        choice(name: 'TARGET', choices: [
            '127.0.0.1',
            '192.168.25.220',
            '192.168.25.114',
            '192.168.25.102'
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
                'Stop-Service -Name IVDataService -Force'
                'Get-Process -Name IVDataService | Stop-Process -Force'
                'Sleep 10'
                'Start-Service -Name IVDataService'
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
