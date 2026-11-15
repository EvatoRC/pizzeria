package com.example.pizzeriaapp.modelo

import com.example.pizzeriaapp.logica.CORREO_POR_DEFECTO
import java.util.Objects

/**
 * Clase base con los datos comunes de cualquier persona del sistema.
 * Es "open" porque en Kotlin las clases son finales por defecto y no se podrían heredar.
 */
open class Usuario(
    val nombre: String,
    val telefono: String   // dato de contacto común a clientes y repartidores
) {
    open fun rol(): String = "Usuario"

    open fun describir(): String = "[${rol()}] $nombre | Tel: $telefono"
}

/**
 * Cliente: agrega correo y dirección de entrega.
 *
 * Paso 5 - Igualdad: dos clientes son el mismo si coinciden nombre y correo (sin importar
 * mayúsculas ni espacios). El teléfono y la dirección pueden cambiar sin que sea otro cliente.
 * Paso 4 - Desestructuración: val (nombre, correo, telefono) = cliente
 */
class Cliente(
    nombre: String,
    telefono: String,
    val correo: String,
    val direccion: String
) : Usuario(nombre, telefono) {

    override fun rol(): String = "Cliente"

    override fun describir(): String = "${super.describir()} | Correo: $correo | Dirección: $direccion"

    /** Devuelve null si el correo es el predeterminado: a esa dirección no se puede notificar. */
    fun correoParaNotificar(): String? = if (correo == CORREO_POR_DEFECTO) null else correo

    // ---------- Desestructuración ----------
    operator fun component1(): String = nombre
    operator fun component2(): String = correo
    operator fun component3(): String = telefono

    // ---------- Igualdad ----------
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Cliente) return false
        return nombre.trim().equals(other.nombre.trim(), ignoreCase = true) &&
            correo.trim().equals(other.correo.trim(), ignoreCase = true)
    }

    override fun hashCode(): Int = Objects.hash(nombre.trim().lowercase(), correo.trim().lowercase())

    override fun toString(): String = "$nombre <$correo>"
}

/** Repartidor: agrega la distancia a la pizzería y su disponibilidad. */
class Repartidor(
    nombre: String,
    telefono: String,
    val distanciaKm: Double
) : Usuario(nombre, telefono) {

    // Encapsulación: se lee desde fuera, pero solo cambia mediante tomarPedido() / entregarPedido().
    var disponible: Boolean = true
        private set

    override fun rol(): String = "Repartidor"

    override fun describir(): String {
        val estado: String = if (disponible) "Disponible" else "Ocupado"
        return "${super.describir()} | A $distanciaKm km | $estado"
    }

    /** Toma un pedido listo: queda asignado, el pedido pasa a "En camino" y el repartidor queda ocupado. */
    fun tomarPedido(pedido: Pedido) {
        check(disponible) { "$nombre no está disponible para tomar el pedido #${pedido.id}." }
        pedido.asignarRepartidor(this)
        pedido.cambiarEstado(EstadoPedido.EN_CAMINO)
        disponible = false
    }

    /** Entrega el pedido: pasa a "Entregado" y el repartidor vuelve a estar disponible. */
    fun entregarPedido(pedido: Pedido) {
        check(pedido.repartidor == this) { "El pedido #${pedido.id} no está asignado a $nombre." }
        pedido.cambiarEstado(EstadoPedido.ENTREGADO)
        disponible = true
    }
}
