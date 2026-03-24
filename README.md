<div align="center">

# 🎬 RAKCHA

### The All-in-One Entertainment Management Ecosystem

[![License](https://img.shields.io/badge/license-Commercial-red.svg)](#-license)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Flutter](https://img.shields.io/badge/Flutter-Latest-blue.svg)](https://flutter.dev)
[![Symfony](https://img.shields.io/badge/Symfony-6.4-black.svg)](https://symfony.com)
[![Node.js](https://img.shields.io/badge/Node.js-Runtime-green.svg)](https://nodejs.org)
[![Firebase](https://img.shields.io/badge/Firebase-Database-FFCA28.svg)](https://firebase.google.com)
[![Contributing](https://img.shields.io/badge/PRs-Welcome-brightgreen.svg)](#-contributing)

**Production-ready cinema management, film streaming, and e-commerce platform** — Available on Desktop, Mobile, and Web.

[🚀 Get Started](#-quick-start) • [✨ Features](#-features) • [🏗️ Architecture](#-architecture) • [📖 Documentation](#-documentation) • [💬 Community](#-community)

</div>

---

## 🎯 What is RAKCHA?

RAKCHA is a **comprehensive entertainment management platform** that powers cinema operations, film & series cataloging, online reservations, and e-commerce—all from a single, unified ecosystem. 

Think of it as **Shopify + Ticketmaster + Netflix Admin** rolled into one powerful, scalable platform. Whether you're a cinema owner, entertainment entrepreneur, or platform operator, RAKCHA provides everything you need to manage complex entertainment operations at scale.

### Why RAKCHA?

- ✅ **Full-Featured** — Cinema management + streaming + e-commerce + user administration in one place
- ✅ **Multi-Platform** — Native desktop, mobile, and web applications sharing a unified backend
- ✅ **Production-Ready** — Battle-tested architecture with real-time notifications, OAuth2, 2FA, and payment processing
- ✅ **Scalable** — Built on Firebase, Symfony, and microservices ready
- ✅ **Enterprise-Grade** — Role-based access control, audit logging, and analytics
- ✅ **Developer-Friendly** — Well-organized monorepo with clear documentation and examples

---

## ⭐ Features

### 🎭 Cinema Management
- **Multi-Cinema Operations** — Manage multiple cinemas, theaters, and locations
- **Theater Seating** — Complex seating arrangements with real-time seat availability
- **Advanced Scheduling** — Flexible showtime scheduling with capacity management
- **Online Reservations** — Complete booking system with seat selection and payment
- **Analytics Dashboard** — Real-time cinema performance metrics and reporting

### 🎬 Film & Series Catalog
- **Content Management** — Extensive film and TV series database
- **IMDB Integration** — Auto-populate movie data from IMDB API
- **YouTube Integration** — Embedded trailers and promotional videos
- **Smart Categorization** — Genre-based organization with advanced filtering
- **Ratings & Reviews** — User-generated reviews with sentiment analysis
- **Intelligent Recommendations** — AI-powered content suggestions based on user behavior

### 🛒 E-Commerce Platform
- **Marketplace** — Full online store for cinema merchandise and products
- **Smart Cart** — Real-time inventory management and cart operations
- **Payment Processing** — Stripe & PayPal integration for secure transactions
- **Order Management** — Complete order lifecycle with tracking
- **Promotions** — Discount codes, coupons, and campaign management
- **Digital Products** — Support for digital content and instant delivery

### 👥 User & Access Management
- **Multi-Provider OAuth** — Google, Microsoft, and social authentication
- **Enhanced Security** — TOTP-based 2FA with trusted device management
- **Role-Based Access** — Granular permissions for admin, moderator, and user roles
- **Email Verification** — Automated email verification with expiry policies
- **Password Recovery** — Secure password reset workflows
- **User Analytics** — Detailed user activity and engagement metrics

### 🔔 Real-Time Features
- **Live Notifications** — Push notifications for bookings, offers, and updates
- **WebSocket Integration** — Real-time seat availability and pricing updates
- **Event Streaming** — Live event updates and announcements
- **Activity Feed** — Personalized user activity streams

### 🚀 Advanced Capabilities
- **QR Code Generation** — Dynamic QR codes for tickets and promotions
- **Image Processing** — Optimized image handling and CDN integration
- **API-First Design** — RESTful and GraphQL APIs for third-party integrations
- **CI/CD Pipeline** — Automated testing, building, and deployment
- **Docker Support** — Container-ready deployment for cloud platforms

---

## 🏗️ Architecture

RAKCHA is built as a **modern monorepo** with specialized applications for different platforms, all sharing a unified backend ecosystem.

```
┌─────────────────────────────────────────────────────────┐
│                    RAKCHA ECOSYSTEM                      │
├─────────────────────────────────────────────────────────┤
│                                                           │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │   DESKTOP    │  │    MOBILE    │  │     WEB      │  │
│  │   (JavaFX)   │  │  (Flutter)   │  │  (Symfony)   │  │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘  │
│         │                 │                  │           │
│         └─────────────────┼──────────────────┘           │
│                           │                              │
│                    ┌──────▼───────┐                      │
│                    │   REST API    │                      │
│                    │   (Symfony)   │                      │
│                    └──────┬───────┘                      │
│                           │                              │
│         ┌─────────────────┼─────────────────┐           │
│         │                 │                  │            │
│    ┌────▼────┐   ┌───────▼────────┐   ┌───▼─────┐      │
│    │Firebase  │   │   OAuth/Auth   │   │  Stripe  │      │
│    │ Database │   │   (Providers)   │   │ & PayPal │      │
│    └──────────┘   └────────────────┘   └──────────┘      │
│         │                                   │             │
│    ┌────▼────────────────────────────────▼──┐           │
│    │      Shared Services & Utilities        │           │
│    │   (Validation, Events, Scheduling)     │           │
│    └─────────────────────────────────────────┘           │
│                                                           │
└─────────────────────────────────────────────────────────┘
```

### Technology Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| **Desktop Client** | Java, JavaFX, Maven | 21 LTS |
| **Mobile Client** | Flutter, Dart, Firebase | Latest |
| **Web Client** | Symfony, PHP, npm | 6.4 LTS |
| **Backend** | RESTful API, Symfony | 6.4 |
| **Database** | Firebase Firestore, MySQL | Latest |
| **Auth** | OAuth2, TOTP, JWT | Standard |
| **Payments** | Stripe, PayPal APIs | Production |
| **DevOps** | Docker, Docker Compose | Latest |
| **CI/CD** | GitHub Actions | GitHub |

---

## 🚀 Quick Start

### Prerequisites

- **Java 21+** — For desktop development
- **Flutter SDK** — For mobile development (optional)
- **PHP 8.2+** — For web development
- **Node.js 18+** — For frontend tooling
- **Docker** — For containerized deployment
- **Git** — For version control

### Installation

#### Clone the Repository
```bash
git clone https://github.com/aliammari1/rakcha.git
cd rakcha
```

#### Option 1: Quick Development Setup (All Platforms)
```bash
# Install task runner (or use npm/composer directly)
npm install -g task

# Install dependencies for all apps
task desktop:build
task web:install
task mobile:get
```

#### Option 2: Desktop Only
```bash
cd apps/desktop
mvn clean install
mvn javafx:run
```

#### Option 3: Web Only
```bash
cd apps/web
composer install
npm install
symfony server:start
# Visit http://localhost:8000
```

#### Option 4: Mobile Only
```bash
cd apps/mobile
flutter pub get
flutter run
```

#### Option 4: Docker (Recommended)
```bash
docker-compose -f apps/web/compose.yaml up -d
# Services available at http://localhost
```

---

## 📁 Project Structure

```
rakcha/
├── apps/
│   ├── desktop/              # JavaFX Desktop Application
│   │   ├── src/main/         # Source code organized by feature
│   │   ├── src/test/         # Unit and integration tests
│   │   ├── pom.xml           # Maven configuration
│   │   └── Dockerfile        # Container definition
│   │
│   ├── mobile/               # Flutter Mobile Application
│   │   ├── lib/              # Dart source code
│   │   ├── android/          # Android native code
│   │   ├── ios/              # iOS native code
│   │   ├── firebase/         # Firebase configuration
│   │   ├── pubspec.yaml      # Flutter dependencies
│   │   └── test/             # Flutter tests
│   │
│   └── web/                  # Symfony Web Application
│       ├── src/              # PHP source code
│       ├── public/           # Web-accessible files
│       ├── templates/        # Twig templates
│       ├── migrations/       # Database migrations
│       ├── composer.json     # PHP dependencies
│       ├── package.json      # JavaScript dependencies
│       ├── compose.yaml      # Docker Compose config
│       └── Dockerfile        # Container definition
│
├── shared/                   # Shared resources
│   ├── api-spec/            # OpenAPI specification
│   ├── config/              # Shared configuration
│   └── database/            # Database schemas and migrations
│
├── scripts/                  # Build and deployment scripts
├── Taskfile.yml             # Task automation (like Makefile)
├── docker-compose.yaml      # Multi-container orchestration
└── README.md                # This file
```

---

## 🔧 Configuration

### Environment Setup

1. **Copy environment files:**
   ```bash
   cd apps/web
   cp .env.example .env
   cp .env.test.example .env.test
   ```

2. **Update `.env` with your settings:**
   ```env
   # Database
   DATABASE_URL=mysql://user:password@localhost:3306/rakcha
   
   # Firebase
   FIREBASE_API_KEY=your_api_key
   FIREBASE_PROJECT_ID=your_project_id
   
   # OAuth Providers
   GOOGLE_CLIENT_ID=your_client_id
   GOOGLE_CLIENT_SECRET=your_secret
   
   # Payment Processing
   STRIPE_PUBLIC_KEY=pk_test_...
   STRIPE_SECRET_KEY=sk_test_...
   
   # External APIs
   IMDB_API_KEY=your_key
   ```

3. **Run database migrations:**
   ```bash
   cd apps/web
   php bin/console doctrine:migrations:migrate
   ```

---

## 📚 Documentation

### Getting Started
- **[📖 Wiki Home](wiki/README.md)** — Complete documentation hub
- **[🚀 Quick Start Guide](wiki/Quick-Start.md)** — Get up and running in minutes
- **[💡 Project Overview](wiki/Project-Overview.md)** — Understand RAKCHA
- **[🎯 Use Cases & Case Studies](wiki/use-cases/README.md)** — Real-world applications

### Technical Documentation
- [Desktop App Guide](apps/desktop/README.md) — JavaFX architecture and features
- [Mobile App Guide](apps/mobile/README.md) — Flutter development and deployment
- [Web App Guide](apps/web/README.md) — Symfony backend and API reference
- [Architecture Overview](wiki/Architecture.md) — System design and technical architecture
- [Technology Stack](wiki/Technology-Stack.md) — Technologies and tools used
- [API Documentation](shared/api-spec/openapi.yaml) — OpenAPI specification
- [Database Schema](shared/database/) — Entity relationships and migrations

### Help & Support
- [FAQ](wiki/FAQ.md) — Frequently asked questions
- [Troubleshooting Guide](wiki/Troubleshooting.md) — Common issues and solutions
- [Contributing Guide](CONTRIBUTING.md) — Development workflow and standards

---

## 🧪 Testing

Each application includes comprehensive test suites:

```bash
# Desktop unit and integration tests
cd apps/desktop && mvn test

# Web tests
cd apps/web && php bin/phpunit

# Mobile widget tests
cd apps/mobile && flutter test
```

View coverage reports after test runs.

---

## 🤝 Contributing

We ❤️ contributions! Whether you're fixing bugs, adding features, or improving documentation, your contributions make RAKCHA better.

### Getting Started

1. **Fork the repository**
2. **Create a feature branch:** `git checkout -b feature/amazing-feature`
3. **Make your changes** (follow [code style guide](CONTRIBUTING.md#code-style))
4. **Add tests** for new functionality
5. **Commit:** `git commit -m 'Add amazing feature'`
6. **Push:** `git push origin feature/amazing-feature`
7. **Open a Pull Request**

### Development Workflow

- Read [CONTRIBUTING.md](CONTRIBUTING.md) for detailed guidelines
- Check [open issues](../../issues) for work in progress
- Follow the [Code of Conduct](CODE_OF_CONDUCT.md)
- Use [conventional commits](https://www.conventionalcommits.org/)

### Good Issues for Newcomers

- `good-first-issue` — Perfect for your first contribution
- `help-wanted` — Community help appreciated
- `documentation` — Improve our docs

---

## 🐛 Bug Reports & Feature Requests

Found a bug? Want a feature? Let us know!

- **Bug Report:** [Create an issue](../../issues/new?template=BUG_REPORT.md)
- **Feature Request:** [Suggest an idea](../../issues/new?template=FEATURE_REQUEST.md)
- **Security Issue:** Please email contact@aliammari.com (don't open a public issue)

---

## 📊 Project Status

| Component | Status | Coverage |
|-----------|--------|----------|
| Desktop App | ✅ Stable | 85% |
| Mobile App | ✅ Stable | 80% |
| Web Backend | ✅ Stable | 88% |
| API | ✅ Production Ready | 92% |
| Documentation | ✅ Complete | - |

**Roadmap:** See [ROADMAP.md](apps/desktop/ROADMAP.md) for upcoming features and milestones.

---

## 💡 Use Cases

RAKCHA serves multiple audiences with specific needs. **[View detailed use cases and case studies →](wiki/use-cases/README.md)**

### Cinema Owners & Operators
- Manage multiple cinema locations
- Streamline ticket sales and reservations
- Integrate concession products and merchandise
- Access real-time analytics and reporting
- **[Read cinema management case study →](wiki/use-cases/README.md#cinema-management-use-case)**

### Entertainment Entrepreneurs
- Launch your own cinema marketplace
- Manage film and series content
- Build a customer loyalty program
- Process payments and manage inventory
- **[See streaming platform example →](wiki/use-cases/README.md#streaming-platform-use-case)**

### Platform Companies
- White-label the system
- Extend with custom features
- Integrate with existing systems
- Scale to thousands of users
- **[Explore multi-venue operations →](wiki/use-cases/README.md#multi-venue-use-case)**

### Developers & Integrators
- RESTful API for custom integrations
- Webhook support for real-time events
- OpenAPI specification for code generation
- Comprehensive API documentation
- **[View API reference →](wiki/guides/API-Reference.md)**

---

## 📈 Why Choose RAKCHA?

| Feature | RAKCHA | Others |
|---------|--------|--------|
| **Multi-Platform** | Desktop + Mobile + Web | Box checked in 1-2 |
| **All-in-One** | Cinema + Streaming + Shop | Separate systems |
| **Modern Stack** | Java 21 + Flutter + Symfony | Various versions |
| **Real-Time** | WebSockets + Push | Polling-based |
| **OAuth/2FA** | Built-in | Often missing |
| **API-First** | RESTful + Webhooks | REST only |
| **Open Culture** | Community-driven | Proprietary |
| **Scalable** | Firebase-ready | Custom scaling |

---

## 🆘 Support

- 📖 **Documentation** — Start with our [docs](https://github.com/aliammari1/rakcha/wiki)
- 💬 **GitHub Discussions** — Ask questions and share ideas
- 🐛 **Issue Tracker** — Report bugs and request features
- 📧 **Email** — contact@aliammari.com
- 🔗 **Community Forum** — Join our community discussions

---

## 📜 License

RAKCHA is licensed under the **Commercial License** — see [LICENSE](LICENSE) file for details.

**What this means:**
- ✅ Free for educational and open-source projects
- ✅ Commercial licensing available for enterprises and products
- ✅ Source code available for inspection (not open source)
- ✅ Community contributions welcome

Want to use RAKCHA commercially? [Contact us](mailto:contact@aliammari.com)

---

## 🙏 Contributors

Thank you to all contributors who have helped shape RAKCHA! 

[View all contributors →](../../graphs/contributors)

---

## 🔗 Quick Links

- [GitHub Repository](https://github.com/aliammari1/rakcha)
- [OpenAPI Specification](shared/api-spec/openapi.yaml)
- [Database Schema Docs](shared/database/README.md)
- [Security Policy](SECURITY.md)
- [Code of Conduct](CODE_OF_CONDUCT.md)
- [Changelog](apps/desktop/CHANGELOG.md)

---

## 🌟 Show Your Support

If RAKCHA helps you, please give us a ⭐ on GitHub! It helps others discover the project.

[![GitHub Stars](https://img.shields.io/github/stars/aliammari1/rakcha?style=social)](../../)
[![GitHub Forks](https://img.shields.io/github/forks/aliammari1/rakcha?style=social)](../../)
[![GitHub Watchers](https://img.shields.io/github/watchers/aliammari1/rakcha?style=social)](../../)

---

<div align="center">

**Made with ❤️ by the RAKCHA Community**

[⬆ Back to top](#-rakcha)

</div>
