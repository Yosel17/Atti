package yosel.dev.atti.core.room.tables.patient

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {

    @Query("SELECT * FROM patients ORDER BY name ASC")
    fun getAllPatients(): Flow<List<PatientEntity>>

    @Query("SELECT * FROM patients WHERE client_id = :clientId ORDER BY name ASC")
    fun getPatientsByClientId(clientId: String): Flow<List<PatientEntity>>

    @Query("SELECT * FROM patients WHERE id = :patientId")
    suspend fun getPatientById(patientId: String): PatientEntity?

    @Upsert
    suspend fun upsertPatients(patients: List<PatientEntity>)

    @Upsert
    suspend fun upsertPatient(patient: PatientEntity)

    @Query("DELETE FROM patients WHERE id = :patientId")
    suspend fun deletePatientById(patientId: String)

    @Query("DELETE FROM patients WHERE client_id = :clientId")
    suspend fun deletePatientsByClientId(clientId: String)

    @Query("DELETE FROM patients")
    suspend fun clearAllPatients()
}