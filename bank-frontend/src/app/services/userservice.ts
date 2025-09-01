import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface User {
  id?: number;
  username: string;
  password?: string; // Optional for updates (you might not want to show/edit password)
  address: string;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private baseUrl = 'http://localhost:9090/api/users';

  constructor(private http: HttpClient) {}

  private getHeaders(): { headers: HttpHeaders } {
    const token = localStorage.getItem('token');
    console.log('Token for user service:', token);
    
    if (!token) {
      console.error('No token found for user service!');
    }
    return {
      headers: new HttpHeaders({ 
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json' 
      })
    };
  }

  // Get user by ID
  getUserById(userId: number): Observable<User> {
    console.log('Getting user by ID:', userId);
    const url = `${this.baseUrl}/${userId}`;
    console.log('Get user URL:', url);
    
    return this.http.get<User>(url, this.getHeaders());
  }

  // Update user
  updateUser(userId: number, user: Partial<User>): Observable<User> {
    console.log('Updating user with ID:', userId);
    console.log('User data:', user);
    
    const url = `${this.baseUrl}/${userId}`;
    console.log('Update user URL:', url);
    
    // Remove id field from the request body if present
    const { id, ...userData } = user;
    
    return this.http.put<User>(url, userData, this.getHeaders());
  }
}