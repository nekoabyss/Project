package co.project.client.data.local

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import co.project.client.ui.base.BaseActivity
import co.project.client.ui.base.BaseFragment

class LocationHelper {

    init {
        println("LocationHelper init-ed")
    }

    companion object {
        private val PERMISSION_REQUEST_CODE = 6969

        val instance = LocationHelper()
    }

    private fun getActivity(context: Context): Activity? {
        return if (context is BaseFragment) context.activity else context as? BaseActivity
    }

    fun checkPermission(context: Context): Boolean {
        return getActivity(context)?.let { checkPermission(it) } ?: false
    }

    fun checkPermission(activity: Activity): Boolean {
        return if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder(activity).apply {
                    setTitle("WHY?")
                    setCancelable(false)
                    setPositiveButton(context.getString(android.R.string.ok)) { _, _ -> }
                    show()
                }
            } else {
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LocationHelper.PERMISSION_REQUEST_CODE)
            }
            false
        } else {
            true
        }
    }

    fun getLocation(context: Context): Location? {
        return getActivity(context)?.let { getLocation(it) }
    }

    fun getLocation(activity: Activity): Location? {
        return if (checkPermission(activity)) {
            (activity.getSystemService(Context.LOCATION_SERVICE) as? LocationManager)
                    ?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        } else {
            null
        }
    }

}