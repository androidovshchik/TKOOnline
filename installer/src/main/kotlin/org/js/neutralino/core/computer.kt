package org.js.neutralino.core
{
    "ram" :  {
    "available" : 6790840,
    "total" : 6790840
}
}

external interface Computer {

    fun getRamUsage(s: (Any) -> Unit, e: (dynamic) -> Unit)
}