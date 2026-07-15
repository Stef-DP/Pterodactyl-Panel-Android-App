# Pterodactyl Panel Android App

An Android app to manage your [Pterodactyl Panel](https://pterodactyl.io) servers (both on user and admin side)

# Features

- User server management
- User account management
- Admin Server management (along with their databases)
- Admin users management
- Admin locations management
- Admin nodes management (along with their allocations)
- Admin locations management
- Admin nests management (alonf with their eggs, both read-only as per API limitations)

# Download

The app will be available on the following platofrms:
- Google Play Store
- Forgejo Releases
- (maybe F-Droid, but idk since my last app is taking way too long, still in review after 3 months)

# Creating a development build

To create a development build just run `./gradlew assembleDebug` or use the Android Studio Emulator

This will create an APK in `app/build/outputs/apk/debug/app-debug.apk`

> Building an apk

just run `./gradlew assembleRelease`

This will create an APK in `app/build/outputs/apk/release/app-release(-unsigned).apk`

# Support

You can support me by sponsoring on [GitHub](https://github.com/sponsors/Stef-00012) or donatng on [Stripe](https://donate.stripe.com/00w3cvef7bHyfyHdoK63K00) or [Ko-Fi](https://ko-fi.com/stef_dp)

# Stardance

Once the app is finished and shipped on stardance, here I will add credentials for a normal user on my panel so they can try the user side (if they want to try their amdin side, they must self-host it themselves, i think nest)