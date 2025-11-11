import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ErrorAccesoDenegadoComponent } from './error-acceso-denegado.component';

describe('ErrorAccesoDenegadoComponent', () => {
  let component: ErrorAccesoDenegadoComponent;
  let fixture: ComponentFixture<ErrorAccesoDenegadoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ErrorAccesoDenegadoComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ErrorAccesoDenegadoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
