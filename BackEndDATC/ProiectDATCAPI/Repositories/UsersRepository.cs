using System.Collections.Generic;
using System.Net;
using System.Text.Json;
using System.Threading.Tasks;
using Microsoft.Azure.Cosmos.Table;
using Microsoft.Extensions.Configuration;
using Models;

namespace Proiect
{
    public class UsersRepository : IUsersRepository
    {
        private string _connectionString;

        private CloudTableClient _tableClient;

        private CloudTable _usersTable;

        public UsersRepository(IConfiguration configuration)
        {
            _connectionString=(string)configuration.GetValue(typeof(string),"AzureStorageAccountConnectionString");

            Task.Run(async () => { await InitializeTable(); }).GetAwaiter().GetResult();
        }

        public async Task<List<UsersEntity>> GetUsers()
        {
            var users = new List<UsersEntity>();

            TableQuery<UsersEntity> query = new TableQuery<UsersEntity>();

            TableContinuationToken token = null;
            do
            {
                TableQuerySegment<UsersEntity> resultSegment = await _usersTable.ExecuteQuerySegmentedAsync(query, token);
                token = resultSegment.ContinuationToken;

                users.AddRange(resultSegment.Results);

            } while (token != null);

            return users;
        }

        public async Task InsertNewUser(UsersEntity user)
        {
            var insertOperation = TableOperation.Insert(user);

            await _usersTable.ExecuteAsync(insertOperation);
        }
        public async Task<UsersEntity> GetUser(string partitionKey,string rowKey)
        {
            UsersEntity user=new UsersEntity();
            TableQuery<UsersEntity> query=new TableQuery<UsersEntity>().Where(TableQuery.CombineFilters(TableQuery.GenerateFilterCondition("PartitionKey",QueryComparisons.Equal,partitionKey),
            TableOperators.And,TableQuery.GenerateFilterCondition("RowKey", QueryComparisons.Equal, rowKey)));
            TableContinuationToken token =null;
            do{
                TableQuerySegment<UsersEntity> resultSegment = await _usersTable.ExecuteQuerySegmentedAsync(query,token);
                token= resultSegment.ContinuationToken;
                if(resultSegment.Results.Count!=0)
                {
                user=resultSegment.Results[0];
                break;
                }
            }while(token!=null);
            return user;
        }
        public async Task DeleteUser(string partitionKey,string rowKey)
        {
            UsersEntity user=await GetUser(partitionKey,rowKey);
            TableOperation delete= TableOperation.Delete(user);
            await _usersTable.ExecuteAsync(delete);
        }
        private async Task InitializeTable()
        {
            var account = CloudStorageAccount.Parse(_connectionString);
            _tableClient = account.CreateCloudTableClient();

            _usersTable = _tableClient.GetTableReference("users");

            await _usersTable.CreateIfNotExistsAsync();
        }
    }
}