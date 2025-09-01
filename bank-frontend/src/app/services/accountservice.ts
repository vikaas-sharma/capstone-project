import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface Account {
  accountId?: number; // Made optional with proper type
  id?: number;
  accountType: string;
  accountBalance: number;
  secretPassword: string;
  userId?: number;
}

export interface TransferRequest {
  fromId: number;
  toId: number;
  amount: number;
}

@Injectable({
  providedIn: 'root'
})
export class Accountservice {
  private baseUrl = 'http://localhost:9090/api/accounts';

  constructor(private http: HttpClient) {}

  private getHeaders(): { headers: HttpHeaders } {
    const token = localStorage.getItem('token');
    console.log('Token for account service:', token);
    
    if (!token) {
      console.error('No token found for account service!');
    }
    return {
      headers: new HttpHeaders({ 
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json' 
      })
    };
  }

  createAccount(userId: number, account: Partial<Account>): Observable<any> {
    console.log('Creating account for userId:', userId);
    console.log('Account data:', account);

    const headers = this.getHeaders();
    console.log('Headers for createAccount:', headers);

    const url = `${this.baseUrl}/${userId}`;
    console.log('Create account URL:', url);

    // Ensure we don't send accountId for new accounts
    const { accountId, id, userId: accountUserId, ...accountData } = account;
    
    return this.http.post(`${this.baseUrl}/${userId}`, accountData, this.getHeaders());
  }

  
  getUserAccounts(userId: number): Observable<Account[]> {
    return this.http.get<Account[]>(`${this.baseUrl}/user/${userId}`, this.getHeaders());
  }

  transferMoney(fromId: number, toId: number, amount: number): Observable<any> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.http.post<any>(
      `${this.baseUrl}/transfer/${fromId}/${toId}/${amount}`, 
      {}, 
      { headers }
    );
  }
}