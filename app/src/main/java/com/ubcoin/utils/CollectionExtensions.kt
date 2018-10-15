package com.ubcoin.utils

/**
 * Created by Yuriy Aizenberg
 */

object CollectionExtensions {

    fun nullOrEmpty(list: List<Any>?): Boolean = list == null || list.isEmpty()


}

fun <T, V> Map<T, V>.hashCodeKeys() : Int {
    return HashSet<V>(values).hashCode()
}