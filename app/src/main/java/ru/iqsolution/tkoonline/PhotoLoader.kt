package ru.iqsolution.tkoonline

import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.signature.ObjectKey
import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import java.io.File
import java.io.FileNotFoundException

class PhotoLoader : ModelLoader<PhotoEvent, File> {

    override fun buildLoadData(
        reference: PhotoEvent,
        height: Int,
        width: Int,
        options: Options
    ): ModelLoader.LoadData<File>? {
        return ModelLoader.LoadData(ObjectKey(System.currentTimeMillis()), PhotoFetcher(reference))
    }

    override fun handles(reference: PhotoEvent): Boolean {
        return true
    }

    @Suppress("unused")
    class Factory : ModelLoaderFactory<PhotoEvent, File> {

        override fun build(factory: MultiModelLoaderFactory): ModelLoader<PhotoEvent, File> {
            return PhotoLoader()
        }

        override fun teardown() {}
    }
}

private class PhotoFetcher(private val photoEvent: PhotoEvent) : DataFetcher<File> {

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in File>) {
        val file = File(photoEvent.path)
        if (file.exists()) {
            callback.onDataReady(file)
        } else {
            callback.onLoadFailed(FileNotFoundException(photoEvent.path))
        }
    }

    override fun cleanup() {}

    override fun cancel() {}

    override fun getDataClass() = File::class.java

    override fun getDataSource() = DataSource.LOCAL
}