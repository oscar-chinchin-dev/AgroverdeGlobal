package com.example.agroverdeglobal

import org.junit.Test

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class EnvironmentalStatusTest {
    @Test
    fun simulatedSource_returnsExpectedNormalEnvironmentalReading() {
        val lectura = FuenteDatosAmbientalesSimulada.obtenerLectura()

        assertEquals(24.8, lectura.temperatura, 0.0)
        assertEquals(65.0, lectura.humedadAmbiental, 0.0)
        assertEquals(55.0, lectura.humedadSuelo, 0.0)
        assertEquals(70.0, lectura.nivelLuz, 0.0)
    }

    @Test
    fun normalMeasurements_showNormalStatus_andNormalGeneralIndicator() {
        val statuses = FuenteDatosAmbientalesSimulada.obtenerLectura()
            .toEnvironmentalMeasurements()
            .map(::evaluateEnvironmentalStatus)

        assertTrue(statuses.none { it.isAlert })
        assertEquals("Condiciones ambientales normales", overallEnvironmentalMessage(statuses))
    }

    @Test
    fun highTemperature_showsTemperatureHighAlert_andGeneralAlert() {
        val readingWithHighTemperature = FuenteDatosAmbientalesSimulada.obtenerLectura()
            .copy(temperatura = 35.0)
        val status = evaluateEnvironmentalStatus(
            readingWithHighTemperature.toEnvironmentalMeasurements()
                .first { it.type == MeasurementType.TEMPERATURE }
        )

        assertTrue(status.isAlert)
        assertEquals("Temperatura alta", status.message)
        assertEquals("Alerta: se detectaron condiciones ambientales críticas", overallEnvironmentalMessage(listOf(status)))
    }

    @Test
    fun lowSoilHumidity_showsSoilHumidityLowAlert_andGeneralAlert() {
        val status = statusOf(MeasurementType.SOIL_HUMIDITY, 15.0)

        assertTrue(status.isAlert)
        assertEquals("Humedad del suelo baja", status.message)
        assertEquals("Alerta: se detectaron condiciones ambientales críticas", overallEnvironmentalMessage(listOf(status)))
    }

    @Test
    fun lowLightLevel_showsLightLevelLowAlert_andGeneralAlert() {
        val status = statusOf(MeasurementType.LIGHT_LEVEL, 10.0)

        assertTrue(status.isAlert)
        assertEquals("Nivel de luz bajo", status.message)
        assertEquals("Alerta: se detectaron condiciones ambientales críticas", overallEnvironmentalMessage(listOf(status)))
    }

    @Test
    fun thresholdLimits_areNormal() {
        assertFalse(statusOf(MeasurementType.TEMPERATURE, 18.0).isAlert)
        assertFalse(statusOf(MeasurementType.TEMPERATURE, 30.0).isAlert)
        assertFalse(statusOf(MeasurementType.AMBIENT_HUMIDITY, 40.0).isAlert)
        assertFalse(statusOf(MeasurementType.AMBIENT_HUMIDITY, 80.0).isAlert)
        assertFalse(statusOf(MeasurementType.SOIL_HUMIDITY, 30.0).isAlert)
        assertFalse(statusOf(MeasurementType.SOIL_HUMIDITY, 80.0).isAlert)
        assertFalse(statusOf(MeasurementType.LIGHT_LEVEL, 20.0).isAlert)
        assertFalse(statusOf(MeasurementType.LIGHT_LEVEL, 90.0).isAlert)
    }

    private fun statusOf(type: MeasurementType, value: Double): EnvironmentalStatus =
        evaluateEnvironmentalStatus(EnvironmentalMeasurement("Prueba", value, "%", type))
}
