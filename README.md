# ☕ Artisan Coffee App — Version 2.0

[![Android](https://img.shields.io/badge/Platform-Android-green.svg?logo=android)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF.svg?logo=kotlin)](https://kotlinlang.org/)
[![Firebase](https://img.shields.io/badge/Backend-Firebase%20Realtime%20Database-FFA611.svg?logo=firebase)](https://firebase.google.com/)
[![Version](https://img.shields.io/badge/Release-v2.0.0-orange.svg)](https://github.com/mahamatkher/Mobile-Application-Development-Project)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

A modern, native Android Coffee Shop application engineered with **Kotlin**, **Android Jetpack**, and **Google Firebase Realtime Database**. Version 2.0 introduces a complete visual overhaul featuring a rich artisan dark mocha & amber theme, dynamic cloud user profiles, interactive wishlist management, and a revamped Cart & Checkout experience.

---

## 🚀 What's New in Version 2.0

* **✨ Artisan Onboarding & Welcome Flow**: Redesigned splash screen featuring a hero cappuccino visual, animated carousel indicators, and one-tap guest/member entrance.
* **🔐 Multi-Method Authentication**: Dedicated login screen supporting Email/Password validation, One-Tap Demo Login ("Quick Login as Hiba"), and Guest browsing.
* **☁️ Cloud Sync User Profile**: Fully integrated with Firebase Realtime Database to store user contact details, address, membership tier ("Gold Member"), and Coffee Club Loyalty Points.
* **❤️ Live Wishlist / Favorites System**: Add/remove favorite coffees from the home cards or detail screen with instant TinyDB local cache and Firebase cloud persistence.
* **☕ Revamped Coffee Detail Screen**:
  * Permanently docked bottom bar with live price and **"Add to Cart 🛒"** CTA.
  * Interactive size selector (`Small`, `Medium`, `Large`) with active amber highlights.
  * Dynamic price recalculation on quantity increment/decrement.
* **🛒 Complete Cart Detail Overhaul**:
  * Delivery address preview with real-time ETA and in-app edit dialog.
  * Artisan item rows with coffee thumbnail, size badges, and quantity stepper.
  * Interactive promo code engine supporting `COFFEE10` (10% off), `FREESHIP` (free delivery), `SPECIAL3` ($3 off), and `HIBA` (15% VIP).
  * Multi-method payment selector (Cash on Delivery, Coffee Club Wallet, Credit Card, Google Pay/UPI).
  * Transparent bill breakdown (Subtotal, Delivery Fee with free tier over $15, Tax, Discount, Total).
  * Order Confirmation Dialog with unique Order IDs and loyalty points reward (+25 Pts).
  * Graceful empty cart state with "Explore Menu" redirection.
* **🎨 Custom Adaptive App Icon**: Tailored artisan coffee mug emblem across all Android mipmap densities (`mdpi` through `xxxhdpi` and `v26` adaptive).

---

## 🛠️ Tech Stack & Architecture

* **Language**: Kotlin 1.9+
* **UI & Layouts**: Android XML, ConstraintLayout, Material 3 Components, ViewBinding
* **Image Loading & Caching**: [Bumptech Glide 4.16](https://github.com/bumptech/glide)
* **Backend & Database**: Google Firebase Realtime Database
* **Local Persistence**: SharedPreferences via TinyDB
* **Architecture Pattern**: MVVM (Model-View-ViewModel) with LiveData observers

---

## 📂 Project Structure

```
Coffee/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/coffee/
│   │   │   │   ├── activites/       # Splash, Login, Main, Detail, Cart, Wishlist, Profile
│   │   │   │   ├── adapters/        # PopularAdapter, CartAdapter, WishlistAdapter, CategoryAdapter
│   │   │   │   ├── domain/          # ItemModel, CategoryModel, UserModel, BannerModel
│   │   │   │   ├── Helper/          # ManagmentCart, ManagmentWishlist, TinyDB
│   │   │   │   └── viewmodel/       # MainViewModel
│   │   │   ├── res/
│   │   │   │   ├── drawable/        # Vector icons, rounded cards, gradient buttons
│   │   │   │   ├── layout/          # Clean, modern UI layouts
│   │   │   │   ├── values/          # Themes, colors, strings
│   │   │   │   └── mipmap-*/        # Coffee app icons
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle.kts
│   └── google-services.json
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

## ⚙️ Installation & Setup

1. **Clone the repository**:
   ```bash
   git clone https://github.com/mahamatkher/Mobile-Application-Development-Project.git
   cd Mobile-Application-Development-Project
   ```

2. **Open in Android Studio**:
   * Open Android Studio (Ladybug / Koala / Hedgehog or newer).
   * Select **File > Open...** and navigate to the project directory.

3. **Configure JDK**:
   * Ensure Gradle JDK is set to **JDK 17** or **JDK 21+** in `Settings > Build, Execution, Deployment > Build Tools > Gradle`.

4. **Build and Run**:
   * Connect an Android device or launch an emulator (API 26+).
   * Run:
     ```bash
     ./gradlew installDebug
     ```

---

## 👤 Author

* **Mahamat Kher**
* GitHub: [@mahamatkher](https://github.com/mahamatkher)
* Email: `mahamatkher6@gmail.com`

---

## 📄 License
This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.
