import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateTribunal } from './update-tribunal';

describe('UpdateTribunal', () => {
  let component: UpdateTribunal;
  let fixture: ComponentFixture<UpdateTribunal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UpdateTribunal]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UpdateTribunal);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
