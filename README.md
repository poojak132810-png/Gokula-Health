# Gokula-Health

A digital health card and livestock management Android app for small-scale dairy farmers. Built with Kotlin, MVVM architecture, and Material Design 3.

## Project Details

| | |
|---|---|
| **Name** | POOJA K |
| **USN** | 1ME22CS105 |
| **College** | MS Engineering College |
| **Academic Year** | 2025–2026 |

## Features

- **Cattle Management** — Register cattle with photo, ear tag ID, name, breed, and date of birth. View full profiles with linked milk entries and vaccination history.
- **Milk Diary** — Record daily morning and evening milk yields per cattle. Auto-calculates total yield and monthly average.
- **Vaccination Tracker** — Log vaccinations (FMD, HS, BQ, Deworming, etc.) with due date tracking. Color-coded status: red (overdue), yellow (due within 7 days), green (upcoming).
- **Yield Reports** — 30-day milk yield line chart powered by MPAndroidChart with dashed average line and monthly summary.
- **Offline Notifications** — AlarmManager-based vaccination reminders at 8:00 AM on due dates. Works fully offline. Persists across device reboots.
- **Fully Offline** — All data stored locally with Room DB. No internet required.

## Download APK

You can download the latest APK from the [Releases](../../releases) section of this repository.

## Tech Stack

| Component | Technology |
|---|---|
| Language | Kotlin |
| Architecture | MVVM (ViewModel + LiveData) |
| Database | Room DB |
| UI | Material Design 3 |
| Charts | MPAndroidChart |
| Image Loading | Glide |
| Navigation | Jetpack Navigation Component |
| Notifications | AlarmManager + BroadcastReceiver |

## Requirements

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Min SDK: 24 (Android 7.0)
- Target SDK: 34 (Android 14)

## How to Build and Run

1. Clone the repository:
   ```bash
   git clone https://github.com/YOUR_USERNAME/Gokula-Health.git
   ```
2. Open the project in Android Studio.
3. Let Gradle sync and download all dependencies.
4. Connect an Android device or start an emulator (API 24+).
5. Click **Run** or press `Shift+F10`.

### Build APK from command line

```bash
./gradlew assembleDebug
```

The APK will be at `app/build/outputs/apk/debug/app-debug.apk`.

## Project Structure

```
app/src/main/java/com/gokulahealth/
├── GokulaHealthApp.kt              # Application class (notification channel)
├── data/
│   ├── entity/                     # Room entities (Cattle, MilkEntry, Vaccination)
│   ├── dao/                        # Data Access Objects
│   ├── database/                   # AppDatabase singleton
│   └── repository/                 # Repository layer
├── viewmodel/                      # ViewModels (Cattle, Milk, Vaccination)
├── ui/
│   ├── MainActivity.kt             # Main activity with bottom navigation
│   ├── cattle/                     # Cattle list, add, detail fragments
│   ├── milk/                       # Milk diary fragment
│   ├── vaccination/                # Vaccination tracker fragment
│   ├── reports/                    # Yield chart fragment
│   └── adapter/                    # RecyclerView adapters
└── notification/                   # NotificationHelper, AlarmReceiver, BootReceiver
```

## Screens

| Screen | Description |
|---|---|
| Dashboard | List of all registered cattle as cards with FAB to add new |
| Cattle Profile | Register new cattle with photo upload, view details |
| Milk Diary | Daily AM/PM yield entry with auto-total and monthly average |
| Vaccination | Add records, color-coded status, offline alarm notifications |
| Reports | 30-day line chart with dashed average line |

## Permissions

- `POST_NOTIFICATIONS` — Vaccination reminders (Android 13+ runtime request)
- `SCHEDULE_EXACT_ALARM` — Precise alarm scheduling
- `RECEIVE_BOOT_COMPLETED` — Re-schedule alarms after reboot
- `READ_MEDIA_IMAGES` — Load cattle photos from gallery

## License

This project is developed as part of academic coursework at MS Engineering College.
