# Document Server - Multi-Module Maven Project

## 📋 Overview

Progetto multi-modulo Maven che implementa un sistema completo di elaborazione e indicizzazione documenti con architettura a microservizi.

## 🏗️ Struttura Modulare

```
document-server/
├── pom.xml                         # Parent POM
├── orchestrator-service/           # Modulo 1: Orchestratore
│   ├── pom.xml
│   └── src/
├── extraction-service/             # Modulo 2: Estrazione testo
│   ├── pom.xml
│   └── src/
├── indexing-service/               # Modulo 3: Indicizzazione Elasticsearch
│   ├── pom.xml
│   └── src/
└── ui-service/                     # Modulo 4: UI Vaadin
    ├── pom.xml
    └── src/
```

## 📦 Moduli

### 1. **orchestrator-service** (Port 8080)
- Orchestratore principale del sistema
- Database H2 per tracking metadata
- MinIO client per storage file
- RabbitMQ publisher/consumer
- REST API per upload e monitoraggio

**Tecnologie:**
- Spring Boot Web
- Spring Data JPA + H2
- RabbitMQ
- MinIO
- Apache Camel

### 2. **extraction-service** (Port 8081)
- Estrazione testo da documenti con Apache Tika
- Supporto multi-formato (PDF, DOC, DOCX, XLS, XLSX, TXT, HTML, etc.)
- Chunking intelligente del testo
- RabbitMQ consumer/publisher

**Tecnologie:**
- Spring Boot
- Apache Tika 2.9.1
- RabbitMQ
- MinIO

### 3. **indexing-service** (Port 8082)
- Indicizzazione documenti su Elasticsearch
- REST API per ricerca full-text
- Highlighting risultati
- RabbitMQ consumer

**Tecnologie:**
- Spring Boot
- Elasticsearch
- RabbitMQ
- MinIO

### 4. **ui-service** (Port 8083)
- Interfaccia utente web con Vaadin 24
- Upload documenti
- Monitoraggio stato elaborazione
- Ricerca e visualizzazione risultati

**Tecnologie:**
- Spring Boot
- Vaadin 24.4.4
- TypeScript

## 🔧 Build

### Build completo (tutti i moduli)

```bash
cd /home/valyc-pc/lavoro/document-server/server
mvn clean install
```

### Build di un singolo modulo

```bash
# Orchestrator
cd orchestrator-service
mvn clean install

# Extraction
cd extraction-service
mvn clean install

# Indexing
cd indexing-service
mvn clean install

# UI
cd ui-service
mvn clean install
```

### Build con skip test

```bash
mvn clean install -DskipTests
```

## 🚀 Esecuzione

### Opzione 1: Tutti i servizi con Docker Compose (raccomandato)

```bash
cd /home/valyc-pc/lavoro/document-server
docker-compose up --build -d
```

### Opzione 2: Singoli moduli in locale

**Prerequisiti:**
- Java 17+
- Maven 3.8+
- MinIO in esecuzione (localhost:9000)
- RabbitMQ in esecuzione (localhost:5672)
- Elasticsearch in esecuzione (localhost:9200)

```bash
# Terminal 1 - Orchestrator
cd orchestrator-service
mvn spring-boot:run

# Terminal 2 - Extraction
cd extraction-service
mvn spring-boot:run

# Terminal 3 - Indexing
cd indexing-service
mvn spring-boot:run

# Terminal 4 - UI
cd ui-service
mvn spring-boot:run
```

## 🔄 Workflow

1. **Upload** → Client carica file su orchestrator-service
2. **Extraction** → extraction-service estrae testo con Tika
3. **Indexing** → indexing-service indicizza su Elasticsearch
4. **Search** → Ricerca full-text tramite indexing-service o UI

## 📡 Endpoints

### Orchestrator Service (8080)
```
POST   /api/documents/upload          # Upload file
GET    /api/documents/{id}/status     # Status elaborazione
GET    /api/documents/{id}            # Metadata file
GET    /api/documents/{id}/download   # Download originale
GET    /api/documents                 # Lista tutti i file
GET    /actuator/health               # Health check
```

### Indexing Service (8082)
```
GET    /api/search?q=query            # Ricerca full-text
GET    /api/documents                 # Lista file indicizzati
GET    /actuator/health               # Health check
```

### UI Service (8083)
```
GET    /                              # Interfaccia web Vaadin
GET    /actuator/health               # Health check
```

## 🧪 Test

```bash
# Test tutti i moduli
mvn test

# Test singolo modulo
cd orchestrator-service
mvn test

# Test con coverage
mvn verify
```

## 📊 Dipendenze Gestite

Il parent POM gestisce centralmente le versioni di:
- Spring Boot: 3.3.0
- Vaadin: 24.4.4
- MinIO: 8.5.7
- Apache Tika: 2.9.1
- Apache Camel: 4.4.0

## 🐳 Docker

Ogni modulo ha il proprio `Dockerfile` per containerizzazione:

```bash
# Build singola immagine
cd orchestrator-service
docker build -t orchestrator-service:1.0.0 .

# Build tutte le immagini
cd /home/valyc-pc/lavoro/document-server/server
./build.sh
```

## 🔒 Configurazione

Ogni modulo ha il proprio `application.properties` in `src/main/resources/`:

**orchestrator-service:**
```properties
server.port=8080
spring.datasource.url=jdbc:h2:file:./data/documents
minio.url=http://minio:9000
spring.rabbitmq.host=rabbitmq
```

**extraction-service:**
```properties
server.port=8081
minio.url=http://minio:9000
spring.rabbitmq.host=rabbitmq
```

**indexing-service:**
```properties
server.port=8082
spring.elasticsearch.uris=http://elasticsearch:9200
spring.rabbitmq.host=rabbitmq
```

**ui-service:**
```properties
server.port=8083
orchestrator.url=http://orchestrator-service:8080
```

## 📝 Note Importanti

1. **Parent POM**: Tutti i moduli ereditano da `document-server` parent
2. **Versioni Centralizzate**: Le versioni delle dipendenze sono gestite nel parent
3. **Relative Path**: I moduli referenziano il parent con `<relativePath>../pom.xml</relativePath>`
4. **Build Order**: Maven costruisce automaticamente i moduli nell'ordine corretto

## 🆘 Troubleshooting

### Errore: "Parent POM not found"
```bash
# Assicurati di essere nella directory corretta
cd /home/valyc-pc/lavoro/document-server/server
mvn clean install
```

### Errore: "Port already in use"
```bash
# Verifica processi attivi
netstat -tlnp | grep -E '8080|8081|8082|8083'

# Termina processo specifico
kill -9 <PID>
```

### Errore: "Cannot connect to MinIO/RabbitMQ/Elasticsearch"
```bash
# Verifica che i servizi infrastrutturali siano attivi
docker-compose ps

# Avvia l'infrastruttura
cd /home/valyc-pc/lavoro/document-server
docker-compose up -d minio rabbitmq elasticsearch
```

## 📚 Documentazione Completa

Per maggiori dettagli:
- **ARCHITECTURE.md**: Architettura completa del sistema
- **PROJECT_SUMMARY.md**: Riepilogo implementazione
- **README.md** (principale): Istruzioni Docker Compose

## 👥 Moduli e Porte

| Modulo | Porta | Descrizione |
|--------|-------|-------------|
| orchestrator-service | 8080 | Orchestratore principale |
| extraction-service | 8081 | Estrazione testo |
| indexing-service | 8082 | Indicizzazione e ricerca |
| ui-service | 8083 | Interfaccia web |

## ✅ Vantaggi Architettura Multi-Modulo

1. **Build Unificato**: Un solo comando per compilare tutto
2. **Gestione Centralizzata**: Dipendenze gestite nel parent POM
3. **Versionamento Coerente**: Versione unica per tutto il progetto
4. **IDE Friendly**: Progetti importabili come workspace Maven
5. **CI/CD Semplificato**: Pipeline unica per tutti i moduli
6. **Riuso Codice**: Possibilità di creare moduli comuni condivisi

---

**🎉 Progetto Multi-Modulo Pronto!**

Per avviare l'intero sistema: `mvn clean install && docker-compose up -d`
