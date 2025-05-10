package com.talenteer.izintakip.controller;

import com.talenteer.izintakip.model.LeaveRequest;
import com.talenteer.izintakip.repository.LeaveRequestRepository;
import com.talenteer.izintakip.service.ExcelService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leave")
public class ExcelController {

    private final ExcelService excelService;
    private final LeaveRequestRepository leaveRequestRepository;

    @Autowired
    public ExcelController(ExcelService excelService, LeaveRequestRepository leaveRequestRepository) {
        this.excelService = excelService;
        this.leaveRequestRepository = leaveRequestRepository;
    }

    /**
     * Excel dosyasından izin taleplerini içe aktarır
     */
    @PostMapping("/import")
    public ResponseEntity<?> importExcelFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lütfen bir dosya seçin");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            List<LeaveRequest> importedRequests = excelService.processExcelFile(file);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", importedRequests.size() + " adet izin talebi başarıyla içe aktarıldı.");
            response.put("importedRequests", importedRequests);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Dosya işlenirken hata oluştu: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Excel şablonu indirir
     */
    @GetMapping("/template")
    public void downloadExcelTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=izin_talepleri_sablonu.xlsx");
        
        Workbook workbook = excelService.createExcelTemplate();
        
        try (OutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
            workbook.close();
        }
    }

    /**
     * Varsayılan Excel dosyasını (izinler.xlsx) yükler
     */
    @PostMapping("/load-default")
    public ResponseEntity<?> loadDefaultExcelData() {
        try {
            System.out.println("/load-default API çağrısı alındı");
            List<LeaveRequest> loadedRequests = excelService.loadDefaultExcelData();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", loadedRequests.size() + " adet izin talebi varsayılan dosyadan yüklendi.");
            response.put("loadedRequests", loadedRequests);
            
            System.out.println("Varsayılan yükleme tamamlandı: " + loadedRequests.size() + " izin talebi");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Varsayılan yükleme sırasında hata: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Varsayılan dosya yüklenirken hata oluştu: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Veritabanı durumunu kontrol eder ve veri yoksa yükleme yapar
     */
    @GetMapping("/check-data")
    public ResponseEntity<?> checkAndLoadData() {
        try {
            System.out.println("/check-data API çağrısı alındı");
            
            long count = leaveRequestRepository.count();
            System.out.println("Veritabanındaki mevcut kayıt sayısı: " + count);
            
            Map<String, Object> response = new HashMap<>();
            
            if (count == 0) {
                System.out.println("Veritabanında kayıt bulunamadı. Veriler yükleniyor...");
                
                // Excel dosyasını bulmaya çalış
                ClassPathResource resource = new ClassPathResource("izinler.xlsx");
                
                if (resource.exists()) {
                    System.out.println("izinler.xlsx dosyası bulundu. Boyut: " + resource.contentLength() + " bytes");
                    List<LeaveRequest> loadedRequests = excelService.loadDefaultExcelData();
                    
                    response.put("success", true);
                    response.put("message", "Veriler başarıyla yüklendi. Toplam: " + loadedRequests.size() + " kayıt.");
                    response.put("count", loadedRequests.size());
                    response.put("requests", loadedRequests);
                } else {
                    System.out.println("izinler.xlsx dosyası bulunamadı!");
                    response.put("success", false);
                    response.put("message", "izinler.xlsx dosyası bulunamadı!");
                }
            } else {
                response.put("success", true);
                response.put("message", "Veritabanında zaten " + count + " kayıt bulunuyor.");
                response.put("count", count);
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Veri kontrolü sırasında hata: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Hata: " + e.getMessage());
            response.put("stackTrace", e.getStackTrace().toString());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Veritabanındaki tüm izin taleplerini temizler
     */
    @PostMapping("/clean-database")
    public ResponseEntity<?> cleanDatabase() {
        try {
            System.out.println("/clean-database API çağrısı alındı");
            
            // Tüm izin taleplerini veritabanından sil
            long deletedCount = leaveRequestRepository.count();
            leaveRequestRepository.deleteAll();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", deletedCount + " adet izin talebi veritabanından silindi.");
            
            System.out.println("Veritabanı temizleme tamamlandı: " + deletedCount + " izin talebi silindi");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Veritabanı temizleme sırasında hata: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Veritabanı temizlenirken hata oluştu: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
} 