import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VeteranConfirm } from './veteran-confirm';

describe('VeteranConfirm', () => {
  let component: VeteranConfirm;
  let fixture: ComponentFixture<VeteranConfirm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VeteranConfirm]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VeteranConfirm);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
