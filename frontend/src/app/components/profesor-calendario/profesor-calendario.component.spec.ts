import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProfesorCalendarioComponent } from './profesor-calendario.component';

describe('ProfesorCalendarioComponent', () => {
  let component: ProfesorCalendarioComponent;
  let fixture: ComponentFixture<ProfesorCalendarioComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProfesorCalendarioComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProfesorCalendarioComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
