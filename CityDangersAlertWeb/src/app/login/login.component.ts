import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router'

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  username: string | undefined;
  password: string | undefined;

  constructor(private router: Router) {
  }

  ngOnInit(): void {

  }

  loginUser(){
    if(this.username == "admin" && this.password == "admin")
    {
      console.log("Success!");
      this.router.navigateByUrl('/home');
    }
    else
    {
      console.log("Failed!");
      alert('Nume de utilizator sau parola gresita!\n\
            Username: admin\n\
            Password: admin');
    }
  }

}
