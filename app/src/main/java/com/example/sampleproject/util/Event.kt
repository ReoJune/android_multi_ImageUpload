package com.example.sampleproject.util
/**
 * @see Event wrapper class
 */
open class Event<out T>(private val content :T) {
    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * 한번만 호출할경우 사용
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * 기본 호출시 사용
     */
    fun peekContent(): T = content
}