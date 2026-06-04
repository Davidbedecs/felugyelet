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
public class MsSrFezSyncService {

    private final OnCallRepository onCallRepository;

    public MsSrFezSyncService(OnCallRepository onCallRepository) {
        this.onCallRepository = onCallRepository;
    }

    @PostConstruct
    public void syncFezData() {
        System.out.println("--- Ms-Sr-Fez Különálló Beolvasó Indul ---");
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath*:excels/*készenlét*.xls*");

            for (Resource resource : resources) {
                String filename = resource.getFilename();
                if (filename == null || filename.startsWith("~$")) continue;

                if (!filename.toLowerCase().contains("fez")) continue; 

                System.out.println("-> FEZ Fájl feldolgozása: " + filename);
                String department = "Ms-Sr-Fez";

                try (InputStream is = resource.getInputStream();
                     Workbook workbook = new XSSFWorkbook(is)) {

                    for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                        Sheet sheet = workbook.getSheetAt(sheetIndex);
                        String sheetName = sheet.getSheetName().trim();

                        if (!sheetName.matches("^20\\d{2}\\.\\d{2}$")) continue; 

                        int year = Integer.parseInt(sheetName.substring(0, 4));
                        int month = Integer.parseInt(sheetName.substring(5, 7));

                        Map<String, String> dictPhone = new HashMap<>();

                        // =======================================================
                        // 1. FEZ TELEFONOK (Egybeírt A oszlop szétvágása)
                        // =======================================================
                        for (int r = 3; r <= 14; r++) {
                            Row topRow = sheet.getRow(r);
                            if (topRow == null) continue;
                            
                            // A oszlop (index: 0) - pl. "A KALAS LAJOS"
                            String combinedNameCell = getCellValue(topRow.getCell(0)).trim(); 
                            // D oszlop (index: 3) - Telefon
                            String phone = getCellValue(topRow.getCell(3)).trim();      
                            
                            if (!phone.isEmpty() && !combinedNameCell.isEmpty()) {
                                // Megkeressük a legelső szóközt, hogy kettévágjuk a betűt és a nevet!
                                int firstSpaceIndex = combinedNameCell.indexOf(' ');
                                
                                if (firstSpaceIndex > 0) {
                                    String letter = combinedNameCell.substring(0, firstSpaceIndex);
                                    String name = combinedNameCell.substring(firstSpaceIndex + 1);
                                    
                                    dictPhone.put(cleanTextForMatch(letter), phone);
                                    dictPhone.put(cleanTextForMatch(name), phone);
                                } else {
                                    // Ha nincs szóköz, egyben mentjük el
                                    dictPhone.put(cleanTextForMatch(combinedNameCell), phone);
                                }
                            }
                        }

                        // =======================================================
                        // 2. FEZ NAPI BEOSZTÁS ("+" jel kereső algoritmus)
                        // =======================================================
                        for (int r = 16; r <= sheet.getLastRowNum(); r++) {
                            Row row = sheet.getRow(r);
                            if (row == null) continue;

                            String dayStr = getCellValue(row.getCell(0)); 
                            String rawNameText = "";
                            
                            // Végigmegyünk a dátum melletti oszlopokon, és azt tekintjük névnek, amiben van "+" jel!
                            for (int c = 1; c <= 5; c++) {
                                String val = getCellValue(row.getCell(c)).trim();
                                if (val.contains("+")) {
                                    rawNameText = val;
                                    break;
                                }
                            }
                            
                            if (dayStr.isEmpty() || rawNameText.isEmpty()) continue;

                            String dayNumberStr = dayStr.replaceAll("[^0-9]", "");

                            try {
                                int day = Integer.parseInt(dayNumberStr);
                                if (day >= 1 && day <= 31) {
                                    LocalDate date = LocalDate.of(year, month, day);
                                    
                                    StringBuilder phonesBuilder = new StringBuilder();
                                    String[] nameParts = rawNameText.split("\\+");
                                    
                                    for (int i = 0; i < nameParts.length; i++) {
                                        String searchKey = cleanTextForMatch(nameParts[i]);
                                        String foundPhone = dictPhone.getOrDefault(searchKey, "Nincs adat");
                                        
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
            System.out.println("--- Ms-Sr-Fez beolvasás sikeresen befejeződött! ---");
        } catch (Exception e) {
            System.err.println("!!! HIBA A FEZ EXCEL FELDOLGOZÁSAKOR !!!");
            e.printStackTrace();
        }
    }

    private String cleanTextForMatch(String input) {
        if (input == null) return "";
        return input.replaceAll("[\\s\\u00A0]+", "").toUpperCase();
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