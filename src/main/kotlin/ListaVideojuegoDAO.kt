import java.sql.SQLException
import java.time.LocalDate

object ListaVideojuegoDAO {

    fun insertar(lv: ListaVideojuego): Boolean =
        anyadirALista(lv.idLista, lv.idVideojuego, lv.fechaAnyadido)

    // Práctica 5: comprobar que el videojuego existe + insertar, en una única transacción
    fun anyadirALista(idLista: Int, idVideojuego: Int, fecha: String): Boolean {
        val conn = ConexionBD.requireConnection()
        try {
            conn.autoCommit = false

            // 1. Comprobar que el videojuego existe
            val existe = conn.prepareStatement("SELECT 1 FROM videojuegos WHERE id = ?").use { ps ->
                ps.setInt(1, idVideojuego)
                ps.executeQuery().use { it.next() }
            }
            if (!existe) throw SQLException("El videojuego con ID $idVideojuego no existe")

            // 2. Insertar en la tabla intermedia
            conn.prepareStatement(
                "INSERT INTO listas_videojuegos (id_lista, id_videojuego, fecha_añadido) VALUES (?, ?, ?)"
            ).use { ps ->
                ps.setInt(1, idLista)
                ps.setInt(2, idVideojuego)
                ps.setObject(3, LocalDate.parse(fecha))
                ps.executeUpdate()
            }

            conn.commit()
            return true
        } catch (e: Exception) {
            conn.rollback()
            println("Transacción cancelada (rollback): ${mensajeError(e)}")
            return false
        } finally {
            conn.autoCommit = true
            ConexionBD.closeConnection(conn)
        }
    }

    fun obtenerVideojuegosDeLista(idLista: Int): List<Videojuego> {
        val lista = mutableListOf<Videojuego>()
        ConexionBD.requireConnection().use { conn ->
            conn.prepareStatement(
                """
                SELECT v.* FROM videojuegos v
                JOIN listas_videojuegos lv ON lv.id_videojuego = v.id
                WHERE lv.id_lista = ?
                ORDER BY lv.fecha_añadido
                """.trimIndent()
            ).use { ps ->
                ps.setInt(1, idLista)
                ps.executeQuery().use { rs ->
                    while (rs.next()) lista.add(VideojuegoDAO.mapear(rs))
                }
            }
        }
        return lista
    }
}
