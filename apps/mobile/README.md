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

```bash
cd apps/mobile
mise install
flutter pub get
flutter run                         # normal Firebase project
```

For a local walkthrough against the Firebase Emulator Suite:

```bash
firebase emulators:start --project demo-rakcha   # from apps/mobile/firebase
adb reverse tcp:9099 tcp:9099
adb reverse tcp:8080 tcp:8080
flutter run --dart-define=USE_FIREBASE_EMULATOR=true
```

`USE_FIREBASE_EMULATOR` is opt-in; production builds use native Firebase
configuration and never connect to a workstation emulator.

## 🧭 App structure

The app has role-aware homes. Clients get five bottom tabs — Discover,
Cinemas, Shop, Series and Tickets — with Profile in the top bar. Managers and
administrators get their own workspaces. `lib/features/` holds the screens and
Firestore helpers; `lib/backend/` and `lib/flutter_flow/` retain the generated
data models and still-used legacy routes.

Seat selection creates a **seat request**; it is not a paid ticket or an
exclusive hold. The cinema room preview is illustrative, not AR/VR. The
basket persists to Firestore; checkout is not implemented.

## 📄 License

MIT. See `LICENSE`.
