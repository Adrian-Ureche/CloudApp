using System;
using Microsoft.Azure.Functions.Worker;
using Microsoft.Extensions.Logging;
using Models;

namespace Company.Function
{
    public static class PostFunction
    {
        [Function("PostFunction")]
        [TableOutput("problems")]
        public static ProblemsEntity Run([QueueTrigger("coadaptproiect", Connection = "azurestoragetema3_STORAGE")] string myQueueItem,
            FunctionContext context)
        {
            var logger = context.GetLogger("PostFunction");
            logger.LogInformation($"C# Queue trigger function processed: {myQueueItem}");
            var student =Newtonsoft.Json.JsonConvert.DeserializeObject<ProblemsEntity>(myQueueItem);
            return student;
        }
    }
}
