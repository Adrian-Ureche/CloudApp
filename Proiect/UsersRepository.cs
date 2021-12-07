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
    public class UsersRepository : IUsersRepository
    {
        private string _connectionString;

        private CloudTableClient _tableClient;

        private CloudTable _usersTable;

        public UsersRepository(IConfiguration configuration)
        {
            _connectionString = "DefaultEndpointsProtocol=https;AccountName=storageproiectdatc;AccountKey=W4wRThzGKh+2X1h7CUdb1DvjlkQsB8QOYePSFzZiF02XPFolgXf8aKXbiz7Li3k96Ui0PrIYmEXVxjdEWMp9iw==;EndpointSuffix=core.windows.net";

            Task.Run(async () => { await InitializeTable(); }).GetAwaiter().GetResult();
        }


       public async Task<List<UsersEntity>> GetUsers()  // get all problems
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

        public async Task InsertNewUser(UsersEntity user)   // insert new user
        {;

            var jsonUser = JsonConvert.SerializeObject(user);
            var plainTextBytes = System.Text.Encoding.UTF8.GetBytes(jsonUser);
            var base64String = System.Convert.ToBase64String(plainTextBytes);

            QueueClient queueClient = new QueueClient(
                _connectionString,
                "user-queue"
                );
            queueClient.CreateIfNotExists();

            await queueClient.SendMessageAsync(base64String);
        }

        public async Task EditUser(UsersEntity user)   // edit users
        {
            var editOperation = TableOperation.Merge(user);

            // Implemented using optimistic concurrency
            try
            {
                await _usersTable.ExecuteAsync(editOperation);
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

            _usersTable = _tableClient.GetTableReference("useri");

            await _usersTable.CreateIfNotExistsAsync();

        }

    }
}