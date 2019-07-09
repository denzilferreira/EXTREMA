package fi.oulu.ubicomp.extrema.workers

import android.content.Context
import androidx.room.Room
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import fi.oulu.ubicomp.extrema.Home
import fi.oulu.ubicomp.extrema.database.*
import org.json.JSONObject

class SyncWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val prefs = applicationContext.getSharedPreferences(Home.EXTREMA_PREFS, 0)
        val db: ExtremaDatabase = Room.databaseBuilder(applicationContext, ExtremaDatabase::class.java, "extrema").build()

        val requestQueue = Volley.newRequestQueue(applicationContext)

        println("Sync started...")

        val pendingParticipant: Array<Participant>
        if (prefs.getBoolean(Home.FORCE_SYNC, false)) {
            pendingParticipant = db.participantDao().getPendingSync(0)
        } else {
            pendingParticipant = db.participantDao().getPendingSync(prefs.getLong("participant", 0))
        }

        if (pendingParticipant.isNotEmpty()) {
            val jsonBuilder = GsonBuilder()
            val jsonPost = jsonBuilder.create()

            for (participantRecord in pendingParticipant) {
                val data = JSONObject()
                        .put("tableName", "participant")
                        .put("deviceId", prefs.getString(Home.UUID, ""))
                        .put("data", jsonPost.toJson(participantRecord))
                        .put("timestamp", System.currentTimeMillis())

                val serverRequest = object : JsonObjectRequest(Method.POST, Home.STUDY_URL, data,
                        Response.Listener {
                            println("Sync OK [participant]: $it")
                            prefs.edit().putLong("participant", participantRecord.onboardDate).apply()
                        },
                        Response.ErrorListener {
                            if (it.networkResponse != null)
                                println("Error ${it.networkResponse.statusCode}")
                        }
                ) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val params = HashMap<String, String>()
                        params.put("Content-Type", "application/json")
                        return params
                    }
                }
                requestQueue.add(serverRequest)
            }
        }

        val pendingBluetooth: Array<Bluetooth>
        if (prefs.getBoolean(Home.FORCE_SYNC, false)) {
            pendingBluetooth = db.bluetoothDao().getPendingSync(0)
        } else {
            pendingBluetooth = db.bluetoothDao().getPendingSync(prefs.getLong("bluetooth", 0))
        }

        if (pendingBluetooth.isNotEmpty()) {
            val jsonBuilder = GsonBuilder()
            val jsonPost = jsonBuilder.create()
            for (bluetoothRecord in pendingBluetooth) {
                val data = JSONObject()
                        .put("tableName", "bluetooth")
                        .put("deviceId", prefs.getString(Home.UUID, ""))
                        .put("data", jsonPost.toJson(bluetoothRecord))
                        .put("timestamp", System.currentTimeMillis())

                val serverRequest = object : JsonObjectRequest(Method.POST, Home.STUDY_URL, data,
                        Response.Listener {
                            println("Sync OK [bluetooth]: $it")
                            prefs.edit().putLong("bluetooth", bluetoothRecord.entryDate).apply()
                        },
                        Response.ErrorListener {
                            if (it.networkResponse != null)
                                println("Error ${it.networkResponse.statusCode}")
                        }
                ) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val params = HashMap<String, String>()
                        params.put("Content-Type", "application/json")
                        return params
                    }
                }
                requestQueue.add(serverRequest)
            }
        }

        val pendingLocation: Array<Location>
        if (prefs.getBoolean(Home.FORCE_SYNC, false)) {
            pendingLocation = db.locationDao().getPendingSync(0)
        } else {
            pendingLocation = db.locationDao().getPendingSync(prefs.getLong("location", 0))
        }

        if (pendingLocation.isNotEmpty()) {
            val jsonBuilder = GsonBuilder()
            val jsonPost = jsonBuilder.create()
            for (locationRecord in pendingLocation) {
                val data = JSONObject()
                        .put("tableName", "location")
                        .put("deviceId", prefs.getString(Home.UUID, ""))
                        .put("data", jsonPost.toJson(locationRecord))
                        .put("timestamp", System.currentTimeMillis())

                val serverRequest = object : JsonObjectRequest(Method.POST, Home.STUDY_URL, data,
                        Response.Listener {
                            println("Sync OK [locations]: $it")
                            prefs.edit().putLong("location", locationRecord.entryDate).apply()
                        },
                        Response.ErrorListener {
                            if (it.networkResponse != null)
                                println("Error ${it.networkResponse.statusCode}")
                        }
                ) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val params = HashMap<String, String>()
                        params.put("Content-Type", "application/json")
                        return params
                    }
                }
                requestQueue.add(serverRequest)
            }
        }

        val pendingSurvey: Array<Survey>
        if (prefs.getBoolean(Home.FORCE_SYNC, false)) {
            pendingSurvey = db.surveyDao().getPendingSync(0)
        } else {
            pendingSurvey = db.surveyDao().getPendingSync(prefs.getLong("survey", 0))
        }

        if (pendingSurvey.isNotEmpty()) {
            val jsonBuilder = GsonBuilder()
            val jsonPost = jsonBuilder.create()
            for (surveyRecord in pendingSurvey) {
                val data = JSONObject()
                        .put("tableName", "diary")
                        .put("deviceId", prefs.getString(Home.UUID, ""))
                        .put("data", jsonPost.toJson(surveyRecord))
                        .put("timestamp", System.currentTimeMillis())

                val serverRequest = object : JsonObjectRequest(Method.POST, Home.STUDY_URL, data,
                        Response.Listener {
                            println("Sync OK [survey]: $it")
                            prefs.edit().putLong("survey", surveyRecord.entryDate).apply()
                        },
                        Response.ErrorListener {
                            if (it.networkResponse != null)
                                println("Error ${it.networkResponse.statusCode}")
                        }
                ) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val params = HashMap<String, String>()
                        params.put("Content-Type", "application/json")
                        return params
                    }
                }
                requestQueue.add(serverRequest)
            }
        }

        db.close()

        println("Sync finished!")

        if (prefs.getBoolean(Home.FORCE_SYNC, false)) prefs.edit().putBoolean(Home.FORCE_SYNC, false).apply()

        return Result.success()
    }
}