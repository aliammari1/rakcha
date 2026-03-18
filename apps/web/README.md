# Rakcha Web 🎬🌐

A comprehensive entertainment and cinema management web platform built with Symfony 6.4. Rakcha Web combines cinema management, film catalog, product marketplace, and user management into a powerful, feature-rich web application designed for the entertainment industry.

## 🎯 Overview

Rakcha Web is a sophisticated entertainment platform that serves as the web counterpart to the Rakcha ecosystem. Built with Symfony's robust framework, it provides comprehensive tools for managing cinemas, films, series, products, and user interactions while delivering a seamless web experience for both administrators and end users.

## ✨ Key Features

### 🎭 Cinema Management System
- **Cinema Operations**: Complete cinema management with location-based services
- **Theater Management**: Multi-theater support with seating capacity control
- **Showtime Scheduling**: Advanced scheduling system for movie screenings
- **Reservation System**: Online booking with seat selection and payment integration
- **Capacity Management**: Real-time seat availability and booking limits
- **Cinema Analytics**: Detailed analytics and reporting for cinema operations

### 🎬 Film & Series Catalog
- **Comprehensive Database**: Extensive film and TV series management
- **IMDB Integration**: Automatic data fetching from IMDB API
- **YouTube Integration**: Trailer and promotional content embedding
- **Category Management**: Genre-based organization and filtering
- **Actor & Director Profiles**: Detailed cast and crew information
- **Review & Rating System**: User-generated reviews with sentiment analysis
- **Content Recommendations**: AI-powered content suggestion engine

### 🛒 E-commerce Platform
- **Product Marketplace**: Full-featured online store for cinema merchandise
- **Inventory Management**: Real-time stock tracking and management
- **Payment Processing**: Stripe and PayPal integration for secure transactions
- **Order Management**: Complete order lifecycle from cart to delivery
- **Discount System**: Coupon codes and promotional campaigns
- **Digital Products**: Support for digital content sales

### 👥 User Management & Authentication
- **Multi-Provider OAuth**: Google, Microsoft, and social media authentication
- **Two-Factor Authentication**: TOTP-based 2FA with trusted device management
- **Role-Based Access Control**: Granular permission system for different user types
- **User Profiles**: Comprehensive user profile management
- **Email Verification**: Secure email verification with auto-expiry
- **Password Reset**: Secure password recovery system

### 📊 Advanced Features
- **Real-Time Notifications**: Flash messaging with Noty integration
- **Live Components**: Real-time UI updates with Symfony UX
- **Chart Integration**: Data visualization with Chart.js
- **QR Code Generation**: Dynamic QR codes for tickets and promotions
- **Image Processing**: Advanced image optimization and manipulation
- **Machine Learning**: Content analysis and recommendation algorithms
- **Multi-Language Support**: Internationalization with Symfony Translation

### 🔒 Security & Performance
- **Rate Limiting**: API and request rate limiting for DDoS protection
- **CSRF Protection**: Cross-site request forgery prevention
- **XSS Protection**: Input sanitization and output encoding
- **SQL Injection Prevention**: Doctrine ORM with prepared statements
- **Cache Management**: Redis and APCu cache integration
- **CDN Integration**: Asset optimization and delivery

## 🛠️ Technology Stack

### Backend Framework
- **Symfony 6.4**: Latest LTS version with enhanced performance
- **PHP 8.1+**: Modern PHP with type declarations and performance improvements
- **Doctrine ORM 2.16**: Advanced database abstraction and management
- **Doctrine DBAL 3**: Database abstraction layer with migration support

### Database & Storage
- **MySQL/PostgreSQL**: Primary database with full ACID compliance
- **Redis**: Session storage and caching layer
- **Flysystem**: Flexible filesystem abstraction for file management
- **Doctrine Migrations**: Version-controlled database schema management

### Frontend Technologies
- **Twig 3**: Powerful templating engine with inheritance and macros
- **Bootstrap 5**: Responsive UI framework with custom components
- **Webpack Encore**: Asset management and optimization
- **Stimulus**: JavaScript framework for enhanced interactivity
- **Turbo**: Fast navigation with minimal JavaScript
- **Chart.js**: Interactive data visualization and analytics

### API Integrations
- **IMDB API**: Movie and TV series data integration
- **YouTube API**: Video content embedding and management
- **Google OAuth2**: Social authentication and user management
- **Microsoft OAuth2**: Enterprise authentication support
- **Stripe API**: Payment processing and subscription management
- **PayPal API**: Alternative payment gateway integration
- **Twilio SDK**: SMS notifications and two-factor authentication

### Development Tools
- **Symfony Maker**: Code generation and scaffolding
- **PHPUnit**: Comprehensive testing framework
- **Doctrine Fixtures**: Database seeding and test data management
- **Symfony Profiler**: Development debugging and performance analysis
- **PHPStan**: Static analysis for code quality assurance

## 📁 Project Structure

```
rakcha-web/
├── assets/                       # Frontend assets
│   ├── styles/                  # SCSS/CSS stylesheets
│   ├── images/                  # Static images and graphics
│   ├── js/                      # JavaScript modules
│   └── controllers/             # Stimulus controllers
├── bin/                         # Executable scripts
│   └── console                  # Symfony console command
├── config/                      # Configuration files
│   ├── packages/                # Bundle configurations
│   ├── routes/                  # Route definitions
│   └── services.yaml            # Service container configuration
├── migrations/                  # Database migrations
│   └── Version*.php             # Migration files
├── public/                      # Web root directory
│   ├── index.php               # Front controller
│   ├── build/                  # Compiled assets
│   └── uploads/                # User-uploaded files
├── src/                        # Application source code
│   ├── Controller/             # Request controllers
│   │   ├── Admin/              # Administrative controllers
│   │   ├── Api/                # REST API controllers
│   │   ├── Cinema/             # Cinema management
│   │   ├── Film/               # Film catalog controllers
│   │   ├── User/               # User management
│   │   └── Shop/               # E-commerce controllers
│   ├── Entity/                 # Doctrine entities
│   │   ├── Cinema.php          # Cinema entity
│   │   ├── Film.php            # Film entity
│   │   ├── User.php            # User entity
│   │   ├── Product.php         # Product entity
│   │   ├── Order.php           # Order entity
│   │   └── Review.php          # Review entity
│   ├── Repository/             # Data repositories
│   │   ├── CinemaRepository.php
│   │   ├── FilmRepository.php
│   │   ├── UserRepository.php
│   │   └── ProductRepository.php
│   ├── Form/                   # Form types
│   │   ├── CinemaType.php      # Cinema form
│   │   ├── FilmType.php        # Film form
│   │   ├── UserType.php        # User form
│   │   └── ProductType.php     # Product form
│   ├── Service/                # Business logic services
│   │   ├── CinemaService.php   # Cinema operations
│   │   ├── FilmService.php     # Film operations
│   │   ├── PaymentService.php  # Payment processing
│   │   ├── NotificationService.php # Notifications
│   │   └── RecommendationService.php # ML recommendations
│   ├── Security/               # Security components
│   │   ├── Authenticator.php   # Custom authenticators
│   │   ├── Voter.php           # Access control voters
│   │   └── UserChecker.php     # User validation
│   ├── EventListener/          # Event listeners
│   │   ├── SecurityListener.php
│   │   ├── ExceptionListener.php
│   │   └── RequestListener.php
│   ├── Command/                # Console commands
│   │   ├── DataImportCommand.php
│   │   ├── CacheWarmupCommand.php
│   │   └── CleanupCommand.php
│   └── Kernel.php              # Application kernel
├── templates/                  # Twig templates
│   ├── base.html.twig          # Base template
│   ├── layout/                 # Layout templates
│   ├── cinema/                 # Cinema templates
│   ├── film/                   # Film templates
│   ├── user/                   # User templates
│   ├── shop/                   # E-commerce templates
│   └── admin/                  # Admin panel templates
├── tests/                      # Test suites
│   ├── Controller/             # Controller tests
│   ├── Entity/                 # Entity tests
│   ├── Service/                # Service tests
│   └── Integration/            # Integration tests
├── translations/               # Translation files
│   ├── messages.en.yaml        # English translations
│   ├── messages.fr.yaml        # French translations
│   └── validators.en.yaml      # Validation messages
├── .env                        # Environment configuration
├── .env.test                   # Test environment
├── composer.json               # PHP dependencies
├── package.json                # Node.js dependencies
├── webpack.config.js           # Webpack configuration
├── phpunit.xml.dist           # PHPUnit configuration
├── symfony.lock               # Symfony recipe lock
├── rakcha_db.sql              # Database schema
├── compose.yaml               # Docker Compose configuration
└── README.md                  # Project documentation
```

## 🚀 Getting Started

### Prerequisites
- **PHP 8.1+** with required extensions:
  - ext-ctype, ext-curl, ext-gd, ext-iconv
  - OpenSSL, PDO, Mbstring, Tokenizer, XML
- **Composer 2.0+**: PHP dependency manager
- **Node.js 16+** and **npm/yarn**: Frontend asset management
- **MySQL 8.0+** or **PostgreSQL 13+**: Database server
- **Redis 6.0+**: Cache and session storage (optional but recommended)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/aliammari1/rakcha-web.git
   cd rakcha-web
   ```

2. **Install PHP dependencies**
   ```bash
   composer install
   ```

3. **Install Node.js dependencies**
   ```bash
   npm install
   # or using yarn
   yarn install
   ```

4. **Environment configuration**
   ```bash
   # Copy environment template
   cp .env .env.local
   
   # Edit configuration file
   nano .env.local
   ```
   
   Configure the following variables:
   ```env
   # Database Configuration
   DATABASE_URL="mysql://username:password@localhost:3306/rakcha_db"
   
   # Application Settings
   APP_ENV=dev
   APP_SECRET=your-secret-key-here
   
   # Mailer Configuration
   MAILER_DSN=smtp://localhost:1025
   
   # OAuth Configuration
   GOOGLE_CLIENT_ID=your-google-client-id
   GOOGLE_CLIENT_SECRET=your-google-client-secret
   MICROSOFT_CLIENT_ID=your-microsoft-client-id
   MICROSOFT_CLIENT_SECRET=your-microsoft-client-secret
   
   # Payment Gateway Configuration
   STRIPE_PUBLIC_KEY=pk_test_...
   STRIPE_SECRET_KEY=sk_test_...
   PAYPAL_CLIENT_ID=your-paypal-client-id
   PAYPAL_CLIENT_SECRET=your-paypal-client-secret
   
   # API Keys
   IMDB_API_KEY=your-imdb-api-key
   YOUTUBE_API_KEY=your-youtube-api-key
   TWILIO_SID=your-twilio-sid
   TWILIO_TOKEN=your-twilio-token
   
   # Cache Configuration
   REDIS_URL=redis://localhost:6379
   ```

5. **Database setup**
   ```bash
   # Create the database
   php bin/console doctrine:database:create
   
   # Run migrations
   php bin/console doctrine:migrations:migrate
   
   # Load fixtures (optional)
   php bin/console doctrine:fixtures:load
   ```

6. **Build frontend assets**
   ```bash
   # Development build
   npm run dev
   
   # Production build
   npm run build
   
   # Watch mode for development
   npm run watch
   ```

7. **Start the development server**
   ```bash
   # Using Symfony CLI (recommended)
   symfony serve -d
   
   # Using PHP built-in server
   php -S localhost:8000 -t public/
   ```

8. **Access the application**
   - Open your browser to [http://localhost:8000](http://localhost:8000)
   - Admin panel: [http://localhost:8000/admin](http://localhost:8000/admin)
   - API documentation: [http://localhost:8000/api/doc](http://localhost:8000/api/doc)

### Docker Setup

```bash
# Start all services
docker-compose up -d

# Install dependencies inside container
docker-compose exec app composer install
docker-compose exec app npm install

# Set up database
docker-compose exec app php bin/console doctrine:migrations:migrate

# Build assets
docker-compose exec app npm run build
```

## 🎯 Usage Guide

### Cinema Management
1. **Add Cinemas**: Create new cinema locations with details
2. **Manage Theaters**: Configure theater halls and seating arrangements
3. **Schedule Showings**: Set up movie showtimes and pricing
4. **Monitor Bookings**: Track reservations and occupancy rates
5. **Generate Reports**: Analyze performance and revenue metrics

### Film Catalog Management
1. **Import Films**: Add movies manually or import from IMDB
2. **Manage Categories**: Organize content by genres and themes
3. **Upload Media**: Add posters, trailers, and promotional content
4. **Review Management**: Moderate user reviews and ratings
5. **Content Analytics**: Track popularity and engagement metrics

### User Administration
1. **User Registration**: Manage user accounts and verification
2. **Role Assignment**: Configure user permissions and access levels
3. **Security Settings**: Enable 2FA and security monitoring
4. **Profile Management**: Update user information and preferences
5. **Activity Monitoring**: Track user engagement and behavior

### E-commerce Operations
1. **Product Management**: Add and manage cinema merchandise
2. **Inventory Control**: Track stock levels and reorder points
3. **Order Processing**: Handle customer orders and fulfillment
4. **Payment Management**: Configure payment gateways and processing
5. **Sales Analytics**: Monitor revenue and sales performance

## 🔧 Configuration

### Environment Variables
```env
# Core Application
APP_ENV=prod|dev|test
APP_SECRET=your-secret-key
DATABASE_URL=mysql://user:pass@host:port/dbname

# Cache & Session
REDIS_URL=redis://localhost:6379
SESSION_LIFETIME=3600

# Security
CORS_ALLOW_ORIGIN=^https?://(localhost|127\.0\.0\.1)(:[0-9]+)?$
TRUSTED_PROXIES=127.0.0.1,REMOTE_ADDR
TRUSTED_HOSTS=localhost,api

# Email Configuration
MAILER_DSN=smtp://user:pass@host:port
FROM_EMAIL=noreply@rakcha.com
ADMIN_EMAIL=admin@rakcha.com

# File Upload
MAX_FILE_SIZE=10M
UPLOAD_DIRECTORY=%kernel.project_dir%/public/uploads

# API Rate Limiting
API_RATE_LIMIT=100
API_RATE_WINDOW=3600
```

### Service Configuration
```yaml
# config/services.yaml
services:
    App\Service\FilmService:
        arguments:
            $imdbApiKey: '%env(IMDB_API_KEY)%'
            $youtubeApiKey: '%env(YOUTUBE_API_KEY)%'

    App\Service\PaymentService:
        arguments:
            $stripeSecretKey: '%env(STRIPE_SECRET_KEY)%'
            $paypalClientId: '%env(PAYPAL_CLIENT_ID)%'

    App\Service\NotificationService:
        arguments:
            $twilioSid: '%env(TWILIO_SID)%'
            $twilioToken: '%env(TWILIO_TOKEN)%'
```

## 🧪 Testing

### Running Tests
```bash
# Run all tests
php bin/phpunit

# Run specific test suite
php bin/phpunit tests/Controller/
php bin/phpunit tests/Service/
php bin/phpunit tests/Entity/

# Run tests with coverage
php bin/phpunit --coverage-html coverage/

# Run functional tests
php bin/phpunit tests/Functional/
```

### Test Categories
- **Unit Tests**: Individual component testing
- **Integration Tests**: Database and service integration
- **Functional Tests**: End-to-end user journey testing
- **API Tests**: REST API endpoint testing
- **Security Tests**: Authentication and authorization testing

### Writing Tests
```php
// tests/Controller/FilmControllerTest.php
namespace App\Tests\Controller;

use Symfony\Bundle\FrameworkBundle\Test\WebTestCase;

class FilmControllerTest extends WebTestCase
{
    public function testFilmList(): void
    {
        $client = static::createClient();
        $crawler = $client->request('GET', '/films');

        $this->assertResponseIsSuccessful();
        $this->assertSelectorTextContains('h1', 'Film Catalog');
    }

    public function testFilmCreation(): void
    {
        $client = static::createClient();
        
        // Authenticate as admin
        $this->logIn($client, 'admin@rakcha.com');
        
        $crawler = $client->request('GET', '/admin/films/new');
        $form = $crawler->selectButton('Save')->form();
        
        $form['film[title]'] = 'Test Film';
        $form['film[description]'] = 'Test Description';
        
        $client->submit($form);
        
        $this->assertResponseRedirects('/admin/films');
    }
}
```

## 🚀 Deployment

### Production Setup
1. **Server Requirements**
   - Ubuntu 20.04+ or CentOS 8+
   - Nginx or Apache with PHP-FPM
   - MySQL 8.0+ or PostgreSQL 13+
   - Redis 6.0+ for caching
   - SSL certificate for HTTPS

2. **Environment Configuration**
   ```bash
   # Set production environment
   APP_ENV=prod
   APP_DEBUG=false
   
   # Configure database
   DATABASE_URL=mysql://user:pass@localhost:3306/rakcha_prod
   
   # Set cache adapter
   CACHE_ADAPTER=cache.adapter.redis
   SESSION_HANDLER_ID=session.handler.redis
   ```

3. **Deployment Commands**
   ```bash
   # Install production dependencies
   composer install --no-dev --optimize-autoloader
   
   # Clear and warm up cache
   php bin/console cache:clear --env=prod
   php bin/console cache:warmup --env=prod
   
   # Run database migrations
   php bin/console doctrine:migrations:migrate --no-interaction
   
   # Build production assets
   npm run build
   
   # Set proper permissions
   chown -R www-data:www-data var/
   chmod -R 755 var/
   ```

### CI/CD Pipeline
```yaml
# .github/workflows/deploy.yml
name: Deploy to Production

on:
  push:
    branches: [main]

jobs:
  deploy:
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/checkout@v3
      
      - name: Setup PHP
        uses: shivammathur/setup-php@v2
        with:
          php-version: '8.1'
          extensions: mbstring, xml, ctype, iconv, intl, pdo_mysql, gd
          
      - name: Setup Node.js
        uses: actions/setup-node@v3
        with:
          node-version: '18'
          
      - name: Install dependencies
        run: |
          composer install --no-dev --optimize-autoloader
          npm ci
          
      - name: Run tests
        run: |
          php bin/phpunit
          npm run test
          
      - name: Build assets
        run: npm run build
        
      - name: Deploy to server
        run: |
          rsync -avz --delete . user@server:/var/www/rakcha/
          ssh user@server 'cd /var/www/rakcha && php bin/console doctrine:migrations:migrate --no-interaction'
```

## 🔒 Security Features

### Authentication & Authorization
- **Multi-Factor Authentication**: TOTP with backup codes
- **OAuth Integration**: Google, Microsoft, and social providers
- **Session Security**: Secure session handling with Redis
- **Password Security**: Argon2 hashing with salt
- **Account Lockout**: Brute force protection
- **Email Verification**: Mandatory email confirmation

### Data Protection
- **Input Validation**: Comprehensive form validation
- **Output Encoding**: XSS prevention with Twig auto-escaping
- **SQL Injection Prevention**: Doctrine ORM with prepared statements
- **CSRF Protection**: Token-based request validation
- **Rate Limiting**: API and form submission limits
- **File Upload Security**: Type validation and virus scanning

### Infrastructure Security
- **HTTPS Enforcement**: SSL/TLS encryption for all traffic
- **Security Headers**: CSP, HSTS, and other security headers
- **Database Encryption**: Encrypted sensitive data at rest
- **Audit Logging**: Comprehensive security event logging
- **Backup Encryption**: Encrypted database and file backups

## 📊 Performance Optimization

### Caching Strategy
- **Application Cache**: Symfony cache with Redis backend
- **Database Query Cache**: Doctrine result and query caching
- **Template Cache**: Twig template compilation caching
- **Asset Optimization**: Webpack bundling and minification
- **CDN Integration**: Static asset delivery optimization

### Database Optimization
- **Index Optimization**: Strategic database indexing
- **Query Optimization**: Doctrine query builder optimization
- **Connection Pooling**: Database connection management
- **Read Replicas**: Scaled read operations
- **Data Archiving**: Historical data management

### Monitoring & Analytics
```php
// src/EventListener/PerformanceListener.php
namespace App\EventListener;

use Symfony\Component\HttpKernel\Event\RequestEvent;
use Symfony\Component\HttpKernel\Event\ResponseEvent;

class PerformanceListener
{
    public function onKernelRequest(RequestEvent $event): void
    {
        $request = $event->getRequest();
        $request->attributes->set('start_time', microtime(true));
    }

    public function onKernelResponse(ResponseEvent $event): void
    {
        $request = $event->getRequest();
        $response = $event->getResponse();
        
        $startTime = $request->attributes->get('start_time');
        $duration = microtime(true) - $startTime;
        
        $response->headers->set('X-Response-Time', $duration);
        
        // Log slow requests
        if ($duration > 2.0) {
            $this->logger->warning('Slow request detected', [
                'url' => $request->getUri(),
                'duration' => $duration
            ]);
        }
    }
}
```

## 🤝 Contributing

We welcome contributions to the Rakcha Web platform! Here's how you can help:

### Development Process
1. **Fork the repository** on GitHub
2. **Clone your fork** locally
3. **Create a feature branch** from `main`
4. **Make your changes** with clear, descriptive commits
5. **Write tests** for new functionality
6. **Update documentation** as needed
7. **Submit a pull request** with detailed description

### Contribution Guidelines
- **Code Style**: Follow PSR-12 coding standards
- **Testing**: Maintain test coverage above 80%
- **Documentation**: Update README and inline documentation
- **Security**: Follow OWASP security best practices
- **Performance**: Consider performance impact of changes
- **Accessibility**: Ensure accessibility compliance (WCAG 2.1)

### Areas for Contribution
- 🎬 **Features**: New cinema and film management features
- 🛒 **E-commerce**: Enhanced shopping and payment features
- 🎨 **UI/UX**: Improved user interface and experience
- 📱 **Mobile**: Mobile-responsive design improvements
- 🔒 **Security**: Security enhancements and auditing
- ⚡ **Performance**: Optimization and caching improvements
- 🧪 **Testing**: Increased test coverage and quality
- 🌍 **Internationalization**: Multi-language support
- 📊 **Analytics**: Enhanced reporting and analytics
- 🔌 **Integrations**: Third-party service integrations

## 📄 License

This project is proprietary software. All rights reserved.

### License Terms
- ❌ **Commercial use**: Requires explicit permission
- ❌ **Modification**: Contact project maintainers
- ❌ **Distribution**: Not permitted without authorization
- ✅ **Private use**: Allowed for evaluation purposes
- ❌ **Liability and warranty**: Not provided

## 🙏 Acknowledgments

### Framework & Libraries
- **Symfony Team**: For the excellent web framework
- **Doctrine Project**: For the powerful ORM and DBAL
- **Twig Team**: For the flexible templating engine
- **Composer**: For dependency management
- **PHPUnit**: For comprehensive testing framework

### API Providers
- **IMDB**: For movie and TV series data
- **YouTube**: For video content integration
- **Stripe**: For payment processing services
- **Google**: For OAuth and various APIs
- **Microsoft**: For enterprise authentication
- **Twilio**: For SMS and communication services

### Development Tools
- **PhpStorm**: For excellent PHP development environment
- **GitHub**: For version control and collaboration
- **Docker**: For containerization and deployment
- **Webpack**: For asset management and optimization

### Community
- **Symfony Community**: For extensive documentation and support
- **Stack Overflow**: For troubleshooting and solutions
- **Packagist**: For the vast ecosystem of PHP packages
- **Open Source Community**: For inspiration and best practices

## 📞 Support & Contact

### Community Support
- **GitHub Issues**: [Report bugs and request features](https://github.com/aliammari1/rakcha-web/issues)
- **GitHub Discussions**: [Ask questions and share ideas](https://github.com/aliammari1/rakcha-web/discussions)
- **Documentation**: [Access detailed documentation](https://github.com/aliammari1/rakcha-web/wiki)

### Development Team
- **Project Lead**: Ali Ammari - [@aliammari1](https://github.com/aliammari1)
- **GitHub**: [Project Repository](https://github.com/aliammari1/rakcha-web)

### Enterprise Support
- **Technical Support**: Available for enterprise deployments
- **Custom Development**: Tailored features and integrations
- **Training**: Platform training and onboarding services
- **Consulting**: Architecture and performance consulting

### Social Media
- **LinkedIn**: [Connect with the team](https://linkedin.com/in/aliammari1)
- **Portfolio**: [View more projects](https://aliammari1.github.io)

---

**Rakcha Web** - Powering the future of entertainment management! 🎬✨

*Built with ❤️ using Symfony, Doctrine, and modern web technologies*