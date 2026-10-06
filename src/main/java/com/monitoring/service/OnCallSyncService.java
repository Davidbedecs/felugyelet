package com.monitoring.service;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import com.monitoring.model.OnCall;
import com.monitoring.repository.OnCallRepository;

import jakarta.annotation.PostConstruct;

@Service
public class OnCallSyncService {

    private final OnCallRepository onCallRepository;

    public OnCallSyncService(OnCallRepository onCallRepository) {
        this.onCallRepository = onCallRepository;
    }

    @PostConstruct
    public void syncOnCallDataFromExcel() {
        System.out.println("--- Visszaállás az eredeti, stabil beolvasóra ---");
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath*:excels/*készenlét*.xls*");

            for (Resource resource : resources) {
                String filename = resource.getFilename();
                if (filename == null || filename.startsWith("~$")) continue;

                
                if (filename.toLowerCase().contains("ms-sr-fez")) {
                    System.out.println("-> " + filename + " átugrása (csak az eredeti táblázatot olvassuk)");
                    continue;
                }

                System.out.println("-> Fájl feldolgozása: " + filename);
                String department = "Üzemfelügyelet";

                try (InputStream is = resource.getInputStream();
                     Workbook workbook = new XSSFWorkbook(is)) {

                    for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                        Sheet sheet = workbook.getSheetAt(sheetIndex);
                        String sheetName = sheet.getSheetName().trim();

                        if (!sheetName.matches("^20\\d{2}\\.\\d{2}$")) continue; 

                        int year = Integer.parseInt(sheetName.substring(0, 4));
                        int month = Integer.parseInt(sheetName.substring(5, 7));

                        // 1. Eredeti szótárépítés (Fix oszlopok: B, C, E)
                        Map<String, String> dictPhone = new HashMap<>();
                        for (int r = 3; r <= 14; r++) {
                            Row topRow = sheet.getRow(r);
                            if (topRow == null) continue;
                            
                            String letter = getCellValue(topRow.getCell(1)).toUpperCase(); // B oszlop
                            String name = getCellValue(topRow.getCell(2)).toUpperCase();   // C oszlop
                            
                            String phone = "";
                            for(int c = 4; c <= 10; c++) { // E oszloptól
                                String val = getCellValue(topRow.getCell(c));
                                if(!val.isEmpty()) { phone = val; break; }
                            }
                            
                            if (!phone.isEmpty()) {
                                if (!letter.isEmpty()) dictPhone.put(letter, phone);
                                if (!name.isEmpty()) dictPhone.put(name, phone);
                            }
                        }

                        
                        for (int r = 16; r <= sheet.getLastRowNum(); r++) {
                            Row row = sheet.getRow(r);
                            if (row == null) continue;

                            String dayStr = getCellValue(row.getCell(1)); 
                            String rawNameText = getCellValue(row.getCell(3)); 
                            
                            if (dayStr.isEmpty() || rawNameText.isEmpty()) continue;

                            
                            String dayNumberStr = dayStr;
                            if (dayStr.contains(".")) dayNumberStr = dayStr.substring(0, dayStr.indexOf(".")).trim();
                            else if (dayStr.contains(" ")) dayNumberStr = dayStr.substring(0, dayStr.indexOf(" ")).trim();

                            try {
                                int day = Integer.parseInt(dayNumberStr);
                                if (day >= 1 && day <= 31) {
                                    LocalDate date = LocalDate.of(year, month, day);
                                    
                                    StringBuilder phonesBuilder = new StringBuilder();
                                    String[] nameParts = rawNameText.split("\\+");
                                    
                                    for (int i = 0; i < nameParts.length; i++) {
                                        String part = nameParts[i].trim().toUpperCase();
                                        String foundPhone = dictPhone.getOrDefault(part, "Nincs adat");
                                        phonesBuilder.append(foundPhone);
                                        if (i < nameParts.length - 1) phonesBuilder.append(", ");
                                    }

                                    saveOrUpdateOnCall(date, rawNameText, phonesBuilder.toString(), department);
                                }
                            } catch (Exception ex) {}
                        }
                    }
                }
            }
            System.out.println("--- Eredeti Készenléti beolvasás kész! ---");
        } catch (Exception e) {
            System.err.println("!!! HIBA AZ EXCEL FELDOLGOZÁSAKOR !!!");
            e.printStackTrace();
        }
    }

    private void saveOrUpdateOnCall(LocalDate date, String names, String phones, String department) {
        Optional<OnCall> existing = onCallRepository.findByDateAndDepartment(date, department);
        if (existing.isPresent()) {
            OnCall onCall = existing.get();
            onCall.setNames(names);
            onCall.setPhones(phones);
            onCallRepository.save(onCall);
        } else {
            onCallRepository.save(new OnCall(date, names, phones, department));
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue().trim();
            case NUMERIC: 
                if (DateUtil.isCellDateFormatted(cell)) return String.valueOf(cell.getLocalDateTimeCellValue().getDayOfMonth());
                return String.valueOf((long) cell.getNumericCellValue());
            case FORMULA:
                switch (cell.getCachedFormulaResultType()) {
                    case STRING: return cell.getStringCellValue().trim();
                    case NUMERIC: return String.valueOf((long) cell.getNumericCellValue());
                    default: return "";
                }
            default: return "";
        }
    }
}