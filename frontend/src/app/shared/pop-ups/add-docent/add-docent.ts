import { DialogRef } from '@angular/cdk/dialog';
import { Component, inject, signal } from '@angular/core';

interface Docent {
  email: string;
  name: string;
  spec: string;
}

@Component({
  selector: 'app-add-docent',
  imports: [],
  templateUrl: './add-docent.html',
  styleUrl: './add-docent.scss',
})
export class AddDocent {
  private dialogRef = inject(DialogRef);

  newDocent = signal<Docent | null>(null);

  submit() {
    //TODO criada a endpoint
    this.dialogRef.close();
  }
  close() {
    this.dialogRef.close();
  }
}
