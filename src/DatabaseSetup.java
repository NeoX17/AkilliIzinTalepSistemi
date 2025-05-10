import java.sql.Connection;
import java.sql.Statement;

public class DatabaseSetup {
    public static void createTables() {
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement()) {

            // Veritabanı oluşturma
            String createDB = "CREATE DATABASE IF NOT EXISTS izin_sistemi";
            stmt.executeUpdate(createDB);
            stmt.executeUpdate("USE izin_sistemi");

            // Çalışanlar tablosu
            String createEmployees = """
                CREATE TABLE IF NOT EXISTS employees (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    isim VARCHAR(50),
                    soyisim VARCHAR(50),
                    pozisyon VARCHAR(50),
                    departman VARCHAR(50),
                    ise_giris DATE,
                    izin_hakki INT,
                    kullanilan_izin INT DEFAULT 0,
                    kullanici_adi VARCHAR(50) UNIQUE,
                    sifre VARCHAR(100)
                )
            """;
            stmt.executeUpdate(createEmployees);

            // İzin talepleri tablosu
            String createLeaveRequests = """
                CREATE TABLE IF NOT EXISTS leave_requests (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    calisan_id INT,
                    baslangic_tarihi DATE,
                    bitis_tarihi DATE,
                    izin_turu VARCHAR(50),
                    talep_tarihi DATE,
                    durum VARCHAR(20),
                    aciklama TEXT,
                    FOREIGN KEY (calisan_id) REFERENCES employees(id)
                )
            """;
            stmt.executeUpdate(createLeaveRequests);

            // İK kullanıcı tablosu
            String createHRUsers = """
                CREATE TABLE IF NOT EXISTS hr_users (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    kullanici_adi VARCHAR(50) UNIQUE,
                    sifre VARCHAR(100),
                    rol VARCHAR(50)
                )
            """;
            stmt.executeUpdate(createHRUsers);

            // İzin kuralları tablosu
            String createLeaveRules = """
                CREATE TABLE IF NOT EXISTS leave_rules (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    kural_adi VARCHAR(100),
                    kural_aciklama TEXT,
                    baslangic_tarihi DATE,
                    bitis_tarihi DATE
                )
            """;
            stmt.executeUpdate(createLeaveRules);

            // İzin analiz tablosu
            String createLeaveAnalysis = """
                CREATE TABLE IF NOT EXISTS leave_analysis (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    talep_id INT,
                    analiz_sonucu VARCHAR(50),
                    gerekce TEXT,
                    FOREIGN KEY (talep_id) REFERENCES leave_requests(id)
                )
            """;
            stmt.executeUpdate(createLeaveAnalysis);

            System.out.println("Tablolar başarıyla oluşturuldu!");

        } catch (Exception e) {
            System.out.println("Tablo oluşturma hatası: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        createTables();
    }
}
