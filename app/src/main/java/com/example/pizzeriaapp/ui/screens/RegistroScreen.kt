package com.example.pizzeriaapp.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockReset
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PhoneIphone
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pizzeriaapp.logica.Menu
import com.example.pizzeriaapp.logica.PORCENTAJE_DESCUENTO_PERIODO
import com.example.pizzeriaapp.logica.SEDES
import com.example.pizzeriaapp.logica.describirPeriodoPromocion
import com.example.pizzeriaapp.logica.errorClave
import com.example.pizzeriaapp.logica.errorConfirmacion
import com.example.pizzeriaapp.logica.errorCorreo
import com.example.pizzeriaapp.logica.errorDireccion
import com.example.pizzeriaapp.logica.errorNombre
import com.example.pizzeriaapp.logica.errorTelefono
import com.example.pizzeriaapp.modelo.productosPromocionables
import com.example.pizzeriaapp.modelo.promocion
import com.example.pizzeriaapp.ui.components.BotonPildora
import com.example.pizzeriaapp.ui.components.FranjaTricolor
import com.example.pizzeriaapp.ui.components.Insignia
import com.example.pizzeriaapp.ui.components.LogoPizzeria
import com.example.pizzeriaapp.ui.theme.Albahaca
import com.example.pizzeriaapp.ui.theme.AlbahacaSuave
import com.example.pizzeriaapp.ui.theme.PizzeriaAppTheme
import com.example.pizzeriaapp.ui.theme.PrimaryContainer
import com.example.pizzeriaapp.ui.theme.Secondary

private const val DOMICILIO_PARTICULAR = "Despacho a domicilio particular"

/**
 * Registro de cliente (diseño "registro_pizzería_duoc").
 * Las validaciones son las de la semana 3 (Regex de correo y teléfono); en vez de volver a
 * preguntar por consola, el error se muestra bajo cada campo.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    onVolver: () -> Unit,
    onRegistrar: (nombre: String, telefono: String, correo: String, direccion: String) -> Unit
) {
    var nombre by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var telefono by rememberSaveable { mutableStateOf("") }
    var sede by rememberSaveable { mutableStateOf("") }
    var domicilio by rememberSaveable { mutableStateOf("") }
    // La contraseña solo se valida en el formulario: el modelo Cliente de la semana 3 no la almacena.
    var clave by rememberSaveable { mutableStateOf("") }
    var confirmacion by rememberSaveable { mutableStateOf("") }
    var verClave by remember { mutableStateOf(false) }
    var verConfirmacion by remember { mutableStateOf(false) }
    var aceptaTerminos by rememberSaveable { mutableStateOf(false) }
    var alertasJueves by rememberSaveable { mutableStateOf(true) }
    var intentoEnviar by rememberSaveable { mutableStateOf(false) }
    var menuSedesAbierto by remember { mutableStateOf(false) }

    val direccion: String = if (sede == DOMICILIO_PARTICULAR) domicilio else sede
    val errores: Map<String, String?> = mapOf(
        "nombre" to errorNombre(nombre),
        "correo" to errorCorreo(correo),
        "telefono" to errorTelefono(telefono),
        "direccion" to errorDireccion(direccion),
        "clave" to errorClave(clave),
        "confirmacion" to errorConfirmacion(clave, confirmacion),
        "terminos" to if (aceptaTerminos) null else "Debes aceptar los términos para continuar."
    )
    // Los errores aparecen al escribir en el campo o al intentar crear la cuenta.
    fun error(campo: String, valor: String): String? =
        if (intentoEnviar || valor.isNotEmpty()) errores[campo] else null

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
    ) {
        // Barra superior
        Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onVolver) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") }
            LogoPizzeria(tamano = 36.dp)
            Spacer(Modifier.width(10.dp))
            Column {
                Text("Registro", style = MaterialTheme.typography.headlineMedium)
                FranjaTricolor(Modifier.width(44.dp).clip(CircleShape), alto = 3.dp)
            }
        }

        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Insignia("Beneficio exclusivo DUOC", fondo = AlbahacaSuave, colorTexto = Secondary, icono = Icons.Outlined.LocalOffer)
            Spacer(Modifier.height(8.dp))
            Text("Únete al Club de la Pizza", style = MaterialTheme.typography.headlineLarge)
            Spacer(Modifier.height(4.dp))
            Text(
                "Crea tu cuenta y aprovecha las promociones del ${describirPeriodoPromocion()}.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
            TarjetaBeneficios()
            Spacer(Modifier.height(20.dp))

            CampoFormulario("Nombre y apellido", Icons.Outlined.Person, PrimaryContainer, nombre, { nombre = it },
                "Ej. Constanza Morales", error("nombre", nombre))
            CampoFormulario("Correo electrónico", Icons.Outlined.AlternateEmail, PrimaryContainer, correo, { correo = it },
                "c.morales@duocuc.cl", error("correo", correo), KeyboardType.Email)

            // Teléfono: Regex de la semana 3 -> 10 dígitos; si no trae código de país se asume +56.
            Etiqueta("Teléfono móvil", Icons.Outlined.PhoneIphone, PrimaryContainer)
            Row(verticalAlignment = Alignment.Top) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text("🇨🇱 +56", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(horizontal = 14.dp, vertical = 17.dp))
                }
                Spacer(Modifier.width(8.dp))
                CampoTexto(telefono, { telefono = it.filter { c -> c.isDigit() || c in " ().-" } }, "912 345 6789",
                    error("telefono", telefono), KeyboardType.Phone, Modifier.weight(1f))
            }

            Etiqueta("Dirección o sede DUOC frecuente", Icons.Outlined.LocationOn, Albahaca)
            ExposedDropdownMenuBox(expanded = menuSedesAbierto, onExpandedChange = { menuSedesAbierto = it }) {
                OutlinedTextField(
                    value = sede,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Selecciona tu sede preferida...") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuSedesAbierto) },
                    isError = error("direccion", direccion) != null && sede.isEmpty(),
                    shape = RoundedCornerShape(12.dp),
                    colors = coloresCampo(),
                    modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(expanded = menuSedesAbierto, onDismissRequest = { menuSedesAbierto = false }) {
                    SEDES.forEach { opcion ->
                        DropdownMenuItem(text = { Text(opcion) }, onClick = { sede = opcion; menuSedesAbierto = false })
                    }
                }
            }
            if (sede == DOMICILIO_PARTICULAR) {
                Spacer(Modifier.height(8.dp))
                CampoTexto(domicilio, { domicilio = it }, "Calle, número y comuna", error("direccion", domicilio))
            } else {
                MensajeError(if (sede.isEmpty()) error("direccion", sede) else null)
            }

            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Etiqueta("Crear contraseña", Icons.Outlined.Lock, PrimaryContainer, Modifier.weight(1f))
                Text("Mínimo 6 caracteres", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
            }
            CampoClave(clave, { clave = it }, verClave, { verClave = !verClave }, error("clave", clave))
            BarraFortaleza(clave)

            Etiqueta("Confirmar contraseña", Icons.Outlined.LockReset, PrimaryContainer)
            CampoClave(confirmacion, { confirmacion = it }, verConfirmacion, { verConfirmacion = !verConfirmacion },
                error("confirmacion", confirmacion))

            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = aceptaTerminos, onCheckedChange = { aceptaTerminos = it })
                Text(
                    "Acepto los Términos del Club Pizzería DUOC y la Política de Privacidad.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            MensajeError(if (intentoEnviar) errores["terminos"] else null)

            Spacer(Modifier.height(8.dp))
            Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surfaceContainerLow) {
                Row(Modifier.padding(8.dp), verticalAlignment = Alignment.Top) {
                    Checkbox(
                        checked = alertasJueves,
                        onCheckedChange = { alertasJueves = it },
                        colors = CheckboxDefaults.colors(checkedColor = Secondary)
                    )
                    Column(Modifier.padding(top = 10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Celebration, contentDescription = null, tint = Secondary, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Quiero recibir promociones", style = MaterialTheme.typography.labelLarge)
                        }
                        Text(
                            "Avisos de descuentos en mi sede habitual.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            BotonPildora(
                "Crear mi cuenta",
                onClick = {
                    intentoEnviar = true
                    if (errores.values.all { it == null }) onRegistrar(nombre, telefono, correo, direccion)
                },
                color = Secondary,
                iconoInicio = Icons.Outlined.CheckCircle
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "¿Ya tienes cuenta? Ingresa el mismo nombre y correo: el sistema reconoce clientes ya registrados y no crea duplicados.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

/**
 * Reemplaza el "−20 % con DUOCPIZZA" del diseño por las promociones reales: el descuento del
 * período y los productos marcados con @Promocionable (detectados con reflection).
 */
@Composable
private fun TarjetaBeneficios() {
    val promocionables = productosPromocionables(Menu.todos())
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        shadowElevation = 3.dp
    ) {
        Column {
            FranjaTricolor()
            Column(Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(48.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.CardGiftcard, contentDescription = null, tint = PrimaryContainer)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Promociones de temporada", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f, fill = false))
                            Spacer(Modifier.width(6.dp))
                            Insignia("−$PORCENTAJE_DESCUENTO_PERIODO %", fondo = Secondary, colorTexto = Color.White)
                        }
                        Text(
                            "En todo tu pedido + descuentos extra en productos seleccionados",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
                Surface(shape = RoundedCornerShape(10.dp), color = MaterialTheme.colorScheme.surfaceContainerLow) {
                    Column(Modifier.fillMaxWidth().padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        promocionables.forEach { producto ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.Verified, contentDescription = null, tint = Secondary, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(producto.nombre, style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f))
                                Text(
                                    "−${producto.promocion()?.porcentaje} %",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Secondary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Etiqueta(texto: String, icono: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Row(modifier.padding(top = 14.dp, bottom = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icono, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(texto, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun coloresCampo() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
    errorContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
    unfocusedBorderColor = Color(0xFFDCD7D0),
    focusedBorderColor = PrimaryContainer
)

@Composable
private fun CampoFormulario(
    etiqueta: String,
    icono: ImageVector,
    colorIcono: Color,
    valor: String,
    onCambio: (String) -> Unit,
    ejemplo: String,
    error: String?,
    teclado: KeyboardType = KeyboardType.Text
) {
    Etiqueta(etiqueta, icono, colorIcono)
    CampoTexto(valor, onCambio, ejemplo, error, teclado)
}

@Composable
private fun CampoTexto(
    valor: String,
    onCambio: (String) -> Unit,
    ejemplo: String,
    error: String?,
    teclado: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        OutlinedTextField(
            value = valor,
            onValueChange = onCambio,
            placeholder = { Text(ejemplo, color = MaterialTheme.colorScheme.outline) },
            singleLine = true,
            isError = error != null,
            keyboardOptions = KeyboardOptions(keyboardType = teclado),
            shape = RoundedCornerShape(12.dp),
            colors = coloresCampo(),
            modifier = Modifier.fillMaxWidth()
        )
        MensajeError(error)
    }
}

@Composable
private fun CampoClave(valor: String, onCambio: (String) -> Unit, visible: Boolean, onAlternar: () -> Unit, error: String?) {
    Column {
        OutlinedTextField(
            value = valor,
            onValueChange = onCambio,
            placeholder = { Text("••••••••", color = MaterialTheme.colorScheme.outline) },
            singleLine = true,
            isError = error != null,
            visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = onAlternar) {
                    Icon(
                        if (visible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = if (visible) "Ocultar contraseña" else "Mostrar contraseña"
                    )
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = coloresCampo(),
            modifier = Modifier.fillMaxWidth()
        )
        MensajeError(error)
    }
}

/** Cuatro segmentos: largo mínimo, número, mayúscula y símbolo. */
@Composable
private fun BarraFortaleza(clave: String) {
    val puntaje: Int = listOf(
        clave.length >= 6,
        clave.any { it.isDigit() },
        clave.any { it.isUpperCase() },
        clave.any { !it.isLetterOrDigit() }
    ).count { it }
    val color: Color = when (puntaje) {
        0, 1 -> PrimaryContainer
        2, 3 -> Color(0xFFF4A261)
        else -> Albahaca
    }
    Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(4) { indice ->
            Box(
                Modifier
                    .weight(1f)
                    .height(5.dp)
                    .clip(CircleShape)
                    .background(if (indice < puntaje) color else MaterialTheme.colorScheme.surfaceContainerHigh)
            )
        }
    }
}

@Composable
private fun MensajeError(error: String?) {
    if (error != null) {
        Text(
            error,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
        )
    }
}

@Preview(showBackground = true, heightDp = 1400)
@Composable
private fun RegistroPreview() {
    PizzeriaAppTheme { RegistroScreen({}, { _, _, _, _ -> }) }
}
