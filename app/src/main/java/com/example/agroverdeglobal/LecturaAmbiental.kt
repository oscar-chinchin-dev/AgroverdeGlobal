package com.example.agroverdeglobal

/**
 * Representa una lectura completa recibida desde el sistema de monitoreo.
 */
data class LecturaAmbiental(
    val temperatura: Double,
    val humedadAmbiental: Double,
    val humedadSuelo: Double,
    val nivelLuz: Double
)

