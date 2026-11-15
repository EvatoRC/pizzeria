package com.example.pizzeriaapp.logica

import com.example.pizzeriaapp.modelo.DetalleTotal
import com.example.pizzeriaapp.modelo.Producto
import com.example.pizzeriaapp.modelo.Repartidor
import com.example.pizzeriaapp.modelo.promocion
import java.time.LocalDate
import kotlin.math.roundToInt

const val PORCENTAJE_IVA: Int = 19
const val PORCENTAJE_DESCUENTO: Int = 10
const val MINIMO_PIZZAS_PARA_DESCUENTO: Int = 3   // el descuento aplica con MÁS de 3 pizzas
const val PORCENTAJE_DESCUENTO_PERIODO: Int = 5   // descuento al total dentro del período de promociones
const val MARGEN_REABASTECIMIENTO: Int = 5         // unidades extra sugeridas al pedir al proveedor

/**
 * Range de fechas: período de promociones (ambos extremos incluidos).
 * LocalDate es Comparable, por eso se puede crear el rango con .. y consultar con "in".
 */
val PERIODO_PROMOCION: ClosedRange<LocalDate> = LocalDate.of(2026, 11, 1)..LocalDate.of(2026, 11, 30)

fun describirPeriodoPromocion(): String =
    "${formatearFecha(PERIODO_PROMOCION.start)} al ${formatearFecha(PERIODO_PROMOCION.endInclusive)}"

/** Ingrediente que no alcanza para un pedido. */
data class Faltante(val ingrediente: String, val requerido: Int, val disponible: Int) {
    val faltan: Int
        get() = requerido - disponible
}

// ---------- Cálculo del total ----------

/**
 * Calcula el total de una lista de productos en una fecha.
 * Descuentos (todos sobre precios netos):
 *  1. Volumen: 10 % del subtotal si hay más de 3 pizzas (los combos cuentan su pizza).
 *  2. Promocionables: solo si la fecha está en PERIODO_PROMOCION; cada producto con @Promocionable
 *     recibe el porcentaje indicado en su anotación (se lee con reflection).
 *  3. Período: si la fecha está en PERIODO_PROMOCION, 5 % del subtotal.
 * Luego se calcula el IVA sobre el monto con descuentos.
 */
fun calcularTotalPedido(productos: List<Producto>, fecha: LocalDate): DetalleTotal {
    val subtotal: Int = productos.sumOf { it.precio }
    val pizzas: Int = productos.sumOf { it.cantidadPizzas() }
    val enPeriodo: Boolean = fecha in PERIODO_PROMOCION

    val descuentoVolumen: Int =
        if (pizzas > MINIMO_PIZZAS_PARA_DESCUENTO) subtotal * PORCENTAJE_DESCUENTO / 100 else 0
    val descuentoPromocionables: Int = if (enPeriodo) {
        productos.sumOf { producto -> producto.promocion()?.let { producto.precio * it.porcentaje / 100 } ?: 0 }
    } else 0
    val descuentoPeriodo: Int = if (enPeriodo) subtotal * PORCENTAJE_DESCUENTO_PERIODO / 100 else 0

    val baseImponible: Int = subtotal - descuentoVolumen - descuentoPromocionables - descuentoPeriodo
    val impuesto: Int = (baseImponible * PORCENTAJE_IVA / 100.0).roundToInt()
    return DetalleTotal(
        subtotal, descuentoVolumen, descuentoPromocionables, descuentoPeriodo,
        impuesto, baseImponible + impuesto
    )
}

// ---------- Inventario (Map: ingrediente -> cantidad disponible) ----------

/** Suma las recetas de todos los productos: insumo -> unidades necesarias para el lote. */
fun calcularIngredientesNecesarios(productos: List<Producto>): Map<String, Int> {
    val necesarios: MutableMap<String, Int> = mutableMapOf()
    for (producto in productos) {
        for ((ingrediente, cantidad) in producto.ingredientes) {
            necesarios[ingrediente] = (necesarios[ingrediente] ?: 0) + cantidad
        }
    }
    return necesarios
}

/** Compara lo necesario contra el inventario y devuelve solo los ingredientes que no alcanzan. */
fun verificarDisponibilidad(necesarios: Map<String, Int>, inventario: Map<String, Int>): List<Faltante> =
    necesarios.mapNotNull { (ingrediente, requerido) ->
        val disponible: Int = inventario[ingrediente] ?: 0   // si no existe en inventario, hay 0
        when {
            disponible >= requerido -> null
            else -> Faltante(ingrediente, requerido, disponible)
        }
    }

/**
 * Prepara un lote de pizzas: calcula los ingredientes, verifica stock y, si alcanza,
 * los descuenta del inventario. Devuelve los faltantes (lista vacía = lote preparado).
 */
fun prepararLote(productos: List<Producto>, inventario: MutableMap<String, Int>): List<Faltante> {
    val necesarios: Map<String, Int> = calcularIngredientesNecesarios(productos)
    val faltantes: List<Faltante> = verificarDisponibilidad(necesarios, inventario)
    if (faltantes.isEmpty()) {
        for ((ingrediente, cantidad) in necesarios) {
            inventario[ingrediente] = inventario.getValue(ingrediente) - cantidad
        }
    }
    return faltantes
}

/** Agrega stock. Una cantidad cero o negativa lanza IllegalArgumentException. */
fun reabastecer(inventario: MutableMap<String, Int>, ingrediente: String, cantidad: Int) {
    require(cantidad > 0) { "La cantidad a ingresar debe ser mayor a 0 (ingresaste $cantidad)." }
    inventario[ingrediente] = (inventario[ingrediente] ?: 0) + cantidad
}

fun describirFaltante(faltante: Faltante): String = when (faltante.disponible) {
    0 -> "${faltante.ingrediente}: AGOTADO (se necesitan ${faltante.requerido})"
    else -> "${faltante.ingrediente}: insuficiente (hay ${faltante.disponible}, " +
        "se necesitan ${faltante.requerido}, faltan ${faltante.faltan})"
}

fun pasosReabastecimiento(faltantes: List<Faltante>): List<String> {
    val ingredientes: String = faltantes.joinToString(", ") { it.ingrediente }
    val pedidoProveedor: String = faltantes.joinToString(", ") {
        "${it.faltan + MARGEN_REABASTECIMIENTO} de ${it.ingrediente}"
    }
    return listOf(
        "1. Revisar bodega por stock no registrado de: $ingredientes.",
        "2. Solicitar al proveedor: $pedidoProveedor (incluye margen de $MARGEN_REABASTECIMIENTO unidades).",
        "3. Registrar el ingreso en el inventario del sistema.",
        "4. Volver a verificar el pedido antes de aceptarlo."
    )
}

// ---------- Repartidores ----------

/** Ordena por distancia y con find toma el primero disponible, es decir, el más cercano libre. */
fun asignarRepartidorMasCercano(repartidores: List<Repartidor>): Repartidor? =
    repartidores.sortedBy { it.distanciaKm }.find { it.disponible }

// ---------- Formato ----------

/** 29970 -> "$29.970" */
fun formatearPesos(monto: Int): String =
    "$" + String.format(java.util.Locale.US, "%,d", monto).replace(",", ".")
