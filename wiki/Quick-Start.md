# Quick Start Guide

Get RAKCHA up and running in minutes! This guide covers the fastest way to start using RAKCHA for development or evaluation.

---

## ⚡ Quick Installation

Choose your preferred method:

### Option 1: Docker (Recommended for Testing)

The fastest way to try RAKCHA:

```bash
# 1. Clone repository
git clone https://github.com/aliammari1/rakcha.git
cd rakcha

# 2. Start with Docker
docker-compose -f apps/web/compose.yaml up -d

# 3. Access the application
# Web: http://localhost:8000
# API: http://localhost:8000/api
```

**That's it!** The web platform is now running.

---

### Option 2: Desktop Application

For cinema staff and management:

```bash
# 1. Prerequisites check
java -version  # Should be Java 21+

# 2. Clone and build
git clone https://github.com/aliammari1/rakcha.git
cd rakcha/apps/desktop

# 3. Run application
mvn clean javafx:run
```

---

### Option 3: Mobile Application

For end users on smartphones:

```bash
# 1. Prerequisites check
flutter doctor  # Fix any issues shown

# 2. Clone and run
git clone https://github.com/aliammari1/rakcha.git
cd rakcha/apps/mobile

# 3. Get dependencies and run
flutter pub get
flutter run
```

---

### Option 4: Web Platform (Manual Setup)

For full control over the web application:

```bash
# 1. Prerequisites
php -v       # Should be PHP 8.2+
node -v      # Should be Node 18+
composer -V  # Check Composer is installed

# 2. Clone repository
git clone https://github.com/aliammari1/rakcha.git
cd rakcha/apps/web

# 3. Install dependencies
composer install
npm install

# 4. Configure environment
cp .env.example .env
# Edit .env with your settings

# 5. Setup database
php bin/console doctrine:database:create
php bin/console doctrine:migrations:migrate

# 6. Start server
symfony server:start
# Or: php -S localhost:8000 -t public/
```

Access at: http://localhost:8000

---

## 🎯 First Steps After Installation

### 1. Create Admin Account

**Web Interface**:
```
Navigate to: http://localhost:8000/register
Fill in:
- Email: admin@example.com
- Password: (choose secure password)
- Name: Admin User
```

**Or via CLI**:
```bash
cd apps/web
php bin/console app:create-user admin@example.com --admin
```

### 2. Configure Firebase (Required)

1. **Create Firebase Project**:
   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Click "Add Project"
   - Follow the setup wizard

2. **Get Configuration**:
   - Project Settings > General
   - Copy Web API Key and Project ID

3. **Update Configuration**:
   ```bash
   # apps/web/.env
   FIREBASE_API_KEY=your-api-key
   FIREBASE_PROJECT_ID=your-project-id
   FIREBASE_AUTH_DOMAIN=your-project.firebaseapp.com
   ```

4. **Setup Firestore**:
   - Firebase Console > Firestore Database
   - Create Database
   - Start in test mode (change to production rules later)

### 3. Add Your First Cinema

**Via Web Interface**:
1. Login with admin account
2. Navigate to: Admin Dashboard > Cinemas
3. Click "Add Cinema"
4. Fill in details:
   ```
   Name: Downtown Cinema
   Address: 123 Main Street
   City: Your City
   Screens: 5
   ```
5. Save

**Via API**:
```bash
curl -X POST http://localhost:8000/api/v1/cinemas \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "name": "Downtown Cinema",
    "address": "123 Main Street",
    "city": "Your City",
    "screens": 5
  }'
```

### 4. Add a Movie

1. Navigate to: Content > Films
2. Click "Add Film"
3. Option 1 - Manual Entry:
   ```
   Title: The Matrix
   Year: 1999
   Genre: Sci-Fi
   Duration: 136 minutes
   ```
4. Option 2 - Import from IMDB:
   ```
   Enter IMDB ID: tt0133093
   Click "Import"
   ```

### 5. Create a Showtime

1. Navigate to: Cinemas > Downtown Cinema > Showtimes
2. Click "Add Showtime"
3. Fill in:
   ```
   Film: The Matrix
   Screen: Screen 1
   Date: Today's date
   Time: 19:00
   Price: $12.00
   ```
4. Save

### 6. Test Booking Flow

1. **As Customer**:
   - Navigate to: Films
   - Select "The Matrix"
   - Click "Book Tickets"
   - Choose showtime: 19:00
   - Select seats: A1, A2
   - Proceed to payment
   - Complete booking

2. **Verify**:
   - Check booking appears in admin dashboard
   - Verify email notification sent
   - Check seats are marked as booked

---

## 🔧 Configuration Options

### Environment Variables

Key configuration options in `.env`:

```bash
# Application
APP_ENV=dev                    # dev|prod
APP_DEBUG=true                 # true|false
APP_SECRET=change-this-secret

# Database
DATABASE_URL=mysql://user:pass@localhost:3306/rakcha

# Firebase
FIREBASE_API_KEY=your-key
FIREBASE_PROJECT_ID=your-project-id

# OAuth (Optional)
GOOGLE_CLIENT_ID=your-client-id
GOOGLE_CLIENT_SECRET=your-secret

# Payment (Optional)
STRIPE_PUBLIC_KEY=pk_test_...
STRIPE_SECRET_KEY=sk_test_...

# Email (Optional)
MAILER_DSN=smtp://localhost:1025
```

### Feature Flags

Enable/disable features:

```bash
# .env
FEATURE_OAUTH=true
FEATURE_PAYMENT=true
FEATURE_2FA=true
FEATURE_ANALYTICS=true
```

---

## 📱 Testing Different Platforms

### Desktop Application

```bash
cd apps/desktop

# Development mode
mvn javafx:run

# Build JAR
mvn clean package
java -jar target/rakcha-desktop-2.0.jar
```

### Mobile Application

```bash
cd apps/mobile

# Android
flutter run -d android

# iOS
flutter run -d ios

# Web (Flutter web)
flutter run -d chrome

# Build APK
flutter build apk --release
```

### Web Platform

```bash
cd apps/web

# Development
symfony server:start

# Production build
composer install --no-dev --optimize-autoloader
npm run build

# Run tests
php bin/phpunit
```

---

## 🧪 Sample Data

Load sample data for testing:

```bash
cd apps/web

# Load fixtures
php bin/console doctrine:fixtures:load

# This creates:
# - 5 sample cinemas
# - 20 films
# - 50 showtimes
# - 10 test users
# - Sample products
```

---

## 🎬 Example Workflows

### Cinema Manager Workflow

1. **Login** to desktop application
2. **Manage Cinemas**:
   - Add/edit cinema details
   - Configure screens and seating
3. **Schedule Showtimes**:
   - Select films
   - Set dates and times
   - Configure pricing
4. **Monitor Bookings**:
   - View real-time bookings
   - Check seat availability
   - Process refunds if needed
5. **Generate Reports**:
   - Revenue reports
   - Attendance statistics
   - Popular films

### Customer Workflow (Mobile)

1. **Browse Films**:
   - View now showing
   - Check ratings and reviews
   - Watch trailers
2. **Book Tickets**:
   - Select cinema and showtime
   - Choose seats on interactive map
   - Add concessions
3. **Payment**:
   - Enter payment details
   - Apply promo code
   - Complete purchase
4. **Receive Ticket**:
   - Email with QR code
   - Add to mobile wallet
   - Set reminder notification
5. **Visit Cinema**:
   - Show QR code at entrance
   - Enjoy the movie!

---

## 🚀 Next Steps

Now that RAKCHA is running:

### Learn More
- [Project Overview](./Project-Overview.md) — Understand the architecture
- [Use Cases](./use-cases/README.md) — See real-world examples
- [Architecture](./Architecture.md) — Technical deep dive

### Customize
- [Development Setup](./guides/Development-Setup.md) — Set up for development
- [API Reference](./guides/API-Reference.md) — Explore the API
- [Contributing](../CONTRIBUTING.md) — Start contributing

### Deploy
- [Deployment Guide](./guides/Deployment.md) — Deploy to production
- [Cloud Deployment](./guides/Cloud-Deployment.md) — Cloud-specific guides
- [Security](../SECURITY.md) — Security best practices

---

## ❓ Common Issues

### Port Already in Use

```bash
# Check what's using port 8000
lsof -i :8000

# Kill the process
kill -9 <PID>

# Or use different port
symfony server:start --port=8001
```

### Database Connection Failed

```bash
# Ensure MySQL is running
sudo systemctl start mysql

# Create database
php bin/console doctrine:database:create

# Run migrations
php bin/console doctrine:migrations:migrate
```

### Firebase Not Configured

```bash
# Check .env has Firebase variables
cat apps/web/.env | grep FIREBASE

# If missing, add them:
FIREBASE_API_KEY=your-key
FIREBASE_PROJECT_ID=your-project-id
```

For more issues, see [Troubleshooting Guide](./Troubleshooting.md).

---

## 💡 Tips for Evaluation

### Testing Scenarios

1. **Cinema Operations**:
   - Create multiple cinemas
   - Add screens with different capacities
   - Schedule overlapping showtimes
   - Test booking flow end-to-end

2. **User Management**:
   - Register multiple users
   - Test OAuth login
   - Enable 2FA for account
   - Try password reset

3. **E-Commerce**:
   - Add products
   - Create combo deals
   - Test checkout flow
   - Process sample orders

4. **Admin Features**:
   - View analytics dashboard
   - Generate reports
   - Manage user roles
   - Configure system settings

### Performance Testing

```bash
# Load test the API
ab -n 1000 -c 10 http://localhost:8000/api/v1/films

# Monitor resource usage
htop  # or top

# Check logs
tail -f apps/web/var/log/dev.log
```

---

## 📞 Getting Help

Stuck? Here's how to get help:

1. **Documentation**: Check [FAQ](./FAQ.md)
2. **Troubleshooting**: See [Troubleshooting Guide](./Troubleshooting.md)
3. **Community**: GitHub Discussions
4. **Issues**: [Report bugs](../../issues/new)
5. **Email**: contact@aliammari.com

---

<div align="center">

**Ready to explore RAKCHA?**

Start building amazing entertainment experiences!

[View Use Cases →](./use-cases/README.md) | [⬆ Back to Wiki Home](./README.md)

</div>
