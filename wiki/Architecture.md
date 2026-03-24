# RAKCHA Architecture

This document provides a comprehensive overview of RAKCHA's system architecture, design patterns, and technical decisions.

---

## 📐 Architecture Overview

RAKCHA follows a **modern monorepo architecture** with multiple client applications sharing a unified backend. This approach provides code reuse, consistency, and simplified deployment while maintaining platform-specific optimizations.

---

## 🏗️ High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                      RAKCHA ECOSYSTEM                        │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │   DESKTOP    │  │    MOBILE    │  │     WEB      │      │
│  │   CLIENT     │  │    CLIENT    │  │   CLIENT     │      │
│  │  (JavaFX)    │  │  (Flutter)   │  │  (Symfony)   │      │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘      │
│         │                 │                  │               │
│         └─────────────────┼──────────────────┘               │
│                           │                                  │
│                    ┌──────▼───────┐                          │
│                    │   REST API    │                          │
│                    │  (Symfony)    │                          │
│                    │   Backend     │                          │
│                    └──────┬───────┘                          │
│                           │                                  │
│         ┌─────────────────┼─────────────────┐               │
│         │                 │                  │                │
│    ┌────▼────┐   ┌───────▼────────┐   ┌───▼─────┐          │
│    │Firebase  │   │   OAuth/Auth   │   │ Payment │          │
│    │ Firestore│   │   Providers    │   │Services │          │
│    │  (NoSQL) │   │(Google, MSFT)  │   │(Stripe) │          │
│    └──────────┘   └────────────────┘   └─────────┘          │
│         │                 │                  │                │
│    ┌────▼─────────────────▼──────────────────▼───┐          │
│    │      Shared Services & Utilities             │          │
│    │  (Events, Scheduling, Validation, Logging)   │          │
│    └──────────────────────────────────────────────┘          │
│                                                               │
└─────────────────────────────────────────────────────────────┘
```

---

## 🎯 Architectural Principles

### 1. **Separation of Concerns**
Each layer has a specific responsibility:
- **Presentation**: UI/UX and user interaction
- **Business Logic**: Domain rules and workflows
- **Data Access**: Database operations
- **Integration**: External services

### 2. **API-First Design**
All functionality is exposed via REST API, enabling:
- Multiple client applications
- Third-party integrations
- Future extensibility
- Testing isolation

### 3. **Domain-Driven Design (DDD)**
Business logic organized around domains:
- Cinema Management
- Content Catalog
- Booking & Reservations
- User Management
- E-Commerce

### 4. **Event-Driven Architecture**
Key operations trigger events:
- Booking confirmed → Send notification
- Payment received → Update inventory
- User registered → Send welcome email
- Showtime approaching → Reminder notification

### 5. **Microservices-Ready**
Current monolith can be split into microservices:
- Authentication Service
- Booking Service
- Payment Service
- Notification Service
- Content Service

---

## 📦 Component Architecture

### Desktop Application (JavaFX)

```
apps/desktop/
├── src/main/java/
│   ├── controllers/        # UI controllers (MVC pattern)
│   ├── models/            # Domain models
│   ├── services/          # Business logic
│   ├── utils/             # Utilities
│   ├── views/             # FXML views
│   └── Main.java          # Application entry
├── src/main/resources/
│   ├── fxml/              # UI layouts
│   ├── css/               # Stylesheets
│   └── images/            # Assets
└── pom.xml                # Maven dependencies
```

**Key Patterns**:
- **MVC**: Model-View-Controller for UI
- **Service Layer**: Business logic separation
- **Repository**: Data access abstraction
- **Observer**: Event handling

**Technology**:
- Java 21 LTS
- JavaFX for UI
- Maven for build
- Jackson for JSON
- OkHttp for API calls

---

### Mobile Application (Flutter)

```
apps/mobile/
├── lib/
│   ├── main.dart          # App entry point
│   ├── screens/           # UI screens
│   ├── widgets/           # Reusable widgets
│   ├── models/            # Data models
│   ├── services/          # API services
│   ├── providers/         # State management
│   ├── utils/             # Utilities
│   └── constants/         # App constants
├── test/                  # Unit & widget tests
└── pubspec.yaml           # Dependencies
```

**Key Patterns**:
- **BLoC/Provider**: State management
- **Repository**: Data access layer
- **Factory**: Object creation
- **Singleton**: Shared services

**Technology**:
- Flutter (latest stable)
- Dart language
- Provider for state
- http package for API
- Firebase integration

---

### Web Platform (Symfony)

```
apps/web/
├── src/
│   ├── Controller/        # HTTP controllers
│   ├── Entity/            # Doctrine entities
│   ├── Repository/        # Data repositories
│   ├── Service/           # Business services
│   ├── EventListener/     # Event listeners
│   ├── Security/          # Auth logic
│   └── Validator/         # Custom validators
├── templates/             # Twig templates
├── public/                # Web root
│   ├── css/
│   ├── js/
│   └── index.php
├── config/                # Configuration
├── migrations/            # Database migrations
└── composer.json          # Dependencies
```

**Key Patterns**:
- **MVC**: Symfony's architecture
- **Dependency Injection**: Service container
- **Repository**: Data access
- **Event Dispatcher**: Event handling
- **Voter**: Authorization logic

**Technology**:
- Symfony 6.4 LTS
- PHP 8.2+
- Doctrine ORM
- Twig templating
- Webpack Encore

---

## 🔌 API Architecture

### REST API Design

**Base URL**: `https://api.rakcha.com/v1/`

**Endpoints Structure**:
```
/auth
  POST /login
  POST /register
  POST /logout
  POST /refresh-token

/cinemas
  GET    /cinemas
  GET    /cinemas/{id}
  POST   /cinemas
  PUT    /cinemas/{id}
  DELETE /cinemas/{id}

/films
  GET    /films
  GET    /films/{id}
  POST   /films
  PUT    /films/{id}
  DELETE /films/{id}

/bookings
  GET    /bookings
  GET    /bookings/{id}
  POST   /bookings
  PUT    /bookings/{id}
  DELETE /bookings/{id}

/payments
  POST   /payments/create-intent
  POST   /payments/confirm
  GET    /payments/{id}
```

### API Patterns

**Request/Response Format**:
```json
// Request
{
  "data": {
    "type": "booking",
    "attributes": {
      "showtime_id": 123,
      "seats": ["A1", "A2"],
      "customer_email": "user@example.com"
    }
  }
}

// Response
{
  "data": {
    "type": "booking",
    "id": "456",
    "attributes": {
      "booking_id": 456,
      "status": "confirmed",
      "total_price": 25.00,
      "qr_code": "https://..."
    }
  },
  "meta": {
    "timestamp": "2026-03-24T12:00:00Z"
  }
}

// Error Response
{
  "errors": [
    {
      "status": "400",
      "code": "INVALID_SEATS",
      "title": "Invalid seat selection",
      "detail": "Seats A1 and A2 are already booked"
    }
  ]
}
```

### Authentication Flow

```
┌────────┐                    ┌────────┐                    ┌────────┐
│ Client │                    │  API   │                    │ OAuth  │
└───┬────┘                    └───┬────┘                    └───┬────┘
    │                             │                             │
    │  1. Login Request           │                             │
    ├────────────────────────────>│                             │
    │                             │                             │
    │                             │  2. Validate with Provider  │
    │                             ├────────────────────────────>│
    │                             │                             │
    │                             │  3. Provider Response       │
    │                             │<────────────────────────────┤
    │                             │                             │
    │  4. JWT Token + Refresh     │                             │
    │<────────────────────────────┤                             │
    │                             │                             │
    │  5. API Request (with JWT)  │                             │
    ├────────────────────────────>│                             │
    │                             │                             │
    │  6. Response                │                             │
    │<────────────────────────────┤                             │
```

---

## 💾 Data Architecture

### Database Schema

**Primary Database**: Firebase Firestore (NoSQL)

```
Collections:
├── users
│   ├── {userId}
│   │   ├── profile
│   │   ├── preferences
│   │   └── bookings [subcollection]
│
├── cinemas
│   ├── {cinemaId}
│   │   ├── details
│   │   ├── theaters [subcollection]
│   │   └── showtimes [subcollection]
│
├── films
│   ├── {filmId}
│   │   ├── metadata
│   │   ├── ratings [subcollection]
│   │   └── reviews [subcollection]
│
├── bookings
│   ├── {bookingId}
│   │   ├── details
│   │   ├── seats
│   │   └── payment
│
└── products
    ├── {productId}
        ├── details
        └── inventory
```

**Secondary Database**: MySQL (relational)

Used for complex queries and reporting:
- Analytics data
- Audit logs
- Complex joins
- Historical data

### Data Flow

```
┌──────────┐    1. Request    ┌──────────┐
│  Client  ├─────────────────>│   API    │
└──────────┘                  └────┬─────┘
                                   │
                      ┌────────────┼────────────┐
                      │            │            │
                 2. Validate  3. Business  4. Events
                      │         Logic         │
                      │            │            │
                 ┌────▼────┐  ┌───▼────┐  ┌───▼────┐
                 │ Security│  │Service │  │ Event  │
                 │ Layer   │  │ Layer  │  │Dispatch│
                 └────┬────┘  └───┬────┘  └───┬────┘
                      │            │            │
                      └────────────┼────────────┘
                                   │
                              5. Persist
                                   │
                    ┌──────────────┼──────────────┐
                    │              │              │
               ┌────▼────┐    ┌───▼────┐    ┌───▼────┐
               │Firebase │    │ MySQL  │    │ Cache  │
               │Firestore│    │  DB    │    │(Redis) │
               └─────────┘    └────────┘    └────────┘
```

---

## 🔐 Security Architecture

### Layers of Security

1. **Network Layer**
   - HTTPS/TLS encryption
   - Firewall rules
   - DDoS protection
   - Rate limiting

2. **Authentication Layer**
   - OAuth2 providers
   - JWT tokens
   - 2FA (TOTP)
   - Session management

3. **Authorization Layer**
   - Role-Based Access Control (RBAC)
   - Permission checks
   - Resource ownership validation
   - API key authentication

4. **Application Layer**
   - Input validation
   - Output encoding
   - CSRF protection
   - SQL injection prevention
   - XSS protection

5. **Data Layer**
   - Encryption at rest
   - Encrypted backups
   - Secure key management
   - PII protection

### Security Flow

```
Request → WAF → Rate Limiter → Auth Check → Authorization →
Input Validation → Business Logic → Data Access → Response
```

---

## 📊 Scalability Architecture

### Horizontal Scaling

```
                    ┌──────────────┐
                    │ Load Balancer│
                    └──────┬───────┘
           ┌───────────────┼───────────────┐
           │               │               │
      ┌────▼────┐     ┌───▼────┐     ┌───▼────┐
      │ API     │     │ API    │     │ API    │
      │Server 1 │     │Server 2│     │Server 3│
      └────┬────┘     └───┬────┘     └───┬────┘
           │              │              │
           └──────────────┼──────────────┘
                          │
                  ┌───────▼────────┐
                  │    Database    │
                  │   (Clustered)  │
                  └────────────────┘
```

### Caching Strategy

```
┌────────┐     ┌───────┐     ┌─────────┐     ┌──────────┐
│ Client │────>│ CDN   │────>│ App     │────>│ Database │
└────────┘     └───────┘     │ Cache   │     └──────────┘
                              │ (Redis) │
                              └─────────┘
```

**Caching Layers**:
1. **CDN**: Static assets (images, CSS, JS)
2. **Application Cache**: API responses, session data
3. **Database Cache**: Query results

---

## 🔄 Deployment Architecture

### Container Architecture

```
┌─────────────────────────────────────────┐
│           Docker Compose                │
├─────────────────────────────────────────┤
│                                          │
│  ┌──────────┐  ┌──────────┐  ┌────────┐│
│  │   Web    │  │   API    │  │ Worker ││
│  │Container │  │Container │  │Container││
│  └────┬─────┘  └────┬─────┘  └───┬────┘│
│       │             │            │     │
│       └─────────────┼────────────┘     │
│                     │                  │
│  ┌──────────────────▼─────────────┐   │
│  │      Shared Network             │   │
│  └────────────────┬────────────────┘   │
│                   │                    │
│  ┌────────────────▼────────────────┐   │
│  │   Volumes (persistent data)     │   │
│  └─────────────────────────────────┘   │
└─────────────────────────────────────────┘
```

---

## 🛠️ Development Architecture

### Monorepo Structure

```
rakcha/
├── apps/
│   ├── desktop/    # JavaFX application
│   ├── mobile/     # Flutter application
│   └── web/        # Symfony application
├── shared/
│   ├── api-spec/   # OpenAPI specs
│   ├── config/     # Shared config
│   └── database/   # DB schemas
├── scripts/        # Build scripts
├── Taskfile.yml    # Task automation
└── docker-compose.yaml
```

### CI/CD Pipeline

```
┌─────────┐
│  Push   │
│  Code   │
└────┬────┘
     │
     ▼
┌─────────────┐
│   Lint &    │
│   Format    │
└────┬────────┘
     │
     ▼
┌─────────────┐
│   Build     │
│   Apps      │
└────┬────────┘
     │
     ▼
┌─────────────┐
│   Run       │
│   Tests     │
└────┬────────┘
     │
     ▼
┌─────────────┐
│  Security   │
│  Scan       │
└────┬────────┘
     │
     ▼
┌─────────────┐
│   Deploy    │
│   (if main) │
└─────────────┘
```

---

## 📈 Performance Architecture

### Optimization Strategies

1. **Database**
   - Indexing on frequently queried fields
   - Query optimization
   - Connection pooling
   - Read replicas

2. **API**
   - Response compression
   - Pagination
   - Caching headers
   - ETags

3. **Frontend**
   - Lazy loading
   - Code splitting
   - Asset optimization
   - Service workers

4. **Infrastructure**
   - CDN for static assets
   - Load balancing
   - Auto-scaling
   - Monitoring

---

## 🔗 Related Documentation

- [Technology Stack](./Technology-Stack.md)
- [API Reference](./guides/API-Reference.md)
- [Deployment Guide](./guides/Deployment.md)
- [Security Policy](../SECURITY.md)

---

<div align="center">

**Understand the architecture, build better features!**

[⬆ Back to Wiki Home](./README.md)

</div>
