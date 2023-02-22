properties([
    disableConcurrentBuilds()
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
                'Remove-Item C:\\Users\\ivolrt\\AppData\\Local\\Microsoft\\Windows\\INetCache\\IE\\*.js'
            ].join(';')
            sh(
                returnStdout: false,
                script: '#!/usr/bin/env python' +
"""
from sys import stderr
from winrm import Session

s = Session("${env.JOB_NAME.tokenize('_')[-4..-1].join('.')}", auth=('${user}', '${pass}'), transport='ntlm')
stderr.write(str(s.run_cmd('powershell', ['-command', r'${SCRIPT}']).std_out))
"""
            )
        }
        cleanWs()
    }
}
