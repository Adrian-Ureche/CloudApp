export class Problem
{
    type: string;
    location: string;
    user: string;
    status: string;

    constructor(type: string, location: string, user: string, status: string){
        this.type = type;
        this.location = location;
        this.user = user;
        this.status = status;
    }
}