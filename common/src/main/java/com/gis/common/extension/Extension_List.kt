package com.gis.common.extension

/**
 * 会对position进行判断 如果都大于数组大小那么会直接返回null
 */
fun <E> List<E>?.safeGet(position: Int): E? {
    return if (this == null || position < 0 || position >= size) null else get(position)
}
