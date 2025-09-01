import { Routes } from '@angular/router';
import { Register } from './user-service/register/register';
import { Login } from './user-service/login/login';
import { Menu } from './menu/menu/menu';
import { Dashboard } from './user-service/dashboard/dashboard';

export const routes: Routes = [
{path: '', component: Menu},
{ path: 'register', component: Register },
{ path: 'login', component: Login },
{path:'dashboard', component: Dashboard }
];
