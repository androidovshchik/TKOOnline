package neutralino

external class StorageData {

    var bucket: String

    var content: Any
}

external interface Storage {

    fun putData(data: StorageData, s: Success<dynamic>, e: Error)

    fun getData(bucket: String, s: Success<Any>, e: Error)
}