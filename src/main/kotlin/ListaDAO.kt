object ListaDAO {

    // Si el jugador no existe, la FK hace fallar el INSERT y la lista no se crea
    fun insertar(lista: Lista): Boolean {
        ConexionBD.requireConnection().use { conn ->
            conn.prepareStatement("INSERT INTO listas (nombre, id_jugador) VALUES (?, ?)").use { ps ->
                ps.setString(1, lista.nombre)
                ps.setInt(2, lista.idJugador)
                return ps.executeUpdate() > 0
            }
        }
    }

    // Sus filas en listas_videojuegos se borran solas por el ON DELETE CASCADE
    fun eliminar(idLista: Int): Boolean {
        ConexionBD.requireConnection().use { conn ->
            conn.prepareStatement("DELETE FROM listas WHERE id_lista = ?").use { ps ->
                ps.setInt(1, idLista)
                return ps.executeUpdate() > 0
            }
        }
    }
}
