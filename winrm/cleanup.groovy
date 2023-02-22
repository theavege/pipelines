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
            credentialsId: 'b8cc5d5da274bddee03c425b6269837e',
            passwordVariable: 'pass',
            usernameVariable: 'user'
        )]) {
            final String SCRIPT = [
                'Remove-Item C:\\Users\\ivolrt\\AppData\\Local\\Microsoft\\Windows\\INetCache\\IE\\*.js',
                'echo "remove files in INetCache"'
            ].join(';')
            sh(
                returnStdout: false,
                script: '#!/usr/bin/env python' +
"""
from sys import stderr
from winrm import Session

s = Session('${params.TARGET}', auth=('${user}', '${pass}'), transport='ntlm')
stderr.write(str(s.run__cmd('powershell', ['-command', '${SCRIPT}']).std_out))
"""
            )
        }
        cleanWs()
    }
}
