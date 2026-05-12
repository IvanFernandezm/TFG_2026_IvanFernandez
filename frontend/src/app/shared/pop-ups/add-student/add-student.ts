import { DialogRef } from '@angular/cdk/dialog';
import { Component, inject, signal } from '@angular/core';

interface Student {
  email: string;
  name: string;
  tfg: string;
  spec: string;
}

@Component({
  selector: 'app-add-student',
  imports: [],
  templateUrl: './add-student.html',
  styleUrl: './add-student.scss',
})
export class AddStudent {
  private dialogRef = inject(DialogRef);

  newStudent = signal<Student | null>(null);
  submit() {
    //TODO criada a endpoint
    this.dialogRef.close();
  }
  close() {
    this.dialogRef.close();
  }

}
