
package komunikator_v3;

import java.io.*; 
import java.net.*; 
import java.util.Scanner; 

public class Klient1 {
    
    final static int ServerPort = 2222;
    
    public static void main(String[] args) throws UnknownHostException, IOException  
    {
        Scanner scn = new Scanner(System.in);

        InetAddress ip = InetAddress.getByName("localhost");
            
        Socket s = new Socket(ip, 2222);
            
        DataInputStream dis = new DataInputStream(s.getInputStream()); 
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());

        Thread wyslijWiadomosc = new Thread(new Runnable()
        {
            @Override
            public void run(){
                while(true){
                    String msg = scn.nextLine();
                    
                    try{
                        dos.writeUTF(msg);
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }
        });
        
        Thread odczytajWiadomosc = new Thread(new Runnable()
        {
           @Override
           public void run(){
               while(true){
                   try{
                       String msg = dis.readUTF();
                       System.out.println(msg);
                   }catch(IOException e){
                       e.printStackTrace();
                   }
               }
           }
        });
        
        wyslijWiadomosc.start();
        odczytajWiadomosc.start();
    }
}
