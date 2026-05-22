package org.example.Data;

import org.example.Data.Domain.*;

import java.time.LocalDateTime;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class NewExampleData {
    public List<Docent> docents = new ArrayList<>();
    public List<Estudiant> estudiants = new ArrayList<>();
    public List<Treball> treballs = new ArrayList<>();
    public List<Disponibilitat> slots = new ArrayList<>();
    public List<Tribunal> tribunals = new ArrayList<>();
    public List<Expertesa> experteses = new ArrayList<>();
    public int maxDefensesPerSlot = 1;

    public int getNumProfessors() {
        return docents.size();
    }

    public int getTfgCount() {
        return treballs.size();
    }

    public int getSlotsCount() {
        return slots.size();
    }

    public Docent getDocentBySolverId(int solverId) {
        if (solverId < 1 || solverId > docents.size()) {
            return null;
        }
        return docents.get(solverId - 1);
    }

    public Treball getTreballByIndex(int tfgIndex) {
        return treballs.get(tfgIndex);
    }

    public int getTutorSolverIdOfTFG(int tfgIndex) {
        Docent tutor = treballs.get(tfgIndex).getTutor();
        int idx = docents.indexOf(tutor);
        return idx + 1;
    }

    public boolean isProfAvailableAt(int profSolverId, int slotSolverId) {
        Docent docent = getDocentBySolverId(profSolverId);
        if (docent == null || slotSolverId < 1 || slotSolverId > slots.size()) {
            return false;
        }

        List<Disponibilitat> availability = docent.getAvailability();
        if (availability == null) {
            return false;
        }

        Disponibilitat slot = slots.get(slotSolverId - 1);
        return availability.contains(slot);
    }

    public List<Tribunal> guardarSolucioEnTribunals(
            int[] presidenciesPerTFG,
            int[] vocalsPerTFG,
            int[] slotsPerTFG
    ) {
        if (presidenciesPerTFG.length != treballs.size()
                || vocalsPerTFG.length != treballs.size()
                || slotsPerTFG.length != treballs.size()) {
            throw new IllegalArgumentException("Les dimensions de la solucio no coincideixen amb els TFGs");
        }

        List<Tribunal> tribunals = new ArrayList<>();
        for (int t = 0; t < treballs.size(); t++) {
            Docent presidencia = getDocentBySolverId(presidenciesPerTFG[t]);
            Docent vocal = getDocentBySolverId(vocalsPerTFG[t]);
            Treball treball = treballs.get(t);
            int slotSolverId = slotsPerTFG[t];

            if (slotSolverId < 1 || slotSolverId > slots.size()) {
                throw new IllegalArgumentException("Slot invalid per al TFG index " + t + ": " + slotSolverId);
            }

            Tribunal tribunal = new Tribunal(
                    "SLOT-" + slotSolverId,
                    presidencia,
                    vocal,
                    treball
            );
            tribunal.setAdjudicacio(slots.get(slotSolverId - 1).getDataDis());
            tribunals.add(tribunal);
        }

        this.tribunals.addAll(tribunals);
        return tribunals;
    }

    public static NewExampleData buildExampleData() {
        NewExampleData d = new NewExampleData();
        // Intentar leer el Excel con los datos (primero intento desde classpath, si falla uso path relativo)
        InputStream is = null;
        Workbook workbook = null;
        try {
            is = NewExampleData.class.getResourceAsStream("Tutors_TFG_20242025.xlsx");
            if (is == null) {
                File f = new File("src/main/java/org/example/Data/Tutors_TFG_20242025.xlsx");
                if (f.exists()) {
                    is = new FileInputStream(f);
                }
            }

            if (is == null) {
                throw new IllegalStateException("No s'ha trobat l'arxiu Excel Tutors_TFG_20242025.xlsx");
            }

            workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);

            Map<String, Docent> docentByEmail = new LinkedHashMap<>();
            Map<String, Estudiant> estudiantByEmail = new LinkedHashMap<>();
            Map<String, Expertesa> expertesaById = new LinkedHashMap<>();

            boolean firstRow = true;
            for (Row row : sheet) {
                // Saltar header si existe
                if (firstRow) { firstRow = false; continue; }

                Cell c0 = row.getCell(0); // student name
                Cell c1 = row.getCell(1); // student email (id)
                Cell c2 = row.getCell(2); // tutor name
                Cell c3 = row.getCell(3); // tutor email
                Cell c4 = row.getCell(4); // descripcion
                Cell c5 = row.getCell(5); // titulo
                Cell c6 = row.getCell(6); // expertesa id

                String studentName = getCellString(c0);
                String studentEmail = getCellString(c1);
                String tutorName = getCellString(c2);
                String tutorEmail = getCellString(c3);
                String description = getCellString(c4);
                String title = getCellString(c5);
                String expertesaId = getCellString(c6);

                // Requerir mínimo email e titulo
                if (studentEmail == null || studentEmail.isBlank() || title == null || title.isBlank()) {
                    continue;
                }

                // crear/obtener estudiant
                Estudiant estudiant = estudiantByEmail.computeIfAbsent(studentEmail.trim(), em -> new Estudiant(em, studentName == null ? em : studentName));

                // crear/obtener docent
                Docent docent = null;
                if (tutorEmail != null && !tutorEmail.isBlank()) {
                    docent = docentByEmail.computeIfAbsent(tutorEmail.trim(), em -> new Docent(em, tutorName == null ? em : tutorName));
                }

                // crear/obtener expertesa
                Expertesa exp = null;
                if (expertesaId != null && !expertesaId.isBlank()) {
                    String id = expertesaId.trim();
                    exp = expertesaById.computeIfAbsent(id, k -> new Expertesa(k, k));
                }

                // crear treball
                Treball t = new Treball();
                t.setTitle(title.trim());
                t.setDescription(description == null ? "" : description.trim());
                t.setStudent(estudiant);
                if (docent != null) t.setTutor(docent);
                if (exp != null) t.setExpertesa(exp);

                d.treballs.add(t);
                //TODO System.out.println("[Excel] TFG creat: " + describeTreball(t));
            }

            // rellenar listas a partir de maps
            d.docents = new ArrayList<>(docentByEmail.values());
            d.estudiants = new ArrayList<>(estudiantByEmail.values());
            d.experteses = new ArrayList<>(expertesaById.values());

            // slots: del 2 al 13 de juny, de 9:00 a 13:00 i de 15:00 a 18:00
            d.slots = buildJuneSlots();

            // por defecto, todos los docentes tienen todas las disponibilidades
            for (Docent docent : d.docents) {
                for (Disponibilitat slotItem : d.slots) {
                    docent.addDisponibilitat(slotItem);
                }
            }

            // si hay experteses en los treballs, asignarlas también a sus tutores (si existen)
            for (Treball treball : d.treballs) {
                Expertesa e = treball.getExpertesa();
                Docent tutor = treball.getTutor();
                if (e != null && tutor != null) {
                    tutor.addExpertesa(e);
                    if (!d.experteses.contains(e)) d.experteses.add(e);
                }
            }

            d.maxDefensesPerSlot = 1;

            //TODO printExcelDatasetSummary(d);

            return d;

        } catch (Exception ex) {
            System.err.println("[Excel] Error al leer el Excel: " + ex.getMessage());
            // Si falla la lectura del Excel, caer al dataset de ejemplo previo (hardcoded)
        } finally {
            try { if (workbook != null) workbook.close(); } catch (Exception ignored) {}
            try { if (is != null) is.close(); } catch (Exception ignored) {}
        }

        // FALLBACK: dataset hardcoded (antiguo)
        d.docents = List.of(
                new Docent("jboix@tecnocampus.cat", "Jordi Boix"),
                new Docent("rherrero@tecnocampus.cat", "Rosa Herrero"),
                new Docent("sesa@tecnocampus.cat", "Enric Sesa"),
                new Docent("barberan@tecnocampus.cat", "Pere Barberán"),
                new Docent("ptuset@tecnocampus.cat", "Pere Tuset"),
                new Docent("imorenoa@tecnocampus.cat", "Immaculada Moreno"),
                new Docent("cbonet@tecnocampus.cat", "Carles Bonet"),
                new Docent("alopez@tecnocampus.cat", "Anna López"),
                new Docent("dgarcia@tecnocampus.cat", "David García"),
                new Docent("mserra@tecnocampus.cat", "Marta Serra")
        );

        d.estudiants = List.of(
                new Estudiant("ifernandezm@edu.tecnocampus.cat", "Fernández Muñoz, Iván"),
                new Estudiant("amarting@edu.tecnocampus.cat", "Martín Giol, Arnau"),
                new Estudiant("mpuiga@edu.tecnocampus.cat", "Puig Alcaide, Marc"),
                new Estudiant("ftorrus@edu.tecnocampus.cat", "Torrus Mora, Francesc"),
                new Estudiant("xxiangc@edu.tecnocampus.cat", "Xiang Chen, Xinyang"),
                new Estudiant("jrodriguezmi@edu.tecnocampus.cat", "Pujol Rodríguez, David"),
                new Estudiant("cguinovarti@edu.tecnocampus.cat", "Guinovart i Galofre, Carlos"),
                new Estudiant("lmartinez@edu.tecnocampus.cat", "Martínez López, Laura"),
                new Estudiant("pgomez@edu.tecnocampus.cat", "Gómez Ruiz, Pablo"),
                new Estudiant("nruiz@edu.tecnocampus.cat", "Ruiz Torres, Núria")
        );

        d.experteses = List.of(
                new Expertesa("Seguretat i comunicacions"),
                new Expertesa("Internet of Things"),
                new Expertesa("Signal Processing"),
                new Expertesa("Big data i machine Learning"),
                new Expertesa("Solucions Web / Mòbil"),
                new Expertesa("Human Computer Interaction"),
                new Expertesa("Analítica de dades")
        );

        d.slots = buildJuneSlots();

        for (Docent docent : d.docents) {
            for (Disponibilitat slotItem : d.slots) {
                docent.addDisponibilitat(slotItem);
            }
        }

        int[][] expertesesByDocent = {
                {1, 2},      // Boix
                {1, 3, 6},   // Herrero
                {2, 4, 6},   // Sesa
                {2, 4, 5},   // Barberán
                {4, 5, 6},   // Tuset
                {1, 5},      // Moreno
                {6},         // Bonet
                {5, 6},      // López
                {3, 4},      // García
                {1, 2}       // Serra
        };

        for (int i = 0; i < d.docents.size(); i++) {
            Docent docent = d.docents.get(i);
            for (int expId : expertesesByDocent[i]) {
                docent.addExpertesa(d.experteses.get(expId - 1));
            }
        }

        String[] titols = {
                "Sistema IoT per monitorització ambiental",
                "Aplicació web per gestió de reserves",
                "Anàlisi de dades amb machine learning",
                "Sistema de seguretat en xarxes",
                "App mòbil per seguiment esportiu",
                "Interfície HCI per persones grans",
                "Processament de senyal en temps real",
                "Plataforma Big Data per IoT",
                "Web intel·ligent amb recomanador",
                "Sistema distribuït segur"
        };

        int[] tutors = {1,2,3,4,5,6,7,8,9,10};
        int[] expertesesTFG = {2,5,4,1,5,6,3,4,5,1};

        for (int i = 0; i < titols.length; i++) {
            Treball t = new Treball();
            t.setTitle(titols[i]);
            t.setDescription("TFG real " + (i + 1));
            t.setStudent(d.estudiants.get(i));

            Docent tutor = d.docents.get(tutors[i] - 1);
            t.setTutor(tutor);

            t.setExpertesa(d.experteses.get(expertesesTFG[i] - 1));

            d.treballs.add(t);
        }

        d.maxDefensesPerSlot = 1;

        return d;
    }

    private static void printExcelDatasetSummary(NewExampleData d) {
        System.out.println("========== DADES LLEGIDES DEL EXCEL ==========");

        System.out.println("-- Docents (" + d.docents.size() + ") --");
        for (Docent docent : d.docents) {
            System.out.println(describeDocent(docent));
        }

        System.out.println("-- Estudiants (" + d.estudiants.size() + ") --");
        for (Estudiant estudiant : d.estudiants) {
            System.out.println(describeEstudiant(estudiant));
        }

        System.out.println("-- Experteses (" + d.experteses.size() + ") --");
        for (Expertesa expertesa : d.experteses) {
            System.out.println(describeExpertesa(expertesa));
        }

        System.out.println("-- Slots (" + d.slots.size() + ") --");
        for (Disponibilitat slot : d.slots) {
            System.out.println(describeDisponibilitat(slot));
        }

        System.out.println("-- Treballs (" + d.treballs.size() + ") --");
        for (Treball treball : d.treballs) {
            System.out.println(describeTreball(treball));
        }

        System.out.println("=============================================");
    }

    private static String describeDocent(Docent docent) {
        return "Docent{mail='" + docent.getMail() + "', name='" + docent.getName() + "', veteran=" + docent.isVeteran()
                + ", availability=" + docent.getAvailability().size()
                + ", experteses=" + docent.getExperteses().size() + "}";
    }

    private static String describeEstudiant(Estudiant estudiant) {
        return "Estudiant{mail='" + estudiant.getMail() + "', name='" + estudiant.getName() + "'}";
    }

    private static String describeExpertesa(Expertesa expertesa) {
        return "Expertesa{id='" + expertesa.getId() + "', description='" + expertesa.getDescription() + "'}";
    }

    private static String describeDisponibilitat(Disponibilitat disponibilitat) {
        return "Disponibilitat{id=" + disponibilitat.getId() + ", dataDis=" + disponibilitat.getDataDis()
                + ", docents=" + disponibilitat.getDocents().size() + "}";
    }

    private static String describeTreball(Treball treball) {
        String student = treball.getStudent() == null ? "null" : treball.getStudent().getMail();
        String tutor = treball.getTutor() == null ? "null" : treball.getTutor().getMail();
        String expertesa = treball.getExpertesa() == null ? "null" : treball.getExpertesa().getId();
        return "Treball{id=" + treball.getId()
                + ", title='" + treball.getTitle() + "'"
                + ", description='" + treball.getDescription() + "'"
                + ", student='" + student + "'"
                + ", tutor='" + tutor + "'"
                + ", expertesa='" + expertesa + "'}";
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

    private static List<Disponibilitat> buildJuneSlots() {
        List<Disponibilitat> generatedSlots = new ArrayList<>();

        for (int day = 2; day <= 13; day++) {
            // Franja matí: 9:00h a 13:00h
            for (int hour = 9; hour <= 13; hour++) {
                generatedSlots.add(new Disponibilitat(LocalDateTime.of(2026, 6, day, hour, 0)));
            }

            // Franja tarda: 15:00h a 18:00h
            for (int hour = 15; hour <= 18; hour++) {
                generatedSlots.add(new Disponibilitat(LocalDateTime.of(2026, 6, day, hour, 0)));
            }
        }

        return generatedSlots;
    }
}
