package com.klice.pressureheight.service

interface DataProviderService<T> {
    fun registerListener(listener: (d: T) -> Unit)
}

abstract class SubscriptionService<T> {
    protected val subscribers = mutableListOf<(d: T) -> Unit>()
    fun registerListener(listener: (d: T) -> Unit) {
        subscribers.add(listener)
    }

    protected fun notifySubscribers(value: T) {
        subscribers.forEach {
            it(value)
        }
    }
}