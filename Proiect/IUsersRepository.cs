using System.Collections.Generic;
using System.Threading.Tasks;
using Models;

public interface IUsersRepository
{
    Task<List<UsersEntity>> GetUsers();

    Task InsertNewUser(UsersEntity user);

    Task EditUser(UsersEntity user);

}