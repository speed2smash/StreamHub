# StreamHub MVP

Android proof-of-concept for a legal streaming discovery/launcher app.

## Features
- Streaming-style dark home screen
- Search titles and genres
- Movie/TV detail pages
- Watchlist
- Provider launch buttons for Netflix, Prime Video, Disney+ and Crunchyroll
- GitHub Actions workflow that produces an installable debug APK

## Build an APK with GitHub Actions
1. Put the **contents of this folder at the root of the GitHub repository**. Do not upload this project as another ZIP inside the repository.
2. Open the repository's **Actions** tab.
3. Select **Build Android APK**.
4. Tap **Run workflow**.
5. When the workflow completes, open the run.
6. Under **Artifacts**, download **StreamHub-MVP-debug**.
7. Extract the downloaded ZIP and install `app-debug.apk` on an Android device.

## Current limitations
The catalogue and streaming availability are demo data. This MVP does not host, copy or bypass DRM-protected streams. A commercial release needs licensed/current catalogue and availability data.
