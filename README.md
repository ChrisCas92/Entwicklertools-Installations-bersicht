# Entwicklertools-Installationsübersicht

JavaFX-Anwendung zur Erfassung der benötigten Software/Anwendungen inkl. Versionen, Lizenzpflicht und Bedarf.

## Anforderungen

- Java 17
- Maven 3.9+

## Build (FatJar)

```bash
mvn package
```

Das FatJar liegt danach unter `target/installationsuebersicht-0.1.0-SNAPSHOT-shaded.jar`.

## Start

```bash
java -jar target/installationsuebersicht-0.1.0-SNAPSHOT-shaded.jar
```

## Lokale Speicherung

Die Eingaben werden pro Windowskennung unter `~/.entwicklertools-installationsuebersicht/<windowskennung>.csv` gespeichert und beim Start automatisch geladen.

## Softwarekatalog

Die vorgefertigte Liste der Software/Anwendungen wird aus `src/main/resources/software/software-list.xml` geladen.
