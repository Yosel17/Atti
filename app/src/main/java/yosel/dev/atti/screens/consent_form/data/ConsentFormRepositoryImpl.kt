package yosel.dev.atti.screens.consent_form.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import android.net.Uri
import androidx.core.graphics.scale
import androidx.room.withTransaction
import dagger.hilt.android.qualifiers.ApplicationContext
import yosel.dev.atti.core.models.model.ConsentModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.room.config.AppDatabase
import yosel.dev.atti.core.room.tables.consent.ConsentDao
import yosel.dev.atti.core.room.tables.consultation.ConsultationDao
import yosel.dev.atti.core.room.tables.consultation_step_progress.ConsultationStepProgressDao
import yosel.dev.atti.core.room.tables.consultation_step_progress.ConsultationStepProgressEntity
import yosel.dev.atti.core.supabase.ConsentsDataSource
import yosel.dev.atti.core.supabase.MultimediaDataSource
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.core.utils.toDtoForInsert
import yosel.dev.atti.core.utils.toDtoForUpdate
import yosel.dev.atti.core.utils.toEntity
import yosel.dev.atti.core.utils.toModel
import yosel.dev.atti.screens.consent_form.domain.ConsentFormRepository
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.UUID
import javax.inject.Inject

class ConsentFormRepositoryImpl @Inject constructor(
    private val consultationDao: ConsultationDao,
    private val consentDao: ConsentDao,
    private val consentsDataSource: ConsentsDataSource,
    private val multimediaDataSource: MultimediaDataSource,
    @ApplicationContext private val context: Context,
    private val appDatabase: AppDatabase,
    private val consultationStepProgressDao: ConsultationStepProgressDao,
) : ConsentFormRepository {

    override suspend fun getConsultation(consultationId: String): Result<ConsultationWithDetailsModel> = runCatching {
        val consultationEntity = consultationDao.getConsultationWithDetailsById(consultationId)
            ?: throw IllegalStateException("No se pudo recuperar la información de la consulta")
        consultationEntity.toModel()
    }

    override suspend fun getConsentByConsultationId(consultationId: String): Result<ConsentModel?> = runCatching {
        val localConsent = consentDao.getConsentByConsultationId(consultationId)
        if (localConsent != null) {
            return@runCatching localConsent.toModel()
        }
        val remoteDto = consentsDataSource.getConsentByConsultationId(consultationId) ?: return@runCatching null
        consentDao.upsertConsent(remoteDto.toEntity())
        remoteDto.toModel()
    }

    override suspend fun saveImageConsent(image: Uri): Result<String> = runCatching {
        val compressedBytes = compressImageUri(image)

        if (compressedBytes.isEmpty()) {
            throw IllegalStateException("No se pudo procesar o comprimir la imagen de consentimiento")
        }

        val fileName = "consent_${UUID.randomUUID()}.jpg"

        multimediaDataSource.uploadImage(
            byteArray = compressedBytes,
            fileName = fileName,
            folderPath = Constants.CONSENTS_SUPABASE
        )
    }

    override suspend fun updateImageConsent(image: Uri, previousImageUrl: String?): Result<String> = runCatching {
        if (!previousImageUrl.isNullOrBlank()) {
            deleteImageFromSupabase(previousImageUrl)
        }
        saveImageConsent(image).getOrThrow()
    }

    override suspend fun saveConsent(consent: ConsentModel): Result<ConsentModel> = runCatching {
        val insertdDto = consentsDataSource.insertAndGetConsent(consent.toDtoForInsert())
        appDatabase.withTransaction {
            consentDao.upsertConsent(insertdDto.toEntity())
            consultationStepProgressDao.upsertSingleProgress(
                ConsultationStepProgressEntity(
                    consultationId = consent.consultationId,
                    stepCatalogId = Constants.CONSULTATION_STEP_CONSENT,
                    recordId = insertdDto.id,
                    isCompleted = true,
                    status = Constants.ACTIVE_STATUS
                )
            )
        }
        insertdDto.toModel()
    }

    override suspend fun updateConsent(consent: ConsentModel): Result<ConsentModel> = runCatching {
        val updatedDto = consentsDataSource.updateConsent(consent.toDtoForUpdate())
        consentDao.upsertConsent(updatedDto.toEntity())
        updatedDto.toModel()
    }

    private fun compressImageUri(imageUri: Uri, maxDimension: Int = 1920, quality: Int = 80): ByteArray {
        val rotationDegrees = context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
            getRotationDegrees(inputStream)
        } ?: 0

        val originalBitmap = context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        } ?: throw IllegalStateException("No se pudo obtener o decodificar el contenido de la imagen")

        val rotatedBitmap = if (rotationDegrees != 0) {
            val matrix = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
            val rotated = Bitmap.createBitmap(
                originalBitmap, 0, 0, originalBitmap.width, originalBitmap.height, matrix, true
            )
            originalBitmap.recycle()
            rotated
        } else {
            originalBitmap
        }

        val scaledBitmap = scaleBitmapIfNeeded(rotatedBitmap, maxDimension)

        val outputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        val compressedBytes = outputStream.toByteArray()

        if (scaledBitmap != rotatedBitmap) {
            scaledBitmap.recycle()
        }
        rotatedBitmap.recycle()

        return compressedBytes
    }

    private fun getRotationDegrees(inputStream: InputStream): Int {
        return try {
            val exifInterface = ExifInterface(inputStream)
            when (exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }
        } catch (_: Exception) {
            0
        }
    }

    private fun scaleBitmapIfNeeded(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        if (width <= maxDimension && height <= maxDimension) {
            return bitmap
        }

        val ratio = width.toFloat() / height.toFloat()
        val (newWidth, newHeight) = if (width > height) {
            maxDimension to (maxDimension / ratio).toInt()
        } else {
            (maxDimension * ratio).toInt() to maxDimension
        }

        return bitmap.scale(newWidth, newHeight, filter = true)
    }

    private suspend fun deleteImageFromSupabase(imageUrl: String) {
        runCatching {
            val bucketPrefix = "/${Constants.MULTIMEDIA_BUCKET_SUPABASE}/"
            val path = if (imageUrl.contains(bucketPrefix)) {
                imageUrl.substringAfter(bucketPrefix)
            } else {
                imageUrl
            }
            if (path.isNotBlank()) {
                multimediaDataSource.deleteImage(path = path)
            }
        }
    }
}
