import { Dialog } from '@angular/cdk/dialog';
import { Component, inject, OnInit, signal } from '@angular/core';
import { AddStudent } from '../../../shared/pop-ups/add-student/add-student';

interface Student {
  email: string;
  name: string;
  tfg: string;
  spec: string;
}

@Component({
  selector: 'app-admin-students',
  standalone: true,
  imports: [],
  templateUrl: './admin-students.html',
  styleUrl: './admin-students.scss',
})
export class AdminStudents implements OnInit {

  students = signal<Student[]>([]);
  currentStudent = signal<Student | null>(null);
  private dialog = inject(Dialog);

  protected openModal() {
    this.dialog.open(AddStudent);
  }

  ngOnInit(): void {
    this.loadStudents();
  }

  loadStudents() {
    // Aquí es on es faria la crida al backend per obtenir la llista d'alumnes
    const mockStudents: Student[] = [
      { email: 'ifernandezm@edu.tencoampus.cat', name: 'Fernandez Muñoz, Ivan', tfg: 'Calendari de defenses de TFG', spec: 'Solucions web / mòbil' },
      { email: 'jgarcia@edu.tencoampus.cat', name: 'Garcia Lopez, Juan', tfg: 'Campanya de phising ètic', spec: 'Infraestructures (Seguretat i comunicacions)' },
      { email: 'chernandez@edu.tencoampus.cat', name: 'Hernández Ferrer, Carles', tfg: 'Web app de seguiment de la cadena de subministrament amb tecnologia blockchain', spec: 'Solucions web / mòbil' },
      { email: 'amartinezsa@edu.tecnocampus.cat', name: 'Martínez Sánchez, Ana', tfg: 'Software de monitorització remota de gasos (Sensotran)', spec: 'Desenvolupament d’aplicacions informàtiques' },
      { email: 'nroset@edu.tecnocampus.cat', name: 'Roset Martínez, Núria', tfg: 'Sistema de recomanació de rutes turístiques basat en preferències personals', spec: 'Solucions web / mòbil' },
    ];
    this.students.set(mockStudents);
  }

  selectStudent(student: Student): void {
    this.currentStudent.set(student);
  }

  deleteStudent() {
    throw new Error('Method not implemented.');
  }

  addStudent() {
    this.openModal();
  }

}
