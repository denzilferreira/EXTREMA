package fi.oulu.ubicomp.extrema.views

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import fi.oulu.ubicomp.extrema.Home
import fi.oulu.ubicomp.extrema.R
import fi.oulu.ubicomp.extrema.database.ExtremaDatabase
import fi.oulu.ubicomp.extrema.database.Participant
import fi.oulu.ubicomp.extrema.database.Survey
import kotlinx.android.synthetic.main.activity_view_survey.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.json.JSONObject

class ViewSurvey : AppCompatActivity() {

    lateinit var rescueCount : EditText
    lateinit var regulateAsthma : ToggleButton
    lateinit var otherMeds : EditText
    lateinit var symptomShortness : RadioGroup
    lateinit var symptomCough : RadioGroup
    lateinit var symptomPhlegm : RadioGroup
    lateinit var symptomWheezing : RadioGroup
    lateinit var frequencyNocturnal : RadioGroup
    lateinit var frequencyOpenMeds : RadioGroup
    lateinit var estimationAsthma : RadioGroup
    lateinit var isActingNormally : ToggleButton
    lateinit var isColdToday : ToggleButton
    lateinit var isVisitDoctor : ToggleButton
    lateinit var otherObs : EditText

    lateinit var save : Button

    var surveyData : JSONObject = JSONObject()
    var db: ExtremaDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_survey)

        doAsync {
            db = Room.databaseBuilder(applicationContext, ExtremaDatabase::class.java, "extrema").build()
        }
    }

    override fun onResume() {
        super.onResume()

        welcomeHeader.text = "${resources.getString(R.string.welcome)} ${Home.participantData?.participantName}"

        rescueCount = timesRescue
        regulateAsthma = regulatesToday
        otherMeds = otherAsthmaMeds

        symptomShortness = shortnessBreath
        symptomShortness.setOnCheckedChangeListener { group, checkedId ->
            val radioSelected : RadioButton = group.findViewById(checkedId)
            surveyData.put("symptomShortness", group.indexOfChild(radioSelected) + 1)
        }

        symptomCough = cough
        symptomCough.setOnCheckedChangeListener { group, checkedId ->
            val radioSelected : RadioButton = group.findViewById(checkedId)
            surveyData.put("symptomCough", group.indexOfChild(radioSelected) + 1)
        }

        symptomPhlegm = phlegm
        symptomPhlegm.setOnCheckedChangeListener { group, checkedId ->
            val radioSelected : RadioButton = group.findViewById(checkedId)
            surveyData.put("symptomPhlegm", group.indexOfChild(radioSelected) + 1)
        }

        symptomWheezing = wheezing
        symptomWheezing.setOnCheckedChangeListener { group, checkedId ->
            val radioSelected : RadioButton = group.findViewById(checkedId)
            surveyData.put("symptomWheezing", group.indexOfChild(radioSelected) + 1)
        }

        frequencyNocturnal = nocturnal
        frequencyNocturnal.setOnCheckedChangeListener { group, checkedId ->
            val radioSelected : RadioButton = group.findViewById(checkedId)
            surveyData.put("frequencyNocturnal", group.indexOfChild(radioSelected) + 1)
        }

        frequencyOpenMeds = opening_meds
        frequencyOpenMeds.setOnCheckedChangeListener { group, checkedId ->
            val radioSelected : RadioButton = group.findViewById(checkedId)
            surveyData.put("frequencyOpenMeds", group.indexOfChild(radioSelected) + 1)
        }

        estimationAsthma = estimation_asthma
        estimationAsthma.setOnCheckedChangeListener { group, checkedId ->
            val radioSelected : RadioButton = group.findViewById(checkedId)
            surveyData.put("estimationAsthmaBalance", group.indexOfChild(radioSelected) + 1)
        }

        isActingNormally = actNormally
        isColdToday = coldToday
        isVisitDoctor = visitDoctor
        otherObs = otherConsiderations

        save = saveDiary
        save.setOnClickListener {
            surveyData.put("rescueCount", rescueCount.text.toString())
            surveyData.put("regulateToday", regulateAsthma.isChecked)
            surveyData.put("otherMeds", otherMeds.text.toString())
            surveyData.put("isActingNormal", isActingNormally.isChecked)
            surveyData.put("isColdToday", isColdToday.isChecked)
            surveyData.put("isVisitDoctor", isVisitDoctor.isChecked)
            surveyData.put("otherObs", otherObs.text.toString())

            doAsync {
                val survey = Survey(null,
                        participantId = Home.participantData?.participantId,
                        entryDate = System.currentTimeMillis(),
                        surveyData = surveyData.toString())

                db?.surveyDao()?.insert(survey)
            }

            toast(getString(R.string.thanks))
            finish()
        }
    }
}