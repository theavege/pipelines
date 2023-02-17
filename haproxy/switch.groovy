import groovy.json.JsonOutput
properties([
    disableConcurrentBuilds(),
    parameters([
        choice(name: 'ENV', choices: ['test', 'prod'], description: 'Контур'),
        choice(name: 'TARGET', choices: ['site', 'api', 'elastic'], description: 'Сервис'),
        choice(name: 'SWITCH', choices: ['off', 'on'], description: 'action')
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
sshpass -e ssh ${user}@${ENV[params.ENV]} 'switch.sh --${params.BACKEND} --${params.SWITCH}'
"""
                )
            }
        }
        cleanWs()
    }
}
