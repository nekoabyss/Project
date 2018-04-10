package co.project.client.data

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager.CONNECTIVITY_ACTION
import android.os.IBinder

import javax.inject.Inject

import rx.Subscription
import timber.log.Timber
import co.project.client.BoilerplateApplication
import co.project.client.util.extension.isNetworkConnected
import co.project.client.util.extension.toggleAndroidComponent

class SyncService : Service() {
    companion object {
        fun getStartIntent(context: Context): Intent {
            return Intent(context, SyncService::class.java)
        }
    }

    @Inject lateinit var dataManager: DataManager

    var subscription: Subscription? = null

    override fun onCreate() {
        super.onCreate()
        (applicationContext as BoilerplateApplication)
                .applicationComponent
                .inject(this)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Timber.i("Starting sync...")

        if (!isNetworkConnected()) {
            Timber.i("Sync canceled, connection not available")
            toggleAndroidComponent(SyncOnConnectionAvailable::class.java, true)
            stopSelf(startId)
            return Service.START_NOT_STICKY
        }

        subscription?.unsubscribe()

        return Service.START_STICKY
    }

    override fun onDestroy() {
        subscription?.unsubscribe()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    class SyncOnConnectionAvailable : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == CONNECTIVITY_ACTION && context.isNetworkConnected()) {
                Timber.i("Connection is now available, triggering sync...")
                context.toggleAndroidComponent(SyncOnConnectionAvailable::class.java, false)
                context.startService(getStartIntent(context))
            }
        }
    }
}
