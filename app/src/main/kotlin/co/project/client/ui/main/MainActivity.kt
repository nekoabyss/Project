package co.project.client.ui.main

import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatTextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import co.project.client.R
import co.project.client.data.SyncService
import co.project.client.data.local.LocationHelper
import co.project.client.data.model.Client
import co.project.client.ui.base.BaseActivity
import co.project.client.ui.compass.CompassActivity
import co.project.client.ui.rssi.RssiActivity
import javax.inject.Inject


class MainActivity : BaseActivity(), MainMvp.View {

    companion object {
        val EXTRA_TRIGGER_SYNC_FLAG =
                "uk.co.ribot.androidboilerplate.ui.main.MainActivity.EXTRA_TRIGGER_SYNC_FLAG"
    }

    @Inject lateinit var presenter: MainPresenter<MainMvp.View>

    @BindView(R.id.reset_btn) lateinit var resetBtn: AppCompatButton
    @BindView(R.id.connect_btn) lateinit var connectBtn: AppCompatButton
    @BindView(R.id.connection_text) lateinit var connectionTxt: AppCompatTextView
    @BindView(R.id.lat_text) lateinit var latText: AppCompatTextView
    @BindView(R.id.long_text) lateinit var longText: AppCompatTextView
    @BindView(R.id.distance_text) lateinit var locationText: AppCompatTextView

    var client: Client? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        activityComponent.inject(this)

        if (intent.getBooleanExtra(EXTRA_TRIGGER_SYNC_FLAG, true)) {
            startService(SyncService.getStartIntent(this))
        }
        setup()
    }

    override fun setup() {
        unbinder = ButterKnife.bind(this)
        presenter.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    @OnClick(R.id.connect_btn)
    fun onConnectBtnClicked() {
        presenter.connectToServer()
    }

    @OnClick(R.id.reset_btn)
    fun onResetBtnClicked() {
        presenter.resetServerConnection()
    }



    @OnClick(R.id.get_location_btn)
    fun onGetLocationClicked() {
        if (LocationHelper.instance.checkPermission(this)) {
            LocationHelper.instance.getLastKnownLocation(this).let {
                if (it == null) {
                    showLoading()
                    LocationHelper.instance.requestLocationUpdate(this, object : LocationListener {
                        override fun onLocationChanged(location: Location?) {
                            hideLoading()
                            location?.let {
                                this@MainActivity.setLocationLabels(it)
                            }
                        }

                        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}
                        override fun onProviderEnabled(p0: String?) {}
                        override fun onProviderDisabled(p0: String?) {}

                    })
                } else {
                    this.setLocationLabels(it)
                }
            }
        }
    }

    private fun setLocationLabels(location: Location) {
        latText.text = "Latitude: ${location.latitude}"
        longText.text = "Longitude: ${location.longitude}"
        val temp = Location(android.location.LocationManager.GPS_PROVIDER)
        temp.latitude = 13.844053
        temp.longitude = 100.448537
        //bearingText.text = "Bearing: ${location.bearingTo(temp)}"
        locationText.text = "Distance: ${location.distanceTo(temp)} meters"
        //desLatText.text = "Destination Lat: ${temp.latitude}"
        //desLongText.text = "Destination Long: ${temp.longitude}"
    }

    @OnClick(R.id.compass_page)
    fun onCompassPage(){
        this.client?.let {
            val intent = Intent(this, CompassActivity::class.java)
            intent.putExtra("client", this.client)
            startActivity(intent)
        }
    }

    @OnClick(R.id.rssi_page)
    fun onRssiPage(){
        val intent = Intent(this, RssiActivity::class.java)
        startActivity(intent)
    }

    override fun onConnected(client: Client) {
        connectionTxt.text = "ID: ${client.id}"
        this.client = client
    }

    override fun onServerConnectionResetted() {
        connectionTxt.text = "Connection Reset แล้วค่ะอีดอกไก่"
        this.client = null
    }
// bssid, lat, long, 10 rssi to server

}
