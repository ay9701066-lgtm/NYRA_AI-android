# NYRA AI — free Android starter project

## Is version mein kya chalega?
- Hindi/Hinglish basic chat replies (offline, rule-based): hello, kaise ho, time/samay, help, YouTube kholo, Google search
- Mic button se Hindi speech-to-text (phone ki Google voice typing service par depend)
- Hindi text-to-speech (phone mein Hindi voice data hona chahiye)
- YouTube aur Google Search ke quick buttons

## Zaroori sachchai
Yeh **source project** hai, ready-made APK nahi. APK banane ke liye GitHub Actions workflow diya gaya hai jo free mein debug APK build karta hai.
Is app mein abhi real ChatGPT-level AI, always-on wake word, background listening, calls/SMS automation ya guaranteed offline speech recognition nahi hai. Inke liye alag development, permissions aur AI/API service chahiye.

## Redmi phone se APK kaise banayein (free)
1. GitHub par login karo: https://github.com
2. Repository `NYRA_AI` (public) kholo.
3. Is project ki **saari files aur folders** repository mein upload karo. `.github` folder bhi upload hona chahiye (`Add file` > `Upload files`).
4. `Actions` tab kholo. `Build NYRA AI APK` workflow select karo aur agar run nahi hua to `Run workflow` dabao.
5. Build complete hone par workflow run kholo. Neeche `Artifacts` mein `NYRA-AI-debug-APK` download karo.
6. ZIP artifact ko phone ke Downloads mein extract karo. Andar `app-debug.apk` milega.
7. APK par tap karo. Android kahe to sirf apne download kiye hue APK ke liye browser/file manager ko `Install unknown apps` permission do; install ke baad permission wapas off kar do.

## Security
- Is project mein API key nahi hai aur messages kisi server par send nahi kiye jaate.
- App ko koi special permission nahi chahiye. Mic button Android ki voice typing screen kholta hai, isliye app khud microphone access nahi leti.
- Speech recognition Google/Android service se chalti hai; uski privacy aur availability us service par depend karti hai.
- Public repository mein kabhi password, API key, personal data ya private files upload mat karna.

## Build details
- Package: `com.nyra.ai`
- Minimum Android: Android 6.0 (API 23), target Android 15 (API 35)
- Build: Debug APK, personal testing ke liye
