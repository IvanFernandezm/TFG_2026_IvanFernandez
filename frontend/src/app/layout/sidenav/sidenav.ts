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
import { Dialog } from '@angular/cdk/dialog';

import { Router } from '@angular/router';

import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatToolbarModule } from '@angular/material/toolbar';
import { TribunalService } from '../../core/services/api/tribunal/tribunal-service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { TribunalCreate } from '../../shared/pop-ups/tribunal-create/tribunal-create';

interface MenuItem {
  label: string;
  icon: string;
  route?: string;
  children?: MenuItem[];
  action?: 'import-excel' | 'exportar-tribunals' | 'crear-tribunal';
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

  private snackBar = inject(MatSnackBar);
  private readonly dialog = inject(Dialog);

  private readonly router = inject(Router);
  private readonly tribunalService = inject(TribunalService);

  private roleSignal = signal<'ADMIN' | 'DOCENT' | 'ESTUDIANT'>('ADMIN');

  menu = computed<MenuItem[]>(() => {
    switch (this.roleSignal()) {

      case 'ADMIN':
        return [
          { label: 'Inici', icon: 'home', route: '/' },
          {
            label: 'Importar dades', icon: 'file_upload',
            children: [
              { label: 'Des de Excel', icon: 'table_chart', action: 'import-excel' },
            ]
          },
          {
            label: 'Tribunals',
            icon: 'gavel',
            children: [
              { label: 'Planificador', icon: 'calendar_month', route: '/admin/planificador' },
              { label: 'Veure organització', icon: 'visibility', route: '/admin/admin-tribunals' },
              { label: 'Crear tribunal', icon: 'add_circle', action: 'crear-tribunal' },
              { label: 'Exportar tribunals a Excel', icon: 'file_download', action: 'exportar-tribunals' }
            ]
          },
          {
            label: 'Gestió d\'usuaris',
            icon: 'people',
            children: [
              { label: 'Alumnes', icon: 'person', route: '/admin/admin-students' },
              { label: 'Docents', icon: 'badge', route: '/admin/admin-docents' }
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

  onImportExcel(fileInput: HTMLInputElement): void {
    fileInput.click();
  }

  onCreateTribunal(): void {
    this.dialog.open(TribunalCreate, { disableClose: true });
  }

  onExcelSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];

    if (!file) {
      return;
    }

    this.tribunalService.importExcel(file).subscribe({
      next: () => {
        input.value = '';
        this.router.navigateByUrl('/admin/planificador');
        this.snackBar.open('Fitxer Excel importat correctament', 'Tancar', { duration: 3000 });
      },
      error: error => {
        input.value = '';
        this.snackBar.open('Error al importar el fitxer Excel: ' + error.message, 'Tancar', { duration: 3000 });
      }
    });
  }

  onExportarTribunals(): void {
    this.tribunalService.exportAExcel().subscribe({
      next: (blob: Blob) => {
        const url = window.URL.createObjectURL(blob);
        const enlace = document.createElement('a');

        enlace.href = url;
        enlace.download = 'tribunales.xlsx';
        enlace.click();

        window.URL.revokeObjectURL(url);
      },
      error: (error) => {
        console.error('Error al exportar a Excel:', error);
      }
    });
  }

}
