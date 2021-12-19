import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { LoginComponent } from './login/login.component';
import { HomeComponent } from './home/home.component';
import { ChartComponent } from './chart/chart.component';

const routes: Routes = [
  { path:'', component:LoginComponent },
  { path:'home', component:HomeComponent },
  { path:'chart', component:ChartComponent },

  { path:'**', component:LoginComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
