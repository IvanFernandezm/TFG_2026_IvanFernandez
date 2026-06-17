import { Dialog } from '@angular/cdk/dialog';
import { Component, inject, OnInit, signal } from '@angular/core';
import { AddStudent } from '../../../shared/pop-ups/add-student/add-student';
import { StudentService } from '../../../core/services/api/student/student-service';
import { Student } from '../../../core/model/student-model';
import { Observable } from 'rxjs';
import { CommonModule } from '@angular/common';
import { StudentDetails } from '../../../core/model/student-details';


@Component({
  selector: 'app-admin-students',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-students.html',
  styleUrl: './admin-students.scss',
})
export class AdminStudents implements OnInit {

  students!: Observable<Student[]>;
  currentStudent: StudentDetails | null = null;
  private dialog = inject(Dialog);

  constructor(private studentService: StudentService) { }

  protected openModal() {
    this.dialog.open(AddStudent,{disableClose: true});
  }

  ngOnInit(): void {
    this.loadStudents();
  }

  loadStudents() {
    this.students = this.studentService.getStudents();

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
    this.openModal();
  }

}
