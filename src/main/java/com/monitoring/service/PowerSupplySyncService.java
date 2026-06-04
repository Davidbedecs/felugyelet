package com.monitoring.service;

import java.io.InputStream;
import java.time.LocalDate;
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

import com.monitoring.model.PowerSupply;
import com.monitoring.repository.PowerSupplyRepository;

import jakarta.annotation.PostConstruct;

@Service
public class PowerSupplySyncService {

    private final PowerSupplyRepository powerSupplyRepository;

    public PowerSupplySyncService(PowerSupplyRepository powerSupplyRepository) {
        this.powerSupplyRepository = powerSupplyRepository;
    }

    @PostConstruct
    public void syncPowerSupplyDataFromExcel() {
        System.out.println("--- Áramellátás Excel feldolgozása indul ---");
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            // Csak az "akkumulator" nevű fájlokat olvassa be
            Resource[] resources = resolver.getResources("classpath*:excels/*akkumulator*.xls*");

            for (Resource resource : resources) {
                String filename = resource.getFilename();
                if (filename != null && filename.startsWith("~$")) continue;

                System.out.println("-> Áramellátás fájl: " + filename);
                try (InputStream is = resource.getInputStream();
                     Workbook workbook = new XSSFWorkbook(is)) {

                    Sheet sheet = workbook.getSheetAt(0); // Feltételezzük, hogy az első fülön van
                    int savedCount = 0;

                    // A mérések a 3. sortól kezdődnek (POI index: 2)
                    for (int r = 2; r <= sheet.getLastRowNum(); r++) {
                        Row row = sheet.getRow(r);
                        if (row == null) continue;

                        String location = getCellValue(row.getCell(0)); // A: Helyszín
                        if (location.isEmpty()) continue; // Ha nincs helyszín, üres sor, ugrunk

                        String exactLoc = getCellValue(row.getCell(1)); // B: Pontos elhelyezés
                        
                        // Dátum összerakása (C, D, E)
                        String yearStr = getCellValue(row.getCell(2));
                        String monthStr = getCellValue(row.getCell(3));
                        String dayStr = getCellValue(row.getCell(4));
                        
                        LocalDate measureDate;
                        try {
                            measureDate = LocalDate.of(
                                (int)Double.parseDouble(yearStr), 
                                (int)Double.parseDouble(monthStr), 
                                (int)Double.parseDouble(dayStr)
                            );
                        } catch (Exception e) {
                            continue; // Ha hibás a dátum formátum
                        }

                        PowerSupply ps = new PowerSupply();
                        ps.setLocation(location);
                        ps.setExactLocation(exactLoc);
                        ps.setMeasurementDate(measureDate);
                        ps.setMeasuredBy(getCellValue(row.getCell(5))); // F
                        ps.setChargerType(getCellValue(row.getCell(6))); // G
                        ps.setBatteryGroup(getCellValue(row.getCell(7))); // H
                        ps.setGroupVoltage(getCellValue(row.getCell(8))); // I
                        ps.setBatteryType(getCellValue(row.getCell(9))); // J
                        ps.setCapacity(getCellValue(row.getCell(10))); // K
                        ps.setInstalledDate(getCellValue(row.getCell(12))); // L
                        ps.setTemperature(getCellValue(row.getCell(13))); // M
                        ps.setInstrument(getCellValue(row.getCell(14))); // N

                        saveOrUpdatePowerSupply(ps);
                        savedCount++;
                    }
                    System.out.println("   Feldolgozva: " + savedCount + " mérési rekord.");
                }
            }
        } catch (Exception e) {
            System.err.println("Hiba az Áramellátás Excel feldolgozásakor: " + e.getMessage());
        }
    }

    private void saveOrUpdatePowerSupply(PowerSupply newData) {
        Optional<PowerSupply> existing = powerSupplyRepository.findByLocationAndExactLocationAndMeasurementDate(
                newData.getLocation(), newData.getExactLocation(), newData.getMeasurementDate());
        
        if (existing.isPresent()) {
            newData.setId(existing.get().getId()); // Ha már létezik, felülírjuk (frissítjük)
        }
        powerSupplyRepository.save(newData);
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue().trim();
            case NUMERIC: 
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate().toString();
                }
                // Tizedespont nélküli számok konvertálása stringgé
                double val = cell.getNumericCellValue();
                if (val == Math.floor(val)) {
                    return String.valueOf((long)val);
                }
                return String.valueOf(val);
            default: return "";
        }
    }
}