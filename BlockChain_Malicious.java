package DSCoinPackage;

import HelperClasses.CRF;
import HelperClasses.MerkleTree;

public class BlockChain_Malicious {

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock[] lastBlocksList;

  public static boolean checkTransactionBlock (TransactionBlock tB) {
    
    CRF c= new CRF(64);

    if(tB==null)return false;

    if(tB.dgst.substring(0,4).equals("0000")==false)return false;

    if(tB.previous==null)
    {
      if(tB.dgst.equals(c.Fn(start_string+"#"+tB.trsummary+"#"+tB.nonce))==false)return false;
    }
    else
    {
      if(tB.dgst.equals(c.Fn(tB.previous.dgst+"#"+tB.trsummary+"#"+tB.nonce))==false)return false;
    }

    //some more check

    MerkleTree x= new MerkleTree();
    String ok= x.Build(tB.trarray);

    if(ok.equals(tB.trsummary)==false)return false;

    //checking all the transactions of tB.trarray array
    for(int qi=0 ; qi<tB.trarray.length ; qi++)
    {
      Transaction t= tB.trarray[qi];

      TransactionBlock it= tB.previous;

      while(it!=t.coinsrc_block)
      {
        for(int i=0 ; i<it.trarray.length ; i++)
        {
          Transaction z= it.trarray[i];

          if(z.coinID.equals(t.coinID))return false;
        }

           it=it.previous;
      }


    }

    return true;

  }

  public TransactionBlock FindLongestValidChain () {
    
    int n= lastBlocksList.length;

    TransactionBlock q= null;
    int mx=0;

    for(int i=0 ; i<n ; i++)
    {
       TransactionBlock z= lastBlocksList[i];
       int a=0; TransactionBlock f=z;

       while(z!=null)
       {
         if(checkTransactionBlock(z)==false)
         {
           a=0; f=z.previous;
         }
         else a++;

         z=z.previous;
       }
       
       if(a>mx)
       {
         mx=a;
         q=f;
       }
       
    }

   
    return q;

    //this seems correct but ready for modifications.
  }

  public void InsertBlock_Malicious (TransactionBlock newBlock) {

    TransactionBlock lastBlock= FindLongestValidChain();

    CRF c= new CRF(64);

    if(lastBlock==null)
    {
      //adding the first block
      for(long i=1000000001L ; ; i++)
      {
         String a= c.Fn(start_string+"#"+newBlock.trsummary+"#"+String.valueOf(i));
         if(a.substring(0,4).equals("0000"))
         {
           newBlock.dgst=a;
           newBlock.nonce=String.valueOf(i);
           break;
         }
      }

      newBlock.previous=lastBlock;
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

    }

    //add the lastBlock to the array
    int n= lastBlocksList.length;

    for(int i=0 ; i<n ; i++)
    {
      if(lastBlocksList[i]==null || lastBlocksList[i]==lastBlock)
      {
        lastBlocksList[i]=newBlock; break;
      }
    }

  }
}
