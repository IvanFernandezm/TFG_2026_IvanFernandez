import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddDocent } from './add-docent';

describe('AddDocent', () => {
  let component: AddDocent;
  let fixture: ComponentFixture<AddDocent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddDocent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddDocent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
