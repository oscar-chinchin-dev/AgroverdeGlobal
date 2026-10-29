package com.example.agroverdeglobal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.agroverdeglobal.ui.theme.AgroVerdeGlobalTheme

data class EnvironmentalMeasurement(
    val name: String,
    val value: Double,
    val unit: String,
    val type: MeasurementType
)

enum class MeasurementType {
    TEMPERATURE,
    AMBIENT_HUMIDITY,
    SOIL_HUMIDITY,
    LIGHT_LEVEL
}

data class EnvironmentalStatus(
    val isAlert: Boolean,
    val message: String
)

fun evaluateEnvironmentalStatus(measurement: EnvironmentalMeasurement): EnvironmentalStatus {
    val rule = when (measurement.type) {
        MeasurementType.TEMPERATURE -> MeasurementRule(18.0, 30.0, "Temperatura", "baja", "alta")
        MeasurementType.AMBIENT_HUMIDITY -> MeasurementRule(40.0, 80.0, "Humedad ambiental", "baja", "alta")
        MeasurementType.SOIL_HUMIDITY -> MeasurementRule(30.0, 80.0, "Humedad del suelo", "baja", "alta")
        MeasurementType.LIGHT_LEVEL -> MeasurementRule(20.0, 90.0, "Nivel de luz", "bajo", "alto")
    }

    return when {
        measurement.value < rule.minimum -> EnvironmentalStatus(true, "${rule.label} ${rule.lowDescription}")
        measurement.value > rule.maximum -> EnvironmentalStatus(true, "${rule.label} ${rule.highDescription}")
        else -> EnvironmentalStatus(false, "Normal")
    }
}

private data class MeasurementRule(
    val minimum: Double,
    val maximum: Double,
    val label: String,
    val lowDescription: String,
    val highDescription: String
)

fun overallEnvironmentalMessage(statuses: List<EnvironmentalStatus>): String =
    if (statuses.any { it.isAlert }) {
        "Alerta: se detectaron condiciones ambientales críticas"
    } else {
        "Condiciones ambientales normales"
    }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AgroVerdeGlobalTheme {
                AgroVerdeApp()
            }
        }
    }
}

@Composable
fun AgroVerdeApp() {
    val lecturaAmbiental = FuenteDatosAmbientalesSimulada.obtenerLectura()
    val measurementsWithStatus = lecturaAmbiental.toEnvironmentalMeasurements()
        .map { it to evaluateEnvironmentalStatus(it) }
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
                .verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("AgroVerde Global", style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text("Monitoreo del invernadero", style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Datos del sistema de monitoreo", style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            measurementsWithStatus.forEach { (measurement, status) ->
                EnvironmentalMeasurementCard(measurement, status)
            }
            OverallStatusIndicator(measurementsWithStatus.map { it.second })
        }
    }
}

@Composable
fun EnvironmentalMeasurementCard(measurement: EnvironmentalMeasurement, status: EnvironmentalStatus) {
    val statusColor = if (status.isAlert) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.primary
    }
    Card(
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(measurement.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(formatMeasurementValue(measurement.value), style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(6.dp))
                Text(measurement.unit, style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 5.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(14.dp))
            StatusLabel(status.message, statusColor)
        }
    }
}

@Composable
private fun StatusLabel(status: String, color: androidx.compose.ui.graphics.Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Spacer(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(8.dp))
        Text(status, style = MaterialTheme.typography.labelLarge, color = color,
            fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun OverallStatusIndicator(statuses: List<EnvironmentalStatus>) {
    val hasAlerts = statuses.any { it.isAlert }
    val containerColor = if (hasAlerts) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer
    val contentColor = if (hasAlerts) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer
    val message = overallEnvironmentalMessage(statuses)
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
        color = containerColor) {
        Text(message, modifier = Modifier.padding(20.dp),
            style = MaterialTheme.typography.titleSmall, color = contentColor,
            fontWeight = FontWeight.SemiBold)
    }
}

private fun formatMeasurementValue(value: Double): String =
    if (value % 1.0 == 0.0) value.toInt().toString() else value.toString()

@Preview(showBackground = true)
@Composable
fun AgroVerdeAppPreview() {
    AgroVerdeGlobalTheme(dynamicColor = false) {
        AgroVerdeApp()
    }
}
