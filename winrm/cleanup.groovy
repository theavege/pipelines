import org.apache.commons.codec.digest.DigestUtils

properties([
    disableConcurrentBuilds()
])

node('worker') {
    stage('Action') {
        cleanWs()
        switch (env.JOB_BASE_NAME.tokenize('_')[0]) {
            case 'cleanup':
                winrm(
                    'b8cc5d5da274bddee03c425b6269837e',
                    [
'Remove-Item "C:\\Users\\ivolrt\\AppData\\Local\\Microsoft\\Windows\\INetCache\\IE\\*.js"',
'Write-Host  "${env:COMPUTERNAME}"'
                    ].join(';')
                )
                break;
            case 'dataprovider' :
                withCredentials([usernamePassword(
                    credentialsId:    DigestUtils.md5Hex(env.JOB_BASE_NAME),
                    passwordVariable: 'pass',
                    usernameVariable: 'user'
                )]) {
                    winrm(
                        '9354ac3f628b97c899667da482e04e9a',
                        [
'Get-Process   -Name     "DataProvider"',
'Stop-Process  -Name     "DataProvider" -Force',
'Get-Process   -Name     "RTEngine"',
'Stop-Process  -Name     "RTEngine" -Force',
'Get-Process   -Name     "ActivExecutive"',
'Stop-Process  -Name     "ActivExecutive" -Force',
'Get-Service   -Name     "ActivWorkstationServiceDaemon"',
'Stop-Service  -Name     "ActivWorkstationServiceDaemon" -Force',
"Start-Service -Name     \"ActivExecutive\" -ArgumentList \"-U${user} -P${pass}\"",
"Start-Process -FilePath \"\${env:PROGRAMFILES(X86)}\\ActivWorkstation\\bin\\ActivExecutive.exe\"",
'Start-Sleep   -Seconds  10',
'Start-Process -FilePath "C:\\RT\\Services\\RTLoader\\DataProvider.exe"',
"Write-Host    \"\${env:COMPUTERNAME}\""
                        ].join(';')
                    )
                }
                break;
            case 'dataservice' :
                winrm(
                    '9354ac3f628b97c899667da482e04e9a',
                    [
'Get-Service   -Name    "IVDataService"',
'Stop-Service  -Name    "IVDataService" -Force',
'Get-Process   -Name    "IVDataService"',
'Stop-Process  -Name    "IVDataService" -Force',
'Start-Sleep   -Seconds 10',
'Start-Service -Name    "IVDataService"',
'Write-Host    "${env:COMPUTERNAME}"'
                    ].join(';')
                )
                break;
        }
        cleanWs()
    }
}

void winrm(String credId, String script) {
    withCredentials([usernamePassword(
        credentialsId:    credId,
        passwordVariable: 'pass',
        usernameVariable: 'user'
    )]) {
        sh(
            returnStdout: false,
            script: '#!/usr/bin/env python' +
"""
from sys import stderr
from winrm import Session

s = Session("${env.JOB_BASE_NAME.tokenize('_')[-4..-1].join('.')}", auth=('${user}', '${pass}'), transport='ntlm')
stderr.write(str(s.run_cmd('powershell', ['-command', r'${script}']).std_out))
"""
        )
    }
}
