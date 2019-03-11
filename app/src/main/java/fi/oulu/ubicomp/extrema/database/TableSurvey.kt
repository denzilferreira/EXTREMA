package fi.oulu.ubicomp.extrema.database

import androidx.room.*

@Entity(tableName = "diary")
data class Survey(
        @PrimaryKey(autoGenerate = true) var uid: Int?,
        @ColumnInfo(name = "participantId") var participantId: String,
        @ColumnInfo(name = "entryDate") var entryDate: Long,
        @ColumnInfo(name = "surveyData") var surveyData: String
)

@Dao
interface SurveyDao {
    @Insert
    fun insert(survey: Survey)
}