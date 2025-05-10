package com.talenteer.izintakip.service;

import com.talenteer.izintakip.model.LeaveRequest;
import com.talenteer.izintakip.model.User;
import com.talenteer.izintakip.repository.LeaveRequestRepository;
import com.talenteer.izintakip.repository.UserRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service
public class ExcelService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final UserRepository userRepository;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Autowired
    public ExcelService(LeaveRequestRepository leaveRequestRepository, UserRepository userRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.userRepository = userRepository;
    }

    /**
     * izinler.xlsx dosyasını okur ve veritabanına kaydeder
     */
    public List<LeaveRequest> loadDefaultExcelData() {
        try {
            System.out.println("Varsayılan Excel dosyası (izinler.xlsx) yükleniyor...");
            Resource resource = new ClassPathResource("izinler.xlsx");
            System.out.println("izinler.xlsx dosyası bulundu: " + resource.exists());
            System.out.println("izinler.xlsx dosya boyutu: " + resource.contentLength() + " bytes");
            
            // Önce debug amaçlı Excel dosyasını incele
            try (InputStream debugStream = resource.getInputStream();
                 Workbook workbook = new XSSFWorkbook(debugStream)) {
                System.out.println("Excel workbook başarıyla açıldı. Sayfa sayısı: " + workbook.getNumberOfSheets());
                if (workbook.getNumberOfSheets() > 0) {
                    Sheet sheet = workbook.getSheetAt(0);
                    System.out.println("İlk sayfa başarıyla açıldı. Satır sayısı: " + sheet.getLastRowNum());
                    // İlk birkaç satırı kontrol et
                    for (int i = 0; i <= Math.min(5, sheet.getLastRowNum()); i++) {
                        Row row = sheet.getRow(i);
                        if (row != null) {
                            System.out.println("Satır " + i + ": " + row.getPhysicalNumberOfCells() + " hücre var");
                            if (i == 0) { // Başlık satırı
                                StringBuilder headerRow = new StringBuilder("Başlıklar: ");
                                for (int j = 0; j < row.getLastCellNum(); j++) {
                                    Cell cell = row.getCell(j);
                                    if (cell != null) {
                                        headerRow.append(j).append(":").append(cell.toString()).append(", ");
                                    }
                                }
                                System.out.println(headerRow.toString());
                            }
                        } else {
                            System.out.println("Satır " + i + " boş");
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Excel dosyası incelenirken hata: " + e.getMessage());
                e.printStackTrace();
            }
            
            // Şimdi gerçek veri yükleme işlemini gerçekleştir
            List<LeaveRequest> result;
            try (InputStream dataStream = resource.getInputStream()) {
                result = processExcelFile(dataStream);
                System.out.println("Excel verilerinden " + result.size() + " adet izin talebi yüklendi.");
            }
            return result;
        } catch (IOException e) {
            System.err.println("izinler.xlsx dosyasını okurken hata oluştu: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Varsayılan izin dosyası okunamadı: " + e.getMessage(), e);
        }
    }

    /**
     * Yüklenen Excel dosyasını işler ve veritabanına kaydeder
     */
    public List<LeaveRequest> processExcelFile(MultipartFile file) {
        try {
            return processExcelFile(file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("Excel dosyası okunamadı: " + e.getMessage(), e);
        }
    }

    /**
     * Excel dosyasını işler ve izin taleplerini liste olarak döndürür
     */
    private List<LeaveRequest> processExcelFile(InputStream inputStream) {
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            
            // Başlık satırını atla
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }
            
            List<LeaveRequest> leaveRequests = new ArrayList<>();
            
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                
                // Boş satırları atla
                if (isEmptyRow(row)) continue;
                
                LeaveRequest leaveRequest = extractLeaveRequestFromRow(row);
                if (leaveRequest != null) {
                    leaveRequestRepository.save(leaveRequest);
                    leaveRequests.add(leaveRequest);
                }
            }
            
            return leaveRequests;
        } catch (IOException e) {
            throw new RuntimeException("Excel dosyası işlenirken hata oluştu: " + e.getMessage(), e);
        }
    }
    
    /**
     * Excel satırından izin talebi oluşturur
     */
    private LeaveRequest extractLeaveRequestFromRow(Row row) {
        try {
            // Görseldeki Excel şablonuna göre sütun indekslerini ayarla
            String employeeId = getStringCellValue(row, 0); // Çalışan ID
            String fullName = getStringCellValue(row, 1);   // Ad-Soyad
            String position = getStringCellValue(row, 2);   // Pozisyon
            String title = getStringCellValue(row, 3);      // Unvan
            // Talep Edilen İzin Tarihleri (8. sütun)
            String leaveDateRange = getStringCellValue(row, 8); 
            
            LocalDate startDate = null;
            LocalDate endDate = null;
            
            if (leaveDateRange != null && !leaveDateRange.trim().isEmpty()) {
                String[] dates = leaveDateRange.split("-");
                if (dates.length >= 2) {
                    startDate = parseDate(dates[0].trim());
                    endDate = parseDate(dates[1].trim());
                } else {
                    startDate = parseDate(leaveDateRange.trim());
                    endDate = startDate; // Tek günlük izin
                }
            }
            
            String reason = getStringCellValue(row, 11); // Talep Açıklaması (11. sütun)
            String status = getStringCellValue(row, 9);  // Talep Durumu (9. sütun)
            
            // Excel'den kullanılan ve kalan izin günlerini al
            Integer usedLeave = getIntegerCellValue(row, 5);  // Kullanılan İzin (5. sütun)
            Integer remainingLeave = getIntegerCellValue(row, 6); // Kalan İzin (6. sütun)
            
            // Talep oluşturma tarihini al
            LocalDate requestDate = null;
            String requestDateStr = getStringCellValue(row, 7); // Talep Oluşturma Tarihi (7. sütun)
            if (requestDateStr != null && !requestDateStr.trim().isEmpty()) {
                requestDate = parseDate(requestDateStr.trim());
            }
            
            if (status == null || status.trim().isEmpty() || 
                !("onaylanan".equals(status) || "reddedilen".equals(status) || "bekleyen".equals(status))) {
                status = "bekleyen";
            }
            
            // Debug için konsola yazdır
            System.out.println("İzin talebi okundu: " + employeeId + " - " + fullName + " - Tarihler: " + 
                              (startDate != null ? startDate.toString() : "null") + " ile " + 
                              (endDate != null ? endDate.toString() : "null") + " - Durum: " + status +
                              " - Açıklama: " + (reason != null ? reason.substring(0, Math.min(reason.length(), 30)) + "..." : "null"));
            
            // Kullanıcıyı bul veya oluştur
            User user = findOrCreateUser(employeeId, fullName, position, title);
            
            // İzin talebi oluştur
            LeaveRequest leaveRequest = new LeaveRequest();
            leaveRequest.setUser(user);
            leaveRequest.setStartDate(startDate);
            leaveRequest.setEndDate(endDate);
            leaveRequest.setReason(reason);
            leaveRequest.setStatus(status);
            leaveRequest.setUsedLeave(usedLeave);
            leaveRequest.setRemainingLeave(remainingLeave);
            leaveRequest.setRequestDate(requestDate);
            
            return leaveRequest;
        } catch (Exception e) {
            System.err.println("Satır işlenirken hata: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Kullanıcıyı ID'ye göre bulur veya yeni kullanıcı oluşturur
     */
    private User findOrCreateUser(String employeeId, String fullName, String position, String title) {
        Optional<User> existingUser = userRepository.findByEmployeeId(employeeId);
        
        if (existingUser.isPresent()) {
            return existingUser.get();
        } else {
            User newUser = new User();
            newUser.setEmployeeId(employeeId);
            newUser.setFullName(fullName);
            newUser.setPosition(position);
            newUser.setTitle(title);
            newUser.setEmail(generateEmailFromName(fullName));
            newUser.setRole("EMPLOYEE");
            
            return userRepository.save(newUser);
        }
    }
    
    /**
     * İsimden basit bir e-posta adresi oluşturur
     */
    private String generateEmailFromName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "user@company.com";
        }
        
        String normalized = fullName.toLowerCase()
                .replace("ı", "i")
                .replace("ğ", "g")
                .replace("ü", "u")
                .replace("ş", "s")
                .replace("ö", "o")
                .replace("ç", "c")
                .replaceAll("[^a-z]", ".");
        
        return normalized.replaceAll("\\.+", ".") + "@company.com";
    }
    
    /**
     * Tarihi parse eder
     */
    private LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            // Farklı format dene
            try {
                return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (DateTimeParseException e2) {
                throw new RuntimeException("Tarih formatı anlaşılamadı: " + dateStr, e2);
            }
        }
    }
    
    /**
     * Hücre değerini string olarak alır
     */
    private String getStringCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) {
            return null;
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate().format(DATE_FORMATTER);
                } else {
                    return String.valueOf((int) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    return String.valueOf((int) cell.getNumericCellValue());
                }
            default:
                return null;
        }
    }
    
    /**
     * Hücre değerini integer olarak alır
     */
    private Integer getIntegerCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) {
            return null;
        }
        
        try {
            switch (cell.getCellType()) {
                case STRING:
                    try {
                        return Integer.parseInt(cell.getStringCellValue().trim());
                    } catch (NumberFormatException e) {
                        return null;
                    }
                case NUMERIC:
                    return (int) cell.getNumericCellValue();
                default:
                    return null;
            }
        } catch (Exception e) {
            System.err.println("Sayısal hücre değeri okunurken hata: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Satırın boş olup olmadığını kontrol eder
     */
    private boolean isEmptyRow(Row row) {
        if (row == null) {
            return true;
        }
        
        if (row.getPhysicalNumberOfCells() == 0) {
            return true;
        }
        
        boolean isEmpty = true;
        for (int cellNum = 0; cellNum < row.getLastCellNum(); cellNum++) {
            Cell cell = row.getCell(cellNum);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                isEmpty = false;
                break;
            }
        }
        
        return isEmpty;
    }
    
    /**
     * Excel şablonu oluşturur
     */
    public Workbook createExcelTemplate() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("İzin Talepleri");
        
        // Başlık satırı
        Row headerRow = sheet.createRow(0);
        String[] headers = {
            "Çalışan ID", "Ad-Soyad", "Pozisyon", "Unvan", "İşe Başlama Tarihi", 
            "Kullanılan İzin (Gün)", "Kalan İzin (Gün)", "Talep Oluşturma Tarihi", 
            "Talep Edilen İzin Tarihleri", "Talep Durumu", "Talep Açıklaması"
        };
        
        // Hücre stilini ayarla
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        
        // Başlıkları ekle
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 256 * 20); // 20 karakter genişlik
        }
        
        // Örnek veri satırı
        Row sampleRow = sheet.createRow(1);
        sampleRow.createCell(0).setCellValue("EMP001");
        sampleRow.createCell(1).setCellValue("Ahmet Yılmaz");
        sampleRow.createCell(2).setCellValue("Yazılım Geliştirici");
        sampleRow.createCell(3).setCellValue("Uzman");
        sampleRow.createCell(4).setCellValue("01.01.2022");
        sampleRow.createCell(5).setCellValue("5");
        sampleRow.createCell(6).setCellValue("15");
        sampleRow.createCell(7).setCellValue("25.08.2024");
        sampleRow.createCell(8).setCellValue("01.09.2024 - 05.09.2024");
        sampleRow.createCell(9).setCellValue("bekleyen");
        sampleRow.createCell(10).setCellValue("Yıllık izin");
        
        return workbook;
    }
} 