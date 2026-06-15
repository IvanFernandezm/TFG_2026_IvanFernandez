import { TestBed } from '@angular/core/testing';

import { DocentService } from './docent-service';

describe('DocentService', () => {
  let service: DocentService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DocentService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
