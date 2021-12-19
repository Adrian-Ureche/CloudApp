using System.Collections.Generic;
using System.Threading.Tasks;
using Models;

public interface IUsersRepository
{
    Task<List<UsersEntity>> GetUsers();

    Task InsertNewUser(UsersEntity user);
    Task DeleteUser(string partitionKey,string rowKey);
    Task<UsersEntity> GetUser(string partitionKey,string rowKey);

}