import { Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { NavbarComponent } from './navbar/navbar.component';
import { DeveloperTestListComponent } from './components/developer-test-list/developer-test-list.component';

export const routes: Routes = [

    { path: '', component: HomeComponent },
    { path: 'nav', component: NavbarComponent },
    { path: 'developer/tests', component: DeveloperTestListComponent }

];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
  })
  export class AppRoutingModule {}