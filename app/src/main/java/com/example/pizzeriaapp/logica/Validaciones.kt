package com.example.pizzeriaapp.logica

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.ResolverStyle

const val TELEFONO_POR_DEFECTO: String = "+00 (000) 000-0000"
const val CORREO_POR_DEFECTO: String = "correo@invalido.com"
const val TEXTO_POR_DEFECTO: String = "No informado"
const val CODIGO_PAIS_POR_DEFECTO: String = "56"   // se usa si el número viene sin código de país

/** Range: cantidad permitida por producto (pizzas, bebidas o ingredientes adicionales). */
val RANGO_CANTIDAD_PRODUCTOS: IntRange = 1..100

val FORMATO_FECHA: DateTimeFormatter =
    DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT)

// ---------- Expresiones regulares ----------

/** nombre@dominio.com: usuario, @, dominio (con subdominios opcionales) y extensión de 2+ letras. */
private val REGEX_CORREO = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$")

/** Caracteres que se ignoran al leer un teléfono: espacios, paréntesis, puntos y guiones. */
private val REGEX_SEPARADORES_TELEFONO = Regex("[\\s().-]")

/**
 * Teléfono ya limpio: "+" opcional, código de país de 2 dígitos opcional y 10 dígitos.
 * Grupos: (país)(XXX)(XXX)(XXXX) -> se usan para armar el formato +XX (XXX) XXX-XXXX.
 */
private val REGEX_TELEFONO = Regex("^\\+?(\\d{2})?(\\d{3})(\\d{3})(\\d{4})$")

private val REGEX_FECHA = Regex("^\\d{2}-\\d{2}-\\d{4}$")

// ---------- Validación y formato ----------

fun esCorreoValido(correo: String?): Boolean = correo != null && REGEX_CORREO.matches(correo.trim())

/** Correo válido -> en minúsculas y sin espacios. Inválido o nulo -> null. */
fun normalizarCorreo(entrada: String?): String? =
    entrada?.trim()?.lowercase()?.takeIf { esCorreoValido(it) }

/**
 * Procesa un teléfono escrito de cualquier forma y lo deja como +XX (XXX) XXX-XXXX.
 * "+56 912 345 6789", "56-912-345-6789" o "(912) 345.6789" -> "+56 (912) 345-6789".
 * Si no tiene 10 dígitos (o 12 con código de país), devuelve null.
 */
fun formatearTelefono(entrada: String?): String? {
    val limpio: String = entrada?.replace(REGEX_SEPARADORES_TELEFONO, "") ?: return null
    val coincidencia: MatchResult = REGEX_TELEFONO.matchEntire(limpio) ?: return null
    val (pais, area, bloque1, bloque2) = coincidencia.destructured   // desestructuración de los grupos
    val codigoPais: String = pais.ifEmpty { CODIGO_PAIS_POR_DEFECTO }
    return "+$codigoPais ($area) $bloque1-$bloque2"
}

/** Range: true si la cantidad está entre 1 y 100. */
fun cantidadValida(cantidad: Int): Boolean = cantidad in RANGO_CANTIDAD_PRODUCTOS

/** "dd-MM-aaaa" -> LocalDate. Formato incorrecto o fecha inexistente (31-02-2026) -> null. */
fun convertirFecha(texto: String?): LocalDate? {
    val limpio: String = texto?.trim() ?: return null
    if (!REGEX_FECHA.matches(limpio)) return null
    return try {
        LocalDate.parse(limpio, FORMATO_FECHA)
    } catch (e: DateTimeParseException) {
        null
    }
}

fun formatearFecha(fecha: LocalDate): String = fecha.format(FORMATO_FECHA)

// ---------- Validaciones para los formularios de Android ----------
// En la versión de consola se usaban readln() y se volvía a preguntar hasta que el dato fuera válido.
// En Android no hay consola: cada campo se valida mientras el usuario escribe y la función devuelve
// el mensaje de error (o null si el dato es correcto) para mostrarlo bajo el campo.

const val LARGO_MINIMO_CLAVE: Int = 6

fun errorNombre(nombre: String): String? =
    if (nombre.isBlank()) "Ingresa tu nombre y apellido." else null

fun errorCorreo(correo: String): String? = when {
    correo.isBlank() -> "Ingresa tu correo electrónico."
    !esCorreoValido(correo) -> "Formato incorrecto. Usa nombre@dominio.com"
    else -> null
}

fun errorTelefono(telefono: String): String? = when {
    telefono.isBlank() -> "Ingresa tu teléfono."
    formatearTelefono(telefono) == null -> "Debe tener 10 dígitos (formato +XX (XXX) XXX-XXXX)."
    else -> null
}

fun errorDireccion(direccion: String): String? =
    if (direccion.isBlank()) "Selecciona una sede o dirección de entrega." else null

fun errorClave(clave: String): String? =
    if (clave.length < LARGO_MINIMO_CLAVE) "Mínimo $LARGO_MINIMO_CLAVE caracteres." else null

fun errorConfirmacion(clave: String, confirmacion: String): String? =
    if (confirmacion != clave) "Las contraseñas no coinciden." else null
