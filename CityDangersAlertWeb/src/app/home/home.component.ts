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


  problems: Problem[] = []


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
            console.log("Error Occured : " + error);
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

    }

}
