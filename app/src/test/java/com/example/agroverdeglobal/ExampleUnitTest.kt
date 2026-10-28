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
    fun normalMeasurements_showNormalStatus_andNormalGeneralIndicator() {
        val statuses = listOf(
            statusOf(MeasurementType.TEMPERATURE, 24.8),
            statusOf(MeasurementType.AMBIENT_HUMIDITY, 65.0),
            statusOf(MeasurementType.SOIL_HUMIDITY, 55.0),
            statusOf(MeasurementType.LIGHT_LEVEL, 70.0)
        )

        assertTrue(statuses.none { it.isAlert })
        assertEquals("Condiciones ambientales normales", overallEnvironmentalMessage(statuses))
    }

    @Test
    fun highTemperature_showsTemperatureHighAlert_andGeneralAlert() {
        val status = statusOf(MeasurementType.TEMPERATURE, 35.0)

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
