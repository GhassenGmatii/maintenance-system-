import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from './header/header.component';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [RouterOutlet, HeaderComponent],
  template: `
    <app-header></app-header>
    <main class="main-content">
      <router-outlet></router-outlet>
    </main>
  `,
  styles: [`
    .main-content {
      margin-top: 62px;
      padding: 32px 32px;
      max-width: 1400px;
      margin-left: auto;
      margin-right: auto;
      min-height: calc(100vh - 62px);
    }

    @media (max-width: 768px) {
      .main-content { padding: 20px 16px; }
    }
  `]
})
export class LayoutComponent {}
