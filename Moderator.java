package DSCoinPackage;

import HelperClasses.*;
import java.util.*;

public class Moderator
 {

  public void initializeDSCoin(DSCoin_Honest DSObj, int coinCount) {

    int lol= 100000 + coinCount;
    DSObj.latestCoinID= String.valueOf(lol);

    int a= 100000;  //start with this coin
    int mem=0;

    int b= 100000;
    int goa=0;

    int n= DSObj.memberlist.length;
    
    int cnt= DSObj.bChain.tr_count;

    int tot= coinCount/cnt; 
    //'tot' numbers of transaction block, each having 'cnt' numbers of transactions.

    for(int i=0 ; i<tot ; i++)
    {
      Transaction[] ok= new Transaction[cnt];

      for(int j=0 ; j<cnt ; j++)
      {
        //create a transaction here
        Transaction h= new Transaction();
        h.coinsrc_block=null;
        h.coinID= String.valueOf(a); a++;
        h.Destination= DSObj.memberlist[mem%n]; mem++;

        Members poo= new Members();
        poo.UID= "Moderator";
        poo.mycoins = new ArrayList<Pair<String,TransactionBlock>>();
        poo.in_process_trans = new Transaction[100];
        
        h.Source= poo;

        ok[j]= h;
      }
      
      TransactionBlock tb=new TransactionBlock(ok);   
          
      DSObj.bChain.InsertBlock_Honest(tb);

      //change my coin list also here.

      for(int j=0 ; j<cnt ; j++)
      { 
        Pair<String, TransactionBlock> pk= new Pair<String, TransactionBlock>(String.valueOf(b), tb);
        DSObj.memberlist[goa%n].mycoins.add(pk);


        b++; goa++;
      }     

    }

  }
    
  public void initializeDSCoin(DSCoin_Malicious DSObj, int coinCount) {

    int lol= 100000 + coinCount;
    DSObj.latestCoinID= String.valueOf(lol);

    int a= 100000;  //start with this coin
    int mem=0;

    int b=100000;
    int goa=0;

    int n= DSObj.memberlist.length;
    
    int cnt= DSObj.bChain.tr_count;

    int tot= coinCount/cnt; 
    //'tot' numbers of transaction block, each having 'cnt' numbers of transactions.

    for(int i=0 ; i<tot ; i++)
    {
      Transaction[] ok= new Transaction[cnt];

      for(int j=0 ; j<cnt ; j++)
      {
        //create a transaction here
        Transaction h= new Transaction();
        h.coinsrc_block=null;
        h.coinID= String.valueOf(a); a++;
        h.Destination= DSObj.memberlist[mem%n]; mem++;

        Members poo= new Members();
        poo.UID= "Moderator";
        
        h.Source= poo;

        ok[j]= h;
      }

      TransactionBlock tb= new TransactionBlock(ok);
      DSObj.bChain.InsertBlock_Malicious(tb);

      //change my coin list also here.

      for(int j=0 ; j<cnt ; j++)
      {
        Pair<String, TransactionBlock> pk= new Pair<String, TransactionBlock>(String.valueOf(b), tb);
        DSObj.memberlist[goa%n].mycoins.add(pk);


        b++; goa++;
      }

      if(i==(tot-1))DSObj.bChain.lastBlocksList[0]=tb;

    }

  }
}
