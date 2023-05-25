package ro.pub.acs.playersneeded.metrics

/**
 * Interface that provides methods to be used when gatherin metrics
 * for the performance of the device when running the app
 * */
interface MetricsApi {
    /**
     * Function that gets the available memory
     * */
    fun getMaximumAvailableMemory(): Long {
        val runtime = Runtime.getRuntime()
        return runtime.maxMemory()
    }

    /**
     * Function that gets the total memory
     * */
    fun getTotalMemory(): Long {
        val runtime = Runtime.getRuntime()
        return runtime.totalMemory()
    }

    /**
     * Function that gets the free memory
     * */
    fun getFreeMemory(): Long {
        val runtime = Runtime.getRuntime()
        return runtime.freeMemory()
    }

    /**
     * Function that gets the received bytes by the app
     */
    fun getRxBytes(): Long {
        return android.net.TrafficStats.getUidRxBytes(android.os.Process.myUid())
    }

    /**
     * Function that gets the transmitted bytes by the app
     */
    fun getTxBytes(): Long {
        return android.net.TrafficStats.getUidTxBytes(android.os.Process.myUid())
    }
}
