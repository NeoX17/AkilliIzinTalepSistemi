package com.talenteer.izin_takip.service;

import com.talenteer.izin_takip.model.LeaveRequest;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelExportService {
    public void exportToExcel(List<LeaveRequest> requests, String filePath) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Izinler");

        // Başlık satırı
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Kullanıcı");
        header.createCell(2).setCellValue("Başlangıç Tarihi");
        header.createCell(3).setCellValue("Bitiş Tarihi");
        header.createCell(4).setCellValue("Açıklama");
        header.createCell(5).setCellValue("Durum");

        int rowNum = 1;
        for (LeaveRequest req : requests) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(req.getId());
            row.createCell(1).setCellValue(req.getUser().getUsername());
            row.createCell(2).setCellValue(req.getStartDate().toString());
            row.createCell(3).setCellValue(req.getEndDate().toString());
            row.createCell(4).setCellValue(req.getReason());
            row.createCell(5).setCellValue(req.getStatus());
        }

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            workbook.write(fos);
        }
        workbook.close();
    }
} 