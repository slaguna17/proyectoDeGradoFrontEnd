package com.example.proyectodegrado.data.model

/**
 * Cuerpo de la solicitud para confirmar al backend que una imagen se subió a S3
 * y para que guarde la URL en la base de datos.
 *
 * @property entityId El ID de la entidad (Usuario, Producto, Tienda, etc.) a la que pertenece la imagen. Asegúrate que el tipo (Int, Long, String) coincida con tu modelo de datos.
 * @property entityType El tipo de entidad ("user", "product", "store", "category").
 * @property imageUrl La URL pública/final (`accessUrl`) de la imagen en S3.
 * @property imageKey La clave única del objeto en S3 (útil si el backend necesita borrarla en caso de error).
 */
data class ConfirmUploadRequest(
    val entityId: Int, // O Long, o String si tus IDs son así
    val entityType: String,
    val imageUrl: String,
    val imageKey: String
)