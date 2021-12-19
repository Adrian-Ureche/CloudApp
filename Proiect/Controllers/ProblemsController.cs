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

	    // [HttpGet("{[FromBody] Location current_loc}")] 
        //public async Task<ProblemsEntity> GetProblems([FromRoute] string current_loc) // get problems, toate problemele dintr-o arie 
        //{
            // return await _problemsRepository.GetProblem(current_loc);
        // }
	
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
        public async Task<string> Edit([FromBody] ProblemsEntity problem) {   // update pentru problems 
            try
            {
                await _problemsRepository.EditProblem(problem);

                return "S-a modificat cu succes!";
            }
            catch (System.Exception e)
            {
                return "Eroare: " + e.Message;
            }
        }
    }
}

// toate operatiile pe api se fac pe database mai putin postul pe tabela problems care se face pe coada
