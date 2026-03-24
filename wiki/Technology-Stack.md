# Technology Stack

Comprehensive overview of all technologies, frameworks, libraries, and tools used in RAKCHA.

---

## 📋 Table of Contents

- [Backend Technologies](#backend-technologies)
- [Desktop Application](#desktop-application)
- [Mobile Application](#mobile-application)
- [Web Platform](#web-platform)
- [Database & Storage](#database--storage)
- [Infrastructure & DevOps](#infrastructure--devops)
- [External Services](#external-services)
- [Development Tools](#development-tools)

---

## 🔧 Backend Technologies

### Symfony Framework
- **Version**: 6.4 LTS
- **Language**: PHP 8.2+
- **Purpose**: Main backend framework, REST API
- **Key Components**:
  - Symfony Console
  - Symfony Messenger
  - Symfony Mailer
  - Symfony Security
  - Event Dispatcher

### PHP Extensions
```
Required:
- pdo_mysql
- json
- mbstring
- xml
- curl
- openssl
- tokenizer
- ctype
- fileinfo

Recommended:
- opcache (performance)
- apcu (caching)
- redis (session/cache)
```

### Doctrine ORM
- **Version**: 2.x
- **Purpose**: Database abstraction layer
- **Features**:
  - Entity mapping
  - Query builder
  - Migrations
  - Fixtures

### API Platform (Optional)
- **Version**: 3.x
- **Purpose**: REST and GraphQL APIs
- **Features**:
  - Auto-generated documentation
  - Pagination
  - Filtering
  - Validation

---

## 🖥️ Desktop Application

### Java
- **Version**: Java 21 LTS (OpenJDK)
- **Why Java 21**:
  - Long-term support until 2029
  - Virtual threads (Project Loom)
  - Pattern matching
  - Record patterns
  - Latest language features

### JavaFX
- **Version**: 21+
- **Purpose**: Rich desktop UI framework
- **Components Used**:
  - FXML for layouts
  - CSS for styling
  - Scene Builder for design
  - Charts and graphs
  - TableView for data

### Build Tools
- **Maven**: 3.9+
  - Dependency management
  - Build automation
  - Plugin ecosystem
  - Multi-module support

### Key Libraries

#### HTTP Client
```xml
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>okhttp</artifactId>
    <version>4.12.0</version>
</dependency>
```

#### JSON Processing
```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.16.0</version>
</dependency>
```

#### Logging
```xml
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>2.0.9</version>
</dependency>
```

#### Testing
```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.0</version>
    <scope>test</scope>
</dependency>
```

---

## 📱 Mobile Application

### Flutter
- **Version**: Latest stable (3.x)
- **SDK**: Dart 3.x
- **Platforms**: iOS, Android, Web
- **Why Flutter**:
  - Single codebase
  - Native performance
  - Hot reload
  - Rich widget library
  - Strong community

### State Management
- **Provider**: Simple state management
- **Riverpod**: Advanced state management (alternative)
- **BLoC**: Business logic separation (alternative)

### Key Packages

#### HTTP & API
```yaml
dependencies:
  http: ^1.1.0
  dio: ^5.3.0  # Advanced HTTP client
```

#### State Management
```yaml
dependencies:
  provider: ^6.0.5
  riverpod: ^2.4.0
```

#### Firebase
```yaml
dependencies:
  firebase_core: ^2.20.0
  firebase_auth: ^4.12.0
  cloud_firestore: ^4.12.0
  firebase_messaging: ^14.7.0
```

#### UI Components
```yaml
dependencies:
  flutter_svg: ^2.0.9
  cached_network_image: ^3.3.0
  flutter_launcher_icons: ^0.13.1
  flutter_native_splash: ^2.3.5
```

#### Local Storage
```yaml
dependencies:
  shared_preferences: ^2.2.2
  hive: ^2.2.3
  sqflite: ^2.3.0
```

#### Navigation
```yaml
dependencies:
  go_router: ^12.1.0
```

#### Testing
```yaml
dev_dependencies:
  flutter_test:
    sdk: flutter
  mockito: ^5.4.3
  integration_test:
    sdk: flutter
```

---

## 🌐 Web Platform

### Frontend Stack

#### Symfony Components
- **Twig**: Template engine
- **Webpack Encore**: Asset management
- **Stimulus**: JavaScript framework
- **Turbo**: SPA-like navigation

#### JavaScript/TypeScript
- **Node.js**: 18+ LTS
- **npm**: Package manager
- **Webpack**: Module bundler
- **Babel**: JavaScript compiler

#### CSS Framework
```json
{
  "dependencies": {
    "bootstrap": "^5.3.0",
    "sass": "^1.69.0"
  }
}
```

#### Frontend Libraries
```json
{
  "dependencies": {
    "axios": "^1.6.0",
    "chart.js": "^4.4.0",
    "sweetalert2": "^11.10.0",
    "select2": "^4.1.0"
  }
}
```

### Backend Components

#### Symfony Bundles
```json
{
  "require": {
    "symfony/framework-bundle": "6.4.*",
    "symfony/console": "6.4.*",
    "symfony/dotenv": "6.4.*",
    "symfony/flex": "^2",
    "symfony/mailer": "6.4.*",
    "symfony/messenger": "6.4.*",
    "symfony/monolog-bundle": "^3.8",
    "symfony/security-bundle": "6.4.*",
    "symfony/twig-bundle": "6.4.*",
    "symfony/validator": "6.4.*"
  }
}
```

#### Authentication & Security
```json
{
  "require": {
    "lexik/jwt-authentication-bundle": "^2.19",
    "scheb/2fa-bundle": "^6.11",
    "knpuniversity/oauth2-client-bundle": "^2.18"
  }
}
```

#### API & Serialization
```json
{
  "require": {
    "jms/serializer-bundle": "^5.3",
    "nelmio/cors-bundle": "^2.3",
    "api-platform/core": "^3.2"
  }
}
```

---

## 💾 Database & Storage

### Primary Database
- **Firebase Firestore**
  - NoSQL document database
  - Real-time synchronization
  - Offline support
  - Automatic scaling
  - Security rules

### Secondary Database
- **MySQL**: 8.0+
  - Relational data
  - Complex queries
  - Reporting
  - Analytics

### Caching
- **Redis**: 7.0+ (optional)
  - Session storage
  - API response cache
  - Rate limiting
  - Queue management

### File Storage
- **Firebase Storage**
  - Images
  - Documents
  - User uploads
- **Local Storage** (development)
  - Temporary files
  - Cache

---

## 🚀 Infrastructure & DevOps

### Containerization
```yaml
# Docker
Version: 24.0+
Components:
  - Docker Engine
  - Docker Compose
  - Multi-stage builds

# Images Used
- php:8.2-fpm-alpine
- nginx:alpine
- mysql:8.0
- redis:7-alpine
- node:18-alpine
```

### CI/CD
```yaml
# GitHub Actions
Workflows:
  - Lint and format
  - Run tests
  - Build applications
  - Security scan
  - Deploy (on main)

Tools:
  - PHPStan (static analysis)
  - PHP-CS-Fixer (code style)
  - ESLint (JavaScript linting)
  - Psalm (PHP type checking)
```

### Hosting & Cloud
**Supported Platforms**:
- AWS (Elastic Beanstalk, ECS, EC2)
- Google Cloud (App Engine, Cloud Run, GCE)
- Azure (App Service, Container Instances)
- DigitalOcean (Droplets, App Platform)
- Heroku (Containers)
- Self-hosted (VPS)

---

## 🔌 External Services

### Authentication
- **Google OAuth2**
  - User authentication
  - Profile information
- **Microsoft OAuth2**
  - Azure AD integration
  - Enterprise SSO

### Payment Processing
- **Stripe**
  - Credit card processing
  - Subscription management
  - Webhook handling
- **PayPal**
  - Alternative payment method
  - International support

### Communication
- **Firebase Cloud Messaging (FCM)**
  - Push notifications
  - Mobile and web
- **SendGrid** / **Mailgun** (optional)
  - Transactional emails
  - Marketing campaigns

### Content Services
- **IMDB API**
  - Film metadata
  - Cast information
  - Ratings
- **YouTube API**
  - Trailer embedding
  - Video management
- **TMDb API** (alternative)
  - Movie database
  - Images and posters

### Analytics
- **Google Analytics**
  - User behavior
  - Traffic sources
- **Firebase Analytics**
  - Mobile app analytics
  - User engagement

---

## 🛠️ Development Tools

### IDEs & Editors
- **IntelliJ IDEA** (Desktop - Java)
- **Android Studio** (Mobile - Flutter)
- **VS Code** (Web, Mobile)
- **PhpStorm** (Web - PHP)

### Code Quality
```bash
# PHP
- PHPStan (level 8)
- Psalm (strict mode)
- PHP-CS-Fixer (PSR-12)
- PHPMD (mess detector)

# JavaScript
- ESLint
- Prettier
- TypeScript (optional)

# Java
- Checkstyle
- SpotBugs
- PMD
```

### Testing Tools
```bash
# Backend
- PHPUnit (unit tests)
- Behat (BDD tests)
- Codeception (acceptance tests)

# Desktop
- JUnit 5 (unit tests)
- TestFX (UI tests)
- Mockito (mocking)

# Mobile
- flutter_test (unit tests)
- integration_test (E2E tests)
- mockito (mocking)
```

### API Development
- **Postman** - API testing
- **Insomnia** - REST client
- **OpenAPI** - API specification
- **Swagger UI** - API documentation

### Database Tools
- **MySQL Workbench** - MySQL management
- **Firebase Console** - Firestore management
- **DBeaver** - Universal database tool
- **Redis Commander** - Redis GUI

### Version Control
- **Git** - Version control
- **GitHub** - Repository hosting
- **Git Flow** - Branching strategy

### Task Automation
```yaml
# Taskfile.yml
task:
  version: '3'

tasks:
  desktop:build:
    cmds:
      - cd apps/desktop && mvn clean install

  mobile:run:
    cmds:
      - cd apps/mobile && flutter run

  web:serve:
    cmds:
      - cd apps/web && symfony server:start
```

---

## 📊 Technology Decisions

### Why These Technologies?

#### Backend: Symfony
- ✅ Mature and stable (LTS support)
- ✅ Excellent documentation
- ✅ Large ecosystem
- ✅ Enterprise-ready
- ✅ PHP 8.2 features

#### Desktop: JavaFX
- ✅ Cross-platform (Windows, Mac, Linux)
- ✅ Rich UI components
- ✅ Java ecosystem
- ✅ Long-term support
- ✅ Performance

#### Mobile: Flutter
- ✅ Single codebase for iOS & Android
- ✅ Native performance
- ✅ Hot reload for fast development
- ✅ Rich widget library
- ✅ Growing community

#### Database: Firebase + MySQL
- ✅ Firebase for real-time features
- ✅ MySQL for complex queries
- ✅ Best of both worlds
- ✅ Scalable architecture

---

## 🔄 Version Matrix

### Current Versions (2026)

| Component | Minimum | Recommended | Production |
|-----------|---------|-------------|------------|
| Java | 21 | 21 LTS | 21 LTS |
| PHP | 8.2 | 8.2 | 8.2 |
| Node.js | 18 | 20 LTS | 20 LTS |
| Symfony | 6.4 | 6.4 LTS | 6.4 LTS |
| Flutter | 3.16 | 3.19+ | Latest stable |
| MySQL | 8.0 | 8.0 | 8.0 |
| Docker | 24.0 | 24.0+ | 24.0+ |

---

## 📚 Learning Resources

### Official Documentation
- [Symfony Docs](https://symfony.com/doc/current/index.html)
- [JavaFX Docs](https://openjfx.io/)
- [Flutter Docs](https://flutter.dev/docs)
- [Firebase Docs](https://firebase.google.com/docs)
- [Doctrine Docs](https://www.doctrine-project.org/)

### Tutorials
- Symfony: [SymfonyCasts](https://symfonycasts.com/)
- Java: [Baeldung](https://www.baeldung.com/)
- Flutter: [Flutter Codelabs](https://docs.flutter.dev/codelabs)

---

## 🔗 Related Documentation

- [Architecture Overview](./Architecture.md)
- [Development Setup](./guides/Development-Setup.md)
- [API Reference](./guides/API-Reference.md)
- [Deployment Guide](./guides/Deployment.md)

---

<div align="center">

**Build with the best technologies!**

[⬆ Back to Wiki Home](./README.md)

</div>
