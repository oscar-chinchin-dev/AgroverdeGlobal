package com.example.agroverdeglobal

/**
 * Fuente temporal que simula la lectura entregada por el sistema de monitoreo.
 * Puede sustituirse posteriormente por una fuente de datos real.
 */
object FuenteDatosAmbientalesSimulada {
    fun obtenerLectura(): LecturaAmbiental = LecturaAmbiental(
        temperatura = 24.8,
        humedadAmbiental = 65.0,
        humedadSuelo = 55.0,
        nivelLuz = 70.0
    )
}

fun LecturaAmbiental.toEnvironmentalMeasurements(): List<EnvironmentalMeasurement> = listOf(
    EnvironmentalMeasurement("Temperatura", temperatura, "°C", MeasurementType.TEMPERATURE),
    EnvironmentalMeasurement("Humedad ambiental", humedadAmbiental, "%", MeasurementType.AMBIENT_HUMIDITY),
    EnvironmentalMeasurement("Humedad del suelo", humedadSuelo, "%", MeasurementType.SOIL_HUMIDITY),
    EnvironmentalMeasurement("Nivel de luz", nivelLuz, "%", MeasurementType.LIGHT_LEVEL)
)
