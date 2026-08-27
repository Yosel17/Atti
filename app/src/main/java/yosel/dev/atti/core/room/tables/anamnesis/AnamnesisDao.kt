package yosel.dev.atti.core.room.tables.anamnesis

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AnamnesisDao {
    @Query("SELECT * FROM anamnesis WHERE consultation_id = :consultationId LIMIT 1")
    fun getAnamnesisByConsultationIdFlow(consultationId: String): Flow<AnamnesisEntity?>

    @Query("SELECT * FROM anamnesis WHERE consultation_id = :consultationId LIMIT 1")
    suspend fun getAnamnesisByConsultationId(consultationId: String): AnamnesisEntity?

    @Query("SELECT * FROM anamnesis WHERE id = :anamnesisId LIMIT 1")
    suspend fun getAnamnesisById(anamnesisId: String): AnamnesisEntity?

    @Upsert
    suspend fun upsertAnamnesis(anamnesis: AnamnesisEntity)

    @Upsert
    suspend fun upsertEnvironmentOptions(options: List<AnamnesisEnvironmentOptionEntity>)

    @Upsert
    suspend fun upsertVaccines(vaccines: List<AnamnesisVaccineEntity>)

    @Upsert
    suspend fun upsertDewormings(dewormings: List<AnamnesisDewormingEntity>)

    @Query("DELETE FROM anamnesis_environment_options WHERE anamnesis_id = :anamnesisId")
    suspend fun deleteEnvironmentOptionsByAnamnesisId(anamnesisId: String)

    @Query("DELETE FROM anamnesis_vaccines WHERE anamnesis_id = :anamnesisId")
    suspend fun deleteVaccinesByAnamnesisId(anamnesisId: String)

    @Query("DELETE FROM anamnesis_dewormings WHERE anamnesis_id = :anamnesisId")
    suspend fun deleteDewormingsByAnamnesisId(anamnesisId: String)

    // --- Transacción atómica en Room ---
    @Transaction
    suspend fun saveAnamnesisWithDetails(
        anamnesis: AnamnesisEntity,
        options: List<AnamnesisEnvironmentOptionEntity>,
        vaccines: List<AnamnesisVaccineEntity>,
        dewormings: List<AnamnesisDewormingEntity>
    ) {
        upsertAnamnesis(anamnesis)
        if (options.isNotEmpty()) {
            upsertEnvironmentOptions(options)
        }
        if (vaccines.isNotEmpty()) {
            upsertVaccines(vaccines)
        }
        if (dewormings.isNotEmpty()) {
            upsertDewormings(dewormings)
        }
    }

    // --- Consultas con Relaciones ---
    @Transaction
    @Query("SELECT * FROM anamnesis WHERE consultation_id = :consultationId LIMIT 1")
    fun getAnamnesisWithDetailsByConsultationIdFlow(consultationId: String): Flow<AnamnesisWithDetailsEntity?>

    @Transaction
    @Query("SELECT * FROM anamnesis WHERE consultation_id = :consultationId LIMIT 1")
    suspend fun getAnamnesisWithDetailsByConsultationId(consultationId: String): AnamnesisWithDetailsEntity?

    @Transaction
    @Query("SELECT * FROM anamnesis WHERE id = :anamnesisId LIMIT 1")
    suspend fun getAnamnesisWithDetailsById(anamnesisId: String): AnamnesisWithDetailsEntity?
}