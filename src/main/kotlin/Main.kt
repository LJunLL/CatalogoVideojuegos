import java.sql.SQLException
import java.time.LocalDate
import kotlin.system.exitProcess

fun main() {
    if (!ConexionBD.testConnection()) {
        println("No se puede conectar con PostgreSQL. Revisa que la base de datos esté arrancada.")
        return
    }
    println("Conexión establecida correctamente con PostgreSQL.")

    while (true) {
        mostrarMenu()
        val opcion = leerEntero("Opción: ")
        if (opcion == 0) {
            println("¡Hasta luego!")
            return
        }
        try {
            ejecutarOpcion(opcion)
        } catch (e: Exception) {
            println("Error: ${mensajeError(e)}")
        }
    }
}

fun mostrarMenu() {
    println(
        """
        |
        |===== CATÁLOGO DE VIDEOJUEGOS =====
        |-- Videojuegos --
        | 1. Ver todos
        | 2. Buscar por ID
        | 3. Añadir
        | 4. Modificar
        | 5. Eliminar
        |-- Jugadores --
        | 6. Crear jugador
        | 7. Eliminar jugador
        |-- Listas --
        | 8. Crear lista
        | 9. Eliminar lista
        |10. Añadir videojuego a una lista
        |11. Ver videojuegos de una lista
        | 0. Salir
        """.trimMargin()
    )
}

fun ejecutarOpcion(opcion: Int) {
    when (opcion) {
        1 -> {
            val juegos = VideojuegoDAO.listarVideojuegos()
            if (juegos.isEmpty()) println("No hay videojuegos.") else juegos.forEach(::imprimir)
        }
        2 -> {
            val v = VideojuegoDAO.consultarPorId(leerEntero("ID: "))
            if (v == null) println("No existe ese videojuego.") else imprimir(v)
        }
        3 -> {
            val nuevo = Videojuego(
                titulo = leerTexto("Título: "),
                genero = leerTexto("Género: "),
                anio = leerEntero("Año: "),
                nota = leerDecimal("Nota: ")
            )
            println(if (VideojuegoDAO.insertarVideojuego(nuevo)) "Videojuego añadido." else "No se pudo añadir.")
        }
        4 -> {
            val actual = VideojuegoDAO.consultarPorId(leerEntero("ID del videojuego a modificar: "))
            if (actual == null) {
                println("No existe ese videojuego.")
                return
            }
            imprimir(actual)
            println("(Pulsa Enter para mantener el valor actual)")
            val modificado = actual.copy(
                titulo = leerOpcional("Título: ") ?: actual.titulo,
                genero = leerOpcional("Género: ") ?: actual.genero,
                anio = leerOpcional("Año: ", String::toIntOrNull) ?: actual.anio,
                nota = leerOpcional("Nota: ", String::toDoubleOrNull) ?: actual.nota
            )
            println(if (VideojuegoDAO.actualizarVideojuego(modificado)) "Videojuego modificado." else "No se pudo modificar.")
        }
        5 -> {
            val ok = VideojuegoDAO.eliminarVideojuego(leerEntero("ID: "))
            println(if (ok) "Videojuego eliminado." else "No existe ese videojuego.")
        }
        6 -> {
            val j = Jugador(nombre = leerTexto("Nombre: "), email = leerTexto("Email: "))
            println(if (JugadorDAO.insertar(j)) "Jugador creado." else "No se pudo crear.")
        }
        7 -> {
            val ok = JugadorDAO.eliminar(leerEntero("ID del jugador: "))
            println(if (ok) "Jugador eliminado (junto con sus listas)." else "No existe ese jugador.")
        }
        8 -> {
            val lista = Lista(nombre = leerTexto("Nombre de la lista: "), idJugador = leerEntero("ID del jugador: "))
            println(if (ListaDAO.insertar(lista)) "Lista creada." else "No se pudo crear.")
        }
        9 -> {
            val ok = ListaDAO.eliminar(leerEntero("ID de la lista: "))
            println(if (ok) "Lista eliminada." else "No existe esa lista.")
        }
        10 -> {
            val idLista = leerEntero("ID de la lista: ")
            val idVideojuego = leerEntero("ID del videojuego: ")
            val ok = ListaVideojuegoDAO.anyadirALista(idLista, idVideojuego, LocalDate.now().toString())
            if (ok) println("Videojuego añadido a la lista.")
        }
        11 -> {
            val juegos = ListaVideojuegoDAO.obtenerVideojuegosDeLista(leerEntero("ID de la lista: "))
            if (juegos.isEmpty()) println("La lista no existe o está vacía.") else juegos.forEach(::imprimir)
        }
        else -> println("Opción no válida.")
    }
}

fun imprimir(v: Videojuego) =
    println("[${v.id}] ${v.titulo} | ${v.genero} | ${v.anio} | Nota: ${v.nota}")

// Traduce los errores SQL más habituales de PostgreSQL a mensajes legibles
fun mensajeError(e: Exception): String =
    if (e is SQLException) when (e.sqlState) {
        "23503" -> "referencia inexistente (el jugador, la lista o el videojuego no existe)"
        "23505" -> "registro duplicado (ya existe)"
        else -> e.message ?: "error de base de datos"
    } else e.message ?: e.toString()

// ---------------- Lectura de teclado ----------------

fun leerLinea(prompt: String): String {
    print(prompt)
    return readlnOrNull()?.trim() ?: run {
        println("\nEntrada cerrada. Saliendo.")
        exitProcess(0)
    }
}

fun leerTexto(prompt: String): String {
    while (true) {
        val texto = leerLinea(prompt)
        if (texto.isNotEmpty()) return texto
        println("No puede estar vacío.")
    }
}

fun leerEntero(prompt: String): Int {
    while (true) {
        leerLinea(prompt).toIntOrNull()?.let { return it }
        println("Introduce un número entero.")
    }
}

fun leerDecimal(prompt: String): Double {
    while (true) {
        leerLinea(prompt).replace(',', '.').toDoubleOrNull()?.let { return it }
        println("Introduce un número (ej: 8.5).")
    }
}

// Devuelve null si el usuario pulsa Enter o escribe algo no válido
fun leerOpcional(prompt: String): String? = leerLinea(prompt).ifEmpty { null }

fun <T> leerOpcional(prompt: String, convertir: (String) -> T?): T? =
    leerOpcional(prompt)?.replace(',', '.')?.let(convertir)
