pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
				bat 'set MAVEN_HOME="C:\Users\B3AST\Devtools\apache-maven-3.6.3"'
                bat 'mvn -q clean install'
            }
        }
    }

} 
