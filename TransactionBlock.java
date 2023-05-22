package DSCoinPackage;

import HelperClasses.MerkleTree;
import HelperClasses.CRF;

public class TransactionBlock {

  public Transaction[] trarray;
  public TransactionBlock previous;
  public MerkleTree Tree;
  public String trsummary;
  public String nonce;
  public String dgst;

  TransactionBlock(Transaction[] t) {

    trarray= new Transaction[t.length];

    for(int i=0 ; i<t.length ; i++)
    {
      trarray[i]= t[i];
    }

    previous= null;
    Tree= new MerkleTree();

    trsummary = Tree.Build(t);
    Tree.numdocs= trarray.length;

    dgst=null;
    
  }

  public boolean checkTransaction (Transaction t) {

        TransactionBlock f= t.coinsrc_block;

        boolean a= false;
    
        if(f==null)return true;  //as told in the module
    
        for(int i=0 ; i<f.trarray.length ; i++)
        {
          Transaction z= f.trarray[i];
    
          if(z.coinID==t.coinID && z.Destination.UID == t.Source.UID)a=true; 
        }
    
        boolean b=true;
    
        TransactionBlock it= this;
    
        while(it!=t.coinsrc_block)
        {
          for(int i=0 ; i<it.trarray.length ; i++)
          {
            Transaction z= it.trarray[i];
    
            if(z.coinID==t.coinID)b=false;
          }
    
          it=it.previous;
        }
    
        if(a==true && b==true)return true;
        else return false;
 
    
  }
}
