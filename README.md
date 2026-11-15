# PizzeriaApp — Semana 4: Configurando Android Studio

Proyecto Android (Kotlin + Jetpack Compose) que integra:

- la **lógica de las semanas 1 a 3** (Exp1_S3_Luis_Rebolledo), que antes era una app de consola,
- el **diseño "Artisanal Trattoria"** (pantallas Inicio, Registro y Home exportadas de Stitch).

## Estructura de paquetes (`app/src/main/java/com/example/pizzeriaapp`)

| Paquete | Contenido |
|---|---|
| `modelo` | `Producto` (Pizza, PizzaFamiliar, Bebida, IngredienteExtra, Combo), `Pedido`, `Usuario` (Cliente, Repartidor), anotación `@Promocionable` |
| `logica` | `Operaciones` (totales, descuentos, inventario, repartidores), `Validaciones` (Regex y Ranges), `RegistroDiario`, `RegistroSinDuplicados`, `Menu`, `Pizzeria` (flujo completo de un pedido) |
| `ui` | `PizzeriaApp` (navegación), `PizzeriaViewModel`, pantallas (`screens`), componentes y tema |
| raíz | `MainActivity.kt` con la función `Greeting`: "Bienvenidos a la mejor pizzería de la ciudad" |

## Adaptaciones al entorno Android

- `readln()`/`println()` se reemplazaron por formularios y mensajes en pantalla: las validaciones devuelven el error para mostrarlo bajo cada campo.
- `aceptarPedido()` y `despacharPedido()` ahora devuelven un `ResultadoPedido` (sealed class) en vez de imprimir.
- Se quitaron el listado de miembros con reflection, el reporte por consola y el archivo `resumen_pizzeria.txt`, porque solo tenían sentido en consola. `@Promocionable` se sigue leyendo con reflection para calcular descuentos (con reglas R8 en `keepRules/rules.keep`).
- Los textos promocionales del diseño se reemplazaron por las reglas reales: 10 % por volumen (más de 3 pizzas), 5 % en el período 01-11-2026 al 30-11-2026, 15 % Pizza Familiar y 20 % Combo dentro del período, IVA 19 %.
- El teléfono mantiene la validación de la semana 3: 10 dígitos (se asume +56).

## Pantallas

Inicio → Registro → Home (menú, búsqueda, filtros por categoría) → Carrito (desglose del total y confirmación) → Pedidos del día (estado y entrega).


## Tipografías

Epilogue y Plus Jakarta Sans (Google Fonts, licencia OFL) están en `res/font`.
