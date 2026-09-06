# Python Learning App (Android)

A mobile app to learn Python from beginner to advanced levels, built in Kotlin for Android.

This repository contains the Android application code for "Python Learning App" — a self-paced learning app that teaches Python concepts with examples, quizzes, and code snippets. Although the content teaches Python, the app itself is implemented in Kotlin.

## Features

- Structured lessons from beginner to advanced topics
- Interactive code examples and explanations
- Quizzes and practice questions to test understanding
- Progress tracking for learners
- Searchable topics and bookmarks
- Offline access to downloaded lessons (planned/partial)

## Screenshots

(Place screenshots here — add images to `/assets` or `/screenshots` and reference them below)

![Home screen](./screenshots/home.png)
![Lesson screen](./screenshots/lesson.png)

## Tech stack

- Android (Kotlin)
- Minimum SDK: (set in module-level build.gradle)
- Build system: Gradle

> Note: The app content teaches Python, but all application code is written in Kotlin.

## Getting started — prerequisites

- Android Studio Flamingo or later (recommended)
- JDK 11 or later
- Android SDK (match the project's compileSdkVersion and targetSdkVersion)
- A device or emulator running Android API level (see project settings)

## Build and run

1. Clone the repo:

   git clone https://github.com/K0986/python_learning_app.git

2. Open the project in Android Studio:
   - Choose "Open" and select the cloned project directory.
   - Let Gradle sync and download dependencies.

3. Configure an emulator or connect a device.

4. Run the app from Android Studio (Run ▶️) or build an APK:
   - Build > Build Bundle(s) / APK(s) > Build APK(s)

If you run into dependency or SDK issues, open `build.gradle` files and ensure your installed SDK versions match the project's compile and target SDK settings.

## Project structure (high level)

- app/ - Android application module
  - src/main/java/... - Kotlin source code
  - src/main/res - Resources (layouts, drawables, strings)
  - AndroidManifest.xml
- assets/ or screenshots/ - optional static assets/screenshots
- README.md - this file

Adjust the structure section to match the repo if files are organized differently.

## Contributing

Contributions, ideas, and collaboration are welcome!

- Open an issue to propose features or report bugs.
- Create small, focused pull requests for fixes or enhancements.
- Follow Kotlin/Android best practices and keep commits clear and atomic.

Contribution checklist:

- Ensure the app builds locally
- Add or update unit/UI tests where appropriate
- Update README or docs for any public-facing changes

## Roadmap / Ideas

- Add interactive Python code runner (sandboxed)
- Improve offline lesson download and management
- Add user accounts and cloud sync for progress
- Add more quizzes and learning tracks

If you'd like to help implement an item, open an issue and assign yourself.

## License

If you have a preferred license, add it here (e.g., MIT, Apache 2.0). Example:

This project is licensed under the MIT License — see the LICENSE file for details.

## Contact / Support

- Maintainer: K0986
- For questions or collaboration, open an issue or mention me in a PR.

---

If you want, I can:
- Add a CONTRIBUTING.md with a contribution template
- Generate a basic LICENSE file (MIT/Apache)
- Add a template for issues and PRs
- Add CI (GitHub Actions) for builds and linting

Tell me which of the above you'd like next and I will add it.