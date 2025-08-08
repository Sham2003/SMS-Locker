# 📩 SMS Locker — Revised (Compose) — Readable & Elegant

**This is a revised version of the original `master` branch (XML / view-based).**
The app has been converted from an XML view-based implementation into a modern **Jetpack Compose** codebase. The refactor focuses on **readable code**, **elegant UI**, and improved architecture so the project is easier to maintain and extend.

---

## 🚀 What changed (high-level)

* ✅ **UI migration:** All screens previously in XML are now implemented in **Jetpack Compose**.
* ✅ **Same core logic:** SMS parsing, password/command matching, and lock behavior preserved from master.
* ✅ **Readable code:** Clear packages (`ui`, `data`, `receivers`, `utils`) and single-responsibility composable.
* ✅ **Elegant design:** Material 3 theming, consistent spacing, and accessible controls.
* ✅ **Migration notes:** Keep the `master` XML branch for reference; main branch is the recommended development branch going forward.

---

## ✨ New & Improved Features

* 🔒 **Remote lock via SMS** — same as before, now with cleaner UI and better lifecycle handling.
* 🧭 **Locate (I\_LOCATE)** — request device location and receive it via preferred channel (SMS / logs / callback handler).
* 🔁 **Pattern-based detection** — password list supports full strings and **regex** patterns.
* 🧾 **Editable command list** — add / edit / delete the admin commands from the app (Compose dialogs included).
* ♻️ **Auto-backup & restore** — optional local JSON backup of your passwords/commands.
* 📦 **Modular codebase** — `service` (SMS listener), `ui` (compose screens), `data` (prefs).

---

## 🧩 Example Commands / Patterns

* `LOCK I123` — exact match, locks the device when detected.
* `LOCATE` — triggers current location sms.
* `^I_LOCK\s+([A-Z0-9_]+)` — regex example capturing a token after `I_LOCK` prefix.

> **Note:** The app evaluates regex patterns only if you enable `Use regex` for a password entry.

---

## 🛠️ Project Structure (recommended)

```
/app
  /src
    /main
      /java
        /com/sham/smslocker
          /ui        # Compose screens, dialogs, navigation
          /receivers   # SMS receiver, LocationService, LockService
          /data      # AppPreferences
          /utils     # helpers: PermissionLocker
          Activity Files
```

---

## 🔧 Setup (Run locally)

1. Clone the repo (compose branch):

   ```bash
   git clone https://github.com/Sham2003/SMS-Locker --branch main
   cd SMS-Locker
   ```
2. Open with Android Studio (Electric Eel or newer recommended). Let Gradle sync.
3. Connect a device (recommended) or use an emulator (enable SMS in emulator).
4. Run the app.

**Permissions:** The app requires `RECEIVE_SMS`, `READ_SMS` (if reading existing messages), `ACCESS_FINE_LOCATION` (for locate), and optionally `POST_NOTIFICATIONS` on newer Android versions. Compose screens include runtime permission flows.

---

## 🔄 Migration Notes (XML -> Compose)

* All the XML legacy code are in the `master` branch
---


## 📜 License

MIT — do whatever but credit the original author.

---

## 📎 Quick links

* Original view-based master: `master` branch (kept for history & comparisons).
* Current recommended branch: `main` (this revised version).

---

