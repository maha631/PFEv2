import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { NavbarComponent } from './navbar/navbar.component';
import { SignupComponent } from './signup/signup.component';
import { SigninComponent } from './signin/signin.component';

export const routes: Routes = [

  { path: '', component: HomeComponent },
    
  { path: 'signup', component: SignupComponent },
  { path: 'signin', component: SigninComponent}

];