package org.example.tribunalsbackend.Controller;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.tribunalsbackend.Api.DTO.TribunalDTO;
import org.example.tribunalsbackend.Config.Exceptions.EntityNotFoundException;
import org.example.tribunalsbackend.Domain.*;
import org.example.tribunalsbackend.Persistence.TribunalRepository;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class TFGTribunalController {
    private static final DateTimeFormatter DATA_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    TribunalRepository tribunalRepository;

    public TFGTribunalController(TribunalRepository tribunalRepository) {
        this.tribunalRepository = tribunalRepository;
    }

    public List<TribunalDTO> getAllTribunals() {
        List<Tribunal> tribunals = tribunalRepository.findAll();
        List<TribunalDTO> tribunalDTOs = new ArrayList<>();
        if (tribunals.isEmpty()) {
            return null;
        }
        for (Tribunal tribunal : tribunals) {
            TribunalDTO t = TribunalToDTO(tribunal);
            tribunalDTOs.add(t);
        }
        return tribunalDTOs;
    }

    public List<TribunalDTO> getTribunalsByDocent(String docentEmail) {
        List<Tribunal> docTribs = tribunalRepository.findTribunalsByPresidencia_MailOrVocal_Mail(docentEmail, docentEmail);
        if (docTribs.isEmpty())
            throw new EntityNotFoundException("No s'han trobat tribunals assignats a aquest professor!");

        List<TribunalDTO> respTribs = new ArrayList<>();

        for (Tribunal t : docTribs) {
            respTribs.add(TribunalToDTO(t));
        }
        return respTribs;
    }

    private TribunalDTO TribunalToDTO(Tribunal tribunal) {
        Docent presidencia = tribunal.getPresidencia();
        Docent vocal = tribunal.getVocal();
        Treball treball = tribunal.getTreball();
        Estudiant estudiant = treball.getStudent();
        Docent tutor = treball.getTutor();
        Expertesa exp = treball.getExpertesa();
        return new TribunalDTO(tribunal.getRoom(), presidencia.getName(), vocal.getName(),
                tribunal.getAdjudicacio(), treball.getTitle(), estudiant.getName(), tutor.getName(), exp.getId());
    }

    public byte[] exportarTribunals() throws Exception {
        List<Tribunal> tribunals = tribunalRepository.findAll();
        if (tribunals.isEmpty()) throw new EntityNotFoundException("No hi han tribunals encara establerts!");

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Tribunals_informàtica_2026");

            String[] columnas = {
                    "Aula", "Data i hora", "Presidència",
                    "Vocalía", "Títol del TFG",
                    "Estudiant", "Tutoria", "Expertesa"
            };

            Row header = sheet.createRow(0);

            for (int i = 0; i < columnas.length; i++) {
                header.createCell(i).setCellValue(columnas[i]);
            }

            int numeroFila = 1;

            for (Tribunal tribunal : tribunals) {
                Row fila = sheet.createRow(numeroFila++);
                Treball treball = tribunal.getTreball();

                fila.createCell(0).setCellValue(
                        tribunal.getRoom()
                );

                fila.createCell(1).setCellValue(
                        tribunal.getAdjudicacio() == null
                                ? "-"
                                : DATA_FORMAT.format(
                                tribunal.getAdjudicacio()
                        )
                );

                fila.createCell(2).setCellValue(
                        tribunal.getPresidencia().getName()
                );

                fila.createCell(3).setCellValue(
                        tribunal.getVocal().getName()
                );

                fila.createCell(4).setCellValue(
                        treball.getTitle()
                );

                fila.createCell(5).setCellValue(
                        treball.getStudent().getName()
                );

                fila.createCell(6).setCellValue(
                        treball.getTutor().getName()
                );

                fila.createCell(7).setCellValue(

                        treball.getExpertesa().getId()

                );
            }

            for (int i = 0; i < columnas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(output);
            return output.toByteArray();
        }

    }
}
