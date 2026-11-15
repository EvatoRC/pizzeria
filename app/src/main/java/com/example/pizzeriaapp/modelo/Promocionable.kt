package com.example.pizzeriaapp.modelo

/**
 * Paso 2 - Anotación personalizada.
 *
 * Marca las CLASES de productos (pizzas o combos) que califican para promociones especiales.
 *  - @Target(CLASS): solo se puede poner sobre una clase.
 *  - @Retention(RUNTIME): queda disponible en tiempo de ejecución, para leerla con reflection.
 *  - @MustBeDocumented: aparece en la documentación generada (KDoc/Javadoc).
 *
 * @property porcentaje descuento adicional que recibe el producto dentro del período de promociones.
 * @property motivo     descripción de la promoción.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Promocionable(val porcentaje: Int, val motivo: String)

/**
 * Lee la anotación @Promocionable de la clase REAL del objeto (Pizza, PizzaFamiliar, Combo...).
 * Se usa la reflection de Java (getAnnotation), que viene incluida en la JVM y no depende de kotlin-reflect.
 * Devuelve null si la clase no está anotada.
 */
fun Producto.promocion(): Promocionable? = this::class.java.getAnnotation(Promocionable::class.java)

fun Producto.esPromocionable(): Boolean = promocion() != null

/** Identifica dinámicamente, sin listas manuales, cuáles productos son elegibles para promociones. */
fun productosPromocionables(productos: Collection<Producto>): List<Producto> =
    productos.filter { it.esPromocionable() }.distinct()

fun describirPromocion(producto: Producto): String {
    val promocion: Promocionable = producto.promocion() ?: return "${producto.nombre}: sin promoción"
    return "${producto.nombre} [clase ${producto::class.simpleName}] -> " +
        "${promocion.porcentaje}% de descuento (${promocion.motivo})"
}
