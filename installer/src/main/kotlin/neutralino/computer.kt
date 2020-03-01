package neutralino

external class RamData {

    var available: Int

    var total: Int
}

external class RamUsageData {

    var ram: RamData
}

external interface Computer {

    fun getRamUsage(s: Success<RamUsageData>, e: Error)
}