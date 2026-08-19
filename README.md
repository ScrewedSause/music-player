# music-player

A Kotlin-based music player application and/or library. This repository contains the source code for a simple, modern music player written in Kotlin.

> NOTE: This README is a starting point. If you want the README tailored to the project's exact structure (Android app vs JVM desktop vs library), tell me which module(s) to document or provide the entrypoint and features and I'll update it.

## Features

- Play local audio files (MP3, WAV, AAC, etc.)
- Playlist support
- Basic playback controls: play, pause, stop, next, previous, seek
- Metadata display (title, artist, album)
- Cross-platform Kotlin codebase (JVM/Android-ready)

## Tech

- Language: Kotlin (100%)
- Build: Gradle (recommended)

## Getting started

Prerequisites

- JDK 11 or newer
- Git
- (Optional) Android Studio if this is an Android application

Clone the repository

```bash
git clone https://github.com/ScrewedSause/music-player.git
cd music-player
```

Build

If the project uses the Gradle wrapper:

```bash
./gradlew clean assemble
```

Run

If the project is a JVM application and provides an application plugin or runnable JAR:

```bash
# Build a runnable jar
./gradlew jar
# then run (adjust the jar name as necessary)
java -jar build/libs/*-all.jar
```

Or, if the project exposes a Gradle `run` task:

```bash
./gradlew run
```

Tests

```bash
./gradlew test
```

Running on Android

If this repository is an Android app, open the project in Android Studio and run on an emulator or device.

## Project structure (suggested)

- `app/` — application module (Android or desktop)
- `core/` — playback core and business logic
- `player/` — audio engine / low-level playback
- `ui/` — user interface modules

Adjust these headings to match the actual layout of this repository.

## Contributing

Contributions are welcome! Please open issues for bugs or feature requests and create pull requests for proposed changes. If you have a coding style guide or tests to run, document them here.

Suggested workflow:

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Commit your changes and push: `git push origin feature/my-feature`
4. Open a Pull Request

## License

This repository does not yet contain a LICENSE file. If you want to use a permissive license, consider adding an `MIT` or `Apache-2.0` license.

## Contact

Maintainer: ScrewedSause

---

If you'd like, I can:

- Update this README to match the repo's real module names and build/run steps (I can inspect the code and write exact commands),
- Add CI badges (if you tell me the workflow file name),
- Add screenshots and usage examples from the app.
