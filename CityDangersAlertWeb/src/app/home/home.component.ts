import { assertPlatform, Component, OnInit } from '@angular/core';
import { Problem } from '../Models/Problem';
import { User } from '../Models/User';
import { RestService } from '../rest.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  title = 'CityDangersAlert';

  constructor(private rs : RestService){}

  headers = ["Type", "Location", "User", "Status"];
  index = ["type", "location", "user"];
  status = "status";
  statusChanged = "";


  problems: Problem[] = []
  users: User[] = []


  ngOnInit(): void {
      this.rs.getProblems().subscribe
      (
          (Response)=>
          {
              console.log(Response);
              this.problems = Response;
          },

          (error)=>
          {
            console.log("Error Occured in getProblems : " + error);
          }
      )
      this.rs.getUsers().subscribe
      (
          (Response)=>
          {
              console.log(Response);
              this.users = Response;
          },

          (error)=>
          {
            console.log("Error Occured in getUsers : " + error);
          }
      )
  }
  
  onChange(problemObject:any, target:any): void{
      //alert(problemObject.type + ": " + problemObject.status);
      //alert(target.value);

      console.log(problemObject);
      problemObject.status = target.value;

      
      this.rs.updateProblems(problemObject).subscribe
      (
          (Response)=>
          {
            console.log("Update succesful : " + Response);
          },

          (error)=>
          {
            console.log("Error Occured : " + error);
          }
      )

      if(target.value === "Close")
      {
        this.users.forEach(user => {
          if(user.partitionKey === problemObject.user)
          {  
            user.discount += 1;
            
            this.rs.updateUser(user).subscribe
            (
                (Response)=>
                {
                  console.log("Update succesful : " + Response);
                  console.log(user);
                },

                (error)=>
                {
                  console.log("Error Occured : " + error);
                }
            )
          }
        });
      }

    }

}
