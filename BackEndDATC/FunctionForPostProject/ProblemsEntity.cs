
using Microsoft.Azure.Cosmos.Table;

namespace Models
{
    public class ProblemsEntity : TableEntity
    {
        public ProblemsEntity(string type, string location)
        {
            this.PartitionKey = type;
            this.RowKey = location;
        }

        public ProblemsEntity() { }
        
        public string User { get; set; }
        public string Status { get; set; }
    }
}