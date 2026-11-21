import { Dialog } from '@angular/cdk/dialog';
import { Component, inject, OnInit, signal } from '@angular/core';
import { AddStudent } from '../../../shared/pop-ups/add-student/add-student';
import { StudentService } from '../../../core/services/api/student/student-service';
import { Student } from '../../../core/model/student-model';
import { debounceTime, startWith } from 'rxjs';
import { CommonModule } from '@angular/common';
import { StudentDetails } from '../../../core/model/student-details';
import { ReactiveFormsModule, FormControl } from "@angular/forms";
import { HttpErrorResponse } from '@angular/common/http';
import { MatSnackBar } from '@angular/material/snack-bar';


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
  filteredStudents = signal<Student[]>([]);

  private students = signal<Student[]>([]);
  currentStudent = signal<StudentDetails | null>(null);
  selectedStudentMail = signal<string | null>(null);
  private dialog = inject(Dialog);
  private snackBar = inject(MatSnackBar);

  constructor(private studentService: StudentService) { }

  ngOnInit(): void {
    this.loadStudents();
    this.searchControl.valueChanges.pipe(
      startWith(this.searchControl.value ?? ''),
      debounceTime(300),
    ).subscribe(searchTerm => {
      const term = (searchTerm ?? '').toLowerCase();
      this.filteredStudents.set(this.students().filter(student =>
        student.name.toLowerCase().includes(term) ||
        student.mail.toLowerCase().includes(term)
      ));
    });
  }

  loadStudents(): void {
    this.studentService.getStudents().subscribe(students => {
      this.students.set(students);

      const term = (this.searchControl.value ?? '').toLowerCase();
      this.filteredStudents.set(students.filter(student =>
        student.name.toLowerCase().includes(term) ||
        student.mail.toLowerCase().includes(term)
      ));
    });
  }

  selectStudent(student: Student): void {
    this.selectedStudentMail.set(student.mail);
    this.studentService.getStudentByMail(student.mail).pipe().subscribe(
      (studentDetails: StudentDetails) => {
        this.currentStudent.set(studentDetails);
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

  setApte() {
    const currentStudent = this.currentStudent();
    if (currentStudent) {
      this.studentService.setApte(currentStudent.mail).subscribe(
        (updatedStudent: StudentDetails) => {
          this.currentStudent.set(updatedStudent);
          this.loadStudents();
        },
        (error: HttpErrorResponse) => {
          this.snackBar.open('Error al canviar l\'estat d\'apte: ' + error.message, 'Tancar', {
            duration: 3000,
          });
        }
      );
    }
  }

}
