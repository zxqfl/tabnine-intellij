package com.tabnine.lifecycle

import com.tabnine.binary.BinaryRequestFacade
import com.tabnine.general.StaticConfig.BINARY_PROMOTION_POLLING_DELAY
import com.tabnine.general.StaticConfig.BINARY_PROMOTION_POLLING_INTERVAL
import com.tabnine.statusBar.StatusBarUpdater
import java.util.Timer
import kotlin.concurrent.timerTask

class BinaryPromotionStatusBarLifecycle(private val binaryRequestFacade: BinaryRequestFacade) {
    private val statusBarUpdater = StatusBarUpdater(binaryRequestFacade)

    fun poll() {
        Timer().schedule(
            timerTask {
                statusBarUpdater.requestStatusBarMessage()
            },
            BINARY_PROMOTION_POLLING_DELAY, BINARY_PROMOTION_POLLING_INTERVAL
        )
    }
}
