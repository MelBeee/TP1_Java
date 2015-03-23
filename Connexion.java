import  java.io.*;
import java.net.*;

public class Connexion implements Runnable
{
	public Socket unSocket = null; 
    public Connexion(Socket unSocketUtilise)
    {
		unSocket = unSocketUtilise;
		cCourante++;
    }

	public static final int NBCONN = 7;
	public static int cCourante = 0;
	public String uneLigne = null;
	public String username = null;
	public static final int MAX_USER = 80;
	
    public void run()
    {
		try
		{
			//PrintWriter writer = new PrintWriter(new OutputStreamWriter(unSocket.getOutputStream()));
			BufferedReader reader = new BufferedReader(new InputStreamReader(unSocket.getInputStream()));
			
			username = reader.readLine();
			      if(username.length() >  NBCONN)
					username = username.substring(0, NBCONN);
					else if(username.isEmpty())
						username = unSocket.getInetAddress().getHostAddress();

			uneLigne = username + " viens de joindre la conversation";
			do
			{
				uneLigne = reader.readLine();
				if(!uneLigne.isEmpty())
					if(uneLigne.length() > MAX_USER)
						uneLigne = username + ": " + uneLigne.substring(0, MAX_USER);
					else
						uneLigne = username + ": " + uneLigne;
			}while(uneLigne != null && !uneLigne.isEmpty());
		}
		catch(IOException ioe)
		{
			//System.err.println("Fermeture innattendue de session sans fermer la connexion");
			System.exit(1);
		}	
		
		finally
		{
			try
			{
				reader.close();
				unSocket.close();
				cCourante --;
			}
			catch(IOException ioe)
			{ 
				
			}
		}
		System.out.println("Client déconnecté");
	}
}

