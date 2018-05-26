package co.project.client.ui.rssi

import android.net.wifi.ScanResult
import co.project.client.ui.base.BaseMvp

interface RssiMvp {
    interface View: BaseMvp.View {
        fun scanNetwork()
        fun onFabClicked(view: android.view.View)
    }
    interface Presenter<in V: BaseMvp.View>: BaseMvp.Presenter<V>
    interface Interaction {
        fun onItemClicked(item: ScanResult)
    }
}
