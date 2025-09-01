import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface Transaction {
  id?: number;
  frmAccountId: number;
  toAccountId: number;
  transactionType: string;
  amount: number;
  userId: number;
  timestamp?: string;
}

@Injectable({
  providedIn: 'root'
})
export class Transactionservice {
 
  private baseUrl = 'http://localhost:9090/api/transactions'; // API Gateway port

  constructor(private http: HttpClient) {}

  // Helper method to get headers with Authorization token
  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    console.log('Token from localStorage:', token); // Debug log
    
    if (!token) {
      console.error('No token found in localStorage!');
    }
    
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  // Add Transaction
  addTransaction(transaction: Transaction): Observable<any> {
    console.log('Adding transaction:', transaction);
    const headers = this.getAuthHeaders();
    console.log('Headers for addTransaction:', headers);
    
    return this.http.post(this.baseUrl, transaction, { headers });
  }

  // Get Transactions by User and Account
  getTransactions(userId: number, accountId: number): Observable<Transaction[]> {
    console.log(`Getting transactions for userId: ${userId}, accountId: ${accountId}`);
    const headers = this.getAuthHeaders();
    console.log('Headers for getTransactions:', headers);
    
    const url = `${this.baseUrl}/user/${userId}/account/${accountId}`;
    console.log('Request URL:', url);
    
    return this.http.get<Transaction[]>(url, { headers });
  }
}