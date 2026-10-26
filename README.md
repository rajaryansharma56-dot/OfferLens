\# OfferLens — Android App



\*\*OfferLens\*\* is a student-focused Android application that helps users identify potential warning signs in job offers, internship messages, and recruiter communications.



It analyzes pasted text using an on-device, rule-based screening system and provides a risk indicator, matched warning phrases, explanations, and recommended safety actions.



\## Features



\- \*\*Job Offer Analysis:\*\* Analyze job and internship messages for common warning signs.

\- \*\*Risk Assessment:\*\* View a risk score and a risk category.

\- \*\*Evidence-Based Explanations:\*\* See matched phrases and why they may be concerning.

\- \*\*Positive Signals:\*\* Identify potentially reassuring language where applicable.

\- \*\*Safety Recommendations:\*\* Get practical steps to help verify recruiters and offers.

\- \*\*Recruiter Verification Checklist:\*\* Review important verification steps and track progress.

\- \*\*Scan History:\*\* Revisit previous analyses stored locally on the device.

\- \*\*Sample Messages:\*\* Test the application with example messages.

\- \*\*Dark UI:\*\* A modern, dark-themed interface with cyan accents.

\- \*\*Privacy-Conscious Design:\*\* Message analysis runs on-device; scan history is stored locally.



\## Tech Stack



\- \*\*Language:\*\* Kotlin

\- \*\*UI:\*\* Jetpack Compose

\- \*\*Design Components:\*\* Material 3

\- \*\*Local Storage:\*\* SharedPreferences

\- \*\*Data Handling:\*\* JSON

\- \*\*Development Environment:\*\* Android Studio

\- \*\*Version Control:\*\* Git and GitHub



\## How It Works



1\. Paste a job offer, internship message, or recruiter communication.

2\. Tap the analysis button.

3\. Review the risk category, score, matched phrases, and explanations.

4\. Follow the suggested safety checks before sharing information or making payments.



\## Getting Started



\### Requirements



\- Android Studio

\- Android SDK compatible with the project configuration

\- Kotlin and Gradle support provided by Android Studio



\### Run the Project



1\. Clone this repository:



&#x20;  ```bash

&#x20;  git clone https://github.com/rajaryansharma56-dot/OfferLens.git

&#x20;  ```



2\. Open the `OfferLens` project in Android Studio.

3\. Allow Gradle to sync.

4\. Run the app on an Android device or emulator.



\## Important Limitations



OfferLens uses \*\*rule-based text matching\*\*, not a validated machine-learning fraud detection model. Its score is a screening indicator, not the probability that an offer is fraudulent.



\- A high-risk result does not conclusively prove fraud.

\- A lower-risk result does not guarantee that an offer is genuine.

\- Some scams may not contain the phrases covered by the current rules.

\- Always independently verify the employer, recruiter, official company contact details, and offer terms.

\- Never share an OTP, UPI PIN, password, or other authentication secret with a recruiter.



\## Project Goal



The goal of OfferLens is to make basic job-offer safety checks more accessible to students and job seekers through a simple Android interface.



\## Author



\*\*Raj Aryan Sharma\*\*



GitHub: \[rajaryansharma56-dot](https://github.com/rajaryansharma56-dot)



\## Disclaimer



OfferLens is an educational safety-support tool. It does not replace independent verification or professional advice and does not guarantee scam detection.



