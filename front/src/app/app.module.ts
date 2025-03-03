import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { routes } from './app.routes';
import { DeveloperTestListComponent } from './components/developer-test-list/developer-test-list.component';

import { CommonModule } from '@angular/common';

@NgModule({
  imports: [
    BrowserModule,
    RouterModule.forRoot(routes),
    DeveloperTestListComponent,
    CommonModule
  ],
  providers: [],
  bootstrap: [],
})
export class AppModule {}
