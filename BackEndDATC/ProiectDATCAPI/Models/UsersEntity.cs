

using Microsoft.Azure.Cosmos.Table;

namespace Models
{
    public class UsersEntity : TableEntity			
    {
        public UsersEntity(string username, string password)
        {
            this.PartitionKey = username;
            this.RowKey = password;
        }

        public UsersEntity() { }
        public int Discount { get; set; }
    }
}