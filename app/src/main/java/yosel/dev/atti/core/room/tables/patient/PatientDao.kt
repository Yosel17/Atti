package yosel.dev.atti.core.room.tables.patient

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import yosel.dev.atti.core.room.tables.client.ClientEntity

@Dao
interface PatientDao {

    @Query("""
    SELECT * FROM patients 
    ORDER BY 
        CASE WHEN status = 3 THEN 1 ELSE 0 END ASC,
        created_at DESC
""")
    fun getAllPatientsFlow(): Flow<List<PatientEntity>>

    @Query("SELECT * FROM patients WHERE client_id = :clientId ORDER BY name ASC")
    fun getPatientsByClientIdFlow(clientId: String): Flow<List<PatientEntity>>

    @Query("SELECT * FROM patients WHERE client_id = :clientId ORDER BY created_at ASC")
    suspend fun getPatientsByClientId(clientId: String): List<PatientEntity>

    @Query("SELECT * FROM patients WHERE id = :patientId")
    suspend fun getPatientById(patientId: String): PatientEntity?

    @Query("SELECT * FROM patients WHERE id = :patientId")
    fun getPatientByIdFlow(patientId: String): Flow<PatientEntity?>

    @Upsert
    suspend fun upsertPatients(patients: List<PatientEntity>)

    @Upsert
    suspend fun upsertPatient(patient: PatientEntity)

    @Query("DELETE FROM patients WHERE id = :patientId")
    suspend fun deletePatientById(patientId: String)

    @Query("UPDATE patients SET status = :newStatus WHERE id = :patientId")
    suspend fun updatePatientStatus(patientId: String, newStatus: Int)

    @Query("UPDATE patients SET status = :newStatus WHERE id IN (:patientIds)")
    suspend fun updatePatientsStatus(patientIds: List<String>, newStatus: Int)

    @Transaction
    @Query("""
    SELECT * FROM patients 
    ORDER BY 
        CASE WHEN status = 3 THEN 1 ELSE 0 END ASC,
        created_at DESC
""")
    fun getAllPatientsWithCatalogsFlow(): Flow<List<PatientWithCatalogsEntity>>

    @Transaction
    @Query("SELECT * FROM patients WHERE id = :patientId")
    fun getPatientWithCatalogsByIdFlow(patientId: String): Flow<PatientWithCatalogsEntity?>
}