import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Problem } from './Models/Problem';
import { User } from './Models/User';

@Injectable({
  providedIn: 'root'
})
export class RestService {

  // old storage: https://proiectdatc2021.azurewebsites.net/problems
  urlProblems : string = "https://citydangersapi.azurewebsites.net/problems";
  urlUsers : string = "https://citydangersapi.azurewebsites.net/users";

  constructor(private http: HttpClient) { }


  getProblems()
  {
    return this.http.get<Problem[]>(this.urlProblems)
  }

  updateProblems(problem:Problem)
  {
    return this.http.put<Problem>(this.urlProblems, problem)
  }

  getUsers()
  {
    return this.http.get<User[]>(this.urlUsers)
  }

  updateUser(user:User)
  {
    return this.http.put<User>(this.urlUsers, user)
  }
}
