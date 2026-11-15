package com.example.pizzeriaapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pizzeriaapp.logica.formatearFecha
import com.example.pizzeriaapp.logica.formatearPesos
import com.example.pizzeriaapp.modelo.EstadoPedido
import com.example.pizzeriaapp.modelo.Pedido
import com.example.pizzeriaapp.ui.components.BotonSecundario
import com.example.pizzeriaapp.ui.components.FranjaTricolor
import com.example.pizzeriaapp.ui.components.Insignia
import com.example.pizzeriaapp.ui.theme.AlbahacaSuave
import com.example.pizzeriaapp.ui.theme.AmbarSuave
import com.example.pizzeriaapp.ui.theme.AmbarTexto
import com.example.pizzeriaapp.ui.theme.Secondary

/** Pedidos del día (RegistroDiario) con su estado y repartidor. */
@Composable
fun PedidosScreen(
    pedidos: List<Pedido>,
    version: Int,   // Pedido no es observable: al cambiar un estado este número cambia y se redibuja
    onEntregado: (Pedido) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Text("Pedidos del día", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(16.dp))
        FranjaTricolor(alto = 2.dp)
        if (pedidos.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                Text("Aún no hay pedidos registrados hoy.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(pedidos, key = { it.id }) { pedido -> TarjetaPedido(pedido, version, onEntregado) }
            }
        }
    }
}

@Composable
private fun TarjetaPedido(
    pedido: Pedido,
    @Suppress("UNUSED_PARAMETER") version: Int,
    onEntregado: (Pedido) -> Unit
) {
    // Desestructuración (semana 3): val (cliente, productos, total) = pedido
    val (cliente, productos, total) = pedido
    val entregado: Boolean = pedido.estado == EstadoPedido.ENTREGADO
    Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surfaceContainerLowest, shadowElevation = 2.dp) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Pedido #${pedido.id}", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                Insignia(
                    pedido.estado.etiqueta,
                    fondo = if (entregado) AlbahacaSuave else AmbarSuave,
                    colorTexto = if (entregado) Secondary else AmbarTexto
                )
            }
            Text("${cliente.nombre} · ${formatearFecha(pedido.fecha)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                pedido.cantidadesPorProducto().entries.joinToString { (producto, cantidad) -> "${cantidad}x ${producto.nombre}" },
                style = MaterialTheme.typography.bodyMedium
            )
            Text("Notas: ${pedido.notasParaMostrar()}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Repartidor: ${pedido.nombreRepartidor()}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("${productos.size} unidades · Total ${formatearPesos(total)}", style = MaterialTheme.typography.titleMedium)
            if (pedido.estado == EstadoPedido.EN_CAMINO) {
                Spacer(Modifier.height(6.dp))
                BotonSecundario("Marcar como entregado", onClick = { onEntregado(pedido) }, color = Secondary, icono = Icons.Outlined.TaskAlt)
            }
        }
    }
}
