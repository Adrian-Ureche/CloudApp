export class User
{
    partitionKey: string;
    rowKey: string;
    discount: number;

    constructor(username: string, password: string, discount: number){
        this.partitionKey = username;
        this.rowKey = password;
        this.discount = discount;
    }
}