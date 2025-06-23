# Rakcha Mobile App 🎬📱

A comprehensive entertainment and cinema management Flutter application built with FlutterFlow. Rakcha combines cinema management, film catalog, product marketplace, and user management in a single mobile platform.

## 🎯 Overview

Rakcha is a multi-feature mobile application that serves as:
- **Cinema Management System**: Manage cinemas, theaters, showtimes, and reservations
- **Film & Series Catalog**: Browse, search, and manage movies and TV series
- **E-commerce Platform**: Product marketplace with shopping cart functionality
- **User Management**: Authentication, profiles, and user administration

## ✨ Key Features

### 🎭 Cinema Management
- **Cinema Directory**: Browse and search cinemas by location and amenities
- **Theater Management**: Manage theater halls (salles) with seating capacity
- **Showtime Scheduling**: Create and manage movie showtimes (séances)
- **Reservations**: Book tickets with seat selection and capacity management

### 🎬 Film & Series Management
- **Film Catalog**: Comprehensive movie database with categories and actors
- **Series Management**: TV series with summaries, directors, and country information
- **Categories**: Organize content by genres and themes
- **Actor Management**: Maintain actor profiles and filmographies
- **Reviews & Ratings**: User reviews and rating system

### 🛒 E-commerce Features
- **Product Catalog**: Browse products with categories and descriptions
- **Shopping Cart**: Add items, manage quantities, and calculate totals
- **Product Management**: Admin interface for product CRUD operations
- **Category Management**: Organize products by categories

### 👥 User Management
- **Authentication**: Sign up, sign in, and password recovery
- **User Profiles**: Manage personal information and preferences
- **Admin Panel**: Administrative interface for user management
- **Favorites**: Save favorite events and content

### 📅 Event Management
- **Event Creation**: Create and manage entertainment events
- **Event Suggestions**: AI-powered event recommendations
- **Reservation System**: Book events with capacity management
- **Event Categories**: Organize events by type and theme

## 🏗️ Technical Architecture

### Backend (Firebase)
- **Firestore Database**: Real-time NoSQL database for all data
- **Firebase Authentication**: Secure user authentication and authorization
- **Firebase Storage**: Image and file storage for media content
- **Firebase Analytics**: User behavior tracking and analytics

### Data Models
- **Users**: User profiles with authentication data
- **Cinema**: Cinema information (name, address, status, manager)
- **Salle**: Theater halls with capacity and cinema associations
- **Film**: Movie information with categories, actors, and metadata
- **Serie**: TV series data with directors and summaries
- **Seance**: Showtimes linking films, cinemas, and schedules
- **Produit**: Product catalog with pricing and inventory
- **Cart**: Shopping cart items with user associations
- **Event**: Entertainment events with dates and locations
- **Review**: User reviews and ratings for content
- **Reservation**: Booking system for events and movies

### Frontend (Flutter)
- **FlutterFlow**: Visual development platform for rapid prototyping
- **Material Design**: Modern UI/UX following Material Design principles
- **Responsive Design**: Optimized for various screen sizes
- **Real-time Updates**: Live data synchronization with Firestore

## 📱 App Structure

```
lib/
├── auth/                    # Authentication logic
├── backend/                 # Firebase backend integration
│   ├── schema/             # Firestore data models
│   └── firebase/           # Firebase services
├── flutter_flow/           # FlutterFlow framework components
├── gestion_cinema/         # Cinema management features
│   ├── cinema_list_user/   # Cinema browsing interface
│   ├── cinema_details/     # Cinema detail view
│   ├── edit_cinema/        # Cinema editing interface
│   └── planning_interface/ # Showtime planning
├── gestio_film/           # Film management features
│   ├── add_film/          # Add new films
│   ├── listof_films/      # Film catalog
│   ├── update_film/       # Edit film information
│   └── add_category/      # Category management
├── gestion_series/        # Series management
│   ├── serieaffichage/    # Series display
│   └── reviewaffichage/   # Review interface
├── gestion_users/         # User management
│   ├── auth3/             # Authentication UI
│   ├── profile/           # User profiles
│   ├── edit/              # Profile editing
│   └── forgot_password01/ # Password recovery
├── home1/                 # Main home screen
├── homealll/              # Alternative home interface
├── list_product_client/   # Product catalog for customers
├── list_panier/           # Shopping cart interface
├── add_product/           # Product management
└── main.dart              # Application entry point
```

## 🚀 Getting Started

### Prerequisites
- Flutter SDK (>=3.0.0)
- Dart SDK
- Android Studio / VS Code
- Firebase account
- FlutterFlow account (optional for modifications)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/rakcha-mobile.git
   cd rakcha-mobile
   ```

2. **Install dependencies**
   ```bash
   flutter pub get
   ```

3. **Firebase Setup**
   - Create a new Firebase project
   - Add Android/iOS apps to your project
   - Download configuration files:
     - `google-services.json` for Android (place in `android/app/`)
     - `GoogleService-Info.plist` for iOS (place in `ios/Runner/`)

4. **Configure Firebase**
   - Enable Authentication (Email/Password, Google Sign-in)
   - Set up Firestore Database
   - Configure Storage rules
   - Enable Analytics (optional)

5. **Run the application**
   ```bash
   flutter run
   ```

### Firebase Configuration

Update your Firestore security rules:
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users can read/write their own data
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Public read access for cinemas, films, and products
    match /cinema/{document} {
      allow read: if true;
      allow write: if request.auth != null;
    }
    
    match /Film/{document} {
      allow read: if true;
      allow write: if request.auth != null;
    }
    
    match /produit/{document} {
      allow read: if true;
      allow write: if request.auth != null;
    }
    
    // Cart items are user-specific
    match /cart/{document} {
      allow read, write: if request.auth != null;
    }
  }
}
```

## 🎨 Key Dependencies

### Core Framework
- `flutter`: Cross-platform mobile framework
- `firebase_core`: Firebase core functionality
- `cloud_firestore`: Firestore database integration
- `firebase_auth`: Authentication services
- `firebase_storage`: File storage services

### UI Components
- `google_fonts`: Custom typography
- `font_awesome_flutter`: Icon library
- `cached_network_image`: Optimized image loading
- `flutter_rating_bar`: Rating components
- `badges`: Notification badges

### Navigation & State
- `go_router`: Advanced routing
- `provider`: State management
- `flutter_animate`: Smooth animations

### Additional Features
- `image_picker`: Camera and gallery access
- `file_picker`: File selection
- `google_sign_in`: Google authentication
- `text_search`: Search functionality
- `floating_bottom_navigation_bar`: Custom navigation

## 🎯 Usage Examples

### Adding a New Cinema
1. Navigate to Cinema Management
2. Tap "Add Cinema"
3. Fill in cinema details (name, address, manager, status)
4. Upload cinema image
5. Save to add to the database

### Booking a Movie
1. Browse cinema list
2. Select a cinema
3. Choose available showtime
4. Select number of seats
5. Confirm reservation

### Managing Products
1. Access Admin Panel
2. Navigate to Product Management
3. Add new products with categories
4. Set pricing and inventory
5. Manage product images and descriptions

## 🔧 Development

### FlutterFlow Integration
This project was initially built using FlutterFlow, which provides:
- Visual UI builder
- Firebase integration
- Automatic code generation
- Custom actions and functions

### Customization
To modify the app:
1. Import the project into FlutterFlow (if you have access)
2. Make visual changes using the FlutterFlow editor
3. Export updated code
4. Merge with your local repository

### Manual Development
For manual development without FlutterFlow:
1. Use the generated code as a foundation
2. Modify widgets in the respective files
3. Update models in the `backend/schema/` directory
4. Test changes using `flutter run`

## 📊 Database Schema

### Main Collections
- `users`: User profiles and authentication data
- `cinema`: Cinema information and management
- `Film`: Movie catalog with metadata
- `serie`: TV series information
- `produit`: Product marketplace data
- `cart`: Shopping cart items
- `Event`: Entertainment events
- `Reservation`: Booking and reservation data

### Relationships
- Films are linked to categories and actors
- Cinemas have multiple theater halls (salles)
- Showtimes (séances) link films to specific times and theaters
- Cart items reference products and users
- Reviews are associated with films/series

## 🚦 Deployment

### Android
1. Build APK: `flutter build apk`
2. Build App Bundle: `flutter build appbundle`
3. Sign with release keystore
4. Upload to Google Play Store

### iOS
1. Build iOS: `flutter build ios`
2. Open `ios/Runner.xcworkspace` in Xcode
3. Configure signing and certificates
4. Archive and upload to App Store Connect

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/new-feature`)
3. Commit your changes (`git commit -am 'Add new feature'`)
4. Push to the branch (`git push origin feature/new-feature`)
5. Create a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Built with [Flutter](https://flutter.dev/) and [FlutterFlow](https://flutterflow.io/)
- Backend powered by [Firebase](https://firebase.google.com/)
- UI components from [Material Design](https://material.io/)
- Icons from [Font Awesome](https://fontawesome.com/)

## 📞 Support

For support and questions:
- Create an issue in this repository
- Contact the development team
- Check the FlutterFlow documentation for UI modifications

---

**Rakcha** - Your all-in-one entertainment and cinema management platform! 🎬✨
