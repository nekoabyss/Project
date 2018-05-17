package co.project.client.ui.compass

import android.hardware.SensorEventListener
import android.location.Location
import co.project.client.ui.base.BaseMvp

interface CompassMvp {
    interface View: BaseMvp.View {
        fun adjustArrow(azimuth: Float)
        fun onGetLocationClicked()
        fun onReceiveDestination(lat: Double, long: Double)
    }
    interface Presenter<in V: BaseMvp.View>: BaseMvp.Presenter<V>, SensorEventListener {
        var currentLocation: Location?
        fun post(id: String, rssi: Int, ssid: String)
    }
}
