package com.example.pizzeriaapp.logica

import com.example.pizzeriaapp.modelo.Bebida
import com.example.pizzeriaapp.modelo.Combo
import com.example.pizzeriaapp.modelo.IngredienteExtra
import com.example.pizzeriaapp.modelo.Pizza
import com.example.pizzeriaapp.modelo.PizzaFamiliar
import com.example.pizzeriaapp.modelo.Producto
import com.example.pizzeriaapp.modelo.Repartidor

/** Productos del menú. Se definen una sola vez y se registran en el catálogo sin duplicados. */
object Menu {
    val pepperoni = Pizza(
        "Pepperoni", 8390,
        mapOf("masa" to 1, "salsa de tomate" to 1, "queso mozzarella" to 2, "pepperoni" to 1)
    )
    val cuatroQuesos = Pizza(
        "Cuatro Quesos", 10070,
        mapOf(
            "masa" to 1, "salsa de tomate" to 1, "queso mozzarella" to 1,
            "queso azul" to 1, "queso parmesano" to 1, "queso gouda" to 1
        )
    )
    val napolitana = Pizza(
        "Napolitana", 8810,
        mapOf("masa" to 1, "salsa de tomate" to 1, "queso mozzarella" to 2, "tomate" to 2, "orégano" to 1)
    )
    val pepperoniFamiliar = PizzaFamiliar(
        "Pepperoni Familiar", 13990,
        mapOf("masa" to 2, "salsa de tomate" to 1, "queso mozzarella" to 3, "pepperoni" to 2)
    )
    val bebida = Bebida("Bebida 1.5 L", 2490)
    val extraQueso = IngredienteExtra("Extra queso", 1190, "queso mozzarella")
    val comboNapolitana = Combo("Combo Napolitana + Bebida", 10290, napolitana, bebida)

    fun todos(): List<Producto> =
        listOf(pepperoni, cuatroQuesos, napolitana, pepperoniFamiliar, bebida, extraQueso, comboNapolitana)
}

fun crearInventario(): MutableMap<String, Int> = mutableMapOf(
    "masa" to 20,
    "salsa de tomate" to 20,
    "queso mozzarella" to 30,
    "pepperoni" to 6,
    "queso azul" to 3,
    "queso parmesano" to 4,
    "queso gouda" to 4,
    "tomate" to 14,
    "orégano" to 8,
    "Bebida 1.5 L" to 12
)

fun crearRepartidores(): List<Repartidor> = listOf(
    Repartidor("Luis Rebolledo", formatearTelefono("+56 911 112 2222") ?: TELEFONO_POR_DEFECTO, 3.2),
    Repartidor("Joaquín Gómez", formatearTelefono("933-334-4444") ?: TELEFONO_POR_DEFECTO, 1.5),
    Repartidor("Felipe Peñaloza", formatearTelefono("(955) 556.6666") ?: TELEFONO_POR_DEFECTO, 2.4)
)

/** Sedes que se ofrecen en el formulario de registro (vienen del diseño). */
val SEDES: List<String> = listOf(
    "Sede Antonio Varas (Providencia)",
    "Sede San Joaquín (Vicuña Mackenna)",
    "Sede Alameda (Santiago Centro)",
    "Sede Plaza Vespucio (La Florida)",
    "Sede Plaza Oeste (Cerrillos)",
    "Sede Viña del Mar (Álvarez)",
    "Sede Valparaíso (Edificio Cousiño)",
    "Sede San Bernardo",
    "Despacho a domicilio particular"
)
