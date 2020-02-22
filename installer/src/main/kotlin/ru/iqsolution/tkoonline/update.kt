package ru.iqsolution.tkoonline

import Axios

class ResponseVersion {

    var version: Number = 0

    lateinit var url: String
}

fun update() {
    Axios.get<ResponseVersion>("https://msknt3.iqsolution.ru/mobile/version.json")
        .then {
            execCommand("sh app/tools/download.sh ${it.data.url}") {

            }
        }
        .catch {

        }
}