package com.icodetest.api.controllers;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.icodetest.api.Repository.DynamicEntityRepository;
import com.icodetest.api.model.DynamicEntity;
import com.icodetest.api.service.DynamicEntityService;

import jakarta.persistence.EntityManager;

import java.util.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/dynamic-entities")
public class DynamicEntityController {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private DynamicEntityService dynamicEntityService;

    @Autowired
    private DynamicEntityRepository dynamicEntityRepository;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadXlsxFile(@RequestParam("file") MultipartFile file) {
        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);

            int numColumns = sheet.getRow(0).getPhysicalNumberOfCells();

            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                Row row = sheet.getRow(i);
                Map<String, String> fieldValues = new LinkedHashMap<>(); 

                for (int j = 0; j < numColumns; j++) {
                    String fieldName = sheet.getRow(0).getCell(j).getStringCellValue(); 
                    Cell cell = row.getCell(j);

                    if (cell.getCellType() == CellType.NUMERIC) {
                        double numericValue = cell.getNumericCellValue();
                        String stringValue = String.valueOf((int) numericValue);
                        fieldValues.put(fieldName, stringValue);
                    } else if (cell.getCellType() == CellType.STRING) {
                        String fieldValue = cell.getStringCellValue();
                        fieldValues.put(fieldName, fieldValue);
                    }

                }
                DynamicEntity dynamicEntity = dynamicEntityService.createDynamicEntity(fieldValues);
                dynamicEntityRepository.save(dynamicEntity);
            }

            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing the file");
        }

        return ResponseEntity.ok("File uploaded and processed successfully");
    }

    @PostMapping("/createPivotedTable")
    public void createPivotedTable(@RequestParam("table_name") String table_name) {
        dynamicEntityRepository.createPivotedTable(table_name);
    }

    @GetMapping("/getDynamicTableData")
    public ResponseEntity<?> getDynamicTableData(@RequestParam String table_name) {
        String dataQuery = "SELECT * FROM " + table_name;
        String fieldNamesQuery = "DESCRIBE " + table_name;

        try {
            List<Object[]> dataResult = entityManager.createNativeQuery(dataQuery).getResultList();

            List<String> fieldNamesResult = entityManager.createNativeQuery(fieldNamesQuery).getResultList();

            Map<String, Object> response = new HashMap<>();
            response.put("fieldNames", fieldNamesResult);
            response.put("data", dataResult);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching data");
        }
    }

}
