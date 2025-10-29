# AudioToggle

[![Android](https://img.shields.io/badge/Android-9.0%2B-green.svg)](https://www.android.com)
[![Stars flat](https://img.shields.io/github/stars/szymonbonkowski/AudioToggle?style=flat&color=FFD700)](https://github.com/szymonbonkowski/AudioToggle/stargazers)

Quick Settings tile for Android that forces audio output to speaker when connected to Bluetooth devices.

## 📱 The Problem

When you connect a Bluetooth sound source, Android automatically routes all audio through them. If you want to quickly play something through your phone's speaker without disconnecting Bluetooth, there's no built-in way to do it.

## ✨ The Solution

AudioToggle adds a Quick Settings tile that lets you override Bluetooth audio routing with a single tap, forcing audio to play through your phone's speaker while keeping Bluetooth connected.

## 🎯 Use Cases

- Show a video to a friend without disconnecting your headphones
- Play a quick sound on speaker while Bluetooth is connected
- Switch between speaker and Bluetooth without diving into settings

## 📥 Installation

1. Download the latest APK from [Releases](https://github.com/szymonbonkowski/AudioToggle/releases)
2. Install on your Android device
3. Open the app and grant permissions:
   - Disable battery optimization
   - Allow background activity
4. Add the "Audio Output" tile to Quick Settings
5. Tap the tile to toggle audio output

## 📋 Requirements

- **Android 9.0 (Pie) or higher**

## 🔧 How It Works

The app uses Android's audio focus system with a foreground service that maintains speaker routing. When active, it holds audio focus with `USAGE_VOICE_COMMUNICATION` attributes, which forces Android to route audio through the speaker instead of connected Bluetooth devices.

## ⚠️ Known Limitations

Since Android doesn't natively support manual audio output switching, this app uses a workaround by activating speakerphone mode:

- **Volume control**: Hardware volume buttons may not respond. Use the on-screen volume slider
- **Rapid toggling**: Quickly tapping the tile multiple times may cause Bluetooth to disconnect temporarily
- **Device compatibility**: Behavior may vary across manufacturers and Android versions

These are inherent limitations of the workaround approach. Contributions to improve this are welcome!

## 🛠️ Built With

- **Language**: Kotlin
- **Min SDK**: 28 (Android 9.0)
- **Target SDK**: 36
- **Architecture**: Foreground Service + Quick Settings Tile API

## 🚀 Building from Source

1. Clone the repository:
```bash
git clone https://github.com/SzymonBonkowski/AudioToggle.git
```

2. Open in Android Studio

3. Build the project:
```bash
./gradlew assembleRelease
```

4. Find the APK in `app/build/outputs/apk/release/`

## 📝 License
```
Copyright (c) 2025 Szymon Bonkowski

Licensed under a custom Non-Commercial License with Attribution requirement.

- ✅ Free for personal and non-commercial use
- ✅ Attribution required
- ✅ Modifications must be open source
- ❌ Commercial use prohibited without permission

See the LICENSE file for full details.
```

See [LICENSE](LICENSE) for full details.

## 🐛 Issues & Contributing

Found a bug? Have an idea for improvement? 

- **Issues**: [Open an issue](https://github.com/SzymonBonkowski/AudioToggle/issues)
- **Pull Requests**: Contributions are welcome! Please open a PR with your changes

## 🙏 Acknowledgments

This app was created to solve a simple but annoying Android limitation. Thanks to everyone who uses it and contributes!

## ⭐ Support

If you find this app useful, consider giving it a star on GitHub!
