# OfferLens 🔍
### Android App for Job & Internship Offer Safety

OfferLens is a student-focused Android application that helps users review job offers, internship messages, and recruiter communications for common warning signs.

It analyzes text using an **on-device, rule-based screening system** and provides a risk indicator, matched phrases, explanations, and recommended safety checks.

> ⚠️ OfferLens is a screening aid, not a guarantee that an offer is genuine or fraudulent.

## 📱 App Screenshots

<!-- Replace these filenames with the exact image filenames in the repository. -->

| Home Screen | High-Risk Analysis |
|---|---|
| ![OfferLens Home](home.png) | ![High-Risk Analysis](high-risk.png) |

### Recruiter Verification Checklist

![Verification Checklist](checklist.png)

## ✨ Features

- **Message Analysis:** Review job offers, internship messages, and recruiter communications.
- **Risk Indicator:** View a risk score and category based on detected rules.
- **Warning Evidence:** See matched phrases and explanations of potential concerns.
- **Positive Signals:** Display potentially reassuring signals when detected.
- **Recommended Actions:** Get practical suggestions for checking an offer.
- **Verification Checklist:** Review important recruiter and employer verification steps.
- **Scan History:** Revisit previous analyses stored locally.
- **Sample Messages:** Try the analyzer with example text.
- **Dark Interface:** A modern dark-themed UI with cyan accents.

## 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| Kotlin | Application programming language |
| Jetpack Compose | Declarative Android UI |
| Material 3 | UI components and styling |
| SharedPreferences | Local scan-history storage |
| JSON | Local history serialization |
| Android Studio | Development environment |
| Git & GitHub | Version control and source hosting |

## ⚙️ How It Works

1. Paste a job or internship message into the app.
2. Run the analysis.
3. Review the risk category, score, and matched warning phrases.
4. Read the explanations and recommended actions.
5. Independently verify the recruiter and employer before sharing information or making decisions.

## 🚀 Getting Started

### Requirements

- Android Studio
- Android SDK compatible with the project configuration
- An Android device or emulator

### Run Locally

1. Clone the repository:

   ```bash
   git clone https://github.com/rajaryansharma56-dot/OfferLens.git
   ```

2. Open the `OfferLens` folder in Android Studio.
3. Allow Gradle to sync.
4. Run the app on a connected Android device or emulator.

## 🔐 Privacy

The message analysis is performed on-device by the app's rule-based analyzer. Scan history is stored locally using SharedPreferences.

This describes the current implementation; it should not be interpreted as a guarantee about every Android backup or device-level data setting.

## ⚠️ Limitations

- OfferLens uses rule-based text matching, not a validated machine-learning fraud detector.
- The risk score is **not a probability** that a message is fraudulent.
- A high-risk result does not conclusively prove fraud.
- A lower-risk result does not guarantee that an offer is legitimate.
- Scams may use wording that the current rules do not recognize.
- Always verify job offers through official company websites and independently obtained contact details.
- Never share OTPs, UPI PINs, passwords, or other authentication secrets with recruiters.

## 🎯 Project Goal

OfferLens aims to make basic job-offer safety checks more accessible to students and job seekers through a simple Android application.

## 👨‍💻 Author

**Raj Aryan Sharma**

GitHub: [rajaryansharma56-dot](https://github.com/rajaryansharma56-dot)

**Repository:** [OfferLens on GitHub](https://github.com/rajaryansharma56-dot/OfferLens)

## 📄 Disclaimer

OfferLens is an educational safety-support tool. It does not guarantee scam detection and does not replace independent verification or professional advice.
