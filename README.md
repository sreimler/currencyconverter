# 💱 Currency Converter – Android App

A modern, modular, and production-style **Currency Converter app** built with Kotlin, Jetpack Compose, and a clean multi-module architecture. This project is designed as a portfolio piece to demonstrate strong Android engineering practices, testability, and UI craftsmanship.



## ✨ Features

- 🌐 Real-time exchange rate conversion via a public currency API
- 🧭 Bottom navigation with screens for Rates, Converter, and Settings
- 🔄 Pull-to-refresh and automatic data staleness checking
- ⚙️ Onboarding flow to select base currency on first launch
- 📱 Jetpack Compose UI with modern Android theming
- 📦 Modular architecture (per-feature modules and shared core)
- 🧪 ViewModel-driven state management with clean domain layers
- 📤 Offline-first sync policy with local caching



## 🧰 Tech Stack

- **Kotlin**, **Jetpack Compose**, **Coroutines**, **Flow**
- **Koin** for dependency injection
- **MVVM** architecture with unidirectional data flow
- **Modularization** by feature and layer (`app`, `rates`, `converter`, `settings`, `core`, etc.)
- **Room** or in-memory caching for offline-first behavior
- **GitHub Actions** planned for CI setup



## 🚧 Development Status

The app is currently under active development. Key components already implemented:

- ✅ Navigation setup with multi-module support
- ✅ Real data loading and currency list screen
- ✅ Swipe-to-refresh and sync strategy
- 🔄 Conversion logic and onboarding in progress
- 🧪 Test coverage coming soon



## 📸 Screenshots (Coming Soon)



## 📃 License

This project is licensed under the terms of the [MIT License](./LICENSE).
