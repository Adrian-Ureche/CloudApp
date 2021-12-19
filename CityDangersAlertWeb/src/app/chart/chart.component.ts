import { Component, OnInit } from '@angular/core';
import { Problem } from '../Models/Problem';
import { RestService } from '../rest.service';

@Component({
  selector: 'app-chart',
  templateUrl: './chart.component.html',
  styleUrls: ['./chart.component.css']
})
export class ChartComponent implements OnInit {


  constructor(private rs : RestService) { }


  problems: Problem[] = []
  public new = 0;
  public inWork = 0;
  public done = 0;
  public close = 0;

  public barChartOptions = {
    scaleShowVerticalLines: false,
    responsive: true
  };
  public barChartLabels = ['Status'];
  public barChartType = 'bar';
  public barChartLegend = true;
  public barChartData = [
    {data: [this.new], label: 'New'},
    {data: [this.inWork], label: 'In Work'},
    {data: [this.done], label: 'Done'},
    {data: [this.close], label: 'Close'}
  ];
  
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

  loadChart(event:any)
  {
    this.new = this.getAttribute("New");
    this.inWork = this.getAttribute("In Work");
    this.done = this.getAttribute("Done");
    this.close = this.getAttribute("Close");
      
    this.barChartData = [
        {data: [this.new], label: 'New'},
        {data: [this.inWork], label: 'In Work'},
        {data: [this.done], label: 'Done'},
        {data: [this.close], label: 'Close'}
    ];
  }
  
  getAttribute(attribute:string) : number
  {
    var count = 0;
    for (let pr of this.problems) 
        if (pr.status === attribute) 
            count += 1;

    return count;    
  }

}
