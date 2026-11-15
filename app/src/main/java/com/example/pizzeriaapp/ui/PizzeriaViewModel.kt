package com.example.pizzeriaapp.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.pizzeriaapp.logica.Faltante
import com.example.pizzeriaapp.logica.Pizzeria
import com.example.pizzeriaapp.logica.RANGO_CANTIDAD_PRODUCTOS
import com.example.pizzeriaapp.logica.ResultadoPedido
import com.example.pizzeriaapp.logica.calcularTotalPedido
import com.example.pizzeriaapp.modelo.Categoria
import com.example.pizzeriaapp.modelo.Cliente
import com.example.pizzeriaapp.modelo.DetalleTotal
import com.example.pizzeriaapp.modelo.Pedido
import com.example.pizzeriaapp.modelo.Producto
import java.time.LocalDate

/** Pantallas de la app. Navegación simple por estado (sin librería de navegación). */
enum class Pantalla { INICIO, REGISTRO, HOME, CARRITO, PEDIDOS }

/**
 * Conecta la lógica de las semanas 1-3 (paquetes modelo y logica) con la interfaz Compose.
 * Al ser un ViewModel, el carrito y el cliente sobreviven a la rotación de pantalla.
 */
class PizzeriaViewModel : ViewModel() {

    val pizzeria = Pizzeria()

    var pantalla by mutableStateOf(Pantalla.INICIO)
        private set

    var cliente by mutableStateOf<Cliente?>(null)
        private set

    /** Cada elemento es UNA unidad, igual que en Pedido (3 Pepperoni = 3 veces el producto). */
    val carrito = mutableStateListOf<Producto>()

    var busqueda by mutableStateOf("")
    var categoria by mutableStateOf<Categoria?>(null)
    var notas by mutableStateOf("")

    var ultimoResultado by mutableStateOf<ResultadoPedido?>(null)
        private set

    /** Mensaje corto para mostrar en un Snackbar; la pantalla lo limpia al mostrarlo. */
    var mensaje by mutableStateOf<String?>(null)

    /** Pedido y Repartidor no son observables por Compose: este contador fuerza el redibujado. */
    var versionPedidos by mutableIntStateOf(0)
        private set

    /** Si un invitado intenta pagar, después de registrarse vuelve al carrito. */
    private var volverAlCarrito = false

    val fechaPedido: LocalDate
        get() = LocalDate.now()

    fun avisar(texto: String) {
        mensaje = texto
    }

    // ---------- Navegación ----------

    fun irA(destino: Pantalla) {
        pantalla = destino
    }

    /** Botón atrás del sistema. Devuelve false si la app debe cerrarse (estamos en Inicio). */
    fun volver(): Boolean {
        pantalla = when (pantalla) {
            Pantalla.INICIO -> return false
            Pantalla.REGISTRO -> if (volverAlCarrito) Pantalla.CARRITO else Pantalla.INICIO
            Pantalla.HOME -> Pantalla.INICIO
            Pantalla.CARRITO, Pantalla.PEDIDOS -> Pantalla.HOME
        }
        volverAlCarrito = false
        return true
    }

    // ---------- Catálogo ----------

    /** Filtra el catálogo por categoría y por texto (nombre o ingredientes). */
    fun productosFiltrados(): List<Producto> {
        val texto: String = busqueda.trim().lowercase()
        return pizzeria.catalogo.todos().filter { producto ->
            (categoria == null || producto.categoria == categoria) &&
                (texto.isEmpty() || producto.nombre.lowercase().contains(texto) ||
                    producto.ingredientes.keys.any { it.lowercase().contains(texto) })
        }
    }

    // ---------- Registro ----------

    fun registrar(nombre: String, telefono: String, correo: String, direccion: String) {
        val (registrado, esNuevo) = pizzeria.registrarCliente(nombre, telefono, correo, direccion)
        cliente = registrado
        mensaje = if (esNuevo) "¡Cuenta creada! Bienvenido/a, ${registrado.nombre}."
        else "Ya estabas registrado/a, ${registrado.nombre}: usamos tu cuenta existente."
        pantalla = if (volverAlCarrito) Pantalla.CARRITO else Pantalla.HOME
        volverAlCarrito = false
    }

    // ---------- Carrito ----------

    fun cantidadEnCarrito(producto: Producto): Int = carrito.count { it == producto }

    /** Range 1-100: no se permiten más de RANGO_CANTIDAD_PRODUCTOS.last unidades por producto. */
    fun agregar(producto: Producto) {
        if (cantidadEnCarrito(producto) + 1 !in RANGO_CANTIDAD_PRODUCTOS) {
            mensaje = "Máximo ${RANGO_CANTIDAD_PRODUCTOS.last} unidades de ${producto.nombre}."
            return
        }
        carrito.add(producto)
        ultimoResultado = null
    }

    fun quitar(producto: Producto) {
        carrito.remove(producto)
        ultimoResultado = null
    }

    fun eliminarTodos(producto: Producto) {
        carrito.removeAll { it == producto }
        ultimoResultado = null
    }

    /** Agrupa el carrito: Producto -> cantidad (usa equals/hashCode de Producto). */
    fun cantidadesCarrito(): Map<Producto, Int> = carrito.groupingBy { it }.eachCount()

    fun totalCarrito(): DetalleTotal = calcularTotalPedido(carrito.toList(), fechaPedido)

    fun pizzasEnCarrito(): Int = carrito.sumOf { it.cantidadPizzas() }

    // ---------- Pedido ----------

    fun confirmarPedido() {
        val actual: Cliente = cliente ?: run {
            volverAlCarrito = true
            mensaje = "Regístrate para confirmar tu pedido."
            pantalla = Pantalla.REGISTRO
            return
        }
        val resultado: ResultadoPedido =
            pizzeria.confirmarPedido(actual, carrito.toList(), notas.ifBlank { null }, fechaPedido)
        ultimoResultado = resultado
        if (resultado is ResultadoPedido.Aceptado) {
            carrito.clear()
            notas = ""
        }
        versionPedidos++
    }

    fun reabastecerYReintentar(faltantes: List<Faltante>) {
        pizzeria.reabastecerFaltantes(faltantes)
        mensaje = "Reabastecimiento registrado: ${faltantes.joinToString { it.ingrediente }}."
        confirmarPedido()
    }

    fun cerrarResultado() {
        ultimoResultado = null
    }

    fun pedidos(): List<Pedido> = pizzeria.registro.pedidos().reversed()

    fun marcarEntregado(pedido: Pedido) {
        pizzeria.marcarEntregado(pedido)
        // Al liberarse un repartidor, se despacha el pedido más antiguo que esperaba.
        pizzeria.registro.pedidos().firstOrNull { pizzeria.despacharPendiente(it) != null }
        versionPedidos++
    }
}
