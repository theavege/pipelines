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
    'PROD':'192.168.25.88',
    'TEST':'192.168.25.59'
]
node('worker') {
    stage('Action') {
        sh(
            returnStdout: false,
            script: "switch.sh --${params.BACKEND} --${params.SWITCH}"
        )
        cleanWs()
    }
}
