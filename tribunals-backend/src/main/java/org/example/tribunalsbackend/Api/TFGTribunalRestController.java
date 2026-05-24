package org.example.tribunalsbackend.Api;

import org.example.tribunalsbackend.Controller.DataImportController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/tribunal")
public class TFGTribunalRestController {
    private final DataImportController dataImportController;

    public TFGTribunalRestController(DataImportController dataImportController) {
        this.dataImportController = dataImportController;
    }

    @PostMapping("/import")
    public ResponseEntity<String> importData(@RequestParam("file") MultipartFile excel) throws Exception {
        this.dataImportController.importExcel(excel);
        return ResponseEntity.ok("Importació correcta");
    }
}
