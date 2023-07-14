# Tetris

## Voraussetzungen

- Installiertes Docker & Docker-Compose oder lokal laufende MySQL-Datenbank
- Installiertes JDK 17 (jedes sollte funktionieren, Temurin ist vorzuziehen)
- Installiertes OpenSSL

## Vorbereitungen

Damit der Server laufen kann, muss ein RAS-KeyPair generiert werden. Auf Unix-ähnlichen Systemen geht das
folgendermaßen:

```bash
# Im Projekt-Root-Verzeichnis
./generate_keys.sh
```

Auf Windows müssen die Schritte aus dem Skript ggf. angepasst und manuell ausgeführt werden.

## Inbetriebnahme

### 1. Datenbank hochfahren

Diese Anweisungen beziehen sich auf Docker, für eine lokale MySQL-Instanz, siehe unten.

```bash
# Hochfahren (im Projekt-Root-Verzeichnis)
docker-compose up
```

```bash
# Runterfahren (im Projekt-Root-Verzeichnis)
docker-compose down
```

### 2. Server hochfahren

#### Unix-ähnliche

```bash
# Im Server-Verzeichnis
../mvnw spring-boot:run
```

#### Windows

```powershell
# Im Server-Verzeichnis
../mwnw.cmd spring-boot:run
```

## Lokal laufende MySQL-Datenbank

Die lokal laufende MySQL-Datenbank muss entsprechend der Angaben im Abschnitt `spring.datasource`
der `application.properties`-Datei unter `server/src/main/resources` eingerichtet werden um einen reibungslosen Ablauf
zu gewährleisten. Alternativ kann über eine separate `application-PROFILE_NAME.properties`-Datei die Konfiguration
überschrieben werden, sofern beim Start des Servers das passende Profil aktiviert wird.

## Persistierung für Profilbilder

Standardmäßig werden die Profilbilder nach dem Upload in ein temporäres Verzeichnis geladen, dass von Java verwaltet
wird. Damit die Bilder langfistig auf der Festplatte verbleiben und somit die Datenbank und das Dateisystem synchron
sind, muss ein passender Pfad angegeben werden. Dies kann z.B. über ein lokales Profil erledigt werden. Angenommen das
Profil heißt `local`, so braucht es unter `server/src/main/resources` eine Datei mit dem Namen
`application-local.properties`. Diese Datei sollte folgende Zeile enthalten: `files.location=PFAD_ZUM_BILDVERZEICHNIS`.
In der Folge ist darauf zu achten, dass der Server mit dem passenden Profil gestartet wird. Über die Kommandozeile auf 
Unix-ähnlichen Systemen geht das folgendermaßen (für Windows analog o.g. Anweisung adaptieren):

```bash
# Im Server-Verzeichnis
../mvnw clean spring-boot:run -Dactive.profile=local
```

