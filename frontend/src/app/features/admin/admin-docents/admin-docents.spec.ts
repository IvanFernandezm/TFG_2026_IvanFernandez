import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminDocents } from './admin-docents';

describe('AdminDocents', () => {
  let component: AdminDocents;
  let fixture: ComponentFixture<AdminDocents>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminDocents]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminDocents);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
