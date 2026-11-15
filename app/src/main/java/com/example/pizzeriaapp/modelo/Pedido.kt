package com.example.pizzeriaapp.modelo

import com.example.pizzeriaapp.logica.PERIODO_PROMOCION
import com.example.pizzeriaapp.logica.calcularTotalPedido
import java.time.LocalDate

/** Estados posibles de un pedido, en el orden en que avanza. */
enum class EstadoPedido(val etiqueta: String) {
    EN_PREPARACION("En preparación"),
    LISTO("Listo"),
    EN_CAMINO("En camino"),
    ENTREGADO("Entregado")
}

/** Estados que cuentan como pedido completado; el resto se considera pendiente. */
val ESTADOS_COMPLETADOS: Set<EstadoPedido> = setOf(EstadoPedido.ENTREGADO)

/** Desglose del total de un pedido (montos en pesos). */
data class DetalleTotal(
    val subtotal: Int,
    val descuentoVolumen: Int,          // más de 3 pizzas
    val descuentoPromocionables: Int,   // productos @Promocionable, solo dentro del período
    val descuentoPeriodo: Int,          // pedido dentro del rango de fechas de promoción
    val impuesto: Int,
    val total: Int
) {
    val descuentoTotal: Int
        get() = descuentoVolumen + descuentoPromocionables + descuentoPeriodo
}

/**
 * Pedido de un cliente.
 * Cada elemento de [productos] es UNA unidad: 3 Pepperoni = 3 veces el mismo Producto en la lista.
 * @property notas comentario opcional del cliente (puede ser null).
 * @property fecha fecha del pedido; define si está dentro del período de promociones.
 */
class Pedido(
    val id: Int,
    val cliente: Cliente,
    val productos: List<Producto>,
    val notas: String? = null,
    val fecha: LocalDate = LocalDate.now()
) {
    var estado: EstadoPedido = EstadoPedido.EN_PREPARACION
        private set

    var repartidor: Repartidor? = null   // null hasta que se asigna
        private set

    init {
        require(productos.isNotEmpty()) { "El pedido #$id no tiene productos: no se puede registrar." }
    }

    fun cantidadPizzas(): Int = productos.sumOf { it.cantidadPizzas() }

    /** Agrupa la lista: Producto -> cantidad pedida (usa equals/hashCode de Producto). */
    fun cantidadesPorProducto(): Map<Producto, Int> = productos.groupingBy { it }.eachCount()

    /** Range: el pedido está en promoción si su fecha cae dentro de PERIODO_PROMOCION. */
    fun enPeriodoPromocion(): Boolean = fecha in PERIODO_PROMOCION

    /** Anotaciones: productos del pedido marcados con @Promocionable, detectados con reflection. */
    fun promocionablesDelPedido(): List<Producto> = productosPromocionables(productos)

    fun calcularTotal(): DetalleTotal = calcularTotalPedido(productos, fecha)

    // ---------- Paso 3: sobrecarga del operador + ----------

    /**
     * pedidoA + pedidoB -> un pedido nuevo con los productos de ambos.
     * El total NO se suma: se recalcula con calcularTotal(), así que los descuentos se vuelven a
     * evaluar sobre el pedido combinado (por ejemplo, 3 + 1 pizzas = 4 activa el descuento por volumen).
     * Solo se combinan pedidos del mismo cliente (comparados con el equals() de Cliente).
     * La fecha del combinado es la más reciente de las dos.
     */
    operator fun plus(otro: Pedido): Pedido {
        require(cliente == otro.cliente) {
            "Solo se combinan pedidos del mismo cliente: #$id es de ${cliente.nombre} y #${otro.id} de ${otro.cliente.nombre}."
        }
        return Pedido(
            id = id,
            cliente = cliente,
            productos = productos + otro.productos,
            notas = "Pedido combinado: #$id + #${otro.id}",
            fecha = maxOf(fecha, otro.fecha)   // LocalDate es Comparable
        )
    }

    // ---------- Paso 4: desestructuración ----------
    // val (cliente, productos, total) = pedido
    operator fun component1(): Cliente = cliente
    operator fun component2(): List<Producto> = productos
    operator fun component3(): Int = calcularTotal().total

    // ---------- Estado y repartidor ----------

    /** Solo permite avanzar al estado siguiente; cualquier otro cambio lanza IllegalStateException. */
    fun cambiarEstado(nuevoEstado: EstadoPedido) {
        val permitido: Boolean = when (estado) {
            EstadoPedido.EN_PREPARACION -> nuevoEstado == EstadoPedido.LISTO
            EstadoPedido.LISTO -> nuevoEstado == EstadoPedido.EN_CAMINO
            EstadoPedido.EN_CAMINO -> nuevoEstado == EstadoPedido.ENTREGADO
            EstadoPedido.ENTREGADO -> false
        }
        check(permitido) { "Pedido #$id: no se puede pasar de '${estado.etiqueta}' a '${nuevoEstado.etiqueta}'." }
        estado = nuevoEstado
    }

    fun asignarRepartidor(nuevoRepartidor: Repartidor) {
        repartidor = nuevoRepartidor
    }

    /** Notas del cliente o "Sin notas" si no escribió nada (operadores ?. y ?:). */
    fun notasParaMostrar(): String = notas?.trim()?.takeIf { it.isNotEmpty() } ?: "Sin notas"

    fun nombreRepartidor(): String = repartidor?.nombre ?: "Sin asignar"
}
