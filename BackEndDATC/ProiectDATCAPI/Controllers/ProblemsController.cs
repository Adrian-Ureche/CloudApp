
using System.Collections.Generic;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Models;

namespace Proiect.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class ProblemsController : ControllerBase
    {
        private IProblemsRepository _problemsRepository;

        public ProblemsController(IProblemsRepository problemsRepository)
        {
            _problemsRepository = problemsRepository;
        }

        [HttpGet]
        public async Task<IEnumerable<ProblemsEntity>> Get() // get tot pentru problems
        {
            return await _problemsRepository.GetProblems();
        }
	
        [HttpPost]
        public async Task<string> Post([FromBody] ProblemsEntity problem) // post pentru problems cu toti parametrii
        {
            try
            {
                await _problemsRepository.InsertNewProblem(problem);

                return "S-a adaugat cu succes!";
            }
            catch (System.Exception e)
            {
                return "Eroare: " + e.Message;
            }
        }


        [HttpPut]
        public async void Edit([FromBody] ProblemsEntity problem) { 
            await _problemsRepository.DeleteProblem(problem.PartitionKey,problem.RowKey);
            await _problemsRepository.InsertNewProblemDirectly(problem);
        }
    }
}