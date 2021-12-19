using System.Collections.Generic;
using System.Net;
using System.Text.Json;
using System.Threading.Tasks;
using Azure.Storage.Queues;
using Microsoft.Extensions.Configuration;
using Microsoft.WindowsAzure.Storage;
using Microsoft.WindowsAzure.Storage.Table;
using Models;
using Newtonsoft.Json;

namespace Proiect
{
    public class ProblemsRepository : IProblemsRepository
    {
        private string _connectionString;

        private CloudTableClient _tableClient;

        private CloudTable _problemsTable; 

        public ProblemsRepository(IConfiguration configuration)
        {
            _connectionString = "DefaultEndpointsProtocol=https;AccountName=storageproiectdatc;AccountKey=W4wRThzGKh+2X1h7CUdb1DvjlkQsB8QOYePSFzZiF02XPFolgXf8aKXbiz7Li3k96Ui0PrIYmEXVxjdEWMp9iw==;EndpointSuffix=core.windows.net";

            Task.Run(async () => { await InitializeTable(); }).GetAwaiter().GetResult();
        }

         public async Task<List<ProblemsEntity>> GetProblems()  // get all problems
        {
            var problems = new List<ProblemsEntity>();

            TableQuery<ProblemsEntity> query = new TableQuery<ProblemsEntity>();

            TableContinuationToken token = null;
            do
            {
                TableQuerySegment<ProblemsEntity> resultSegment = await _problemsTable.ExecuteQuerySegmentedAsync(query, token);
                token = resultSegment.ContinuationToken;

                problems.AddRange(resultSegment.Results);

            } while (token != null);

            return problems;
        }

        //public async Task<ProblemsEntity> GetProblem(string current_loc)
       // {
            //ar parsedId = ParseUserloc(current_loc);

           // var rowKey = parsedId.Item2;

           // var query = TableOperation.Retrieve<ProblemsEntity>(partitionKey, rowKey);

           // var result = await _problemsTable.ExecuteAsync(query);

           // return (ProblemsEntity)result.Result;
        // }
 	public async Task InsertNewProblem(ProblemsEntity problem)   // insert new problem // trebuie facuta functia care ia din coada
        {

            var jsonProblem = JsonConvert.SerializeObject(problem);
            var plainTextBytes = System.Text.Encoding.UTF8.GetBytes(jsonProblem);
            var base64String = System.Convert.ToBase64String(plainTextBytes);

            QueueClient queueClient = new QueueClient(
                _connectionString,
                "problems-queue"
                );
            queueClient.CreateIfNotExists();

            await queueClient.SendMessageAsync(base64String);
        }

        
	public async Task EditProblem(ProblemsEntity problem)   // edit problems
        {
            var editOperation = TableOperation.Merge(problem);

            // Implemented using optimistic concurrency
            try
            {
                await _problemsTable.ExecuteAsync(editOperation);
            }
            catch (StorageException e)
            {
                if (e.RequestInformation.HttpStatusCode == (int)HttpStatusCode.PreconditionFailed)
                    throw new System.Exception("Entitatea a fost deja modificata. Te rog sa reincarci entitatea!");
            }
        }

        private async Task InitializeTable()
        {
            var account = CloudStorageAccount.Parse(_connectionString);
            _tableClient = account.CreateCloudTableClient();

            _problemsTable = _tableClient.GetTableReference("probleme");

            await _problemsTable.CreateIfNotExistsAsync();

        }
        
    }
}