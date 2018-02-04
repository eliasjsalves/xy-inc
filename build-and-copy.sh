mvn clean install
if [ ! $? -eq 0 ]; then
    exit 1
fi
cp target/xy-inc-0.0.1-SNAPSHOT.war /opt/wildfly-11.0.0.Final/standalone/deployments/
