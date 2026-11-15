package com.example.pizzeriaapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.DeliveryDining
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pizzeriaapp.Greeting
import com.example.pizzeriaapp.logica.PERIODO_PROMOCION
import com.example.pizzeriaapp.logica.PORCENTAJE_DESCUENTO_PERIODO
import com.example.pizzeriaapp.logica.describirPeriodoPromocion
import com.example.pizzeriaapp.ui.components.BotonPildora
import com.example.pizzeriaapp.ui.components.BotonSecundario
import com.example.pizzeriaapp.ui.components.ChipCaracteristica
import com.example.pizzeriaapp.ui.components.FranjaTricolor
import com.example.pizzeriaapp.ui.components.Insignia
import com.example.pizzeriaapp.ui.components.LogoPizzeria
import com.example.pizzeriaapp.ui.theme.Albahaca
import com.example.pizzeriaapp.ui.theme.AmbarTexto
import com.example.pizzeriaapp.ui.theme.PizzeriaAppTheme
import com.example.pizzeriaapp.ui.theme.PrimaryContainer
import com.example.pizzeriaapp.ui.theme.Secondary
import com.example.pizzeriaapp.ui.theme.SecondaryContainer
import com.example.pizzeriaapp.ui.theme.TertiaryFixed
import java.time.LocalDate

/** Pantalla de bienvenida (diseño "inicio_pizzería_duoc"). */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InicioScreen(
    onComenzar: () -> Unit,
    onTengoCuenta: () -> Unit,
    onInvitado: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FranjaTricolor(Modifier.padding(top = 8.dp).clip(CircleShape))
        Spacer(Modifier.height(28.dp))

        // Logo con insignia D.O.P.
        Box(contentAlignment = Alignment.Center) {
            Surface(shape = CircleShape, shadowElevation = 10.dp, color = MaterialTheme.colorScheme.surfaceContainerLowest) {
                Box(Modifier.size(176.dp).padding(14.dp), contentAlignment = Alignment.Center) {
                    LogoPizzeria(tamano = 148.dp)
                }
            }
            Insignia(
                "D.O.P.",
                fondo = Secondary,
                colorTexto = MaterialTheme.colorScheme.onSecondary,
                icono = Icons.Outlined.Verified,
                modifier = Modifier.align(Alignment.BottomEnd).offset(x = 16.dp)
            )
        }

        Spacer(Modifier.height(20.dp))
        Insignia("Tradición napolitana", fondo = TertiaryFixed, colorTexto = AmbarTexto, icono = Icons.Outlined.LocalOffer)
        Spacer(Modifier.height(12.dp))

        // Paso 5 de la guía: la función Greeting muestra el mensaje de bienvenida.
        Greeting()

        Spacer(Modifier.height(12.dp))
        Text(
            "Masa madre con 48 h de fermentación, ingredientes frescos y beneficios exclusivos para toda la comunidad DUOC.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(20.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ChipCaracteristica("Horno a la leña", Icons.Outlined.LocalFireDepartment, AmbarTexto)
            ChipCaracteristica("Entrega en 30 min", Icons.Outlined.DeliveryDining, Albahaca)
            ChipCaracteristica("100 % Mozzarella", Icons.Outlined.StarOutline, PrimaryContainer)
        }

        Spacer(Modifier.height(24.dp))
        TarjetaPromocion(onClick = onInvitado)

        Spacer(Modifier.height(24.dp))
        BotonPildora(
            "Comenzar a Pedir",
            onClick = onComenzar,
            iconoInicio = Icons.Filled.Restaurant,
            iconoFin = Icons.AutoMirrored.Filled.ArrowForward
        )
        Spacer(Modifier.height(12.dp))
        BotonSecundario("Ya tengo una cuenta DUOC", onClick = onTengoCuenta, color = Secondary, icono = Icons.Outlined.Badge)
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onInvitado) {
            Text("Explorar menú como invitado", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.width(6.dp))
            Icon(Icons.Outlined.Visibility, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.height(24.dp))
    }
}

/**
 * El diseño ofrecía "25 % OFF estudiantes"; se reemplazó por la regla real de la semana 3:
 * descuento del período de promociones (Range de fechas PERIODO_PROMOCION).
 */
@Composable
private fun TarjetaPromocion(onClick: () -> Unit) {
    val vigente: Boolean = LocalDate.now() in PERIODO_PROMOCION
    Surface(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        shadowElevation = 2.dp
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(52.dp).clip(CircleShape).background(SecondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.LocalOffer, contentDescription = null, tint = Secondary)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    if (vigente) "Promociones de temporada" else "Próximas promociones",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "$PORCENTAJE_DESCUENTO_PERIODO % OFF en todo tu pedido del ${describirPeriodoPromocion()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun InicioPreview() {
    PizzeriaAppTheme { InicioScreen({}, {}, {}) }
}
