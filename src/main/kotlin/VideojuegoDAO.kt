import java.sql.ResultSet

object VideojuegoDAO {

    fun listarVideojuegos(): List<Videojuego> {
        val lista = mutableListOf<Videojuego>()
        ConexionBD.requireConnection().use { conn ->
            conn.prepareStatement("SELECT * FROM videojuegos ORDER BY id").use { ps ->
                ps.executeQuery().use { rs ->
                    while (rs.next()) lista.add(mapear(rs))
                }
            }
        }
        return lista
    }

    fun consultarPorId(id: Int): Videojuego? {
        ConexionBD.requireConnection().use { conn ->
            conn.prepareStatement("SELECT * FROM videojuegos WHERE id = ?").use { ps ->
                ps.setInt(1, id)
                ps.executeQuery().use { rs ->
                    return if (rs.next()) mapear(rs) else null
                }
            }
        }
    }

    fun insertarVideojuego(v: Videojuego): Boolean {
        ConexionBD.requireConnection().use { conn ->
            conn.prepareStatement(
                "INSERT INTO videojuegos (titulo, genero, anio, nota) VALUES (?, ?, ?, ?)"
            ).use { ps ->
                ps.setString(1, v.titulo)
                ps.setString(2, v.genero)
                ps.setInt(3, v.anio)
                ps.setDouble(4, v.nota)
                return ps.executeUpdate() > 0
            }
        }
    }

    fun actualizarVideojuego(v: Videojuego): Boolean {
        val id = requireNotNull(v.id) { "El videojuego no tiene id" }
        ConexionBD.requireConnection().use { conn ->
            conn.prepareStatement(
                "UPDATE videojuegos SET titulo = ?, genero = ?, anio = ?, nota = ? WHERE id = ?"
            ).use { ps ->
                ps.setString(1, v.titulo)
                ps.setString(2, v.genero)
                ps.setInt(3, v.anio)
                ps.setDouble(4, v.nota)
                ps.setInt(5, id)
                return ps.executeUpdate() > 0
            }
        }
    }

    fun eliminarVideojuego(id: Int): Boolean {
        ConexionBD.requireConnection().use { conn ->
            conn.prepareStatement("DELETE FROM videojuegos WHERE id = ?").use { ps ->
                ps.setInt(1, id)
                return ps.executeUpdate() > 0
            }
        }
    }

    // También lo usa ListaVideojuegoDAO
    fun mapear(rs: ResultSet) = Videojuego(
        id = rs.getInt("id"),
        titulo = rs.getString("titulo"),
        genero = rs.getString("genero"),
        anio = rs.getInt("anio"),
        nota = rs.getDouble("nota")
    )
}
