# naoTimes App

A mobile application for naoTimes bot Showtimes feature.
This utilize the published API located in [naoTimesUI](https://panel.naoti.me)

## Requirements
- JDK 11+
- Android 6.0+
- Kotlin
  
## Libraries Used
- [Jetpack Compose](https://developer.android.com/jetpack/compose) `Framework`
- [Material Design](https://developer.android.com/jetpack/androidx/releases/compose-material) `UI Kit`
- [Coil](https://coil-kt.github.io/coil/) `Image Loader`
- [Retrofit](https://square.github.io/retrofit/) `HTTP Client`
  - [NetworkResponseAdapter](https://haroldadmin.github.io/NetworkResponseAdapter/) `Interceptor for response by Retrofit`
  - [OkHttp3 Logging Interceptor](https://github.com/square/okhttp/tree/master/okhttp-logging-interceptor) `Network logging`
- [Moshi](https://github.com/square/moshi) `JSON Parser`
- [Accompanist](https://google.github.io/accompanist/) `Utility for Jetpack Compose`
  - [Flow Layout](https://google.github.io/accompanist/flowlayout/)
  - [Placeholder](https://google.github.io/accompanist/placeholder/)
  - [Swipe Refresh](https://google.github.io/accompanist/swiperefresh/)
  - [System UI Controller](https://google.github.io/accompanist/systemuicontroller/)
- [cache4k](https://github.com/ReactiveCircus/cache4k) `In-memory Cache`

## License
This project is licensed with [MIT License](LICENSE).

## Endnotes
This project is a part of my assignment for **Mobile App Engineering (MAE)** module.
