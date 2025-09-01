// import { Component, OnInit } from '@angular/core';
// import { Router } from '@angular/router';
// import { CommonModule } from '@angular/common';
// import { Account, Accountservice } from '../../services/accountservice';
// import { Transaction, Transactionservice } from '../../services/transactionservice';
// import { FormsModule } from '@angular/forms';
// import { User, UserService } from '../../services/userservice'; // ⬅️ Import user service

// @Component({
//   selector: 'app-dashboard',
//   imports: [CommonModule, FormsModule],
//   templateUrl: './dashboard.html',
//   styleUrl: './dashboard.css'
// })
// export class Dashboard implements OnInit {
//   userId!: number;
//   activeTab: string = 'accounts';

//   // Account related data
//   accounts: Account[] = [];
//   newAccount: Account = {
//     accountType: '',
//     accountBalance: 0,
//     secretPassword: '',
//     accountId: undefined
//   };

//   // Transfer related data
//   transferData = {
//     fromId: 0,
//     toId: 0,
//     amount: 0
//   };

//   // Transaction related data
//   transactions: Transaction[] = [];
//   selectedAccountId: number | null = null;

//   // User profile
//   currentUser: User | null = null;
//   editUser: Partial<User> = {};
//   isEditingProfile: boolean = false;

//   constructor(
//     private router: Router,
//     private accountService: Accountservice,
//     private transactionService: Transactionservice,
//     private userService: UserService // ⬅️ Inject user service
//   ) {}

//   ngOnInit() {
//     const token = localStorage.getItem('token');
//     if (!token) {
//       this.router.navigate(['/login']);
//       return;
//     }

//     const storedUserId = localStorage.getItem('userId');
//     if (storedUserId) {
//       this.userId = parseInt(storedUserId);
//       this.loadUserAccounts();
//       this.loadUserProfile(); // ⬅️ load profile
//     }
//   }

//   // ===== User Profile =====
//   loadUserProfile() {
//     this.userService.getUserById(this.userId).subscribe({
//       next: (user) => {
//         this.currentUser = user;
//       },
//       error: (err) => {
//         console.error('Error loading profile:', err);
//         alert('Could not load user profile');
//       }
//     });
//   }

//   startEditProfile() {
//     this.editUser = { ...this.currentUser }; // copy
//     this.isEditingProfile = true;
//   }

//   cancelEditProfile() {
//     this.isEditingProfile = false;
//     this.editUser = {};
//   }

//   onUpdateProfile() {
//     if (!this.editUser.username || !this.editUser.address) {
//       alert('Please fill all fields');
//       return;
//     }

//     this.userService.updateUser(this.userId, this.editUser).subscribe({
//       next: (updatedUser) => {
//         alert('Profile updated successfully!');
//         this.currentUser = updatedUser;
//         this.isEditingProfile = false;
//       },
//       error: (err) => {
//         console.error('Error updating profile:', err);
//         alert('Profile update failed.');
//       }
//     });
//   }

//   // ===== Accounts =====
//   loadUserAccounts() {
//     this.accountService.getUserAccounts(this.userId).subscribe({
//       next: (accounts) => {
//         this.accounts = accounts;
//       },
//       error: (err) => {
//         console.error('Error loading accounts:', err);
//         alert('Error loading accounts.');
//       }
//     });
//   }

//   onCreateAccount() {
//     if (!this.newAccount.accountType || !this.newAccount.accountBalance || !this.newAccount.secretPassword) {
//       alert('Please fill all fields');
//       return;
//     }

//     const accountToCreate = {
//       accountType: this.newAccount.accountType,
//       accountBalance: this.newAccount.accountBalance,
//       secretPassword: this.newAccount.secretPassword
//     };

//     this.accountService.createAccount(this.userId, accountToCreate).subscribe({
//       next: () => {
//         alert('Account created successfully!');
//         this.newAccount = { accountType: '', accountBalance: 0, secretPassword: '', accountId: undefined };
//         this.loadUserAccounts();
//         this.activeTab = 'accounts';
//       },
//       error: (err) => {
//         console.error('Error creating account:', err);
//         alert('Account creation failed.');
//       }
//     });
//   }

//   // ===== Transfer =====
//   onTransferMoney() {
//     if (!this.transferData.fromId || !this.transferData.toId || !this.transferData.amount) {
//       alert('Please fill all fields');
//       return;
//     }
//     if (this.transferData.fromId === this.transferData.toId) {
//       alert('Cannot transfer to the same account');
//       return;
//     }

//     this.accountService.transferMoney(this.transferData.fromId, this.transferData.toId, this.transferData.amount)
//       .subscribe({
//         next: () => {
//           alert('Money transferred successfully!');
//           const transaction: Transaction = {
//             frmAccountId: this.transferData.fromId,
//             toAccountId: this.transferData.toId,
//             transactionType: 'TRANSFER',
//             amount: this.transferData.amount,
//             userId: this.userId
//           };
//           this.transactionService.addTransaction(transaction).subscribe();
//           this.transferData = { fromId: 0, toId: 0, amount: 0 };
//           this.loadUserAccounts();
//         },
//         error: (err) => {
//           console.error('Error transferring money:', err);
//           alert('Transfer failed.');
//         }
//       });
//   }

//   // ===== Transactions =====
//   loadTransactions() {
//     if (!this.selectedAccountId) return;
//     this.transactionService.getTransactions(this.userId, this.selectedAccountId).subscribe({
//       next: (transactions) => {
//         this.transactions = transactions;
//       },
//       error: (err) => {
//         console.error('Error loading transactions:', err);
//         alert('Could not load transactions.');
//       }
//     });
//   }

//   // ===== Logout =====
//   onLogout() {
//     localStorage.removeItem('token');
//     localStorage.removeItem('userId');
//     alert('Logged out successfully!');
//     this.router.navigate(['/login']);
//   }
// }


import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Account, Accountservice } from '../../services/accountservice';
import { Transaction, Transactionservice } from '../../services/transactionservice';
import { User, UserService } from '../../services/userservice';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit {
  
  userId!: number; // You can get this from auth service or token
  activeTab: string = 'accounts';
  
  // Account related data
  accounts: Account[] = [];
  newAccount: Account = {
    accountType: '',
    accountBalance: 0,
    secretPassword: '',
    accountId: undefined // Changed from 0 to undefined
  };
  
  // Transfer related data
  transferData = {
    fromId: 0,
    toId: 0,
    amount: 0
  };
  
  // Transaction related data
  transactions: Transaction[] = [];
  selectedAccountId: number | null = null;

  // User profile related data
  currentUser: User = {
    username: '',
    address: ''
  };
  
  editUser: User = {
    username: '',
    address: ''
  };
  
  isEditingProfile: boolean = false;

  constructor(
    private router: Router,
    private accountService: Accountservice,
    private transactionService: Transactionservice,
    private userService: UserService
  ) {}

ngOnInit() {
  // Check if user is authenticated
  const token = localStorage.getItem('token');
  if (!token) {
    this.router.navigate(['/login']);
    return;
  }
  
  // Get userId from localStorage
  const storedUserId = localStorage.getItem('userId');
  if (storedUserId) {
    this.userId = parseInt(storedUserId);
  }
  
  // Load user profile and accounts on init
  this.loadUserProfile();
  this.loadUserAccounts();
}

  // Load User Profile
  loadUserProfile() {
    this.userService.getUserById(this.userId).subscribe({
      next: (user) => {
        this.currentUser = user;
        this.editUser = { ...user }; // Create a copy for editing
        console.log('User profile loaded:', user);
      },
      error: (error) => {
        console.error('Error loading user profile:', error);
        alert('Error loading user profile. Please try again.');
      }
    });
  }

  // Start editing profile
  startEditProfile() {
    this.isEditingProfile = true;
    this.editUser = { ...this.currentUser }; // Reset edit form to current values
  }

  // Cancel editing profile
  cancelEditProfile() {
    this.isEditingProfile = false;
    this.editUser = { ...this.currentUser }; // Reset to original values
  }

  // Update user profile
  onUpdateProfile() {
    if (!this.editUser.username || !this.editUser.address) {
      alert('Please fill all required fields');
      return;
    }

    // Create update object without sensitive fields
    const updateData = {
      username: this.editUser.username,
      address: this.editUser.address
    };

    this.userService.updateUser(this.userId, updateData).subscribe({
      next: (updatedUser) => {
        this.currentUser = updatedUser;
        this.isEditingProfile = false;
        alert('Profile updated successfully!');
        console.log('Profile updated:', updatedUser);
      },
      error: (error) => {
        console.error('Error updating profile:', error);
        alert('Error updating profile. Please try again.');
      }
    });
  }

  // Load User Accounts
  loadUserAccounts() {
    this.accountService.getUserAccounts(this.userId).subscribe({
      next: (accounts) => {
        this.accounts = accounts;
        console.log('Accounts loaded:', accounts);
      },
      error: (error) => {
        console.error('Error loading accounts:', error);
        alert('Error loading accounts. Please try again.');
      }
    });
  }

  // Create New Account
  onCreateAccount() {
    if (!this.newAccount.accountType || !this.newAccount.accountBalance || !this.newAccount.secretPassword) {
      alert('Please fill all fields');
      return;
    }

    // Create a clean account object without accountId for new accounts
    const accountToCreate = {
      accountType: this.newAccount.accountType,
      accountBalance: this.newAccount.accountBalance,
      secretPassword: this.newAccount.secretPassword
      // Don't include accountId for new accounts
    };

    this.accountService.createAccount(this.userId, accountToCreate).subscribe({
      next: (response) => {
        alert('Account created successfully!');
        this.newAccount = { 
          accountType: '', 
          accountBalance: 0, 
          secretPassword: '', 
          accountId: undefined // Reset to undefined
        };
        this.loadUserAccounts(); // Refresh accounts list
        this.activeTab = 'accounts'; // Switch to accounts tab
      },
      error: (error) => {
        console.error('Error creating account:', error);
        alert('Error creating account. Please try again.');
      }
    });
  }

  // Transfer Money - Using only Transaction API
  onTransferMoney() {
    if (!this.transferData.fromId || !this.transferData.toId || !this.transferData.amount) {
      alert('Please fill all fields');
      return;
    }

    if (this.transferData.fromId === this.transferData.toId) {
      alert('Cannot transfer to the same account');
      return;
    }

    // Create transaction - this will handle both balance update AND transaction record
    const transaction: Transaction = {
      frmAccountId: this.transferData.fromId,
      toAccountId: this.transferData.toId,
      transactionType: 'TRANSFER',
      amount: this.transferData.amount,
      userId: this.userId
    };

    this.transactionService.addTransaction(transaction).subscribe({
      next: (response) => {
        alert('Money transferred successfully!');
        console.log('Transfer completed and transaction recorded:', response);
        
        // Reset form and refresh accounts
        this.transferData = { fromId: 0, toId: 0, amount: 0 };
        this.loadUserAccounts();
      },
      error: (error) => {
        console.error('Error transferring money:', error);
        alert('Transfer failed. Please check your details and try again.');
      }
    });
  }

  // Load Transactions
  loadTransactions() {
    if (!this.selectedAccountId) {
      return;
    }

    this.transactionService.getTransactions(this.userId, this.selectedAccountId).subscribe({
      next: (transactions) => {
        this.transactions = transactions;
        console.log('Transactions loaded:', transactions);
      },
      error: (error) => {
        console.error('Error loading transactions:', error);
        alert('Error loading transactions. Please try again.');
      }
    });
  }

  // Logout
  onLogout() {
    localStorage.removeItem('token');
    localStorage.removeItem('userId'); // Also clear userId
    alert('Logged out successfully!');
    this.router.navigate(['/login']);
  }
}





