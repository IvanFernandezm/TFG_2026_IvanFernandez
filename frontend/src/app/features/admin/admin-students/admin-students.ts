import { Dialog } from '@angular/cdk/dialog';
import { Component, inject, OnInit, signal } from '@angular/core';
import { AddStudent } from '../../../shared/pop-ups/add-student/add-student';
import { StudentService } from '../../../core/services/api/student/student-service';
import { Student } from '../../../core/model/student-model';
import { Observable, debounceTime } from 'rxjs';
import { CommonModule } from '@angular/common';
import { StudentDetails } from '../../../core/model/student-details';
import { ReactiveFormsModule, FormControl } from "@angular/forms";
import { VoidExpr } from '@angular/compiler';


@Component({
  selector: 'app-admin-students',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './admin-students.html',
  styleUrl: './admin-students.scss',
})
export class AdminStudents implements OnInit {

  //Variables necesaries per al buscador d'alumnes
  searchControl = new FormControl('');
  filteredStudents: Student[] = [];

  students: Student[] = [];
  currentStudent: StudentDetails | null = null;
  private dialog = inject(Dialog);

  constructor(private studentService: StudentService) { }

  ngOnInit(): void {
    this.loadStudents();
    this.searchControl.valueChanges.pipe(
      debounceTime(300)
    ).subscribe(searchTerm => {
      const term = (searchTerm ?? '').toLowerCase();
      this.filteredStudents = this.students.filter(student =>
        student.name.toLowerCase().includes(term) ||
        student.mail.toLowerCase().includes(term)
      );
    });
  }

  loadStudents(): void {
    this.studentService.getStudents().subscribe(students => {
      this.students = students;
      this.filteredStudents = students;
    }
    );
  }

  selectStudent(student: Student): void {
    this.studentService.getStudentByMail(student.mail).pipe().subscribe(
      (studentDetails: StudentDetails) => {
        this.currentStudent = studentDetails;
      },
      (error) => {
        console.error('Error al obtenir detalls de l\'alumne: ' + student.name, error);
      }
    );
  }

  deleteStudent() {
    throw new Error('Method not implemented.');
  }

  addStudent() {
    this.dialog.open(AddStudent, { disableClose: true });
  }

}
