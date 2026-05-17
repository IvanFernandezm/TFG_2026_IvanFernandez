package org.example.tribunalsbackend.Controller;

import org.example.tribunalsbackend.Config.Exceptions.EntityNotFoundException;
import org.example.tribunalsbackend.Domain.Admin;
import org.example.tribunalsbackend.Domain.Docent;
import org.example.tribunalsbackend.Domain.Estudiant;
import org.example.tribunalsbackend.Persistence.AdminRepository;
import org.example.tribunalsbackend.Persistence.DocentRepository;
import org.example.tribunalsbackend.Persistence.EstudiantRepository;
import org.springframework.stereotype.Service;

@Service
public class UserController {
    AdminRepository adminRepository;
    DocentRepository docentRepository;
    EstudiantRepository estudiantRepository;

    public UserController(AdminRepository adminRepository, DocentRepository docentRepository, EstudiantRepository estudiantRepository) {
        this.adminRepository = adminRepository;
        this.docentRepository = docentRepository;
        this.estudiantRepository = estudiantRepository;
    }

    public String signIn(String mail) throws Exception {
        if (adminRepository.findById(mail).isPresent()) {
            return "admin";
        }
        else if (docentRepository.findById(mail).isPresent()) {
            return "docent";
        }
        else if (estudiantRepository.findById(mail).isPresent()) {
            return "estudiant";
        }
        throw new EntityNotFoundException("Usuari amb mail: " + mail + " no registrat!");
    }

    public void register(String mail, String name, String userType) {
        switch (userType.toLowerCase()) {
            case "admin":
                adminRepository.save(new Admin(mail, name));
                break;
            case "docent":
                docentRepository.save(new Docent(mail, name));
                break;
            case "estudiant":
                estudiantRepository.save(new Estudiant(mail, name));
                break;
                default:
                    throw new IllegalArgumentException("Tipus d'usuari no vàlid: " + userType);

        }
    }

    public void deleteUser(String mail, String userType) {
        switch (userType.toLowerCase()) {
            case "admin":
                adminRepository.delete(adminRepository.findById(mail).orElseThrow(() -> new EntityNotFoundException("Admin amb mail: " + mail + " no registrat com a admin!")));
                break;
            case "docent":
                docentRepository.delete(docentRepository.findById(mail).orElseThrow(() -> new EntityNotFoundException("Docent amb mail: " + mail + " no registrat coma a docent!")));
                break;
            case "estudiant":
                estudiantRepository.delete(estudiantRepository.findById(mail).orElseThrow(() -> new EntityNotFoundException("Estudiant amb mail: " + mail + " no registrat com a estudiant!")));
                break;
            default:
                throw new IllegalArgumentException("Tipus d'usuari no vàlid: " + userType);
        }

    }
}
