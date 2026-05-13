import { Dialog } from '@angular/cdk/dialog';
import { Component, inject, OnInit, signal } from '@angular/core';
import { AddDocent } from '../../../shared/pop-ups/add-docent/add-docent';

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
export class AdminDocents implements OnInit {

  private dialog = inject(Dialog);

  protected openModal() {
    this.dialog.open(AddDocent, { disableClose: true });
  }

  ngOnInit(): void {
    this.loadDocents();
  }

  currentDocent = signal<Docent | null>(null);
  docents = signal<Docent[]>([]);

  deleteDocent() {
    throw new Error('Method not implemented.');
  }
  addDocent() {
    this.openModal();
  }
  selectDocent(Docent: Docent) {
    this.currentDocent.set(Docent);
  }
  loadDocents() {
    //Crida API per obtenir docents
    const mockDocents: Docent[] = [
      { email: 'imorenoa@tecnocampus.cat', name: 'Immaculada Moreno', spec: 'Desenvolupament d’aplicacions informàtiques' },
      { email: 'jteodoro@tecnocampus.cat', name: 'Jaume Teodoro', spec: 'Desenvolupament d’aplicacions informàtiques' },
      { email: 'lina@tecnocampus.cat', name: 'Lina Juan Nadal', spec: 'Desenvolupament d’aplicacions informàtiques' },
      { email: 'sesa@tecnocampus.cat', name: 'Enric Sesa', spec: 'Desenvolupament d’aplicacions informàtiques' },
      { email: 'rherrero@tecnocampus.cat', name: 'Rosa Herrero', spec: 'Desenvolupament d’aplicacions informàtiques' }];
    this.docents.set(mockDocents);
  }
}
