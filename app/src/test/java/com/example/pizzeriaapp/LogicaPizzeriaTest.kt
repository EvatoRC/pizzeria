package com.example.pizzeriaapp

import com.example.pizzeriaapp.logica.Menu
import com.example.pizzeriaapp.logica.Pizzeria
import com.example.pizzeriaapp.logica.ResultadoPedido
import com.example.pizzeriaapp.logica.calcularTotalPedido
import com.example.pizzeriaapp.logica.formatearPesos
import com.example.pizzeriaapp.logica.formatearTelefono
import com.example.pizzeriaapp.logica.normalizarCorreo
import com.example.pizzeriaapp.modelo.Cliente
import com.example.pizzeriaapp.modelo.EstadoPedido
import com.example.pizzeriaapp.modelo.Pedido
import com.example.pizzeriaapp.modelo.Pizza
import com.example.pizzeriaapp.modelo.promocion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

/**
 * Pruebas de la lógica de las semanas 1-3 ya integrada en el proyecto Android.
 * Corren en el computador (src/test), sin emulador: clic derecho > Run 'LogicaPizzeriaTest'.
 */
class LogicaPizzeriaTest {

    private val dentroDelPeriodo: LocalDate = LocalDate.of(2026, 11, 3)
    private val fueraDelPeriodo: LocalDate = LocalDate.of(2026, 10, 28)

    @Test
    fun totalConDescuentoDelPeriodo() {
        val detalle = calcularTotalPedido(List(2) { Menu.pepperoni }, dentroDelPeriodo)
        assertEquals(16780, detalle.subtotal)
        assertEquals(0, detalle.descuentoVolumen)
        assertEquals(839, detalle.descuentoPeriodo)
        assertEquals(3029, detalle.impuesto)
        assertEquals(18970, detalle.total)
    }

    @Test
    fun descuentoPorVolumenConMasDeTresPizzas() {
        val detalle = calcularTotalPedido(List(4) { Menu.napolitana }, fueraDelPeriodo)
        assertEquals(3524, detalle.descuentoVolumen)
        assertEquals(0, detalle.descuentoPeriodo)
        assertEquals(37742, detalle.total)
    }

    @Test
    fun promocionableSoloDentroDelPeriodo() {
        assertEquals(15, Menu.pepperoniFamiliar.promocion()?.porcentaje)
        assertNull(Menu.pepperoni.promocion())
        assertEquals(2098, calcularTotalPedido(listOf(Menu.pepperoniFamiliar), dentroDelPeriodo).descuentoPromocionables)
        assertEquals(0, calcularTotalPedido(listOf(Menu.pepperoniFamiliar), fueraDelPeriodo).descuentoPromocionables)
    }

    @Test
    fun validacionesConRegex() {
        assertEquals("+56 (912) 345-6789", formatearTelefono("912 345 6789"))
        assertNull(formatearTelefono("987654"))
        assertEquals("ana.fuentes@gmail.com", normalizarCorreo(" ANA.FUENTES@GMAIL.COM "))
        assertNull(normalizarCorreo("pedro@dominio"))
        assertEquals("$29.970", formatearPesos(29970))
    }

    @Test
    fun igualdadDeProductosYClientes() {
        assertEquals(Menu.pepperoni, Pizza("  pepperoni ", 9990, mapOf("masa" to 1)))
        val pizzeria = Pizzeria()
        val (_, primeraVez) = pizzeria.registrarCliente("Ana Fuentes", "9123456789", "ana.fuentes@gmail.com", "Sede Alameda")
        val (_, segundaVez) = pizzeria.registrarCliente("ANA FUENTES", "9000000000", "ANA.FUENTES@GMAIL.COM", "Sede Alameda")
        assertTrue(primeraVez)
        assertFalse(segundaVez)
        assertEquals(1, pizzeria.clientes.cantidad)
    }

    @Test
    fun pedidoSinStockSeReabasteceYSeDespachaAlMasCercano() {
        val pizzeria = Pizzeria()
        val (cliente, _) = pizzeria.registrarCliente("Jorge Díaz", "9777788888", "jorge.diaz@gmail.com", "Sede San Bernardo")

        // Hay 3 de queso azul y 4 Cuatro Quesos necesitan 4.
        val rechazado = pizzeria.confirmarPedido(cliente, List(4) { Menu.cuatroQuesos }, null, dentroDelPeriodo)
        assertTrue(rechazado is ResultadoPedido.SinStock)
        val faltantes = (rechazado as ResultadoPedido.SinStock).faltantes
        assertEquals("queso azul", faltantes.single().ingrediente)

        pizzeria.reabastecerFaltantes(faltantes)
        val aceptado = pizzeria.confirmarPedido(cliente, List(4) { Menu.cuatroQuesos }, null, dentroDelPeriodo)
        assertTrue(aceptado is ResultadoPedido.Aceptado)
        aceptado as ResultadoPedido.Aceptado
        assertEquals("Joaquín Gómez", aceptado.repartidor?.nombre)
        assertEquals(EstadoPedido.EN_CAMINO, aceptado.pedido.estado)

        pizzeria.marcarEntregado(aceptado.pedido)
        assertEquals(EstadoPedido.ENTREGADO, aceptado.pedido.estado)
        assertTrue(aceptado.repartidor!!.disponible)
    }

    @Test
    fun sumaDePedidosRecalculaElTotal() {
        val ana = Cliente("Ana Fuentes", "+56 (912) 345-6789", "ana.fuentes@gmail.com", "Sede Alameda")
        val pedidoA = Pedido(1, ana, List(3) { Menu.napolitana }, fecha = fueraDelPeriodo)
        val pedidoB = Pedido(2, ana, listOf(Menu.pepperoni), fecha = fueraDelPeriodo)
        val combinado = pedidoA + pedidoB
        assertEquals(0, pedidoA.calcularTotal().descuentoVolumen)
        assertTrue(combinado.calcularTotal().descuentoVolumen > 0)
        val (cliente, productos, _) = combinado
        assertEquals(ana, cliente)
        assertEquals(4, productos.size)
    }
}
