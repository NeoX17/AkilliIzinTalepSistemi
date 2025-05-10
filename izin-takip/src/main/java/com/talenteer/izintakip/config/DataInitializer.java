package com.talenteer.izintakip.config;

import com.talenteer.izintakip.repository.LeaveRequestRepository;
import com.talenteer.izintakip.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration
public class DataInitializer {

    private final ExcelService excelService;
    private final LeaveRequestRepository leaveRequestRepository;

    @Autowired
    public DataInitializer(ExcelService excelService, LeaveRequestRepository leaveRequestRepository) {
        this.excelService = excelService;
        this.leaveRequestRepository = leaveRequestRepository;
    }

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // Veritabanında kayıt var mı kontrol et
            if (leaveRequestRepository.count() > 0) {
                System.out.println("Veritabanında zaten " + leaveRequestRepository.count() + " adet kayıt bulunuyor. Yeni veri yüklenmeyecek.");
                return;
            }
            
            // resources klasöründe izinler.xlsx dosyasının var olup olmadığını kontrol et
            ClassPathResource resource = new ClassPathResource("izinler.xlsx");
            try {
                if (resource.exists()) {
                    System.out.println("Varsayılan izin verileri yükleniyor...");
                    excelService.loadDefaultExcelData();
                    System.out.println("Varsayılan izin verileri başarıyla yüklendi. Toplam kayıt sayısı: " + leaveRequestRepository.count());
                } else {
                    System.out.println("izinler.xlsx dosyası bulunamadı. Varsayılan veriler yüklenemedi.");
                }
            } catch (Exception e) {
                System.err.println("Varsayılan izin verileri yüklenirken hata: " + e.getMessage());
            }
        };
    }
} 