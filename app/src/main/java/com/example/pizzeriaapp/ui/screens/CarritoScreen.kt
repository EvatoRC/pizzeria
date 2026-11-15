package com.example.pizzeriaapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pizzeriaapp.logica.MINIMO_PIZZAS_PARA_DESCUENTO
import com.example.pizzeriaapp.logica.PERIODO_PROMOCION
import com.example.pizzeriaapp.logica.PORCENTAJE_DESCUENTO
import com.example.pizzeriaapp.logica.PORCENTAJE_DESCUENTO_PERIODO
import com.example.pizzeriaapp.logica.PORCENTAJE_IVA
import com.example.pizzeriaapp.logica.ResultadoPedido
import com.example.pizzeriaapp.logica.describirFaltante
import com.example.pizzeriaapp.logica.formatearFecha
import com.example.pizzeriaapp.logica.formatearPesos
import com.example.pizzeriaapp.modelo.Cliente
import com.example.pizzeriaapp.modelo.DetalleTotal
import com.example.pizzeriaapp.modelo.Producto
import com.example.pizzeriaapp.ui.components.BotonPildora
import com.example.pizzeriaapp.ui.components.BotonSecundario
import com.example.pizzeriaapp.ui.components.FranjaTricolor
import com.example.pizzeriaapp.ui.components.Insignia
import com.example.pizzeriaapp.ui.components.imagenProducto
import com.example.pizzeriaapp.ui.theme.AlbahacaSuave
import com.example.pizzeriaapp.ui.theme.AmbarSuave
import com.example.pizzeriaapp.ui.theme.AmbarTexto
import com.example.pizzeriaapp.ui.theme.PrimaryContainer
import com.example.pizzeriaapp.ui.theme.Secondary
import java.time.LocalDate

/** Carrito y resumen del pedido (pantalla nueva con el estilo del diseño). */
@Composable
fun CarritoScreen(
    cliente: Cliente?,
    cantidades: Map<Producto, Int>,
    pizzas: Int,
    total: DetalleTotal,
    fecha: LocalDate,
    notas: String,
    onNotas: (String) -> Unit,
    resultado: ResultadoPedido?,
    onAgregar: (Producto) -> Unit,
    onQuitar: (Producto) -> Unit,
    onEliminar: (Producto) -> Unit,
    onConfirmar: () -> Unit,
    onReabastecer: (ResultadoPedido.SinStock) -> Unit,
    onCerrarResultado: () -> Unit,
    onVolver: () -> Unit,
    onVerPedidos: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).imePadding()) {
        Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onVolver) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver al menú") }
            Text("Tu carrito", style = MaterialTheme.typography.headlineMedium)
        }
        FranjaTricolor(alto = 2.dp)

        Column(
            Modifier.verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            when (resultado) {
                is ResultadoPedido.Aceptado -> PedidoAceptado(resultado, onVerPedidos, onVolver)
                is ResultadoPedido.SinStock -> SinStock(resultado, { onReabastecer(resultado) }, onCerrarResultado)
                ResultadoPedido.SinCupo -> Aviso("Por hoy no se aceptan más pedidos: se alcanzó el máximo diario.")
                is ResultadoPedido.Invalido -> Aviso(resultado.mensaje)
                null -> Unit
            }

            if (cantidades.isEmpty()) {
                if (resultado !is ResultadoPedido.Aceptado) CarritoVacio(onVolver)
            } else {
                cantidades.forEach { (producto, cantidad) ->
                    FilaProducto(producto, cantidad, { onAgregar(producto) }, { onQuitar(producto) }, { onEliminar(producto) })
                }

                PistasDescuento(pizzas, fecha)

                OutlinedTextField(
                    value = notas,
                    onValueChange = onNotas,
                    label = { Text("Notas del pedido (opcional)") },
                    placeholder = { Text("Ej. sin cebolla, tocar el timbre") },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                        focusedBorderColor = PrimaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Desglose(total)

                Text(
                    if (cliente != null) "Entrega a ${cliente.nombre} · ${cliente.direccion}"
                    else "Estás como invitado: al confirmar te pediremos registrarte.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                BotonPildora(
                    "Confirmar pedido · ${formatearPesos(total.total)}",
                    onClick = onConfirmar,
                    iconoInicio = Icons.Outlined.ShoppingBag
                )
            }
        }
    }
}

@Composable
private fun FilaProducto(producto: Producto, cantidad: Int, onMas: () -> Unit, onMenos: () -> Unit, onEliminar: () -> Unit) {
    Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surfaceContainerLowest, shadowElevation = 2.dp) {
        Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(imagenProducto(producto)),
                contentDescription = null,
                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(12.dp)).background(AmbarSuave).padding(6.dp)
            )
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(producto.nombre, style = MaterialTheme.typography.titleMedium)
                Text(
                    "${formatearPesos(producto.precio)} c/u · ${formatearPesos(producto.precio * cantidad)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Selector(cantidad, onMenos, onMas)
            }
            IconButton(onClick = onEliminar) {
                Icon(Icons.Outlined.DeleteOutline, contentDescription = "Eliminar ${producto.nombre}", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

/** Explica qué reglas de descuento aplican o cuánto falta para activarlas. */
@Composable
private fun PistasDescuento(pizzas: Int, fecha: LocalDate) {
    val faltan: Int = MINIMO_PIZZAS_PARA_DESCUENTO + 1 - pizzas
    val textoVolumen: String = if (faltan > 0) {
        "Agrega ${if (faltan == 1) "1 pizza más" else "$faltan pizzas más"} y obtén $PORCENTAJE_DESCUENTO % por volumen."
    } else "Descuento por volumen activo: $PORCENTAJE_DESCUENTO % (más de $MINIMO_PIZZAS_PARA_DESCUENTO pizzas)."
    val textoPeriodo: String = if (fecha in PERIODO_PROMOCION) {
        "Pedido del ${formatearFecha(fecha)}: dentro del período de promociones (−$PORCENTAJE_DESCUENTO_PERIODO %)."
    } else "Pedido del ${formatearFecha(fecha)}: fuera del período de promociones."
    Surface(shape = RoundedCornerShape(12.dp), color = AlbahacaSuave) {
        Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(textoVolumen, textoPeriodo).forEach {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(Icons.Outlined.LocalOffer, contentDescription = null, tint = Secondary, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(it, style = MaterialTheme.typography.bodySmall, color = Secondary)
                }
            }
        }
    }
}

/** Desglose de DetalleTotal: mismos conceptos que el resumen por consola de la semana 3. */
@Composable
private fun Desglose(total: DetalleTotal) {
    Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surfaceContainerLowest, shadowElevation = 2.dp) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Resumen", style = MaterialTheme.typography.titleMedium)
            LineaMonto("Subtotal neto", total.subtotal)
            if (total.descuentoVolumen > 0) LineaMonto("Descuento por volumen", -total.descuentoVolumen, Secondary)
            if (total.descuentoPromocionables > 0) LineaMonto("Productos @Promocionable", -total.descuentoPromocionables, Secondary)
            if (total.descuentoPeriodo > 0) LineaMonto("Período de promociones", -total.descuentoPeriodo, Secondary)
            LineaMonto("IVA ($PORCENTAJE_IVA %)", total.impuesto)
            HorizontalDivider(Modifier.padding(vertical = 4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Total", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                Text(formatearPesos(total.total), style = MaterialTheme.typography.titleLarge, color = PrimaryContainer)
            }
            if (total.descuentoTotal > 0) {
                Insignia("Ahorras ${formatearPesos(total.descuentoTotal)}", fondo = AlbahacaSuave, colorTexto = Secondary)
            }
        }
    }
}

@Composable
private fun LineaMonto(concepto: String, monto: Int, color: Color = MaterialTheme.colorScheme.onSurface) {
    Row {
        Text(concepto, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text(
            if (monto < 0) "−${formatearPesos(-monto)}" else formatearPesos(monto),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

@Composable
private fun PedidoAceptado(resultado: ResultadoPedido.Aceptado, onVerPedidos: () -> Unit, onSeguir: () -> Unit) {
    val pedido = resultado.pedido
    Surface(shape = RoundedCornerShape(16.dp), color = AlbahacaSuave) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = Secondary)
                Spacer(Modifier.width(8.dp))
                Text("Pedido #${pedido.id} aceptado", style = MaterialTheme.typography.headlineSmall, color = Secondary)
            }
            Text("Estado: ${pedido.estado.etiqueta}", style = MaterialTheme.typography.bodyMedium)
            resultado.repartidor?.let {
                Text("Repartidor: ${it.nombre} (${it.distanciaKm} km, el más cercano disponible)", style = MaterialTheme.typography.bodyMedium)
            }
            Text(resultado.notificacion, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Total pagado: ${formatearPesos(pedido.calcularTotal().total)}", style = MaterialTheme.typography.titleMedium)
            BotonPildora("Ver mis pedidos", onClick = onVerPedidos, color = Secondary, iconoInicio = Icons.AutoMirrored.Outlined.ReceiptLong)
            BotonSecundario("Seguir comprando", onClick = onSeguir, color = Secondary)
        }
    }
}

@Composable
private fun SinStock(resultado: ResultadoPedido.SinStock, onReabastecer: () -> Unit, onCerrar: () -> Unit) {
    Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.errorContainer) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                Spacer(Modifier.width(8.dp))
                Text(
                    "Pedido #${resultado.idPedido}: ingredientes insuficientes",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
            resultado.faltantes.forEach {
                Text("• ${describirFaltante(it)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer)
            }
            Text("Pasos para reabastecer:", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onErrorContainer)
            resultado.pasos.forEach {
                Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer)
            }
            Spacer(Modifier.height(4.dp))
            BotonPildora("Registrar reabastecimiento y reintentar", onClick = onReabastecer, color = AmbarTexto, iconoInicio = Icons.Outlined.Inventory2)
            BotonSecundario("Modificar pedido", onClick = onCerrar, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
private fun Aviso(texto: String) {
    Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.errorContainer) {
        Text(texto, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.fillMaxWidth().padding(12.dp))
    }
}

@Composable
private fun CarritoVacio(onVolver: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(top = 40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(96.dp).clip(RoundedCornerShape(48.dp)).background(AmbarSuave), contentAlignment = Alignment.Center) {
            Icon(Icons.Outlined.ShoppingBag, contentDescription = null, tint = AmbarTexto, modifier = Modifier.size(44.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text("Tu carrito está vacío", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Agrega pizzas, bebidas o combos desde el menú.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(20.dp))
        BotonPildora("Ver el menú", onClick = onVolver)
    }
}
