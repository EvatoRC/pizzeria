package com.example.pizzeriaapp.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.pizzeriaapp.R
import com.example.pizzeriaapp.modelo.Categoria
import com.example.pizzeriaapp.modelo.Producto
import com.example.pizzeriaapp.ui.theme.Albahaca
import com.example.pizzeriaapp.ui.theme.Carbon
import com.example.pizzeriaapp.ui.theme.PrimaryContainer

/** Franja verde-blanco-rojo que el diseño usa en encabezados y tarjetas. */
@Composable
fun FranjaTricolor(modifier: Modifier = Modifier, alto: Dp = 4.dp) {
    Row(modifier.fillMaxWidth().height(alto)) {
        Box(Modifier.weight(1f).height(alto).background(Albahaca))
        Box(Modifier.weight(1f).height(alto).background(Color(0xFFF5F5F5)))
        Box(Modifier.weight(1f).height(alto).background(PrimaryContainer))
    }
}

/** Insignia pequeña en mayúsculas (D.O.P., Horno a leña, -15 %...). */
@Composable
fun Insignia(
    texto: String,
    fondo: Color,
    colorTexto: Color,
    modifier: Modifier = Modifier,
    icono: ImageVector? = null
) {
    Row(
        modifier
            .clip(CircleShape)
            .background(fondo)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icono != null) {
            Icon(icono, contentDescription = null, tint = colorTexto, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
        }
        Text(texto.uppercase(), style = MaterialTheme.typography.labelSmall, color = colorTexto)
    }
}

/** Chip blanco con ícono (Horno a la leña, Entrega en 30 min...). */
@Composable
fun ChipCaracteristica(texto: String, icono: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        shadowElevation = 1.dp
    ) {
        Row(Modifier.padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icono, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text(texto, style = MaterialTheme.typography.labelLarge, color = color)
        }
    }
}

/** Botón principal tipo píldora (rojo tomate por defecto). */
@Composable
fun BotonPildora(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = PrimaryContainer,
    iconoInicio: ImageVector? = null,
    iconoFin: ImageVector? = null,
    habilitado: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = habilitado,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(if (habilitado) 6.dp else 0.dp, CircleShape, ambientColor = color, spotColor = color),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = color, contentColor = Color.White)
    ) {
        if (iconoInicio != null) {
            Icon(iconoInicio, contentDescription = null)
            Spacer(Modifier.width(10.dp))
        }
        Text(
            texto,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = if (iconoFin != null) Modifier.weight(1f) else Modifier
        )
        if (iconoFin != null) Icon(iconoFin, contentDescription = null)
    }
}

/** Botón secundario blanco con borde suave. */
@Composable
fun BotonSecundario(
    texto: String,
    onClick: () -> Unit,
    color: Color,
    modifier: Modifier = Modifier,
    icono: ImageVector? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(56.dp),
        shape = CircleShape,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceContainerHigh),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            contentColor = color
        )
    ) {
        if (icono != null) {
            Icon(icono, contentDescription = null)
            Spacer(Modifier.width(10.dp))
        }
        Text(texto, style = MaterialTheme.typography.titleMedium)
    }
}

/** Logo circular de la pizzería. */
@Composable
fun LogoPizzeria(modifier: Modifier = Modifier, tamano: Dp = 40.dp) {
    Image(
        painter = painterResource(R.drawable.img_logo_pizzeria),
        contentDescription = "Logo Pizzería DUOC",
        modifier = modifier
            .size(tamano)
            .clip(RoundedCornerShape(tamano / 4)),
        contentScale = ContentScale.Fit
    )
}

/**
 * Imagen de cada producto. Hoy son ilustraciones provisionales por categoría;
 * para usar fotos reales agrega un drawable y asígnalo aquí por nombre de producto.
 */
@DrawableRes
fun imagenProducto(producto: Producto): Int = when (producto.categoria) {
    Categoria.PIZZA, Categoria.COMBO -> R.drawable.img_pizza
    Categoria.BEBIDA -> R.drawable.img_bebida
    Categoria.INGREDIENTE -> R.drawable.img_extra
}

/** Pestañas de la barra inferior del diseño. */
enum class Pestana(val etiqueta: String, val icono: ImageVector) {
    INICIO("Inicio", Icons.Outlined.Home),
    EXPLORAR("Explorar", Icons.Outlined.Explore),
    PEDIDOS("Pedidos", Icons.AutoMirrored.Outlined.ReceiptLong),
    PERFIL("Perfil", Icons.Outlined.Person)
}

@Composable
fun BarraInferior(seleccionada: Pestana, onSeleccionar: (Pestana) -> Unit) {
    Column {
        FranjaTricolor(alto = 2.dp)
        NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest) {
            Pestana.entries.forEach { pestana ->
                NavigationBarItem(
                    selected = pestana == seleccionada,
                    onClick = { onSeleccionar(pestana) },
                    icon = { Icon(pestana.icono, contentDescription = pestana.etiqueta) },
                    label = { Text(pestana.etiqueta, style = MaterialTheme.typography.labelMedium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryContainer,
                        selectedTextColor = PrimaryContainer,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        unselectedIconColor = Carbon,
                        unselectedTextColor = Carbon
                    )
                )
            }
        }
    }
}
