package com.example.pizzeriaapp.logica

/**
 * Registro que evita duplicados usando equals() y hashCode().
 *
 * LinkedHashSet primero compara hashCode() y, si coincide, confirma con equals(); por eso
 * Cliente y Producto sobrescriben ambos métodos con los mismos atributos clave.
 * "Linked" mantiene el orden de inserción (útil para mostrar el menú en orden).
 *
 * Adaptación a Android: la versión de consola imprimía "[Duplicado] ..." con println.
 * Aquí no hay consola, así que el resultado se informa con el valor de retorno
 * y la pantalla decide qué mensaje mostrar.
 */
class RegistroSinDuplicados<T : Any> {
    private val elementos: MutableSet<T> = linkedSetOf()

    val cantidad: Int
        get() = elementos.size

    /** Agrega el elemento solo si no existe otro igual (==). Devuelve true si se agregó. */
    fun registrar(elemento: T): Boolean = elementos.add(elemento)

    /**
     * Si ya existe un elemento igual, devuelve el registrado (así los pedidos quedan asociados
     * al mismo cliente). Si no, registra el nuevo y lo devuelve.
     */
    fun registrarOObtener(elemento: T): T {
        val existente: T? = elementos.find { it == elemento }
        if (existente != null) return existente
        elementos.add(elemento)
        return elemento
    }

    operator fun contains(elemento: T): Boolean = elemento in elementos

    fun todos(): List<T> = elementos.toList()
}
