
package komunikator_v3;
import java.io.*; 
import java.util.*; 
import java.net.*; 

public class Serwer {
    //Magazyn klientów
    static Vector<ClientHandler> ar = new Vector<>();
    //licznik klientów
    static int i =0;
    
    public static void main(String[] args) throws IOException  
    {
        ServerSocket ss = new ServerSocket(2222);
        
        Socket s;
        //Włączenie nieskończonej pętli nasłuchiwania klietów
        while(true)
        {
            s = ss.accept();
            
            System.out.println("Nowe zapytanie kliencie otrzymane: " + s);
            
            DataInputStream dis = new DataInputStream(s.getInputStream()); 
            DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
            
            System.out.println("Tworzenie nowego handlera dla klienta...");
            
            ClientHandler mtch = new ClientHandler(s,"client " + i, dis, dos);
            
            Thread t = new Thread(mtch);
            
            System.out.println("Dodawanie klienta do listy");
            
            ar.add(mtch);
            
            t.start();
            
            i++;
        }
    }  
}

class ClientHandler implements Runnable
{
    Scanner scn = new Scanner(System.in);
    private String name;
    final DataInputStream dis;
    final DataOutputStream dos;
    Socket s;
    boolean czyZalogowany;
    
    public ClientHandler(Socket s, String name, DataInputStream dis, DataOutputStream dos)
    {
        this.s =s;
        this.dis = dis;
        this.dos = dos;
        this.name = name;
        this.czyZalogowany = true;
    }
    
    @Override
    public void run()
    {
        String otrzymane;
        while(true)
        {
            try{
                otrzymane = dis.readUTF();
                System.out.println(otrzymane);
                
                if(otrzymane.equals("wyloguj"))
                {
                    this.czyZalogowany = false;
                    this.s.close();
                    break;
                }
                //rozbijanie stringa na części do interpretacji
                StringTokenizer st = new StringTokenizer(otrzymane, "#");
                String wiadomoscDoWyslania = st.nextToken();
                String odbiorca = st.nextToken();
                
                for(ClientHandler mc : Serwer.ar)
                {
                    if(mc.name.equals(odbiorca) && mc.czyZalogowany==true)
                    {
                        mc.dos.writeUTF(this.name+" : "+wiadomoscDoWyslania);
                        break;
                    }
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        try
        {
            this.dis.close();
            this.dos.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}