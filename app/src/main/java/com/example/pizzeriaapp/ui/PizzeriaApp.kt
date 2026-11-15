package com.example.pizzeriaapp.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pizzeriaapp.ui.components.BarraInferior
import com.example.pizzeriaapp.ui.components.Pestana
import com.example.pizzeriaapp.ui.screens.CarritoScreen
import com.example.pizzeriaapp.ui.screens.HomeScreen
import com.example.pizzeriaapp.ui.screens.InicioScreen
import com.example.pizzeriaapp.ui.screens.PedidosScreen
import com.example.pizzeriaapp.ui.screens.RegistroScreen

/** Raíz de la interfaz: decide qué pantalla mostrar según el estado del ViewModel. */
@Composable
fun PizzeriaApp(vm: PizzeriaViewModel = viewModel()) {
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(vm.mensaje) {
        vm.mensaje?.let {
            snackbar.showSnackbar(it)
            vm.mensaje = null
        }
    }

    // Botón atrás del sistema: en Inicio no se intercepta, así Android cierra la app.
    BackHandler(enabled = vm.pantalla != Pantalla.INICIO) { vm.volver() }

    val pestana: Pestana? = when (vm.pantalla) {
        Pantalla.HOME -> Pestana.INICIO
        Pantalla.PEDIDOS -> Pestana.PEDIDOS
        else -> null
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar = {
            if (pestana != null) {
                BarraInferior(pestana) { seleccion ->
                    when (seleccion) {
                        Pestana.INICIO -> vm.irA(Pantalla.HOME)
                        Pestana.PEDIDOS -> vm.irA(Pantalla.PEDIDOS)
                        Pestana.EXPLORAR -> vm.avisar("Explorar estará disponible en una próxima entrega.")
                        // Polimorfismo: describir() de Cliente (semana 2) muestra sus datos.
                        Pestana.PERFIL -> vm.avisar(vm.cliente?.describir() ?: "Navegas como invitado. Regístrate para ver tu perfil.")
                    }
                }
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding).consumeWindowInsets(padding)) {
            when (vm.pantalla) {
                Pantalla.INICIO -> InicioScreen(
                    onComenzar = { vm.irA(Pantalla.REGISTRO) },
                    onTengoCuenta = { vm.irA(Pantalla.REGISTRO) },
                    onInvitado = { vm.irA(Pantalla.HOME) }
                )

                Pantalla.REGISTRO -> RegistroScreen(
                    onVolver = { vm.volver() },
                    onRegistrar = vm::registrar
                )

                Pantalla.HOME -> HomeScreen(
                    cliente = vm.cliente,
                    productos = vm.productosFiltrados(),
                    busqueda = vm.busqueda,
                    onBusqueda = { vm.busqueda = it },
                    categoria = vm.categoria,
                    onCategoria = { vm.categoria = it },
                    cantidadEn = vm::cantidadEnCarrito,
                    onAgregar = vm::agregar,
                    onQuitar = vm::quitar,
                    unidadesCarrito = vm.carrito.size,
                    totalCarrito = vm.totalCarrito(),
                    onVerCarrito = { vm.irA(Pantalla.CARRITO) }
                )

                Pantalla.CARRITO -> CarritoScreen(
                    cliente = vm.cliente,
                    cantidades = vm.cantidadesCarrito(),
                    pizzas = vm.pizzasEnCarrito(),
                    total = vm.totalCarrito(),
                    fecha = vm.fechaPedido,
                    notas = vm.notas,
                    onNotas = { vm.notas = it },
                    resultado = vm.ultimoResultado,
                    onAgregar = vm::agregar,
                    onQuitar = vm::quitar,
                    onEliminar = vm::eliminarTodos,
                    onConfirmar = vm::confirmarPedido,
                    onReabastecer = { vm.reabastecerYReintentar(it.faltantes) },
                    onCerrarResultado = vm::cerrarResultado,
                    onVolver = { vm.volver() },
                    onVerPedidos = { vm.irA(Pantalla.PEDIDOS) }
                )

                Pantalla.PEDIDOS -> PedidosScreen(
                    pedidos = vm.pedidos(),
                    version = vm.versionPedidos,
                    onEntregado = vm::marcarEntregado
                )
            }
        }
    }
}
