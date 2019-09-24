package ru.iqsolution.tkoonline.remote

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.annotations.SerializedName

class SerializedNameStrategy : ExclusionStrategy {

    override fun shouldSkipField(attributes: FieldAttributes): Boolean {
        return attributes.getAnnotation(SerializedName::class.java) == null
    }

    override fun shouldSkipClass(clazz: Class<*>): Boolean {
        return false
    }
}