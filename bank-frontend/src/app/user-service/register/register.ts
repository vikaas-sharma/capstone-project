import { Component } from '@angular/core';
import { Auth } from '../../services/auth';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-register',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class Register {

  user = { username: '', password: '', address: '' };

  constructor(private authService: Auth) {}

  onSubmit() {
    if (!this.user.username || !this.user.password || !this.user.address) {
    alert('Please fill in all required fields!');
    return;
  }
    this.authService.register(this.user).subscribe(res => {
      alert('User registered successfully!');
    });
  }
}
