import { Component } from '@angular/core';

interface Student {
  email: string;
  name: string;
  tfg: string;
  spec: string;
}

@Component({
  selector: 'app-admin-students',
  imports: [],
  templateUrl: './admin-students.html',
  styleUrl: './admin-students.scss',
})
export class AdminStudents {

}
