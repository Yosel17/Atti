package yosel.dev.atti.core.room.config

import androidx.room.Database
import androidx.room.RoomDatabase
import yosel.dev.atti.core.room.tables.anamnesis.AnamnesisDao
import yosel.dev.atti.core.room.tables.anamnesis.AnamnesisDewormingEntity
import yosel.dev.atti.core.room.tables.anamnesis.AnamnesisEntity
import yosel.dev.atti.core.room.tables.anamnesis.AnamnesisEnvironmentOptionEntity
import yosel.dev.atti.core.room.tables.anamnesis.AnamnesisVaccineEntity
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogDao
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogEntity
import yosel.dev.atti.core.room.tables.client.ClientDao
import yosel.dev.atti.core.room.tables.client.ClientEntity
import yosel.dev.atti.core.room.tables.clinical_examination.ClinicalExamLymphNodeEntity
import yosel.dev.atti.core.room.tables.clinical_examination.ClinicalExaminationDao
import yosel.dev.atti.core.room.tables.clinical_examination.ClinicalExaminationEntity
import yosel.dev.atti.core.room.tables.consultation.ConsultationDao
import yosel.dev.atti.core.room.tables.consultation.ConsultationEntity
import yosel.dev.atti.core.room.tables.consultation_step_progress.ConsultationStepProgressDao
import yosel.dev.atti.core.room.tables.consultation_step_progress.ConsultationStepProgressEntity
import yosel.dev.atti.core.room.tables.consultation_type_step.ConsultationTypeStepDao
import yosel.dev.atti.core.room.tables.consultation_type_step.ConsultationTypeStepEntity
import yosel.dev.atti.core.room.tables.diagnosis.DiagnosisDao
import yosel.dev.atti.core.room.tables.diagnosis.DiagnosisEntity
import yosel.dev.atti.core.room.tables.follow_up.FollowUpDao
import yosel.dev.atti.core.room.tables.follow_up.FollowUpEntity
import yosel.dev.atti.core.room.tables.observation.ObservationDao
import yosel.dev.atti.core.room.tables.observation.ObservationEntity
import yosel.dev.atti.core.room.tables.patient.PatientDao
import yosel.dev.atti.core.room.tables.patient.PatientEntity
import yosel.dev.atti.core.room.tables.physiological_constants.PhysiologicalConstsDao
import yosel.dev.atti.core.room.tables.physiological_constants.PhysiologicalConstsEntity
import yosel.dev.atti.core.room.tables.prescription.PrescriptionDao
import yosel.dev.atti.core.room.tables.prescription.PrescriptionEntity
import yosel.dev.atti.core.room.tables.prescription.PrescriptionItemEntity
import yosel.dev.atti.core.room.tables.product.ProductDao
import yosel.dev.atti.core.room.tables.product.ProductEntity
import yosel.dev.atti.core.room.tables.service.ServiceDao
import yosel.dev.atti.core.room.tables.service.ServiceEntity
import yosel.dev.atti.core.room.tables.service_supply.ServiceSupplyDao
import yosel.dev.atti.core.room.tables.service_supply.ServiceSupplyEntity
import yosel.dev.atti.core.room.tables.supplier.SupplierDao
import yosel.dev.atti.core.room.tables.supplier.SupplierEntity
import yosel.dev.atti.core.room.tables.treatment.TreatmentDao
import yosel.dev.atti.core.room.tables.treatment.TreatmentEntity

@Database(
    entities = [
        ClientEntity::class,
        PatientEntity::class,
        AppCatalogEntity::class,
        SupplierEntity::class,
        ProductEntity::class,
        ServiceEntity::class,
        ServiceSupplyEntity::class,
        ConsultationEntity::class,
        ConsultationTypeStepEntity::class,
        AnamnesisEntity::class,
        AnamnesisEnvironmentOptionEntity::class,
        AnamnesisVaccineEntity::class,
        AnamnesisDewormingEntity::class,
        ConsultationStepProgressEntity::class,
        ClinicalExaminationEntity::class,
        ClinicalExamLymphNodeEntity::class,
        PhysiologicalConstsEntity::class,
        DiagnosisEntity::class,
        TreatmentEntity::class,
        PrescriptionEntity::class,
        PrescriptionItemEntity::class,
        ObservationEntity::class,
        FollowUpEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun clientDao(): ClientDao
    abstract fun patientDao(): PatientDao
    abstract fun appCatalogDao(): AppCatalogDao
    abstract fun supplierDao(): SupplierDao
    abstract fun productDao(): ProductDao
    abstract fun serviceDao(): ServiceDao
    abstract fun serviceSupplyDao(): ServiceSupplyDao
    abstract fun consultationDao(): ConsultationDao
    abstract fun consultationTypeStepDao(): ConsultationTypeStepDao
    abstract fun anamnesisDao(): AnamnesisDao
    abstract fun consultationStepProgressDao(): ConsultationStepProgressDao
    abstract fun clinicalExaminationDao(): ClinicalExaminationDao
    abstract fun physiologicalConstantsDao(): PhysiologicalConstsDao
    abstract fun diagnosisDao(): DiagnosisDao
    abstract fun treatmentDao(): TreatmentDao
    abstract fun prescriptionDao(): PrescriptionDao
    abstract fun observationDao(): ObservationDao
    abstract fun followUpDao(): FollowUpDao
}