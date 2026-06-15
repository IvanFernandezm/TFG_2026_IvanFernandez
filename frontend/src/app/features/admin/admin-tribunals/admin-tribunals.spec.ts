import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminTribunals } from './admin-tribunals';

describe('AdminTribunals', () => {
  let component: AdminTribunals;
  let fixture: ComponentFixture<AdminTribunals>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminTribunals]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminTribunals);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
