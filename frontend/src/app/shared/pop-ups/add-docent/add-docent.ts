import { DialogRef } from '@angular/cdk/dialog';
import { Component, inject, signal } from '@angular/core';
import { MatCheckboxModule } from '@angular/material/checkbox';

interface Docent {
  email: string;
  name: string;
  spec: string[];
}

@Component({
  selector: 'app-add-docent',
  imports: [MatCheckboxModule],
  templateUrl: './add-docent.html',
  styleUrl: './add-docent.scss',
})
export class AddDocent {
  private dialogRef = inject(DialogRef);

  newDocent = signal<Docent | null>(null);
  selectedSpecs = signal<string[]>([]);

  specOptions = [
    'Analitica de dades',
    'Big data i Macjine learning',
    'Desenvolupament d\'aplicacions informàtiques',
    'Infraestructures (Seguretat i comunicacions)',
    'Solucions web / mòbil',
    'Emprenedoria i innoviació',
    'Tractament del senyal'
  ];

  toggleSpec(spec: string) {
    const current = this.selectedSpecs();
    if (current.includes(spec)) {
      this.selectedSpecs.set(current.filter(s => s !== spec));
    } else {
      this.selectedSpecs.set([...current, spec]);
    }
  }

  isSpecSelected(spec: string): boolean {
    return this.selectedSpecs().includes(spec);
  }

  submit() {
    //TODO criada a endpoint
    this.dialogRef.close();
  }
  close() {
    this.dialogRef.close();
  }
}
