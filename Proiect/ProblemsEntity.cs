using Microsoft.WindowsAzure.Storage.Table;

namespace Models
{
    public class ProblemsEntity : TableEntity				// schimb cu users,alta cu problemss
    {
        public ProblemsEntity(string type, string location)
        {
            this.PartitionKey = type;
            this.RowKey = location;
        }

        public ProblemsEntity() { }

        // public string Type { get; set; }

        // public string Location { get; set; }
        
        public string User { get; set; }
        public string Status { get; set; }
    }
}