import { TestBed } from '@angular/core/testing';

import { State } from './state';

describe('State', () => {
  let service: State;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(State);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
