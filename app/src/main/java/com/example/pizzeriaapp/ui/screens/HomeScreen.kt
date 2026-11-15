package com.example.pizzeriaapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DeliveryDining
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.pizzeriaapp.logica.Menu
import com.example.pizzeriaapp.logica.PERIODO_PROMOCION
import com.example.pizzeriaapp.logica.describirPeriodoPromocion
import com.example.pizzeriaapp.logica.formatearPesos
import com.example.pizzeriaapp.modelo.Categoria
import com.example.pizzeriaapp.modelo.Cliente
import com.example.pizzeriaapp.modelo.Combo
import com.example.pizzeriaapp.modelo.DetalleTotal
import com.example.pizzeriaapp.modelo.Producto
import com.example.pizzeriaapp.modelo.promocion
import com.example.pizzeriaapp.ui.components.FranjaTricolor
import com.example.pizzeriaapp.ui.components.Insignia
import com.example.pizzeriaapp.ui.components.LogoPizzeria
import com.example.pizzeriaapp.ui.components.imagenProducto
import com.example.pizzeriaapp.ui.theme.Albahaca
import com.example.pizzeriaapp.ui.theme.AlbahacaSuave
import com.example.pizzeriaapp.ui.theme.AmbarSuave
import com.example.pizzeriaapp.ui.theme.AmbarTexto
import com.example.pizzeriaapp.ui.theme.BordeSuave
import com.example.pizzeriaapp.ui.theme.Carbon
import com.example.pizzeriaapp.ui.theme.PrimaryContainer
import com.example.pizzeriaapp.ui.theme.Secondary
import com.example.pizzeriaapp.ui.theme.SecondaryContainer
import com.example.pizzeriaapp.ui.theme.TertiaryFixed
import java.time.LocalDate

/** Menú principal (diseño "home_pizzería_duoc") con el catálogo de la semana 3. */
@Composable
fun HomeScreen(
    cliente: Cliente?,
    productos: List<Producto>,
    busqueda: String,
    onBusqueda: (String) -> Unit,
    categoria: Categoria?,
    onCategoria: (Categoria?) -> Unit,
    cantidadEn: (Producto) -> Int,
    onAgregar: (Producto) -> Unit,
    onQuitar: (Producto) -> Unit,
    unidadesCarrito: Int,
    totalCarrito: DetalleTotal,
    onVerCarrito: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        LazyColumn(
            contentPadding = PaddingValues(bottom = if (unidadesCarrito > 0) 110.dp else 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Encabezado(cliente) }
            item { TarjetaEntrega(cliente) }
            item { Buscador(busqueda, onBusqueda) }
            item { BannerPromocion(onPedir = { onAgregar(Menu.pepperoniFamiliar) }) }
            item { FiltroCategorias(categoria, onCategoria) }
            item {
                Row(Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Nuestro menú", style = MaterialTheme.typography.headlineMedium)
                        Text(
                            "Precios netos · IVA incluido en el total",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Insignia("${productos.size} productos", fondo = MaterialTheme.colorScheme.primaryContainer, colorTexto = PrimaryContainer)
                }
            }
            if (productos.isEmpty()) {
                item {
                    Text(
                        "No encontramos productos con \"$busqueda\".",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
            items(productos, key = { "${it.categoria}-${it.nombre}" }) { producto ->
                TarjetaProducto(producto, cantidadEn(producto), { onAgregar(producto) }, { onQuitar(producto) })
            }
            item { FilaCaracteristicas() }
        }

        if (unidadesCarrito > 0) {
            BarraCarrito(
                unidades = unidadesCarrito,
                total = totalCarrito.total,
                onVerCarrito = onVerCarrito,
                modifier = Modifier.align(Alignment.BottomCenter).padding(12.dp)
            )
        }
    }
}

@Composable
private fun Encabezado(cliente: Cliente?) {
    Column {
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            LogoPizzeria(tamano = 40.dp)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text("Pizzería DUOC", style = MaterialTheme.typography.titleLarge)
                Text("HOME", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = {}) { Icon(Icons.Outlined.Notifications, contentDescription = "Notificaciones") }
            Box(
                Modifier.size(40.dp).clip(CircleShape).background(TertiaryFixed),
                contentAlignment = Alignment.Center
            ) {
                if (cliente != null) {
                    Text(cliente.nombre.trim().take(1).uppercase(), style = MaterialTheme.typography.titleMedium, color = AmbarTexto)
                } else {
                    Icon(Icons.Outlined.Person, contentDescription = "Invitado", tint = AmbarTexto)
                }
            }
        }
        FranjaTricolor(alto = 2.dp)
    }
}

@Composable
private fun TarjetaEntrega(cliente: Cliente?) {
    Surface(
        Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        shadowElevation = 2.dp
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.DeliveryDining, contentDescription = null, tint = PrimaryContainer)
            }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text("ENTREGAR EN:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    cliente?.direccion ?: "Invitado · regístrate para elegir tu sede",
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Insignia("25–35 min", fondo = SecondaryContainer, colorTexto = Secondary, icono = Icons.Outlined.Schedule)
        }
    }
}

@Composable
private fun Buscador(busqueda: String, onBusqueda: (String) -> Unit) {
    OutlinedTextField(
        value = busqueda,
        onValueChange = onBusqueda,
        placeholder = { Text("Buscar por nombre o ingrediente", color = MaterialTheme.colorScheme.outline) },
        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
        trailingIcon = {
            if (busqueda.isNotEmpty()) {
                IconButton(onClick = { onBusqueda("") }) { Icon(Icons.Outlined.Close, contentDescription = "Limpiar búsqueda") }
            }
        },
        singleLine = true,
        shape = CircleShape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = PrimaryContainer
        ),
        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()
    )
}

/**
 * El diseño anunciaba "30 % OFF en familiares" con cupón; se muestra el porcentaje real que
 * entrega la anotación @Promocionable de PizzaFamiliar y el período de promociones (Range).
 */
@Composable
private fun BannerPromocion(onPedir: () -> Unit) {
    val familiar = Menu.pepperoniFamiliar
    val porcentaje: Int = familiar.promocion()?.porcentaje ?: 0
    val vigente: Boolean = LocalDate.now() in PERIODO_PROMOCION
    Box(
        Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF2B2525), Color(0xFF4A3B37))))
    ) {
        Column {
            FranjaTricolor(alto = 4.dp)
            Column(Modifier.padding(16.dp)) {
                Insignia(if (vigente) "Especial de la temporada" else "Próximamente", fondo = PrimaryContainer, colorTexto = Color.White)
                Spacer(Modifier.height(10.dp))
                Text(
                    "$porcentaje % OFF en ${familiar.nombre}",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Válido del ${describirPeriodoPromocion()}, más el descuento general del período.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = onPedir,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainer, contentColor = Color.White)
                ) {
                    Text("Pedir ya", style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.width(6.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun FiltroCategorias(seleccionada: Categoria?, onSeleccionar: (Categoria?) -> Unit) {
    Row(
        Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ChipCategoria("Todo", seleccionada == null) { onSeleccionar(null) }
        Categoria.entries.forEach { categoria ->
            ChipCategoria(categoria.etiqueta, seleccionada == categoria) { onSeleccionar(categoria) }
        }
    }
}

@Composable
private fun ChipCategoria(texto: String, seleccionado: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .clip(CircleShape)
            .background(if (seleccionado) Carbon else MaterialTheme.colorScheme.surfaceContainerLowest)
            .border(1.dp, if (seleccionado) Carbon else BordeSuave, CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(texto, style = MaterialTheme.typography.labelLarge, color = if (seleccionado) Color(0xFFFAF8F5) else Carbon)
    }
}

/** Descripción para la tarjeta: receta del producto (Map ingrediente -> unidades). */
private fun descripcion(producto: Producto): String = when (producto) {
    is Combo -> "${producto.pizza.nombre} + ${producto.bebida.nombre} a precio especial."
    else -> when (producto.categoria) {
        Categoria.BEBIDA -> "Bebida fría para acompañar tu pizza."
        Categoria.INGREDIENTE -> "Agrega una porción extra de ${producto.ingredientes.keys.first()}."
        else -> producto.ingredientes.entries.joinToString(", ") { (insumo, cantidad) ->
            if (cantidad > 1) "$insumo x$cantidad" else insumo
        }.replaceFirstChar { it.uppercase() } + "."
    }
}

@Composable
private fun TarjetaProducto(producto: Producto, cantidad: Int, onAgregar: () -> Unit, onQuitar: () -> Unit) {
    var favorito by rememberSaveable(producto.nombre) { mutableStateOf(false) }
    val promocion = producto.promocion()
    Surface(
        Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        shadowElevation = 3.dp
    ) {
        Column {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .background(Brush.verticalGradient(listOf(AmbarSuave, MaterialTheme.colorScheme.surfaceContainer)))
            ) {
                Image(
                    painter = painterResource(imagenProducto(producto)),
                    contentDescription = producto.nombre,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize().padding(14.dp)
                )
                Row(Modifier.padding(10.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Insignia(producto.categoria.etiqueta, fondo = AlbahacaSuave, colorTexto = Albahaca, icono = Icons.Outlined.Verified)
                    if (promocion != null) {
                        Insignia("−${promocion.porcentaje} % promo", fondo = PrimaryContainer, colorTexto = Color.White, icono = Icons.Outlined.StarOutline)
                    }
                }
                Box(
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f))
                        .clickable { favorito = !favorito },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (favorito) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = if (favorito) PrimaryContainer else Carbon,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Column(Modifier.padding(16.dp)) {
                Text(producto.nombre, style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(4.dp))
                Text(
                    descripcion(producto),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("PRECIO NETO", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(formatearPesos(producto.precio), style = MaterialTheme.typography.titleLarge)
                    }
                    if (cantidad > 0) {
                        Selector(cantidad, onQuitar, onAgregar)
                    } else {
                        BotonCircular(Icons.Filled.Add, "Agregar ${producto.nombre}", onAgregar)
                    }
                }
            }
        }
    }
}

/** Selector de cantidad (stepper) del diseño. */
@Composable
fun Selector(cantidad: Int, onMenos: () -> Unit, onMas: () -> Unit) {
    Row(
        Modifier.clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceContainer),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onMenos) { Icon(Icons.Filled.Remove, contentDescription = "Quitar uno") }
        Text("$cantidad", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        IconButton(onClick = onMas) { Icon(Icons.Filled.Add, contentDescription = "Agregar uno", tint = PrimaryContainer) }
    }
}

@Composable
private fun BotonCircular(icono: ImageVector, descripcion: String, onClick: () -> Unit) {
    Box(
        Modifier.size(44.dp).clip(CircleShape).background(PrimaryContainer).clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icono, contentDescription = descripcion, tint = Color.White)
    }
}

@Composable
private fun FilaCaracteristicas() {
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
        listOf(
            Triple(Icons.Outlined.LocalFireDepartment, "Horno 450 °C", Albahaca),
            Triple(Icons.Outlined.Schedule, "Masa 48 h", AmbarTexto),
            Triple(Icons.Outlined.Verified, "100 % Mozzarella", PrimaryContainer)
        ).forEach { (icono, texto, color) ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(icono, contentDescription = null, tint = color)
                Text(texto, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

/** Barra flotante del carrito con el total calculado por calcularTotalPedido(). */
@Composable
private fun BarraCarrito(unidades: Int, total: Int, onVerCarrito: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF2B2525),
        shadowElevation = 10.dp
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box {
                Box(
                    Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(PrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.ShoppingBag, contentDescription = null, tint = Color.White)
                }
                Box(
                    Modifier.align(Alignment.TopEnd).size(20.dp).clip(CircleShape).background(Color(0xFFF4A261)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("$unidades", style = MaterialTheme.typography.labelSmall, color = Carbon)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Tu pedido actual", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.75f))
                Text(
                    "${if (unidades == 1) "1 producto" else "$unidades productos"} · ${formatearPesos(total)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
            Button(
                onClick = onVerCarrito,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainer, contentColor = Color.White)
            ) {
                Text("Ver carrito", style = MaterialTheme.typography.labelLarge)
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
            }
        }
    }
}
