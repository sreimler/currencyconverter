# Currency Converter – Android App

A simple, modern **Currency Converter** Android app with offline support, built using Kotlin, Jetpack Compose, and a modular MVVM architecture.

This project was developed as a personal portfolio piece to explore clean, scalable Android patterns and deliver a fully functioning Play Store app — end-to-end.

## 📸 Screenshots

<p align="center">
  <img src="screenshots/rates_screen.png" width="35%" alt="Rates Screen"/>
  <img src="screenshots/converter_screen.png" width="35%" alt="Converter Screen"/>
</p>

## ✨ Features

- Real-time exchange rate conversion using a public currency API
- Pull-to-refresh and automatic staleness checking
- One-tap currency swapping with remembered preferences
- Bottom navigation with screens for Rates, Converter, and Settings
- Jetpack Compose UI with dynamic layout and theming
- Region flags and locale-aware currency formatting
- Offline-first sync strategy with caching and refresh timestamp tracking
- ViewModel-driven state with clean separation between domain and UI
- Modular architecture with feature-based separation (`app`, `rates`, `converter`, etc.)

## 🧰 Tech Stack

- **Kotlin**, **Jetpack Compose**
- **MVVM** architecture + unidirectional data flow
- **Coroutines / Flow** for asynchronous logic
- **Multi-module setup** for scalable code organization
- **Room** for storing currency and exchange rate data
- **DataStore** for persistent settings
- **Retrofit** for API communication
- **Koin** for dependency injection
- **Firebase / Crashlytics** integration for issue detection

## 🚀 Status

The app is **MVP complete and live** on Google Play:<br>
<div align="center">
  <a href="https://play.google.com/store/apps/details?id=com.sreimler.currencyconverter">
    <img src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png" width="200" alt="Download the app on Google Play">
  </a>
</div>

##### Recent improvements

> - Integrated Firebase Analytics and Crashlytics for usage insights and error tracking
> - Refactored data and viewmodel layers to use Kotlin Flows for reactive, lifecycle-aware data handling
> - Introduced custom Result and Error classes to streamline error propagation across app layers
> - App now auto-selects a base currency on first launch based on device locale
> - Enhanced user experience with *tap-to-convert*, a *swap button* on the conversion screen, and a custom `CurrencyAmountField` for better input control
> - Persisted user settings using Jetpack DataStore
> - Published to the Google Play Store with polished visuals and final styling

##### Coming later

> - Onboarding flow with ui feature introduction (press, long press)
> - Unit tests for error handling and sync policy
> - Charts for historical rate data
> - CI setup with GitHub Actions

## 🔐 API Key Handling

This app is a portfolio MVP. API usage is limited to non-sensitive endpoints, and keys are scoped
appropriately.  
In a production-grade app, a secure backend proxy or runtime key protection would be implemented.

## 📃 License

This project is licensed under the terms of the [MIT License](./LICENSE).  
Includes country flag assets from [FlagKit](https://github.com/madebybowtie/FlagKit) by Bowtie (MIT
License).
