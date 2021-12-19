import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Problem } from './Models/Problem';

@Injectable({
  providedIn: 'root'
})
export class RestService {

  url : string = "https://proiectdatc2021.azurewebsites.net/problems";

  constructor(private http: HttpClient) { }


  getProblems()
  {
    return this.http.get<Problem[]>(this.url)
  }

  updateProblems(problem:Problem)
  {
    return this.http.put<Problem>(this.url, problem)
  }
}
