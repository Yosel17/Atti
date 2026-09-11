package yosel.dev.atti.core.supabase

import io.github.jan.supabase.storage.FileObject
import io.github.jan.supabase.storage.Storage
import yosel.dev.atti.core.utils.Constants
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration

@Singleton
class MultimediaDataSource @Inject constructor(
    private val storage: Storage
) {

    /**
     * Sube una imagen (ByteArray) al bucket especificado de Supabase Storage.
     *
     * @param byteArray Bytes de la imagen a subir.
     * @param fileName Nombre del archivo (ej. "imagen_123.jpg").
     * @param folderPath Carpeta opcional donde almacenar el archivo (ej. "pacientes/123").
     * @param bucketName Nombre del bucket en Supabase Storage.
     * @param upsert Si es true, reemplaza el archivo si ya existe.
     * @return La URL pública de la imagen subida.
     */
    suspend fun uploadImage(
        byteArray: ByteArray,
        fileName: String,
        folderPath: String = "",
        bucketName: String = Constants.MULTIMEDIA_BUCKET_SUPABASE,
        upsert: Boolean = true
    ): String {
        val path = buildPath(folderPath, fileName)
        val bucket = storage.from(bucketName)

        bucket.upload(path, byteArray) {
            this.upsert = upsert
        }

        return bucket.publicUrl(path)
    }

    /**
     * Obtiene la URL pública de un archivo en Supabase Storage.
     *
     * @param path Ruta del archivo dentro del bucket (incluyendo carpetas si las hay).
     * @param bucketName Nombre del bucket en Supabase.
     * @return URL pública del archivo.
     */
    fun getPublicUrl(
        path: String,
        bucketName: String = Constants.MULTIMEDIA_BUCKET_SUPABASE
    ): String {
        return storage.from(bucketName).publicUrl(path)
    }

    /**
     * Crea una URL firmada (con fecha de expiración) para archivos o buckets privados.
     *
     * @param path Ruta del archivo dentro del bucket.
     * @param expiresIn Tiempo de validez de la URL firmada.
     * @param bucketName Nombre del bucket en Supabase.
     * @return URL firmada temporal.
     */
    suspend fun createSignedUrl(
        path: String,
        expiresIn: Duration,
        bucketName: String = Constants.MULTIMEDIA_BUCKET_SUPABASE
    ): String {
        return storage.from(bucketName).createSignedUrl(path = path, expiresIn = expiresIn)
    }

    /**
     * Descarga los bytes de una imagen de un bucket público o privado.
     *
     * @param path Ruta del archivo dentro del bucket.
     * @param bucketName Nombre del bucket en Supabase.
     * @return ByteArray con el contenido de la imagen.
     */
    suspend fun downloadImage(
        path: String,
        bucketName: String = Constants.MULTIMEDIA_BUCKET_SUPABASE
    ): ByteArray {
        return storage.from(bucketName).downloadPublic(path)
    }

    /**
     * Elimina un archivo de un bucket en Supabase Storage.
     *
     * @param path Ruta del archivo dentro del bucket.
     * @param bucketName Nombre del bucket en Supabase.
     */
    suspend fun deleteImage(
        path: String,
        bucketName: String = Constants.MULTIMEDIA_BUCKET_SUPABASE
    ) {
        storage.from(bucketName).delete(path)
    }

    /**
     * Elimina múltiples archivos de un bucket en Supabase Storage.
     *
     * @param paths Lista de rutas de los archivos a eliminar.
     * @param bucketName Nombre del bucket en Supabase.
     */
    suspend fun deleteImages(
        paths: List<String>,
        bucketName: String = Constants.MULTIMEDIA_BUCKET_SUPABASE
    ) {
        if (paths.isEmpty()) return
        storage.from(bucketName).delete(paths)
    }

    /**
     * Lista los archivos dentro de un directorio o bucket en Supabase Storage.
     *
     * @param folderPath Carpeta dentro del bucket a consultar.
     * @param bucketName Nombre del bucket en Supabase.
     * @return Lista de objetos [FileObject] encontrados.
     */
    suspend fun listImages(
        folderPath: String = "",
        bucketName: String = Constants.MULTIMEDIA_BUCKET_SUPABASE
    ): List<FileObject> {
        return storage.from(bucketName).list(folderPath)
    }

    private fun buildPath(folderPath: String, fileName: String): String {
        val cleanFolder = folderPath.trim('/')
        return if (cleanFolder.isEmpty()) {
            fileName.trim('/')
        } else {
            "$cleanFolder/${fileName.trim('/')}"
        }
    }
}
