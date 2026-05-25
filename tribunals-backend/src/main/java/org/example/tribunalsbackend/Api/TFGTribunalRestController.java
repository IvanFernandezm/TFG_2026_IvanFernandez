package org.example.tribunalsbackend.Api;

import org.example.tribunalsbackend.Api.DTO.TribunalDTO;
import org.example.tribunalsbackend.Controller.DataImportController;
import org.example.tribunalsbackend.Controller.TFGTribunalController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/tribunal")
public class TFGTribunalRestController {
    private final DataImportController dataImportController;
    private final TFGTribunalController tribunalController;

    public TFGTribunalRestController(DataImportController dataImportController, TFGTribunalController tfgTribunalRestController) {
        this.dataImportController = dataImportController;
        this.tribunalController = tfgTribunalRestController;
    }

    @PostMapping("/import")
    public ResponseEntity<String> importData(@RequestParam("file") MultipartFile excel) throws Exception {
        this.dataImportController.importExcel(excel);
        return ResponseEntity.ok("Importació correcta");
    }

    @GetMapping("/Organitzar")
    public ResponseEntity<List<TribunalDTO>> organitzarTribunals() {
        List<TribunalDTO> tribunals = this.dataImportController.organitzarTribunals();
        return ResponseEntity.ok(tribunals);
    }

    @GetMapping("/all")public ResponseEntity<List<TribunalDTO>> allTribunals() {
        List<TribunalDTO> tribunals = this.tribunalController.getAllTribunals();
        return ResponseEntity.ok(tribunals);
    }
}
