import { Component } from '@angular/core';
import { Auth } from '../../services/auth';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {
  credentials = {
    username: '',
    password: ''
  };

  constructor(
    private authService: Auth,
    private router: Router  // Properly inject Router service
  ) {}

onLogin() {
  this.authService.login(this.credentials).subscribe({
    next: (res: any) => {
      console.log('Full Login Response:', res);
      
      localStorage.setItem('token', res.token);
      
      // Token se userId extract karo
      try {
        const tokenPayload = JSON.parse(atob(res.token.split('.')[1]));
        console.log('Token Payload:', tokenPayload);
        
        const userId = tokenPayload.userId || tokenPayload.sub || tokenPayload.id;
        if (userId) {
          localStorage.setItem('userId', userId.toString());
          console.log('UserId stored:', userId);
        }
      } catch (error) {
        console.error('Token decode error:', error);
      }
      
      alert('Login successful!');
      this.router.navigate(['/dashboard']);
    },
    error: (error) => {
      console.error('Login failed:', error);
      alert('Login failed. Please check your credentials.');
    }
  });
}
}