import { Component, OnInit, signal } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { GeneralInfo } from '../../core/model/genInfo-model';
import { GeneralService } from '../../core/services/api/general/general-service';

@Component({
  selector: 'app-welcome',
  imports: [MatIconModule],
  templateUrl: './welcome.html',
  styleUrl: './welcome.scss',
})
export class Welcome implements OnInit {
  generalInfo = signal<GeneralInfo | null>(null);
  loading = signal(true);
  errorMessage = signal<string | null>(null);

  constructor(private readonly generalService: GeneralService) {}

  ngOnInit(): void {
    this.generalService.getGeneralInfo().subscribe({
      next: info => {
        this.generalInfo.set(info);
        this.loading.set(false);
      },
      error: error => {
        console.error('Error al obtener la información general', error);
        this.errorMessage.set('No se pudo cargar la información general.');
        this.loading.set(false);
      },
    });
  }
}
