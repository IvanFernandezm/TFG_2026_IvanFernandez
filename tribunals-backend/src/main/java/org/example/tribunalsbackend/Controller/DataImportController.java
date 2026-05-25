package org.example.tribunalsbackend.Controller;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.tribunalsbackend.Api.DTO.TribunalDTO;
import org.example.tribunalsbackend.Domain.*;
import org.example.tribunalsbackend.Persistence.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataImportController {
    DocentRepository docentRepository;
    EstudiantRepository estudiantRepository;
    TreballRepository treballRepository;
    ExpertesaRepository expertesaRepository;
    DisponibilitatRepository  disponibilitatRepository;
    TribunalRepository tribunalRepository;

    public DataImportController(DocentRepository docentRepository, EstudiantRepository estudiantRepository,
                                TreballRepository treballRepository, ExpertesaRepository expertesaRepository,
                                DisponibilitatRepository disponibilitatRepository, TribunalRepository tribunalRepository) {
        this.docentRepository = docentRepository;
        this.estudiantRepository = estudiantRepository;
        this.treballRepository = treballRepository;
        this.expertesaRepository = expertesaRepository;
        this.disponibilitatRepository = disponibilitatRepository;
        this.tribunalRepository = tribunalRepository;
    }

    public void importExcel(MultipartFile excelFile) throws Exception {
        List<Disponibilitat> disponibilitats = disponibilitatRepository.findAll();
        Workbook workbook = new XSSFWorkbook(excelFile.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        boolean firstRow = true;

        for (Row row : sheet) {
            if (firstRow) {firstRow = false; continue;} // Salta la primera fila (capçaleres)

            Cell c0 = row.getCell(0); // nom estudiant
            Cell c1 = row.getCell(1); // mail estudiant
            Cell c2 = row.getCell(2); // nom tutor (docent)
            Cell c3 = row.getCell(3); // mail tutor (docent)
            Cell c4 = row.getCell(4); // descripció treball
            Cell c5 = row.getCell(5); // titol treball
            Cell c6 = row.getCell(6); // expertesa id

            String studentName = getCellString(c0);
            String studentEmail = getCellString(c1);
            String tutorName = getCellString(c2);
            String tutorEmail = getCellString(c3);
            String description = getCellString(c4);
            String title = getCellString(c5);
            String expertesaId = getCellString(c6);

            if(studentEmail == null || tutorName == null || description == null || expertesaId == null || title == null){
                throw new IllegalArgumentException("Alguna de les dades obligatòries és nula a la fila " + row.getRowNum());
            }

            Estudiant student = new Estudiant(studentEmail, studentName);
            Docent tutor = docentRepository.findById(tutorEmail).orElse(new Docent(tutorEmail, tutorName));

            Expertesa expertesa = expertesaRepository.findById(expertesaId)
                    .orElse(expertesaRepository.save(new Expertesa(expertesaId, "Expertesa " + expertesaId)));

            tutor.addExpertesa(expertesa);
            tutor.setAvailability(disponibilitats);

            estudiantRepository.save(student);
            docentRepository.save(tutor);

            Treball tfg =  new Treball(title, description,student,tutor,expertesa);

            treballRepository.save(tfg);
        }
    }
    public List<TribunalDTO> organitzarTribunals() {
        List<Tribunal> tribunals = new ArrayList<>();
        List<TribunalDTO> tribunalDTOs = new ArrayList<>();
        this.tribunalRepository.saveAll(tribunals);
        return tribunalDTOs;
    }
    private static String getCellString(Cell c) {
        if (c == null) return null;
        switch (c.getCellType()) {
            case STRING:
                return c.getStringCellValue();
            case NUMERIC:
                double v = c.getNumericCellValue();
                if (v == (long) v) return String.valueOf((long) v);
                return String.valueOf(v);
            case BOOLEAN:
                return String.valueOf(c.getBooleanCellValue());
            case FORMULA:
                try {
                    return c.getStringCellValue();
                } catch (Exception e) {
                    double dv = c.getNumericCellValue();
                    if (dv == (long) dv) return String.valueOf((long) dv);
                    return String.valueOf(dv);
                }
            default:
                return null;
        }
    }
    private TribunalDTO TribunalToDTO (Tribunal tribunal) {
        Docent presidencia = tribunal.getPresidencia();
        Docent vocal = tribunal.getVocal();
        Treball treball = tribunal.getTreball();
        Estudiant estudiant = treball.getStudent();
        Docent tutor = treball.getTutor();
        Expertesa exp = treball.getExpertesa();
        return new TribunalDTO(tribunal.getRoom(),presidencia.getName(), vocal.getName(),
                tribunal.getAdjudicacio(), treball.getTitle(), estudiant.getName(), tutor.getName(), exp.getId());
    }
}
