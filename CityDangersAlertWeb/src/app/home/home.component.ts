import { Component, OnInit } from '@angular/core';
import { Problem } from '../Models/Problem';
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

  problem: Problem = {
    type: "type",
    location: "locat",
    user: "us",
    status: "In Work",
  };

  problems: Problem[] = []

  problems2: Problem [] = [
    {
      type: "type1",
      location: "locat1",
      user: "us1",
      status: "In Work",
    },
    {
      type: "type2",
      location: "locat2",
      user: "us2",
      status: "New",
    },
    {
      type: "type3",
      location: "locat3",
      user: "us3",
      status: "Done",
    }
  ]

  ngOnInit(): void {
      this.rs.getProblems().subscribe
      (
          (Response)=>
          {
              this.problems = Response;
          },

          (error)=>
          {
            console.log("Error Occured : " + error);
          }
      )
  }
  
  onChange(problemObject:any, target:any): void{
      console.log(problemObject);
      alert(problemObject.type + ": " + problemObject.status);
      alert(target.value);

      problemObject.status = target.value;
      console.log(problemObject);

      
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

    }

}
