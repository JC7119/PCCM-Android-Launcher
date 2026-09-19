# PCCM OEM Launcher

An OEM-inspired launcher for Porsche Android head units.

The goal of this project is to provide a clean, factory-style interface that closely resembles Porsche's PCCM+ user experience while retaining the flexibility of Android.

Unlike many aftermarket launchers, PCCM OEM Launcher focuses on simplicity, consistency, and an OEM appearance rather than excessive customization.

Version 1.0 introduces two selectable Porsche-inspired interfaces: **PCCM+ .1 / Classic** and the new **PCCM+ .2 / Modern** design.

<img width="1280" height="720" alt="Screenshot_2026 09 18_19 18 41 991" src="https://github.com/user-attachments/assets/ddafa18a-a12b-448f-acc0-3d97fdc68ea7" />

<img width="1280" height="720" alt="Screenshot_2026 09 18_19 18 34 807" src="https://github.com/user-attachments/assets/5f2a737c-2989-4990-b94d-a80f839c39a4" />

---

## Features

- Two selectable OEM-inspired Porsche PCCM+ interfaces
- **PCCM+ .1 / Classic** interface based on the original PCCM+ design
- **PCCM+ .2 / Modern** interface inspired by Porsche's newer PCCM+ system
- Dedicated high-resolution icon sets for each interface
- User-configurable application shortcuts
- Long-press launcher tiles to assign different applications
- Dedicated full-screen app drawer
- Built-in application search
- Automatic system-language detection
- Support for 19 languages
- Lightweight and fast
- Designed specifically for automotive use
- No unnecessary widgets or distractions

---

## Compatibility

This launcher has been tested on:

- UIS7870-based Android head unit
- Samsung Galaxy Tab S9 FE
- BlueStacks

User tested on:

- Heregoes UIS7862

Other Android devices should also work, but compatibility is not guaranteed.

Android head units vary significantly between manufacturers, firmware versions, and ROMs. The method used to configure a default launcher differs between devices. If you are unsure how to change your launcher, please consult your head unit manufacturer for model-specific instructions.

---

## Installation

**New to PCCM+ Launcher or Android head units? Watch the full installation and setup walkthrough on YouTube:**

▶️ [PCCM+ Launcher Installation Guide](https://youtu.be/VhX00rpIYF4)

1. Download the latest APK from the Releases page.
2. Copy the APK to a USB drive or download it directly to your Android device.
3. Install the APK on your Android head unit.
4. Configure it as your preferred launcher according to your device manufacturer's instructions.
5. Open **Settings** to select your preferred PCCM+ interface.
6. Long-press configurable launcher icons to assign the applications you want them to launch.

Existing app assignments and launcher settings are preserved when updating from previous versions.

---

## Launcher Themes

### PCCM+ .1 / Classic

The original PCCM OEM Launcher interface, based on Porsche's first-generation PCCM+ design.

Classic retains the familiar PCCM+ layout and visual style that the project was originally built around.

### PCCM+ .2 / Modern

Version 1.0 adds a completely new interface inspired by Porsche's newer PCCM+ design for the later 9X7.2 / 981 / 991.1 generation.

Modern includes:

- A completely redesigned home screen
- New high-resolution artwork and icons
- Updated typography and visual styling
- The same configurable application shortcuts as the Classic interface
- Access to the existing PCCM OEM Launcher app drawer and settings

The interface can be changed from the launcher settings without installing a separate version of the application.

---

## Current Layout

Depending on the selected interface, launcher functions include:

- Phone Link
- Phone
- Media
- Radio
- Maps
- Tuner / Sound
- Car
- Apps
- Settings

Application shortcuts can be reassigned to work with the applications installed on your particular Android head unit.

This is especially important on aftermarket units, where applications used for radio, Bluetooth, Android Auto, Apple CarPlay, navigation, and other functions vary between manufacturers.

---

## Language Support

PCCM OEM Launcher automatically follows the Android system language.

Currently supported languages:

- English
- French
- German
- Italian
- Spanish
- Portuguese
- Dutch
- Polish
- Czech
- Danish
- Swedish
- Norwegian
- Finnish
- Romanian
- Greek
- Turkish
- Russian
- Japanese
- Simplified Chinese

Unsupported system languages automatically fall back to English.

Localization includes launcher labels, the App Drawer, search interface, app-assignment dialogs, settings, and notifications.

---

# Version History

## v1.0 — PCCM+ .2 / Modern

The first full release of PCCM OEM Launcher.

### New

- Added a completely new **PCCM+ .2 / Modern** launcher interface.
- Added the ability to switch between **PCCM+ .1 / Classic** and **PCCM+ .2 / Modern** from launcher settings.
- Added a new high-resolution icon set designed specifically for the Modern interface.
- Added new Modern launcher functions including **Sound** and **Car**.
- Added dedicated settings access for both launcher interfaces.
- Updated launcher settings to support interface selection.

### Improvements

- Preserved existing user-configurable application assignments across both interfaces.
- Integrated the existing App Drawer and application search into both launcher styles.
- Retained automatic language detection and localization throughout the launcher.
- Refined interface scaling, spacing, typography, and artwork for a more consistent OEM appearance.
- Additional navigation, lifecycle, and UI refinements.

Version 1.0 marks the transition of PCCM OEM Launcher from the original single-interface project into a launcher supporting multiple generations of Porsche PCCM+ design.

---

## v0.9.2 — Multi-Language Support

- Added automatic system-language detection.
- Added support for **19 languages**:
  - English
  - French
  - German
  - Italian
  - Spanish
  - Portuguese
  - Dutch
  - Polish
  - Czech
  - Danish
  - Swedish
  - Norwegian
  - Finnish
  - Romanian
  - Greek
  - Turkish
  - Russian
  - Japanese
  - Simplified Chinese
- Launcher automatically changes language based on the Android system language.
- Unsupported system languages automatically fall back to English.
- Localized launcher labels, App Drawer, search interface, app-assignment dialogs, and notifications.
- Existing app assignments remain intact when switching languages.
- Added proper support for Latin, Cyrillic, Greek, Japanese, and Chinese character sets.
- No changes to the existing launcher layout or core functionality.

## v0.8.3

### Fixed

- Fixed an issue where pressing the **Home** button while the app drawer was open would not return to the launcher home screen.
- Improved Android activity stack handling so the app drawer is properly dismissed when returning Home.
- Returning to the launcher after launching another application now consistently displays the main launcher interface instead of leaving the app drawer open.

### Improvements

- Minor launcher navigation and lifecycle refinements.
- Additional polish toward a more OEM-like PCCM+ user experience.

## v0.8.2

- Added a dedicated full-screen **Apps** screen.
- Installed applications are displayed in a touch-friendly icon grid.
- Vertical scrolling for larger app collections.
- Built-in search for quickly locating installed apps.
- Improved application discovery across Android head units.
- Launcher updates now preserve user button assignments and settings.

## v0.7.4

- Replaced Navigation with Phone Link.
- Added combined Android Auto / Apple CarPlay icon.
- Updated launcher artwork.
- Preserved OEM PCCM+ layout and scaling.

---

## Roadmap

With the release of v1.0, the primary PCCM OEM Launcher interface and feature set is considered complete.

Future development will primarily focus on:

- Compatibility improvements for additional Android head units
- UI refinements
- Additional language support
- Bug fixes
- Performance optimizations
- Community-requested improvements

Suggestions are always welcome.

---

## Disclaimer

This project is an independent community project and is **not affiliated with or endorsed by Porsche AG**.

Porsche®, PCCM®, Apple CarPlay®, and Android Auto® are trademarks of their respective owners.

Use this software at your own risk.

---

## Contributing

Bug reports, suggestions, and pull requests are welcome.

Android head units vary significantly between manufacturers. If you test PCCM OEM Launcher on additional hardware, please consider sharing the head unit model, Android version, chipset, and your results.

Compatibility information and fixes for additional devices are especially welcome.

---

## License

MIT License

---

## Acknowledgements

Thanks to the Porsche enthusiast community for testing, feedback, and feature suggestions throughout development.

Version 1.0 would not exist without the feedback received from users testing the launcher across different Porsche models and Android head units.

---

## Support Development

If you enjoy this project and would like to support future development, consider buying me a coffee.

The launcher is, and always will be, free and open source. Donations are completely optional, but they help justify spending time improving the project and adding new features.

☕ **PayPal:** https://paypal.me/JOSPEHCOHEN

**Yes, that link is correct. Apparently 17-year-old me fat-fingered my PayPal.Me username, and I've been living with it ever since.**

Thank you for your support!
