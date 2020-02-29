For logcat:

`^(?!CameraFramework)`

Commands:

`adb shell dpm set-device-owner ru.iqsolution.tkoonline/.receivers.AdminReceiver`

`apksigner verify -print-certs *.apk | grep -Po "(?<=SHA-256 digest:) .*" | xxd -r -p | openssl base64 | tr -- '+/' '-_'`