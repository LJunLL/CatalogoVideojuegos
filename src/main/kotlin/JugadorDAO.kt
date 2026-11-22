object JugadorDAO {

    fun insertar(j: Jugador): Boolean {
        ConexionBD.requireConnection().use { conn ->
            conn.prepareStatement("INSERT INTO jugadores (nombre, email) VALUES (?, ?)").use { ps ->
                ps.setString(1, j.nombre)
                ps.setString(2, j.email)
                return ps.executeUpdate() > 0
            }
        }
    }

    // Las listas del jugador se borran solas por el ON DELETE CASCADE de la FK
    fun eliminar(idJugador: Int): Boolean {
        ConexionBD.requireConnection().use { conn ->
            conn.prepareStatement("DELETE FROM jugadores WHERE id_jugador = ?").use { ps ->
                ps.setInt(1, idJugador)
                return ps.executeUpdate() > 0
            }
        }
    }
}
