package com.example.pizzeriaapp.logica

import com.example.pizzeriaapp.modelo.ESTADOS_COMPLETADOS
import com.example.pizzeriaapp.modelo.Pedido

const val MAX_PEDIDOS_DIA: Int = 50

/**
 * Pedidos del día guardados en un arreglo de tamaño fijo.
 * Las posiciones sin usar quedan en null; por eso el arreglo es Array<Pedido?>.
 */
class RegistroDiario {
    private val pedidosDelDia: Array<Pedido?> = arrayOfNulls(MAX_PEDIDOS_DIA)
    private var cantidad: Int = 0
    private var ultimoId: Int = 0

    /** Cada pedido recibe un número nuevo, aunque luego sea rechazado (como en un sistema real). */
    fun siguienteId(): Int {
        ultimoId++
        return ultimoId
    }

    fun hayCupo(): Boolean = cantidad < pedidosDelDia.size

    /** Lanza IllegalStateException si el arreglo ya está lleno. */
    fun registrar(pedido: Pedido) {
        check(hayCupo()) { "Se alcanzó el máximo de $MAX_PEDIDOS_DIA pedidos del día." }
        pedidosDelDia[cantidad] = pedido
        cantidad++
    }

    fun pedidos(): List<Pedido> = pedidosDelDia.filterNotNull()

    fun completados(): List<Pedido> = pedidos().filter { it.estado in ESTADOS_COMPLETADOS }

    fun pendientes(): List<Pedido> = pedidos().filter { it.estado !in ESTADOS_COMPLETADOS }
}
