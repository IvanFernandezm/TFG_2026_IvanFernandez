import { Component, signal } from '@angular/core';

interface Docent {
  email: string;
  name: string;
  spec: string;
}
@Component({
  selector: 'app-admin-docents',
  imports: [],
  templateUrl: './admin-docents.html',
  styleUrl: './admin-docents.scss',
})
export class AdminDocents {

  currentDocent = signal<Docent | null>(null);
  docents = signal<Docent[]>([]);

  deleteDocent() {
    throw new Error('Method not implemented.');
  }
  addDocent() {
    throw new Error('Method not implemented.');
  }
  selectDocent(Docent: Docent) {
    this.currentDocent.set(Docent);
  }

}
