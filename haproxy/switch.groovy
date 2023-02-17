import groovy.json.JsonOutput
properties([
    disableConcurrentBuilds(),
    parameters([
        choice(name: 'ENV', choices: ['test', 'prod'], description: 'Контур'),
        choice(name: 'TARGET', choices: ['api', 'elk', 'ivilive'], description: 'Сервис'),
        choice(name: 'ACTION', choices: ['off', 'on'], description: 'action')
    ])
])

final Map ENV = [
    'prod':'192.168.25.88',
    'test':'192.168.25.59'
]

node('worker') {
    stage('Action') {
        withCredentials([usernamePassword(
            credentialsId: '456b7016a916a4b178dd72b947c152b7',
            passwordVariable: 'pass',
            usernameVariable: 'user'
        )]) {
            withEnv(['SSHPASS=' + pass]) {
                sh(
                    returnStdout: false,
                    script: """
sshpass -e ssh -o 'StrictHostKeyChecking=no' ${user}@${ENV[params.ENV]} 'switch.sh --${params.TARGET} --${params.ACTION}'
"""
                )
            }
        }
        cleanWs()
    }
}
