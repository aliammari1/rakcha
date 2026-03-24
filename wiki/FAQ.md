# Frequently Asked Questions (FAQ)

Common questions and answers about RAKCHA. Can't find your answer? Check our [Troubleshooting Guide](./Troubleshooting.md) or reach out via GitHub Issues.

---

## 📋 Table of Contents

- [General Questions](#general-questions)
- [Getting Started](#getting-started)
- [Technical Questions](#technical-questions)
- [Deployment & Operations](#deployment--operations)
- [Development](#development)
- [Licensing & Commercial Use](#licensing--commercial-use)
- [Support & Community](#support--community)

---

## General Questions

### What is RAKCHA?

RAKCHA is a comprehensive entertainment management platform that combines cinema operations, film cataloging, online reservations, and e-commerce into a single unified ecosystem. It includes native applications for desktop (JavaFX), mobile (Flutter), and web (Symfony).

### Who is RAKCHA for?

RAKCHA is designed for:
- **Cinema owners** managing single or multiple locations
- **Entertainment entrepreneurs** launching streaming or booking platforms
- **Platform operators** building white-label solutions
- **Developers** extending or integrating with the system

### Is RAKCHA production-ready?

Yes! RAKCHA is production-ready with:
- ✅ 80%+ test coverage across all applications
- ✅ OAuth2, JWT, and 2FA authentication
- ✅ Payment processing (Stripe, PayPal)
- ✅ Real-time notifications
- ✅ Comprehensive error handling
- ✅ Security best practices

### What makes RAKCHA different from competitors?

RAKCHA is unique because it:
1. **All-in-One**: Cinema + streaming + e-commerce in one platform
2. **Multi-Platform**: Desktop, mobile, and web applications
3. **Modern Stack**: Latest stable versions (Java 21, Flutter, Symfony 6.4)
4. **Production-Ready**: Not a prototype, includes all enterprise features
5. **Open Development**: Transparent roadmap and active community

---

## Getting Started

### How do I install RAKCHA?

Quick installation steps:

1. **Clone the repository**:
   ```bash
   git clone https://github.com/aliammari1/rakcha.git
   cd rakcha
   ```

2. **Choose your platform**:
   - **Desktop**: `cd apps/desktop && mvn javafx:run`
   - **Mobile**: `cd apps/mobile && flutter run`
   - **Web**: `cd apps/web && symfony server:start`
   - **Docker**: `docker-compose up -d`

For detailed instructions, see [Quick Start Guide](./Quick-Start.md).

### What are the system requirements?

**Development**:
- Java 21+ (for desktop)
- Flutter SDK (for mobile)
- PHP 8.2+ (for web)
- Node.js 18+ (for frontend tools)
- Git

**Production**:
- Docker & Docker Compose (recommended)
- OR: Linux server with Java, PHP, Node.js
- Firebase account
- Cloud hosting (AWS, GCP, Azure, or similar)

### Do I need all three applications?

No! You can use:
- **Desktop only**: For cinema staff/management
- **Mobile only**: For end-user mobile experience
- **Web only**: For both staff and customers
- **Any combination**: Applications work independently

### How long does setup take?

- **Development setup**: 30-60 minutes
- **Basic production deployment**: 2-4 hours
- **Full production with customization**: 1-2 days

---

## Technical Questions

### What technologies does RAKCHA use?

**Backend**:
- Symfony 6.4 (PHP 8.2)
- REST API architecture
- Firebase Firestore & MySQL

**Desktop**:
- JavaFX (Java 21 LTS)
- Maven build system

**Mobile**:
- Flutter (Dart)
- iOS & Android support

**DevOps**:
- Docker & Docker Compose
- GitHub Actions CI/CD

See [Technology Stack](./Technology-Stack.md) for details.

### Can I use a different database?

Currently, RAKCHA uses Firebase Firestore as the primary database. While technically possible to switch, it would require significant refactoring. MySQL is used for some relational data and reporting.

**Future**: Support for PostgreSQL and MongoDB is on the roadmap.

### Does RAKCHA support multiple languages/localization?

The framework for internationalization (i18n) is in place. Currently:
- English is fully supported
- Structure supports adding new languages
- Contributions for translations are welcome!

See [Contributing Guide](../CONTRIBUTING.md) to help with translations.

### Can I customize the UI?

Yes! All UI components can be customized:
- **Desktop**: Modify FXML layouts and CSS
- **Mobile**: Customize Flutter widgets and themes
- **Web**: Edit Twig templates and stylesheets

### How does authentication work?

RAKCHA supports multiple authentication methods:
1. **OAuth2**: Google, Microsoft, and other providers
2. **Email/Password**: Traditional authentication
3. **2FA**: TOTP-based two-factor authentication
4. **JWT**: Token-based API authentication

See [Authentication Guide](./guides/Authentication.md) for implementation details.

### Is there an API?

Yes! RAKCHA has a comprehensive REST API with:
- 100+ endpoints
- OpenAPI specification
- JWT authentication
- Rate limiting
- Webhooks support

See [API Reference](./guides/API-Reference.md) for documentation.

---

## Deployment & Operations

### How do I deploy RAKCHA to production?

**Recommended approach**:
1. Use Docker Compose for containerized deployment
2. Configure environment variables
3. Set up Firebase project
4. Configure payment processors
5. Set up domain and SSL
6. Deploy with your cloud provider

See [Deployment Guide](./guides/Deployment.md) for step-by-step instructions.

### Which cloud providers are supported?

RAKCHA can deploy to any cloud provider:
- ✅ **AWS**: Elastic Beanstalk, ECS, EC2
- ✅ **Google Cloud**: App Engine, Cloud Run, GCE
- ✅ **Azure**: App Service, Container Instances
- ✅ **DigitalOcean**: Droplets, App Platform
- ✅ **Heroku**: Container deployment
- ✅ **Self-hosted**: VPS or dedicated server

See [Cloud Deployment Guide](./guides/Cloud-Deployment.md).

### How do I scale RAKCHA?

**Horizontal Scaling**:
- Deploy multiple API server instances
- Use load balancer
- Firebase auto-scales
- CDN for static assets

**Vertical Scaling**:
- Increase server resources
- Optimize database queries
- Enable caching (Redis)

See [Performance Tuning](./guides/Performance-Tuning.md).

### How do I backup my data?

**Firebase**: Use Firebase's built-in backup
**MySQL**: Use `mysqldump` or automated backups
**Files**: Backup uploads and static assets

See [Backup & Recovery Guide](./guides/Backup-Recovery.md).

### What monitoring is available?

RAKCHA includes:
- Application logs
- Error tracking
- Performance metrics
- Health check endpoints

Can integrate with:
- Sentry for error tracking
- New Relic / DataDog for APM
- CloudWatch / Stackdriver for cloud monitoring

See [Monitoring Guide](./guides/Monitoring.md).

---

## Development

### How do I contribute?

We welcome contributions! Steps:
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a Pull Request

See [Contributing Guide](../CONTRIBUTING.md) for detailed instructions.

### What's the development workflow?

```bash
# 1. Clone and setup
git clone https://github.com/aliammari1/rakcha.git
cd rakcha

# 2. Create feature branch
git checkout -b feature/my-feature

# 3. Make changes and test
# Desktop: mvn test
# Mobile: flutter test
# Web: php bin/phpunit

# 4. Commit and push
git commit -m "feat: add my feature"
git push origin feature/my-feature

# 5. Open Pull Request
```

### How do I run tests?

```bash
# Desktop (Java/JUnit)
cd apps/desktop
mvn test

# Mobile (Flutter)
cd apps/mobile
flutter test

# Web (PHP/PHPUnit)
cd apps/web
php bin/phpunit

# All tests with coverage
task test
```

### Where can I find code examples?

- **API Examples**: [API Reference](./guides/API-Reference.md)
- **Integration Examples**: [Use Cases](./use-cases/README.md)
- **Code in Repository**: Browse `apps/` directories
- **Tests**: Check test files for usage examples

### How do I debug issues?

**Desktop**:
- Use IntelliJ IDEA debugger
- Check console logs
- Enable debug logging in config

**Mobile**:
- Use VS Code / Android Studio debugger
- Flutter DevTools
- Check device logs: `flutter logs`

**Web**:
- Symfony Profiler toolbar
- Application logs: `var/log/`
- Enable debug mode in `.env`

See [Debug Guide](./guides/Debugging.md).

---

## Licensing & Commercial Use

### What license does RAKCHA use?

RAKCHA uses a **Commercial Use License**:
- ✅ **Free** for educational and personal use
- ✅ **Free** for open-source contributions
- ⚖️ **Requires license** for commercial use
- ✅ Source code available for inspection

See [LICENSE](../LICENSE) for full terms.

### Can I use RAKCHA for my business?

Commercial use requires a license. Contact us:
- **Email**: contact@aliammari.com
- **Subject**: "Commercial Use License Request - Rakcha"

We offer:
- Startup licenses (discounted)
- Enterprise licenses
- OEM licenses
- Custom licensing

### Can I modify the code?

Yes! You can:
- ✅ Fork and modify for personal/educational use
- ✅ Contribute improvements back to the project
- ✅ Create derivatives with proper attribution
- ⚖️ Commercial modifications require license

### Can I redistribute RAKCHA?

- ✅ **Open Source Projects**: Yes, with attribution
- ✅ **Non-Profit Use**: Yes, with attribution
- ⚖️ **Commercial Redistribution**: Requires license
- ❌ **Competing Products**: Not permitted without license

---

## Support & Community

### Where can I get help?

1. **Documentation**: Search this wiki first
2. **FAQ**: You're here!
3. **Troubleshooting**: [Common Issues](./Troubleshooting.md)
4. **GitHub Discussions**: Ask questions
5. **GitHub Issues**: Report bugs
6. **Email**: contact@aliammari.com

### How do I report a bug?

1. Check [existing issues](../../issues)
2. Create new issue with [Bug Report template](../../issues/new?template=bug_report.yaml)
3. Include:
   - Component (desktop/mobile/web)
   - Version
   - Steps to reproduce
   - Expected vs actual behavior
   - Environment details

### How do I request a feature?

1. Check [existing requests](../../issues?q=is%3Aissue+label%3Aenhancement)
2. Create new issue with [Feature Request template](../../issues/new?template=feature_request.yaml)
3. Describe:
   - The problem it solves
   - Proposed solution
   - Why it's valuable

### Is there a community?

Yes! Join us:
- **GitHub Discussions**: Community Q&A
- **Contributors**: See [all contributors](../../graphs/contributors)
- **Code of Conduct**: [Community guidelines](../CODE_OF_CONDUCT.md)

### How often is RAKCHA updated?

- **Bug fixes**: As needed
- **Minor updates**: Monthly
- **Major versions**: Quarterly
- **Security patches**: Immediately

Follow the repository to stay updated!

### Can I hire someone to help with RAKCHA?

For commercial support and consulting:
- **Email**: contact@aliammari.com
- **Services**: Custom development, training, consulting
- **Response**: Typically within 5-7 business days

---

## 🔍 Still Have Questions?

If your question isn't answered here:

1. **Search the Wiki**: Browse other documentation pages
2. **GitHub Discussions**: Start a discussion
3. **GitHub Issues**: For bug reports and feature requests
4. **Email**: contact@aliammari.com for direct inquiries

---

<div align="center">

**Hope this helps! 🚀**

[Check Troubleshooting →](./Troubleshooting.md) | [⬆ Back to Wiki Home](./README.md)

</div>
