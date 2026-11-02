import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DIALOG_DATA, DialogRef } from '@angular/cdk/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';

import { TribunalService } from '../../../core/services/api/tribunal/tribunal-service';
import { TribunalDelete } from './tribunal-delete';

describe('TribunalDelete', () => {
  let component: TribunalDelete;
  let fixture: ComponentFixture<TribunalDelete>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TribunalDelete],
      providers: [
        { provide: DialogRef, useValue: { close: () => undefined } },
        { provide: DIALOG_DATA, useValue: 'TFG de prova' },
        { provide: MatSnackBar, useValue: { open: () => undefined } },
        { provide: TribunalService, useValue: { deleteTribunal: () => undefined } },
      ],
    })
    .compileComponents();

    fixture = TestBed.createComponent(TribunalDelete);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
