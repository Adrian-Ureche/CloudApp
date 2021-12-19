
using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Models;

namespace Proiect.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class UsersController : ControllerBase
    {
        private IUsersRepository _usersRepository;

        public UsersController(IUsersRepository usersRepository)
        {
            _usersRepository = usersRepository;
        }

        [HttpGet]
        public async Task<IEnumerable<UsersEntity>> Get() 
        {
            return await _usersRepository.GetUsers();
        }
        [HttpPost]
        public async Task<string> Post([FromBody] UsersEntity user) // post pentru user
        {
            try
            {
                await _usersRepository.InsertNewUser(user);

                return "S-a adaugat cu succes!";
            }
            catch (System.Exception e)
            {
                return "Eroare: " + e.Message;
            }
        }


        [HttpPut]
        public async void Put(UsersEntity user)
        {
            Console.WriteLine(user.RowKey);
            await _usersRepository.DeleteUser(user.PartitionKey,user.RowKey);
            await _usersRepository.InsertNewUser(user);
        }
        
    }
}