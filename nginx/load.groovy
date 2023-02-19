import groovy.json.JsonOutput
properties([
    disableConcurrentBuilds(),
    parameters([
        choice(name: 'PROTO', choices: ['http'], description: 'Протокол'),
        text(name: 'TARGETS', defaultValue: '127.0.0.1', description: 'Цели тестирования'),
        string(name: 'PORT', defaultValue: '80', trim: true, description: 'Порт'),
        string(name: 'CONCURRENT', defaultValue: '8', trim: true, description: 'Количество параллельных потоков'),
        text(name: 'ENDPOINTS', defaultValue: '/', description: 'Endpoints')
    ])
])
node('worker') {
    stage('Action') {
        cleanWs()
        writeFile(
            file: env.BUILD_TAG + '.json',
            text: JsonOutput.toJson([
                proto: params.PROTO,
                port: params.PORT.toInteger(),
                concurrent: params.CONCURRENT.toInteger(),
                targets: params.TARGETS.tokenize('\n').sort(),
                endpoints: params.ENDPOINTS.tokenize('\n').sort()
            ])
        )
        ansiColor('xterm') {
            sh "time rmeter --config '${env.BUILD_TAG}.json' --output '${env.BUILD_TAG}.xlsx'"
        }
        archiveArtifacts(
            artifacts: env.BUILD_TAG + '.xlsx',
            followSymlinks: false
        )
        cleanWs()
    }
}
