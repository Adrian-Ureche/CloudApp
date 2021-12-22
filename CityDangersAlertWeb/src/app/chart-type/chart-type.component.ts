import { Component, OnInit } from '@angular/core';
import { Problem } from '../Models/Problem';
import { RestService } from '../rest.service';

@Component({
  selector: 'app-chart-type',
  templateUrl: './chart-type.component.html',
  styleUrls: ['./chart-type.component.css']
})
export class ChartTypeComponent implements OnInit {

  constructor(private rs : RestService) { }


  problems: Problem[] = []
  public p1 = 0;
  public p2 = 0;
  public p3 = 0;
  public p4 = 0;

  public barChartOptions = {
    scaleShowVerticalLines: false,
    responsive: true
  };
  public barChartLabels = ['Types'];
  public barChartType = 'bar';
  public barChartLegend = true;
  public barChartData = [
    {data: [this.p1], label: 'Destroyed bench'},
    {data: [this.p2], label: 'Illegal dumping'},
    {data: [this.p3], label: 'Pit on road'},
    {data: [this.p4], label: 'Plaster falling off the wall'}
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
    this.p1 = this.getAttribute("Destroyed bench");
    this.p2 = this.getAttribute("Illegal dumping");
    this.p3 = this.getAttribute("Pit on road");
    this.p4 = this.getAttribute("Plaster falling off the wall");
      
    this.barChartData = [
        {data: [this.p1], label: 'Destroyed bench'},
        {data: [this.p2], label: 'Illegal dumping'},
        {data: [this.p3], label: 'Pit on road'},
        {data: [this.p4], label: 'Plaster falling off the wall'}
    ];
  }
  
  getAttribute(attribute:string) : number
  {
    var count = 0;
    for (let pr of this.problems) 
        if (pr.partitionKey === attribute) 
            count += 1;

    return count;    
  }

}
