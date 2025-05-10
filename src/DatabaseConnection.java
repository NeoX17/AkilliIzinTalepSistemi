import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public static Connection connect() {
        String url = "jdbc:mysql://localhost:3306/izin_sistemi"; // Veritabanı adı: izin_sistemi
        String user = "root";  // Kullanıcı adı
        String password = "MehmetUgurSamet17";  // Şifre

        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Veritabanına bağlandı!");
            return conn;
        } catch (SQLException e) {
            System.out.println("Bağlantı hatası: " + e.getMessage());
            return null;
        }
    }
}
