package ro.pub.acs.playersneeded.metrics

/**
 * values that describe the tye of metric message
 */
const val AVAILABLE_MEMORY = "available_memory"
const val TOTAL_MEMORY = "total_memory"
const val FREE_MEMORY = "free_memory"
const val RX_BYTES = "rx_bytes"
const val TX_BYTES = "tx_bytes"

/**
 * class that describes the value that is being sent
 * as a metric
 */
class MetricsData {
    lateinit var type: String
    lateinit var timestamp: String
    var value1: Long = 0
    var value2: Long = 0 // optional
}