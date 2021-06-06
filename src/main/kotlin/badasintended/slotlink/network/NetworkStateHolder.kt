package badasintended.slotlink.network

@Suppress("INAPPLICABLE_JVM_NAME")
interface NetworkStateHolder {

    @get:JvmName("slotlink\$getNetworkState")
    val networkState: NetworkState

}