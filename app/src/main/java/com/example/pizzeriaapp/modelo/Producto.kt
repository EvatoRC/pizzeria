package com.example.pizzeriaapp.modelo

import com.example.pizzeriaapp.logica.calcularIngredientesNecesarios
import com.example.pizzeriaapp.logica.formatearPesos
import java.util.Objects

/** Categoría del producto. Junto con el nombre, define si dos productos son "el mismo". */
enum class Categoria(val etiqueta: String) {
    PIZZA("Pizza"),
    BEBIDA("Bebida"),
    INGREDIENTE("Ingrediente"),
    COMBO("Combo")
}

/**
 * Producto del menú (clase base de Pizza, Bebida, IngredienteExtra y Combo).
 * @property precio       precio NETO en pesos chilenos (el IVA se agrega al calcular el total).
 * @property ingredientes insumos del inventario que consume UNA unidad: insumo -> unidades.
 *
 * Pasos 3 y 5 - Igualdad: en Kotlin el operador == no se declara como "operator fun"; se traduce
 * a una llamada a equals(). Por eso, sobrescribir equals() ES la sobrecarga de == para Producto.
 * Dos productos son iguales si coinciden su nombre (sin importar mayúsculas ni espacios) y su
 * categoría. El precio no cuenta: "Pepperoni" a $8.390 y "pepperoni" a $9.990 son el mismo producto.
 */
open class Producto(
    val nombre: String,
    val precio: Int,
    val categoria: Categoria,
    val ingredientes: Map<String, Int>
) {
    init {
        require(nombre.isNotBlank()) { "El producto debe tener nombre." }
        require(precio > 0) { "Costo no válido para '$nombre': $precio. Debe ser mayor a 0." }
        require(ingredientes.values.all { it > 0 }) { "La receta de '$nombre' tiene cantidades no válidas." }
    }

    /** Clave usada por equals() y hashCode(). */
    private val nombreNormalizado: String
        get() = nombre.trim().lowercase()

    /** Pizzas que aporta este producto al pedido (para el descuento por volumen). */
    open fun cantidadPizzas(): Int = 0

    /** Descripción completa: categoría, nombre, precio, insumos y promoción (si tiene). */
    fun detallarProducto(): String {
        val receta: String = ingredientes.entries.joinToString(", ") { (insumo, cantidad) -> "$insumo x$cantidad" }
        val etiquetaPromo: String = promocion()?.let { "  ★ @Promocionable ${it.porcentaje}%" } ?: ""
        return "[${categoria.etiqueta}] $nombre - ${formatearPesos(precio)} neto | Usa: $receta$etiquetaPromo"
    }

    // final: las subclases no pueden cambiar el criterio de igualdad.
    final override fun equals(other: Any?): Boolean {
        if (this === other) return true          // misma instancia
        if (other !is Producto) return false     // null u otro tipo
        return nombreNormalizado == other.nombreNormalizado && categoria == other.categoria
    }

    // Debe usar los MISMOS atributos que equals(); si no, HashSet/HashMap no detectan duplicados.
    final override fun hashCode(): Int = Objects.hash(nombreNormalizado, categoria)

    override fun toString(): String = "${categoria.etiqueta} \"$nombre\""
}

open class Pizza(nombre: String, precio: Int, ingredientes: Map<String, Int>) :
    Producto(nombre, precio, Categoria.PIZZA, ingredientes) {
    override fun cantidadPizzas(): Int = 1
}

/** Pizza familiar: califica para promociones especiales. */
@Promocionable(porcentaje = 15, motivo = "Pizza familiar de temporada")
class PizzaFamiliar(nombre: String, precio: Int, ingredientes: Map<String, Int>) :
    Pizza(nombre, precio, ingredientes)

/** La bebida se descuenta del inventario con su propio nombre. */
class Bebida(nombre: String, precio: Int) :
    Producto(nombre, precio, Categoria.BEBIDA, mapOf(nombre to 1))

/** Ingrediente adicional que se agrega a una pizza (consume 1 unidad del insumo indicado). */
class IngredienteExtra(nombre: String, precio: Int, insumo: String) :
    Producto(nombre, precio, Categoria.INGREDIENTE, mapOf(insumo to 1))

/** Combo pizza + bebida a precio especial: también califica para promociones. */
@Promocionable(porcentaje = 20, motivo = "Combo pizza + bebida")
class Combo(nombre: String, precio: Int, val pizza: Pizza, val bebida: Bebida) :
    Producto(nombre, precio, Categoria.COMBO, calcularIngredientesNecesarios(listOf(pizza, bebida))) {
    override fun cantidadPizzas(): Int = pizza.cantidadPizzas()
}
