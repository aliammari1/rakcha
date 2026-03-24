# Troubleshooting Guide

Common issues and their solutions for RAKCHA. If you can't find your issue here, check the [FAQ](./FAQ.md) or open a [GitHub Issue](../../issues).

---

## 📋 Table of Contents

- [Installation Issues](#installation-issues)
- [Build & Compilation](#build--compilation)
- [Runtime Errors](#runtime-errors)
- [Database Issues](#database-issues)
- [Authentication Problems](#authentication-problems)
- [API & Network](#api--network)
- [Performance Issues](#performance-issues)
- [Platform-Specific Issues](#platform-specific-issues)

---

## Installation Issues

### Clone Fails or Takes Too Long

**Symptom**: Git clone is slow or fails

**Solutions**:
```bash
# Use shallow clone for faster download
git clone --depth 1 https://github.com/aliammari1/rakcha.git

# Or clone specific branch
git clone -b main --single-branch https://github.com/aliammari1/rakcha.git
```

### Missing Dependencies

**Symptom**: Installation fails with missing dependencies

**Solutions**:

**Desktop**:
```bash
# Ensure Java 21 is installed
java -version  # Should show Java 21+

# Clean and reinstall
cd apps/desktop
mvn clean install -U
```

**Mobile**:
```bash
# Check Flutter installation
flutter doctor

# Fix any issues reported
flutter doctor --android-licenses
flutter pub get
```

**Web**:
```bash
# Check PHP version
php -v  # Should show PHP 8.2+

# Update dependencies
cd apps/web
composer install
npm install
```

### Permission Denied Errors

**Symptom**: "Permission denied" during installation

**Solutions**:
```bash
# Fix file permissions
chmod +x scripts/*.sh

# For Maven wrapper
chmod +x mvnw

# For npm global packages
sudo npm install -g @go-task/cli
```

---

## Build & Compilation

### Maven Build Fails (Desktop)

**Symptom**: `mvn clean install` fails

**Common Causes & Solutions**:

1. **Wrong Java Version**:
   ```bash
   # Check Java version
   java -version

   # Set JAVA_HOME if needed
   export JAVA_HOME=/path/to/java-21
   ```

2. **Dependency Download Issues**:
   ```bash
   # Clear Maven cache
   rm -rf ~/.m2/repository

   # Retry with force update
   mvn clean install -U
   ```

3. **Test Failures**:
   ```bash
   # Skip tests temporarily
   mvn clean install -DskipTests

   # Run tests separately to see details
   mvn test
   ```

### Flutter Build Fails (Mobile)

**Symptom**: Flutter build errors

**Solutions**:

1. **Clean Build**:
   ```bash
   flutter clean
   flutter pub get
   flutter pub upgrade
   flutter build apk  # or ios
   ```

2. **Android Build Issues**:
   ```bash
   # Update Android SDK
   flutter doctor --android-licenses

   # Gradle cache issues
   cd android
   ./gradlew clean
   cd ..
   ```

3. **iOS Build Issues**:
   ```bash
   cd ios
   pod deintegrate
   pod install
   cd ..
   flutter build ios
   ```

### Symfony Build Issues (Web)

**Symptom**: Composer or npm errors

**Solutions**:

1. **Composer Issues**:
   ```bash
   # Clear cache
   composer clear-cache

   # Remove vendor and reinstall
   rm -rf vendor
   composer install

   # Update dependencies
   composer update
   ```

2. **npm Issues**:
   ```bash
   # Clear npm cache
   npm cache clean --force

   # Remove node_modules
   rm -rf node_modules package-lock.json
   npm install

   # Build assets
   npm run build
   ```

---

## Runtime Errors

### Application Won't Start

**Desktop**:
```bash
# Check if port is available
lsof -i :8080

# Kill process if needed
kill -9 <PID>

# Run with debug output
mvn javafx:run -X
```

**Mobile**:
```bash
# Check connected devices
flutter devices

# Run on specific device
flutter run -d <device-id>

# Enable verbose logging
flutter run -v
```

**Web**:
```bash
# Check Symfony requirements
symfony check:requirements

# Clear cache
php bin/console cache:clear

# Run with debug
symfony server:start --port=8000
```

### "Connection Refused" Errors

**Symptom**: API calls fail with connection errors

**Solutions**:

1. **Check API is Running**:
   ```bash
   # For Symfony
   symfony server:status

   # Check if port is listening
   netstat -an | grep 8000
   ```

2. **Verify API URL**:
   ```bash
   # Desktop: Check config/application.properties
   api.base.url=http://localhost:8000

   # Mobile: Check lib/constants/api_config.dart
   static const String baseUrl = "http://localhost:8000";

   # Web: Check .env
   API_URL=http://localhost:8000
   ```

3. **CORS Issues**:
   ```php
   // apps/web/config/packages/nelmio_cors.yaml
   nelmio_cors:
       paths:
           '^/api':
               allow_origin: ['*']
               allow_methods: ['GET', 'POST', 'PUT', 'DELETE']
   ```

### Blank/White Screen

**Symptom**: Application loads but shows blank screen

**Desktop**:
```bash
# Check JavaFX runtime
java -jar target/app.jar
# Look for FXML loading errors

# Verify FXML paths
# Check resources are in target/classes
```

**Mobile**:
```bash
# Check console for errors
flutter run
# Look for widget build errors

# Hot restart
press 'R' in flutter run console
```

**Web**:
```bash
# Check Twig template errors
# Look in var/log/dev.log

# Clear cache
php bin/console cache:clear
```

---

## Database Issues

### Firebase Connection Errors

**Symptom**: "Failed to connect to Firebase" or authentication errors

**Solutions**:

1. **Verify Firebase Configuration**:
   ```bash
   # Check if firebase config file exists
   # Mobile: android/app/google-services.json
   # Web: .env has FIREBASE_* variables
   ```

2. **Check API Keys**:
   ```bash
   # Ensure API keys are valid
   # Firebase Console > Project Settings > General

   # Verify keys in .env match Firebase
   FIREBASE_API_KEY=your-api-key
   FIREBASE_PROJECT_ID=your-project-id
   ```

3. **Network Rules**:
   ```javascript
   // Firebase Console > Firestore > Rules
   // Update security rules if needed
   rules_version = '2';
   service cloud.firestore {
     match /databases/{database}/documents {
       match /{document=**} {
         allow read, write: if request.auth != null;
       }
     }
   }
   ```

### "Permission Denied" from Firestore

**Symptom**: Operations fail with permission errors

**Solutions**:

1. **Check User Authentication**:
   - Ensure user is logged in
   - Verify JWT token is valid
   - Check token expiration

2. **Review Firestore Rules**:
   ```javascript
   // Allow authenticated users
   allow read, write: if request.auth != null;

   // Or allow owners only
   allow read, write: if request.auth.uid == resource.data.userId;
   ```

3. **Test Mode** (development only):
   ```javascript
   // WARNING: Only for development!
   allow read, write: if true;
   ```

### MySQL Connection Fails

**Symptom**: "Connection refused" to MySQL

**Solutions**:

1. **Check MySQL is Running**:
   ```bash
   # Linux
   sudo systemctl status mysql

   # macOS
   brew services list

   # Start if not running
   sudo systemctl start mysql
   ```

2. **Verify Credentials**:
   ```bash
   # Test connection
   mysql -u root -p -h localhost

   # Check .env configuration
   DATABASE_URL="mysql://user:password@127.0.0.1:3306/rakcha"
   ```

3. **Create Database**:
   ```bash
   php bin/console doctrine:database:create
   php bin/console doctrine:migrations:migrate
   ```

---

## Authentication Problems

### OAuth Login Fails

**Symptom**: Google/Microsoft OAuth doesn't work

**Solutions**:

1. **Check OAuth Configuration**:
   ```bash
   # Verify .env has correct credentials
   GOOGLE_CLIENT_ID=your-client-id
   GOOGLE_CLIENT_SECRET=your-secret

   # Check redirect URI matches OAuth console
   # Should be: http://localhost:8000/auth/google/callback
   ```

2. **Authorized Redirect URIs**:
   - Google Console > Credentials > OAuth 2.0 Client ID
   - Add: `http://localhost:8000/auth/google/callback`
   - For production: `https://yourdomain.com/auth/google/callback`

3. **Enable APIs**:
   - Google Cloud Console > APIs & Services
   - Enable: Google+ API, People API

### JWT Token Issues

**Symptom**: "Invalid token" or "Token expired"

**Solutions**:

1. **Check Token Expiration**:
   ```bash
   # Adjust token lifetime in .env
   JWT_TOKEN_TTL=3600  # 1 hour
   ```

2. **Verify Secret Key**:
   ```bash
   # Ensure JWT_SECRET is set and consistent
   JWT_SECRET=your-secret-key-change-this

   # Generate new secret
   php bin/console lexik:jwt:generate-keypair
   ```

3. **Token Refresh**:
   ```javascript
   // Implement token refresh in client
   if (response.status === 401) {
     // Token expired, refresh it
     await refreshToken();
   }
   ```

### 2FA Not Working

**Symptom**: TOTP codes don't validate

**Solutions**:

1. **Time Synchronization**:
   ```bash
   # Ensure server time is correct
   date

   # Sync with NTP
   sudo ntpdate -s time.nist.gov
   ```

2. **QR Code Issues**:
   - Ensure QR code displays correctly
   - Try manual entry of secret key
   - Use Google Authenticator or Authy

---

## API & Network

### 404 Not Found for API Endpoints

**Symptom**: API calls return 404

**Solutions**:

1. **Check Route Configuration**:
   ```bash
   # List all routes
   php bin/console debug:router

   # Look for specific route
   php bin/console debug:router api_cinemas_get
   ```

2. **Verify Base URL**:
   ```bash
   # Ensure base URL is correct
   # Should be: http://localhost:8000/api/v1/
   ```

3. **Apache/Nginx Configuration**:
   ```nginx
   # Nginx example
   location / {
       try_files $uri /index.php$is_args$args;
   }
   ```

### Rate Limiting Errors

**Symptom**: "Too many requests" error

**Solutions**:

1. **Adjust Rate Limits**:
   ```yaml
   # config/packages/rate_limiter.yaml
   framework:
       rate_limiter:
           api:
               policy: 'sliding_window'
               limit: 100
               interval: '1 hour'
   ```

2. **Clear Rate Limit Cache**:
   ```bash
   php bin/console cache:pool:clear cache.rate_limiter
   ```

### CORS Errors

**Symptom**: Browser blocks API requests

**Solutions**:

1. **Configure CORS**:
   ```yaml
   # config/packages/nelmio_cors.yaml
   nelmio_cors:
       defaults:
           origin_regex: true
           allow_origin: ['*']
           allow_methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS']
           allow_headers: ['Content-Type', 'Authorization']
           max_age: 3600
   ```

2. **Preflight Requests**:
   ```php
   // Ensure OPTIONS requests are handled
   // Symfony handles this automatically with nelmio_cors
   ```

---

## Performance Issues

### Slow Application Startup

**Solutions**:

1. **Enable Caching**:
   ```bash
   # Clear and warm up cache
   php bin/console cache:clear
   php bin/console cache:warmup
   ```

2. **Optimize Autoloader**:
   ```bash
   composer dump-autoload --optimize --classmap-authoritative
   ```

3. **Use Production Mode**:
   ```bash
   # Set APP_ENV in .env
   APP_ENV=prod
   ```

### Slow API Responses

**Solutions**:

1. **Enable Query Logging**:
   ```yaml
   # Find slow queries
   doctrine:
       dbal:
           logging: true
           profiling: true
   ```

2. **Add Database Indexes**:
   ```php
   // Entity annotations
   /**
    * @ORM\Index(name="cinema_name_idx", columns={"name"})
    */
   ```

3. **Implement Caching**:
   ```bash
   # Install Redis
   composer require predis/predis

   # Configure cache
   # config/packages/cache.yaml
   ```

### High Memory Usage

**Solutions**:

1. **Increase PHP Memory**:
   ```ini
   ; php.ini
   memory_limit = 512M
   ```

2. **Optimize Queries**:
   ```php
   // Use pagination
   $query->setMaxResults(20);

   // Use select instead of fetching all
   ->select('c.id', 'c.name')
   ```

3. **Clear Doctrine Cache**:
   ```bash
   php bin/console doctrine:cache:clear-metadata
   php bin/console doctrine:cache:clear-query
   ```

---

## Platform-Specific Issues

### Desktop (JavaFX)

**Issue**: "JavaFX runtime components are missing"

**Solution**:
```bash
# Ensure JavaFX is included in Maven
# Check pom.xml has javafx dependencies

# Or use Maven plugin
mvn javafx:run
```

### Mobile (Flutter)

**Issue**: "Gradle build failed"

**Solution**:
```bash
# Update Gradle version
# android/gradle/wrapper/gradle-wrapper.properties
distributionUrl=https\://services.gradle.org/distributions/gradle-7.5-all.zip

# Update Android SDK
flutter doctor
```

**Issue**: "Pod install failed" (iOS)

**Solution**:
```bash
cd ios
pod repo update
pod install
cd ..
```

### Web (Symfony)

**Issue**: "Class not found"

**Solution**:
```bash
# Regenerate autoloader
composer dump-autoload

# Clear cache
php bin/console cache:clear
```

---

## 🆘 Still Having Issues?

If your problem isn't solved:

1. **Check Logs**:
   - Desktop: Console output
   - Mobile: `flutter logs`
   - Web: `var/log/dev.log`

2. **Enable Debug Mode**:
   ```bash
   # .env
   APP_ENV=dev
   APP_DEBUG=true
   ```

3. **Search Existing Issues**:
   - [GitHub Issues](../../issues)

4. **Create New Issue**:
   - Use [Bug Report template](../../issues/new?template=bug_report.yaml)
   - Include error logs and steps to reproduce

5. **Ask Community**:
   - GitHub Discussions
   - Email: contact@aliammari.com

---

<div align="center">

**Need more help?**

[Check FAQ →](./FAQ.md) | [⬆ Back to Wiki Home](./README.md)

</div>
