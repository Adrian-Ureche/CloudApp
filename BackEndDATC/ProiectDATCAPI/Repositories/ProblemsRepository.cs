
using Models;

using System.Collections.Generic;
using System.Net;
using System.Threading.Tasks;
using Microsoft.Azure.Cosmos.Table;
using Microsoft.Extensions.Configuration;

using Newtonsoft.Json;
using Azure.Storage.Queues;
using System.Text;

namespace Proiect
{
    public class ProblemsRepository : IProblemsRepository
    {
        private string _connectionString;

        private CloudTableClient _tableClient;

        private CloudTable _problemsTable; 

        public ProblemsRepository(IConfiguration configuration)
        {
           _connectionString=(string)configuration.GetValue(typeof(string),"AzureStorageAccountConnectionString");
            Task.Run(async () => { await InitializeTable(); }).GetAwaiter().GetResult();
        }

        public async Task<List<ProblemsEntity>> GetProblems()
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

        public async Task InsertNewProblem(ProblemsEntity problem)
        {
            
            var message =Encoding.UTF8.GetBytes(Newtonsoft.Json.JsonConvert.SerializeObject(problem));

            QueueClient queueClient = new QueueClient(
                _connectionString,
                "coadaptproiect"
                );
            queueClient.CreateIfNotExists();
            var base64string = System.Convert.ToBase64String(message);
            await queueClient.SendMessageAsync(base64string);
        }
        public async Task<ProblemsEntity> GetProblem(string partitionKey,string rowKey)
        {
            ProblemsEntity problem=new ProblemsEntity();
            TableQuery<ProblemsEntity> query=new TableQuery<ProblemsEntity>().Where(TableQuery.CombineFilters(TableQuery.GenerateFilterCondition("PartitionKey",QueryComparisons.Equal,partitionKey),
            TableOperators.And,TableQuery.GenerateFilterCondition("RowKey", QueryComparisons.Equal, rowKey)));
            TableContinuationToken token =null;
            do{
                TableQuerySegment<ProblemsEntity> resultSegment = await _problemsTable.ExecuteQuerySegmentedAsync(query,token);
                token= resultSegment.ContinuationToken;
                if(resultSegment.Results.Count!=0)
                {
                problem=resultSegment.Results[0];
                break;
                }
            }while(token!=null);
            return problem;
        }
        public async Task DeleteProblem(string partitionKey,string rowKey)
        {
            ProblemsEntity problem=await GetProblem(partitionKey,rowKey);
            TableOperation delete= TableOperation.Delete(problem);
            await _problemsTable.ExecuteAsync(delete);
        }
         public async Task InsertNewProblemDirectly(ProblemsEntity problem)
        {
            var insertOperation = TableOperation.Insert(problem);

            await _problemsTable.ExecuteAsync(insertOperation);
        }

        private async Task InitializeTable()
        {
            var account = CloudStorageAccount.Parse(_connectionString);
            _tableClient = account.CreateCloudTableClient();

            _problemsTable = _tableClient.GetTableReference("problems");

            await _problemsTable.CreateIfNotExistsAsync();

        }
    }
}