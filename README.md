# Roll Call — Global Video Chat App by Pulselab

**Repository:** `pulselab-coder/Roll-Call-Video-Chat-App-by-Pulselab`  
**Package ID:** `com.aistudio.rollcall.vchatx`

Roll Call is a 1-on-1 global random video, voice, and ephemeral messaging Android application built with Kotlin, Jetpack Compose (Material 3), CameraX, and Room local persistence.

---

## Core Features

- **18+ Age Gate & Biometric ID Verification Onboarding**: Mandatory age verification (blocks underage birth years), 3rd-party ID/liveness verification status, gender and preferred-match-gender selection (`Male`, `Female`, `Any`), interest tags, and runtime Camera/Microphone permissions.
- **Global Gender-Filtered Matchmaking**: Queue-based 1-on-1 matchmaking with active rate-limiting (max 5 skips per 60 seconds with a 30-second cooldown) and live CameraX front/back camera preview.
- **Anti-Recording & Anti-Screenshot Enforcement (`FLAG_SECURE`)**: Enforces `WindowManager.LayoutParams.FLAG_SECURE` and `AudioAttributes.ALLOW_CAPTURE_BY_NONE` during active video/voice calls and full-screen ephemeral media viewing.
- **1-on-1 Ephemeral Chat & 60-Second Media Expiry**: Strict 1:1 messaging (no group chats) with real-time 60-second countdown expiry triggered on first view, blocked save-to-gallery/forwarding for free users, toggleable read receipts, and inline auto-translation.
- **Points & Weekly Global Leaderboard**:
  - `1 hour` of active in-app time = `100 points`
  - `200 points` minimum required to unlock the Weekly Global Leaderboard (resets Mondays at 00:00 UTC).
- **Roll Call Pro ($19.99/mo or $149.99/yr) & Points Store**:
  - Unlocks the **Permanent Media Vault** (keeps sent & received media permanently inside encrypted app storage).
  - Unlocks tiered point packages (`100 pts / $1.49`, `200 pts / $2.49`, `500 pts / $5.49`, `1,000 pts / $9.99`, `2,500 pts / $19.99`).
  - **100% Ad-Free Experience**: Automatically disables all Banner, Interstitial, and Rewarded ads when subscribed to Pro.
- **Trust, Safety & Compliance**: 1-tap in-call Panic Button, Report & Block system, PhotoDNA CSAM hash-matching & NCMEC reporting workflow indicators, and Cloud Vision AI moderation shield status.

---

## AdMob Integration

Configured in `com.example.ui.ads.RollCallAdIds`:
- **Banner Ad Unit ID**: `ca-app-pub-3940256099942544/6300978111`
- **Interstitial Ad Unit ID**: `ca-app-pub-3940256099942544/1033173712`
- **Rewarded Ad Unit ID**: `ca-app-pub-3940256099942544/5224354917`

All ads are suppressed when `userProfile.isPremiumPro == true`.

---

## Building the APK Locally

1. Clone the repository:
   ```bash
   git clone https://github.com/pulselab-coder/Roll-Call-Video-Chat-App-by-Pulselab.git
   cd Roll-Call-Video-Chat-App-by-Pulselab
   ```
2. Build the debug APK:
   ```bash
   ./gradlew assembleDebug
   ```
3. Locate the generated APK at:
   ```text
   app/build/outputs/apk/debug/app-debug.apk
   ```
