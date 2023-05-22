package DSCoinPackage;

import java.util.*;
import HelperClasses.Pair;

public class Members
 {

  public String UID;
  public List<Pair<String, TransactionBlock>> mycoins;
  public Transaction[] in_process_trans;

  public void initiateCoinsend(String destUID, DSCoin_Honest DSobj) {

    Pair<String, TransactionBlock>ok = mycoins.remove(0);
     
    Transaction t= new Transaction();
    t.coinID= ok.first;
      
    t.Source=this;

    int k=DSobj.memberlist.length;
    for(int i=0 ; i<k ; i++)
    {
      if(DSobj.memberlist[i].UID.equals(destUID))t.Destination=DSobj.memberlist[i];
    }

    t.coinsrc_block= ok.second;
    
    int m= in_process_trans.length;

    for(int i=0 ; i<m ; i++)
    {
      if(in_process_trans[i]==null)
      {
        in_process_trans[i]=t; break;
      }
    }
    

    DSobj.pendingTransactions.AddTransactions(t);

  }

  public void initiateCoinsend(String destUID, DSCoin_Malicious DSobj) {

    Pair<String, TransactionBlock>ok = mycoins.remove(0);
     

    Transaction t= new Transaction();
    t.coinID= ok.first;
      
    t.Source=this;

    int k=DSobj.memberlist.length;
    for(int i=0 ; i<k ; i++)
    {
      if(DSobj.memberlist[i].UID.equals(destUID))t.Destination=DSobj.memberlist[i];
    }

    t.coinsrc_block= ok.second;
    
    int m= in_process_trans.length;

    for(int i=0 ; i<m ; i++)
    {
      if(in_process_trans[i]==null)
      {
        in_process_trans[i]=t; break;
      }
    }
    

    DSobj.pendingTransactions.AddTransactions(t);


  }


  public Pair<List<Pair<String, String>>, List<Pair<String, String>>> finalizeCoinsend (Transaction tobj, DSCoin_Honest DSObj) throws MissingTransactionException {
    //return null;
    
    BlockChain_Honest b= DSObj.bChain;
    // we have the block_chain 'b'

    TransactionBlock t= b.lastBlock;
    TransactionBlock tb=null;

    while(t!=null)
    {
      int n= t.trarray.length;

      for(int i=0 ; i<n ; i++)
      {
          if(t.trarray[i]==tobj)
          {
            tb=t; break;
          }
      }

      if(tb!=null)break;

      t=t.previous;
    }

    if(tb==null)throw new MissingTransactionException();

    List< Pair<String,String> >x= new ArrayList<>();

    //in tB block we have this transaction tobj

    x= tb.Tree.give(tobj, tb.Tree);

    List< Pair<String,String> >yy= new ArrayList<>(); //this will be reversed

    TransactionBlock temp= b.lastBlock;

    while(temp!=tb.previous)
    {
          Pair<String,String>q= new Pair<String,String>(null,null);
          q.first= temp.dgst;
          q.second= temp.previous.dgst+"#"+temp.trsummary+"#"+temp.nonce;
          yy.add(q);


      temp=temp.previous;
    }

       Pair<String,String>o= new Pair<String,String>(null,null);
        o.first= temp.dgst;
        o.second=null;

        yy.add(o);

        List< Pair<String,String> >y= new ArrayList<>();

        int op= yy.size();

        for(int i=op-1 ; i>=0 ; i--)
        {
          y.add(yy.get(i));
        }

        //now y is the reversed one....

        Pair<List<Pair<String, String>>, List<Pair<String, String>>>z= new Pair<List<Pair<String, String>>, List<Pair<String, String>>>(null,null);

        z.first=x;
        z.second=y;

        for(int i=0 ; i<in_process_trans.length ; i++)
        {
          if(in_process_trans[i]==tobj)
          {
            in_process_trans[i]=null; break;
          }
        }

        //now some updatation to tobj.destination's myCoin list

        Pair<String, TransactionBlock> lp= new Pair<String, TransactionBlock>(null,null);
        lp.first=tobj.coinID;
        lp.second=tb;

        int yt=Integer.parseInt(lp.first);    int rand= tobj.Destination.mycoins.size();

        if(yt> Integer.parseInt(tobj.Destination.mycoins.get(rand-1).first))tobj.Destination.mycoins.add(lp); 
        else
        {
           for(int i=0 ; i<rand ; i++)
          { 
             int ck=Integer.parseInt(tobj.Destination.mycoins.get(i).first);  
              if(ck>yt)
            {
             tobj.Destination.mycoins.add(i,lp); 
             break;
            }
          }

        }


        return z;

  }

  public void MineCoin(DSCoin_Honest DSObj) throws EmptyQueueException {

    int n=DSObj.bChain.tr_count-1;

    //from the queue we need n valid transactions

    List<Transaction>tq= new ArrayList<Transaction>();

    while(tq.size()!=n)
    {  
      Transaction a;
      if(DSObj.pendingTransactions.numTransactions==0) throw new EmptyQueueException();
      else a= DSObj.pendingTransactions.RemoveTransaction();

      TransactionBlock f= a.coinsrc_block;
      boolean y=false;

      for(int i=0 ; i<f.trarray.length ; i++)
      {
        Transaction z= f.trarray[i];
        if(z.coinID.equals(a.coinID) && z.Destination.UID.equals(a.Source.UID))y=true; 
      }

      if(y==false)continue; 

      boolean ilb = true;

      TransactionBlock it= DSObj.bChain.lastBlock;

       while(it!=a.coinsrc_block)
       {
        for(int i=0 ; i<it.trarray.length ; i++)
        {
          Transaction z= it.trarray[i];
          if(z.coinID.equals(a.coinID))ilb=false;
        }
        it=it.previous;
       }

       if(ilb==false)continue;

      boolean z= true;

      for(int i=0 ; i<tq.size() ; i++)
      {
        if(tq.get(i).coinID.equals(a.coinID))z=false;
      }

      if(z==false)continue;

      tq.add(a);
      
    }

    Transaction h= new Transaction();
    h.coinID= DSObj.latestCoinID;
    h.Source=null;
    h.Destination= this;
    h.coinsrc_block=null;

    tq.add(h);

    n=n+1;

    Transaction[] ok= new Transaction[n];

    for(int i=0 ; i<n ; i++)ok[i]=tq.get(i);

    TransactionBlock tb= new TransactionBlock(ok);

    DSObj.bChain.InsertBlock_Honest(tb);


    Pair<String, TransactionBlock> lp= new Pair<String, TransactionBlock>(null,null);
    lp.first=h.coinID;
    lp.second=tb;

    int yt=Integer.parseInt(lp.first);    int rand= this.mycoins.size();

        if(yt> Integer.parseInt(this.mycoins.get(rand-1).first))this.mycoins.add(lp); 
        else
        {
           for(int i=0 ; i<rand ; i++)
          { 
             int ck=Integer.parseInt(this.mycoins.get(i).first);  
            if(ck>yt)
            {
             this.mycoins.add(i,lp); 
             break;
            }

          }
        }
       
        int nm=Integer.parseInt(DSObj.latestCoinID);
        nm++;
        DSObj.latestCoinID= String.valueOf(nm);



  }  

  public void MineCoin(DSCoin_Malicious DSObj) throws EmptyQueueException {

    int n=DSObj.bChain.tr_count-1;

    //from the queue we need n valid transactions

    List<Transaction>tq= new ArrayList<Transaction>();

    while(tq.size()!=n)
    {
      Transaction a;
      if(DSObj.pendingTransactions.numTransactions==0) throw new EmptyQueueException();
      else a= DSObj.pendingTransactions.RemoveTransaction();

      TransactionBlock k= DSObj.bChain.FindLongestValidChain();
      //check validity of a

      boolean x= false;

      for(int i=0 ; i<a.coinsrc_block.trarray.length ; i++)
      {
        Transaction z= a.coinsrc_block.trarray[i];
        if(z.coinID.equals(a.coinID) && z.Destination.UID.equals(a.Source.UID))x=true; 
      }

      if(x==false)continue;

      boolean b= true;

      while(k!=a.coinsrc_block) //check in each_block
      {
        for(int i=0 ; i<k.trarray.length ; i++)
        {
          Transaction z= k.trarray[i];
          if(z.coinID.equals(a.coinID))b=false;
        }

         k=k.previous;
      }

      if(b==false)continue;

      tq.add(a);
      
    }

    Transaction h= new Transaction();
    h.coinID= DSObj.latestCoinID;
    h.Source=null;
    h.Destination= this;
    h.coinsrc_block=null;

    tq.add(h);

    n=n+1;

    Transaction[] ok= new Transaction[n];

    for(int i=0 ; i<n ; i++)ok[i]=tq.get(i);

    TransactionBlock tb= new TransactionBlock(ok);

    DSObj.bChain.InsertBlock_Malicious(tb);


    Pair<String, TransactionBlock> lp= new Pair<String, TransactionBlock>(null,null);
    lp.first=h.coinID;
    lp.second=tb;

    int yt=Integer.parseInt(lp.first);    int rand= this.mycoins.size();

        if(yt> Integer.parseInt(this.mycoins.get(rand-1).first))this.mycoins.add(lp); 
        else
        {
           for(int i=0 ; i<rand ; i++)
          { 
             int ck=Integer.parseInt(this.mycoins.get(i).first);  
            if(ck>yt)
            {
              this.mycoins.add(i,lp); 
             break;
            }

          }
        }
       
        int nm=Integer.parseInt(DSObj.latestCoinID);
        nm++;
        DSObj.latestCoinID= String.valueOf(nm);

  }  
}
