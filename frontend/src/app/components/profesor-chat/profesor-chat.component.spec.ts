import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProfesorChatComponent } from './profesor-chat.component';

describe('ProfesorChatComponent', () => {
  let component: ProfesorChatComponent;
  let fixture: ComponentFixture<ProfesorChatComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProfesorChatComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProfesorChatComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
