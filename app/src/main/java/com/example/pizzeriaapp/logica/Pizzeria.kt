package com.example.pizzeriaapp.logica

import com.example.pizzeriaapp.modelo.Cliente
import com.example.pizzeriaapp.modelo.EstadoPedido
import com.example.pizzeriaapp.modelo.Pedido
import com.example.pizzeriaapp.modelo.Producto
import com.example.pizzeriaapp.modelo.Repartidor
import java.time.LocalDate

/**
 * Resultado de intentar registrar un pedido.
 * En la versión de consola cada caso se imprimía con println; en Android la pantalla
 * recibe este objeto y decide cómo mostrarlo (sealed class + when exhaustivo).
 */
sealed class ResultadoPedido {
    /** Pedido aceptado. [repartidor] es null si no había repartidores libres (queda "Listo"). */
    data class Aceptado(val pedido: Pedido, val repartidor: Repartidor?, val notificacion: String) : ResultadoPedido()

    /** Faltan ingredientes: el pedido no se acepta y se indican los pasos para reabastecer. */
    data class SinStock(val idPedido: Int, val faltantes: List<Faltante>, val pasos: List<String>) : ResultadoPedido()

    /** Se alcanzó el máximo de pedidos del día (arreglo de RegistroDiario lleno). */
    data object SinCupo : ResultadoPedido()

    /** Datos inválidos (por ejemplo, pedido sin productos). */
    data class Invalido(val mensaje: String) : ResultadoPedido()
}

/**
 * Reúne el estado del negocio que en la versión de consola vivía como variables locales de main():
 * catálogo, clientes, inventario, repartidores y registro diario.
 * No depende de Android, por eso se puede probar con tests unitarios (ver PizzeriaTest).
 */
class Pizzeria {
    val catalogo = RegistroSinDuplicados<Producto>()
    val clientes = RegistroSinDuplicados<Cliente>()
    val inventario: MutableMap<String, Int> = crearInventario()
    val repartidores: List<Repartidor> = crearRepartidores()
    val registro = RegistroDiario()

    init {
        Menu.todos().forEach { catalogo.registrar(it) }
    }

    /**
     * Registra un cliente evitando duplicados (mismo nombre y correo = mismo cliente).
     * Devuelve el cliente registrado y true si es nuevo, false si ya existía.
     */
    fun registrarCliente(nombre: String, telefono: String, correo: String, direccion: String): Pair<Cliente, Boolean> {
        val candidato = Cliente(
            nombre.trim(),
            formatearTelefono(telefono) ?: TELEFONO_POR_DEFECTO,
            normalizarCorreo(correo) ?: CORREO_POR_DEFECTO,
            direccion.trim()
        )
        val yaExistia: Boolean = candidato in clientes
        val cliente: Cliente = clientes.registrarOObtener(candidato)
        return Pair(cliente, !yaExistia)
    }

    /**
     * Versión Android de aceptarPedido() + despacharPedido() de la semana 3:
     * verifica cupo y stock, descuenta ingredientes, registra el pedido y lo asigna
     * al repartidor disponible más cercano.
     */
    fun confirmarPedido(
        cliente: Cliente,
        productos: List<Producto>,
        notas: String?,
        fecha: LocalDate = LocalDate.now()
    ): ResultadoPedido {
        if (!registro.hayCupo()) return ResultadoPedido.SinCupo

        // Si no hay productos, el constructor de Pedido lanza IllegalArgumentException.
        val pedido: Pedido = try {
            Pedido(registro.siguienteId(), cliente, productos, notas, fecha)
        } catch (e: IllegalArgumentException) {
            return ResultadoPedido.Invalido(e.message ?: "Pedido inválido.")
        }

        val faltantes: List<Faltante> = prepararLote(pedido.productos, inventario)
        return when {
            faltantes.isEmpty() -> {
                registro.registrar(pedido)
                despachar(pedido)
            }
            else -> ResultadoPedido.SinStock(pedido.id, faltantes, pasosReabastecimiento(faltantes))
        }
    }

    /** El pedido pasa a "Listo" y se asigna al repartidor disponible más cercano. */
    private fun despachar(pedido: Pedido): ResultadoPedido.Aceptado {
        pedido.cambiarEstado(EstadoPedido.LISTO)
        val repartidor: Repartidor = asignarRepartidorMasCercano(repartidores)
            ?: return ResultadoPedido.Aceptado(
                pedido, null, "No hay repartidores disponibles: el pedido queda listo esperando despacho."
            )
        repartidor.tomarPedido(pedido)
        return ResultadoPedido.Aceptado(pedido, repartidor, notificarCliente(pedido.cliente, repartidor, pedido))
    }

    /** let: la notificación solo se envía si el cliente tiene un correo válido (no nulo). */
    private fun notificarCliente(cliente: Cliente, repartidor: Repartidor, pedido: Pedido): String =
        cliente.correoParaNotificar()?.let {
            "Notificación enviada a $it: tu pedido #${pedido.id} va en camino con ${repartidor.nombre}."
        } ?: "${cliente.nombre} no tiene un correo válido registrado: no se envió notificación."

    /** Asigna a un repartidor libre un pedido que quedó "Listo" por falta de repartidores. */
    fun despacharPendiente(pedido: Pedido): Repartidor? {
        if (pedido.estado != EstadoPedido.LISTO) return null
        val repartidor: Repartidor = asignarRepartidorMasCercano(repartidores) ?: return null
        repartidor.tomarPedido(pedido)
        return repartidor
    }

    /** El repartidor entrega el pedido y vuelve a quedar disponible. */
    fun marcarEntregado(pedido: Pedido) {
        pedido.repartidor?.entregarPedido(pedido)
    }

    /** Ingresa lo que falta más el margen sugerido al proveedor. */
    fun reabastecerFaltantes(faltantes: List<Faltante>) {
        faltantes.forEach { reabastecer(inventario, it.ingrediente, it.faltan + MARGEN_REABASTECIMIENTO) }
    }
}
