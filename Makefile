build:
	./mvnw clean install:install-file package

run:
	java -jar target/OntologyBrowser-*.jar