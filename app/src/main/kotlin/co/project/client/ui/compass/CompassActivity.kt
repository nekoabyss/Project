package co.project.client.ui.compass

import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatTextView
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import co.project.client.R
import co.project.client.data.local.LocationHelper
import co.project.client.data.model.Client
import co.project.client.ui.base.BaseActivity
import javax.inject.Inject


class CompassActivity : BaseActivity(), CompassMvp.View {

    @BindView(R.id.image_view) lateinit var arrowView: AppCompatImageView
    @BindView(R.id.lat_text) lateinit var latText: AppCompatTextView
    @BindView(R.id.long_text) lateinit var longText: AppCompatTextView
    @BindView(R.id.distance_text) lateinit var locationText: AppCompatTextView
    @BindView(R.id.desLat_text) lateinit var desLatText: AppCompatTextView
    @BindView(R.id.desLong_text) lateinit var desLongText: AppCompatTextView

    private var currentAzimuth: Float = 0.toFloat()
    private var client : Client? = null

    @Inject lateinit var presenter: CompassPresenter<CompassMvp.View>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.compass_page)

        client = intent.getParcelableExtra("client")
        setup()
    }

    override fun setup() {
        activityComponent.inject(this)
        unbinder = ButterKnife.bind(this)
        presenter.attachView(this)
    }

    override fun onResume() {
        super.onResume()
        LocationHelper.instance.registerCompassSensor(this, presenter)
    }

    override fun onPause() {
        LocationHelper.instance.unregisterCompassSensor(this, presenter)
        super.onPause()
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }

    @OnClick(R.id.refreshBtn)
    override fun onGetLocationClicked() {
        if (LocationHelper.instance.checkPermission(this)) {
            LocationHelper.instance.getLastKnownLocation(this).let {
                if (it == null) {
                    showLoading()
                    LocationHelper.instance.requestLocationUpdate(this, object : LocationListener {
                        override fun onLocationChanged(location: Location?) {
                            hideLoading()
                            location?.let {
                                this@CompassActivity.gotLocation(it)
                            }
                        }

                        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}
                        override fun onProviderEnabled(p0: String?) {}
                        override fun onProviderDisabled(p0: String?) {}

                    })
                } else {
                    this.gotLocation(it)
                }
            }
        }
    }


    private fun gotLocation(location: Location) {
        presenter.currentLocation = location
        client?.let {
            presenter.post(it.id, -69, "latte")
        }
        latText.text = "Latitude: ${location.latitude}"
        longText.text = "Longitude: ${location.longitude}"
        val temp = Location(LocationManager.GPS_PROVIDER)
        temp.latitude = 13.844053
        temp.longitude = 100.448537
        //bearingText.text = "Bearing: ${location.bearingTo(temp)}"
        locationText.text = "Distance: ${location.distanceTo(temp)} meters"
        //desLatText.text = "Destination Lat: ${temp.latitude}"
        //desLongText.text = "Destination Long: ${temp.longitude}"
    }

    override fun onReceiveDestination(lat: Double, long: Double) {
        //bearingText.text = "Bearing: ${it.bearingTo(temp)}"
//        locationText.text = "Distance: ${it.distanceTo(temp)} meters"
        desLatText.text = "Destination Lat: $lat"
        desLongText.text = "Destination Long: $long"
    }

    override fun adjustArrow(azimuth: Float) {
        RotateAnimation(
                -currentAzimuth,
                -azimuth,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
        ).let {
            it.duration = 500
            it.repeatCount = 0
            it.fillAfter = true
            arrowView.startAnimation(it)
        }
        currentAzimuth = azimuth
    }

}
