package fi.oulu.ubicomp.extrema.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import fi.oulu.ubicomp.extrema.services.Pehmo

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context?.startForegroundService(Intent(context, Pehmo::class.java))
            } else {
                context?.startService(Intent(context, Pehmo::class.java))
            }
        }
    }
}