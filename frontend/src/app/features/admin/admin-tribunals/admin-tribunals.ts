import { Component, OnInit } from '@angular/core';
import { TribunalService } from '../../../core/services/api/tribunal/tribunal-service';
import { Tribunal } from '../../../core/model/tribunal-model';
import { Observable } from 'rxjs';
import { CommonModule } from '@angular/common';

export interface orgTibunals {
  Date: Date;
  tribunals: Tribunal[];
}

@Component({
  selector: 'app-admin-tribunals',
  imports: [CommonModule],
  templateUrl: './admin-tribunals.html',
  styleUrl: './admin-tribunals.scss',
})


export class AdminTribunals implements OnInit {
  tribunals!: Observable<Tribunal[]>
  selected: Tribunal | null = null;
  constructor(private tribunalService: TribunalService) { }

  ngOnInit(): void {
    this.tribunals = this.tribunalService.getTribunals();
  }

}
