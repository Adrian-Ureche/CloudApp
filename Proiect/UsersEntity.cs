using Microsoft.WindowsAzure.Storage.Table;

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

        // public string UserName { get; set; }

        // public string Password { get; set; }

        public int Discount { get; set; }
    }
}