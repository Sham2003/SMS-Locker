# 📩 SMS Locker (Kotlin + XML)

An Android security tool that monitors SMS messages and triggers a lock based on secret keywords or patterns. Built using **Kotlin** and **XML layouts**, this app is perfect for remote phone protection, especially when the device is **lost** or **stolen**.
This is an XML Based before I created the compose version in the main branch
---

## 🚀 Features

- 🔒 Lock your app remotely via SMS
- 🔍 Pattern-based SMS detection
- 📜 Configurable list of passwords/commands
- 🛡️ Useful for tracking & securing a lost phone
- ⚡ Lightweight and fast — no background battery drain
- ✅ Built using Kotlin & XML (no Jetpack Compose)

---

## 🛠️ How It Works

1. The app listens for incoming SMS.
2. If the message matches any pattern in your **password list**, it:
   - Triggers a lock screen
   - Optionally starts security actions (like ringing, alert, etc.)
3. Great for triggering remote locks via SMS if your phone is misplaced or stolen.

---

## 🧠 Use Case Example

Set secret pattern as: LOCKME123
Send SMS:I_LOCK LOCKME123 from any phone

App detects the keyword and locks the screen immediately

Can also get the location using I_LOCATE SMS prefix

---

## 🔧 Setup

1. **Clone the repo**  
   ```bash
   git clone https://github.com/Sham2003/SMS-Locker

2. **Open in Android Studio**
    Open the project folder and let Gradle sync.

3. **Run on device**
    Connect your device and hit "Run".
