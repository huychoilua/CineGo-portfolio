# CineGo
CineGo - Android app for booking movie tickets with AI-powered recommendations and seat selection

## Author
Designed and developed by **Nguyễn Đại Minh Huy** as an academic Android project.

## Technologies and features
- Android Java; Firebase Authentication and Realtime Database.
- Movie browsing/search, movie details, showtime/seat selection, snacks and booking records.
- Ticket QR codes with ZXing; notifications and user profiles.
- AI chat integration with Groq via OkHttp.

## Local setup
Open the project in Android Studio and review the existing Gradle configuration. Configure your own Firebase project and add its `google-services.json` to `app/`. Replace the `YOUR_PROJECT` database URL placeholders with your own database endpoint. Supply your own AI service configuration; do not commit credentials. A production app should route privileged API requests through a backend instead of embedding secrets in the mobile app.

Firebase project configuration and any credential-like strings from the source snapshot have been excluded or replaced. No Firebase data export is included. This portfolio copy has not been build-tested. Payment UI is a project demonstration; a real payment integration is not claimed.
