export class Problem
{
    partitionKey: string;
    rowKey: string;
    user: string;
    status: string;

    constructor(type: string, location: string, user: string, status: string){
        this.partitionKey = type;
        this.rowKey = location;
        this.user = user;
        this.status = status;
    }
}