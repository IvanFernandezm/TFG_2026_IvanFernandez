import { Dialog } from '@angular/cdk/dialog';
import { Component, inject, OnInit, signal } from '@angular/core';
import { AddDocent } from '../../../shared/pop-ups/add-docent/add-docent';

interface Docent {
  email: string;
  name: string;
  specs: string[];
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
      { email: 'imorenoa@tecnocampus.cat', name: 'Immaculada Moreno', specs: ['Desenvolupament d’aplicacions informàtiques'] },
      { email: 'jteodoro@tecnocampus.cat', name: 'Jaume Teodoro', specs: ['Desenvolupament d’aplicacions informàtiques'] },
      { email: 'lina@tecnocampus.cat', name: 'Lina Juan Nadal', specs: ['Desenvolupament d’aplicacions informàtiques', 'Big data'] },
      { email: 'sesa@tecnocampus.cat', name: 'Enric Sesa', specs: ['Desenvolupament d’aplicacions informàtiques', 'Big data'] },
      { email: 'rherrero@tecnocampus.cat', name: 'Rosa Herrero', specs: ['Desenvolupament d’aplicacions informàtiques', 'Big data'] }];
    this.docents.set(mockDocents);
  }
}
