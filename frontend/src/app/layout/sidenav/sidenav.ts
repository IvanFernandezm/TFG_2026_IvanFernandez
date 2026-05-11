import {
  Component,
  Input,
  Output,
  EventEmitter,
  inject,
  computed,
  signal,
  OnChanges,
  SimpleChanges
} from '@angular/core';

import { Router } from '@angular/router';

import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatToolbarModule } from '@angular/material/toolbar';

interface MenuItem {
  label: string;
  icon: string;
  route?: string;
  children?: MenuItem[];
}

@Component({
  selector: 'app-sidenav',
  imports: [
    MatListModule,
    MatIconModule,
    MatExpansionModule,
    MatToolbarModule
  ],
  templateUrl: './sidenav.html',
  styleUrl: './sidenav.scss',
})
export class Sidenav implements OnChanges {
  @Input() role!: 'ADMIN' | 'DOCENT' | 'ESTUDIANT';
  @Output() navigate = new EventEmitter<void>();

  private readonly router = inject(Router);

  private roleSignal = signal<'ADMIN' | 'DOCENT' | 'ESTUDIANT'>('ADMIN');

  menu = computed<MenuItem[]>(() => {
    switch (this.roleSignal()) {

      case 'ADMIN':
        return [
          {
            label: 'Gestió d\'usuaris',
            icon: 'people',
            children: [
              { label: 'Alumnes', icon: 'person', route: '/admin/admin-students' },
              { label: 'Docents', icon: 'badge', route: '/admin/admin-docents' }
            ]
          },
          {
            label: 'Tribunals',
            icon: 'gavel',
            children: [
              { label: 'Planificador', icon: 'calendar_month', route: '/admin/planificador' },
              { label: 'Veure organització', icon: 'visibility', route: '/admin/adjudicacio' }
            ]
          },
          {
            label: 'Importar dades', icon: 'file_upload',
            children: [
              { label: 'Des de Excel', icon: 'table_chart', route: '/admin/import' },
            ]
          }
        ];

      case 'DOCENT':
        return [
          { label: 'Inici', icon: 'home', route: '/docent' },
          {
            label: 'Gestió',
            icon: 'work',
            children: [
              { label: 'Disponibilitat', icon: 'schedule', route: '/docent/disponibilitat' },
              { label: 'Tutories', icon: 'school', route: '/docent/tutorias' },
              { label: 'Tribunals', icon: 'groups', route: '/docent/tribunales' }
            ]
          }
        ];

      case 'ESTUDIANT':
        return [
          { label: 'Inici', icon: 'home', route: '/estudiant' },
          {
            label: 'Accions',
            icon: 'assignment',
            children: [
              { label: 'Justificar absència', icon: 'assignment_late', route: '/estudiant/justificar' }
            ]
          }
        ];
    }
  });

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['role']) {
      this.roleSignal.set(this.role);
    }
  }

  onItemClick(route?: string): void {
    if (route) {
      void this.router.navigateByUrl(route);
    }

    this.navigate.emit();
  }

}
