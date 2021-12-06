using System.Collections.Generic;
using System.Threading.Tasks;
using Models;

public interface IProblemsRepository
{
    Task<List<ProblemsEntity>> GetProblems();
    //Task<ProblemsEntity> GetProblem(string current_loc);

    Task InsertNewProblem(ProblemsEntity problem);

    Task EditProblem(ProblemsEntity problem);
}