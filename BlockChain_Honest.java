package DSCoinPackage;

import HelperClasses.CRF;

public class BlockChain_Honest {

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock lastBlock;

  public void InsertBlock_Honest (TransactionBlock newBlock) {

    CRF c= new CRF(64);

    if(lastBlock==null)
    {
      //adding the first block
      for(long i=1000000001L ; ; i++)
      {
         String a= c.Fn(start_string+"#"+newBlock.trsummary+"#"+String.valueOf(i)); //calculating dgst
         if(a.substring(0,4).equals("0000"))
         {
           newBlock.dgst=a;
           newBlock.nonce=String.valueOf(i);
           break;
         }
      }

      newBlock.previous=lastBlock;
      lastBlock= newBlock;
    }
    else
    {
      for(long i=1000000001L ; ; i++)
      {
         String a= c.Fn(lastBlock.dgst+"#"+newBlock.trsummary+"#"+String.valueOf(i));
         if(a.substring(0,4).equals("0000"))
         {
           newBlock.dgst=a;
           newBlock.nonce=String.valueOf(i);
           break;
         }
      }

      newBlock.previous=lastBlock;
      lastBlock= newBlock;

    }

  }
}
