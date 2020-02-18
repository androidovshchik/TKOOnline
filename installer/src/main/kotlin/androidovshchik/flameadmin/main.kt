package androidovshchik.flameadmin

import org.js.neutralino.Neutralino

fun main() {
    Neutralino.app.exit({
        it.message
    }, {

    })
}