package org.example.tribunalsbackend.Api;

import org.example.tribunalsbackend.Api.DTO.EstudiantDTO;
import org.example.tribunalsbackend.Api.DTO.TFGEstudiantDTO;
import org.example.tribunalsbackend.Controller.TFGEstudiantsController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/estudiant")
public class TFGEstudiantRestController {
    //REST CONTROLLER PER A ESTUDIANTS + ELS SEUS TFG

    private final TFGEstudiantsController estudiantsService;

    public TFGEstudiantRestController(TFGEstudiantsController estudiantsService) {
        this.estudiantsService = estudiantsService;
    }

    @GetMapping("/tfg")
    public ResponseEntity<TFGEstudiantDTO> getTFGEstudiant(@RequestParam String mail) throws Exception {
        TFGEstudiantDTO tfgEstudiant = estudiantsService.getTFGEstudiant(mail);
        return ResponseEntity.ok(tfgEstudiant);
    }

    @GetMapping("/all")
    public ResponseEntity<List<EstudiantDTO>> getAllEstudiants() throws Exception {
        List<EstudiantDTO> estudiants = estudiantsService.getAllEstudiants();
        return ResponseEntity.ok(estudiants);
    }
}
