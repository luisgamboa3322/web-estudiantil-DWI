import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StudentCursoDetalleComponent } from './student-curso-detalle.component';

describe('StudentCursoDetalleComponent', () => {
  let component: StudentCursoDetalleComponent;
  let fixture: ComponentFixture<StudentCursoDetalleComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StudentCursoDetalleComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StudentCursoDetalleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
