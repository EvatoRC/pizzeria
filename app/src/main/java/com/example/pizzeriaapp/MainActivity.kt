package com.example.pizzeriaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.pizzeriaapp.ui.PizzeriaApp
import com.example.pizzeriaapp.ui.theme.PizzeriaAppTheme

/*
 * Semana 4 - "Configurando Android Studio".
 * La lógica de las semanas 1 a 3 (antes una app de consola) quedó organizada en paquetes:
 *   modelo  -> Producto, Pizza, Bebida, Combo..., Pedido, Usuario, Cliente, Repartidor, @Promocionable
 *   logica  -> Operaciones (totales, inventario, repartidores), Validaciones (Regex y Ranges),
 *              RegistroDiario, RegistroSinDuplicados, Menu y Pizzeria (flujo de un pedido)
 *   ui      -> pantallas Compose (Inicio, Registro, Home, Carrito, Pedidos), componentes y tema
 */

const val MENSAJE_BIENVENIDA: String = "Bienvenidos a la mejor pizzería de la ciudad"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PizzeriaAppTheme {
                PizzeriaApp()
            }
        }
    }
}

/** Mensaje de bienvenida pedido en el Paso 5. Se muestra como título de la pantalla de Inicio. */
@Composable
fun Greeting(modifier: Modifier = Modifier, mensaje: String = MENSAJE_BIENVENIDA) {
    Text(
        text = mensaje,
        style = MaterialTheme.typography.displayLarge,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center,
        modifier = modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PizzeriaAppTheme {
        Greeting()
    }
}
