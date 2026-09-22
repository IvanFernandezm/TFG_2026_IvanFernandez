import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateDocent } from './update-docent';

describe('UpdateDocent', () => {
  let component: UpdateDocent;
  let fixture: ComponentFixture<UpdateDocent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UpdateDocent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UpdateDocent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
