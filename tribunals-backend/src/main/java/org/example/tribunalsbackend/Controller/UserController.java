package org.example.tribunalsbackend.Controller;

import org.example.tribunalsbackend.Domain.Abstracts.AppUser;
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
        if(adminRepository.findAdminByMail(mail)!=null){
            return "admin";
        }
        if(docentRepository.findDocentByMail(mail)!=null){
            return "docent";
        }
        if(estudiantRepository.findEstudiantByMail(mail)!=null){
            return "estudiant";
        }
        throw new Exception("Usuari amb mail: "+mail + " no registrat!");
    }

    public void register(AppUser appUser, String userType) {
    }
}
