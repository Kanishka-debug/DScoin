package DSCoinPackage;

public class TransactionQueue {

  public Transaction firstTransaction;
  public Transaction lastTransaction;
  public int numTransactions;

  public void AddTransactions (Transaction transaction) {

    if(firstTransaction==null)
    {
      firstTransaction=transaction;
      lastTransaction=transaction;
      numTransactions++;

    }
    else
    {
      lastTransaction.next= transaction;
      lastTransaction=transaction;
      numTransactions++;
    }

  }
  
  public Transaction RemoveTransaction () throws EmptyQueueException {
   // return null;

   
   if(numTransactions==0)throw new EmptyQueueException();

   if(firstTransaction==lastTransaction)
   {
     Transaction t= firstTransaction;
     numTransactions--;
     firstTransaction=null;
     lastTransaction=null;

     return t;
   }
   else
   {
     Transaction t= firstTransaction;
     numTransactions--;
     firstTransaction=firstTransaction.next;

     return t;
   }


  }

  public int size() {
    //return 0;
    return numTransactions;
  }
}
