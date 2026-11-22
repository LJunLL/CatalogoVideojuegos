import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object ConexionBD {
    private const val HOST = "localhost"
    private const val PORT = 5433
    private const val DATABASE = "videojuegos"
    private const val USER = "videojuegos"
    private const val PASSWORD = "videojuegos"

    private const val URL = "jdbc:postgresql://$HOST:$PORT/$DATABASE"

    // Devuelve la conexión o null si falla
    fun getConnection(): Connection? =
        try {
            DriverManager.getConnection(URL, USER, PASSWORD)
        } catch (e: SQLException) {
            println("Error de conexión: ${e.message}")
            null
        }

    // Igual que getConnection, pero lanza excepción en vez de devolver null (lo usan los DAO)
    fun requireConnection(): Connection =
        getConnection() ?: throw SQLException("No se pudo conectar con la base de datos")

    fun testConnection(): Boolean {
        val conn = getConnection() ?: return false
        conn.use { return it.isValid(2) }
    }

    fun closeConnection(conn: Connection?) {
        try {
            conn?.close()
        } catch (e: SQLException) {
            println("Error al cerrar la conexión: ${e.message}")
        }
    }
}
