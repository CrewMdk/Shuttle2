package com.simplecityapps.playback.queue

import timber.log.Timber

interface QueueChangeCallback {

    enum class QueueChangeReason {
        Unknown, Move
    }

    fun onQueueChanged(reason: QueueChangeReason = QueueChangeReason.Unknown) {
    }

    fun onQueuePositionChanged(oldPosition: Int?, newPosition: Int?) {
    }

    fun onShuffleChanged(shuffleMode: QueueManager.ShuffleMode) {
    }

    fun onRepeatChanged(repeatMode: QueueManager.RepeatMode) {
    }

    fun onQueueRestored() {
    }
}

class QueueWatcher : QueueChangeCallback {

    private var callbacks: MutableList<QueueChangeCallback> = mutableListOf()

    fun addCallback(callback: QueueChangeCallback) {
        callbacks.add(callback)
    }

    fun removeCallback(callback: QueueChangeCallback) {
        callbacks.remove(callback)
    }

    // QueueChangeCallback Implementation

    override fun onQueueChanged(reason: QueueChangeCallback.QueueChangeReason) {
        Timber.v("onQueueChanged()")
        callbacks.forEach { callback -> callback.onQueueChanged(reason) }
    }

    override fun onQueuePositionChanged(oldPosition: Int?, newPosition: Int?) {
        Timber.v("onQueuePositionChanged(oldPosition: $oldPosition, newPosition: $newPosition)")
        callbacks.forEach { callback -> callback.onQueuePositionChanged(oldPosition, newPosition) }
    }

    override fun onShuffleChanged(shuffleMode: QueueManager.ShuffleMode) {
        Timber.v("onShuffleChanged(shuffleMode: $shuffleMode)")
        callbacks.forEach { callback -> callback.onShuffleChanged(shuffleMode) }
    }

    override fun onRepeatChanged(repeatMode: QueueManager.RepeatMode) {
        Timber.v("onRepeatChanged(repeatMode: $repeatMode)")
        callbacks.forEach { callback -> callback.onRepeatChanged(repeatMode) }
    }

    override fun onQueueRestored() {
        Timber.v("onQueueRestored()")
        callbacks.forEach { callback -> callback.onQueueRestored() }
    }
}
