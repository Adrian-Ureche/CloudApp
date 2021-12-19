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
        public async Task<IEnumerable<UsersEntity>> Get() // get pentru users
        {
            return await _usersRepository.GetUsers();
        }

        //[HttpGet("{UserName}")]
        //public async Task<UsersEntity> GetUser([FromRoute] string user) // get un utilizator pentru autentificare dupa username
        //{
            //return await _usersRepository.GetUser(user);
        //}
	
    
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


        
	 public async Task<string> Edit([FromBody] UsersEntity user) {   //update pentru user
            try
            {
                await _usersRepository.EditUser(user);

                return "S-a modificat cu succes!";
            }
            catch (System.Exception e)
            {
                return "Eroare: " + e.Message;
            }
        }
    }
}

