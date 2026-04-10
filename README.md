[![Codacy Badge](https://app.codacy.com/project/badge/Grade/df9b05b34af1456fbb8fe75fbab0f6f2)](https://app.codacy.com/gh/Grigoriym/TaigaMobileNova/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade) [![codecov](https://codecov.io/gh/Grigoriym/TaigaMobileNova/branch/master/graph/badge.svg?token=8SI5NVSBNF)](https://codecov.io/gh/Grigoriym/TaigaMobileNova)

# Taiga Mobile Nova

This is the **unofficial** Kotlin Multiplatform client for the agile project management system [taiga.io](https://www.taiga.io/), targeting **Android**, **iOS**, and **Desktop** (Linux/macOS/Windows).

The previous author archived the original project. This version has been completely rewritten using Kotlin Multiplatform and Compose Multiplatform.

## Platforms

| Platform | Status | Distribution |
|----------|--------|--------------|
| Android  | Released | Google Play & F-Droid |
| iOS      | Builds & runs | Distribution TBD |
| Desktop (Linux / macOS / Windows) | Builds & runs | Distribution TBD |

### Android

[<img src="docs/google-badge.png"
alt="Get it on Google Play"
height="80">](https://play.google.com/store/apps/details?id=com.grappim.taigamobile)
[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png"
alt="Get it on F-Droid"
height="80">](https://f-droid.org/en/packages/com.grappim.taigamobile.fdroid/)

### iOS & Desktop

iOS and Desktop builds are functional but distribution channels are not yet set up. If you want to try them, clone the repo and build locally — see [Build Commands](#build-commands) below.

[Project board](https://tasks.gregstuff.click/project/taigamobilenova/kanban)

## Screenshots

| Dark Mode | Light Mode |
|-----------|------------|
| <img width="400" height="900" alt="Login - Dark" src="./info/art/login-dark.png" /> | <img width="400" height="900" alt="Login - Light" src="./info/art/login-light.png" /> |
| <img width="400" height="900" alt="Dashboard - Dark" src="./info/art/dashboard-dark.png" /> | <img width="400" height="900" alt="Dashboard - Light" src="./info/art/dashboard-light.png" /> |
| <img width="400" height="900" alt="Bookmarks - Dark" src="./info/art/bookmarks-dark.png" /> | <img width="400" height="900" alt="Bookmarks - Light" src="./info/art/bookmarks-light.png" /> |
| <img width="400" height="900" alt="Drawer - Dark" src="./info/art/drawer-dark.png" /> | <img width="400" height="900" alt="Drawer - Light" src="./info/art/drawer-light.png" /> |
| <img width="400" height="900" alt="Kanban - Dark" src="./info/art/kanban-dark.png" /> | <img width="400" height="900" alt="Kanban - Light" src="./info/art/kanban-light.png" /> |
| <img width="400" height="900" alt="Issue Details - Dark" src="./info/art/issue-dark.png" /> | <img width="400" height="900" alt="Issue Comments - Dark" src="./info/art/issue-2-dark.png" /> |
| <img width="400" height="900" alt="Issues List - Dark" src="./info/art/issues-dark.png" /> | |

## Features

### View & Browse
* Projects
* Epics
* User stories
* Tasks
* Issues
* Sprints
* Profiles
* Wiki
* Dashboard

### Create, Edit & Delete
* Epics
* User stories
* Tasks
* Issues
* Sprints
* Wiki pages

### Additional Features
* Leave and delete comments
* Kanban board (for sprints and user stories)
* Filters for user stories, epics, and issues
* Permissions validation

## Build Commands

```bash
# Android
./gradlew :androidApp:assembleGplayDebug
./gradlew :androidApp:assembleFdroidDebug

# Desktop — run or package
./gradlew :composeApp:run
./gradlew :composeApp:packageDistributionForCurrentOS   # .deb / .dmg / .msi

# iOS framework (called automatically by Xcode)
./gradlew :composeApp:linkReleaseFrameworkIosSimulatorArm64
./gradlew :composeApp:linkReleaseFrameworkIosArm64
```

## GitHub OAuth Authentication

TaigaMobileNova supports logging in with GitHub for Taiga instances that have GitHub OAuth configured on the server side.

### How It Works

1. The user enters the Taiga server URL and taps **Continue with GitHub**.
2. The app validates the server URL and opens the GitHub OAuth authorization page in the system browser.
3. After the user grants access on GitHub, the browser redirects to `taigamobile://github-callback?code=<CODE>`.
4. The app receives this callback, extracts the code, and sends it to the Taiga API (`POST /api/v1/auth` with `type=github`).
5. On success, the user is logged in and the auth tokens are stored securely.

### Prerequisites

Before GitHub login works, you need to:

1. **Create a GitHub OAuth app** (one app per Taiga installation):
   - Go to [GitHub → Settings → Developer settings → OAuth Apps → New OAuth App](https://github.com/settings/applications/new)
   - Set **Homepage URL** to your Taiga server URL (e.g., `https://taiga.example.com`)
   - Set **Authorization callback URL** to `taigamobile://github-callback`
   - Note the **Client ID** (the Client Secret stays on the Taiga server)

2. **Configure your Taiga server** to use this GitHub OAuth app (add `GITHUB_API_CLIENT_ID` and `GITHUB_API_CLIENT_SECRET` to your Taiga settings).

3. **Build the mobile app** with the GitHub Client ID:

### Required Environment Variables

| Variable | Description |
|---|---|
| `GITHUB_OAUTH_CLIENT_ID` | The GitHub OAuth App Client ID for your Taiga instance |

### Setting the Client ID

**For local development**, add to `local.properties`:
```properties
github.oauth.client_id=your_github_oauth_client_id_here
```

**For CI/CD (GitHub Actions)**, add a repository secret:
- Go to your repository → **Settings → Secrets and variables → Actions**
- Add a secret named `GITHUB_OAUTH_CLIENT_ID` with the value of your GitHub OAuth App Client ID

Then reference it in your workflow:
```yaml
env:
  GITHUB_OAUTH_CLIENT_ID: ${{ secrets.GITHUB_OAUTH_CLIENT_ID }}
```

### Platform Support

| Platform | Deep-link Handling |
|---|---|
| Android | Handled via `taigamobile://github-callback` intent-filter in `AndroidManifest.xml` |
| iOS | Register `taigamobile` as a URL scheme in `Info.plist` (see note below) |
| Desktop (JVM) | The system browser will open; manual code paste is not yet supported |

> **iOS Note:** To enable the deep link callback on iOS, add the following to `iosApp/Info.plist`:
> ```xml
> <key>CFBundleURLTypes</key>
> <array>
>   <dict>
>     <key>CFBundleURLSchemes</key>
>     <array>
>       <string>taigamobile</string>
>     </array>
>   </dict>
> </array>
> ```

### Notes for the Repository Owner

To enable GitHub OAuth in CI/CD and in the published builds, please:

1. Create a GitHub OAuth App as described above, with callback URL `taigamobile://github-callback`.
2. Add the `GITHUB_OAUTH_CLIENT_ID` secret to this repository's GitHub Actions secrets.
3. Update your Taiga server configuration with the same GitHub OAuth app credentials.

If `GITHUB_OAUTH_CLIENT_ID` is not set at build time, the "Continue with GitHub" button will show an error message when tapped — no build failure will occur.

## About
This project is a complete rewrite of the [original TaigaMobile app](https://github.com/EugeneTheDev/TaigaMobile) (now archived), rebuilt from scratch with Kotlin Multiplatform, Compose Multiplatform, and modern architecture.
