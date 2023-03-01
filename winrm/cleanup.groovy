properties([
    disableConcurrentBuilds()
])

node('worker') {
    stage('Action') {
        cleanWs()
        switch (env.JOB_NAME.tokenize('_')[0]) {
            case 'cleanup':
                winrm(
                    'b8cc5d5da274bddee03c425b6269837e',
                    [
'Remove-Item C:\\Users\\ivolrt\\AppData\\Local\\Microsoft\\Windows\\INetCache\\IE\\*.js',
'hostname'
                    ].join(';')
                )
                break;
            case 'dataprovider' :
                winrm(
                    '9354ac3f628b97c899667da482e04e9a',
                    [
'Stop-ScheduledTask  -TaskName "Restart RT Loader"',
'Start-ScheduledTask -TaskName "Restart RT Loader"'
                    ].join(';')
                )
                break;
            case 'dataservice' :
                winrm(
                    '9354ac3f628b97c899667da482e04e9a',
                    [
'Stop-Service  -Name IVDataService -Force',
'Get-Process   -Name IVDataService | Stop-Process -Force',
'Sleep 10',
'Start-Service -Name IVDataService',
'hostname'
                    ].join(';')
                )
                break;
        }
        cleanWs()
    }
}

void winrm(String credId, String script) {
    withCredentials([usernamePassword(
        credentialsId: credId,
        passwordVariable: 'pass',
        usernameVariable: 'user'
    )]) {
        sh(
            returnStdout: false,
            script: '#!/usr/bin/env python' +
"""
from sys import stderr
from winrm import Session

s = Session("${env.JOB_NAME.tokenize('_')[-4..-1].join('.')}", auth=('${user}', '${pass}'), transport='ntlm')
stderr.write(str(s.run_cmd('powershell', ['-command', r'${script}']).std_out))
"""
        )
    }
}
