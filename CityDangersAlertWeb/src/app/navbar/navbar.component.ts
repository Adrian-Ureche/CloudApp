import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {

  constructor(private router: Router) { }

  ngOnInit(): void {
  }

  loadHome(event:any){
    this.router.navigateByUrl('/home');
  }
  
  loadChart(event:any){
    this.router.navigateByUrl('/chart');
  }
  
  loadChartType(event:any){
    this.router.navigateByUrl('/chart-type');
  }
  
  logOut(event:any){
    this.router.navigateByUrl('#');
  }
}
