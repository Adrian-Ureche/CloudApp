using System.Collections.Generic;
using System.Threading.Tasks;
using Models;

public interface IProblemsRepository
{
    Task<List<ProblemsEntity>> GetProblems();

    Task InsertNewProblem(ProblemsEntity problem);
    Task<ProblemsEntity> GetProblem(string partitionKey,string rowKey);
    Task DeleteProblem(string partitionKey,string rowKey);
    Task InsertNewProblemDirectly(ProblemsEntity problem);

}