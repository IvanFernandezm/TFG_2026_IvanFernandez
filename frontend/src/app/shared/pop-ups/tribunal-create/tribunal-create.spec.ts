import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TribunalCreate } from './tribunal-create';

describe('TribunalCreate', () => {
  let component: TribunalCreate;
  let fixture: ComponentFixture<TribunalCreate>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TribunalCreate]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TribunalCreate);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
