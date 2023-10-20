package com.icodetest.api.controllers;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/")
public class Generator {

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/generateExcelTemplate")
    public void generateExcelTemplate(@RequestBody List<String> fields, HttpServletResponse response) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Template Sheet");

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < fields.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(fields.get(i));
        }

        try {
            response.setHeader("Content-Disposition", "attachment; filename=template.xlsx");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/testCors")
    public String testCors() {
        return "CORS is working!";
    }
}
