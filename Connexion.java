import  java.io.*;
import java.net.*;
import java.lang.Object;

public class Connexion implements Runnable
{	
	final int MAX_USERNAME = 8;
	final int MIN_USERNAME = 1;
	final int MAX_CHAR = 80;
	final int MIN_CHAR = 0;
	public static final int NBRE_CONNEXION_MAX = 5;
	public static int nbreInstanceConnexion = 0;
	
	public BufferedReader reader;
	public Socket unSocket = null; 
	public String uneLigne = null;
	public String username = null;
	private	boolean quitter = false; 
	private	boolean envoyer = true;
	
    public Connexion(Socket unSocketUtilise)
    {		
		unSocket = unSocketUtilise;
		nbreInstanceConnexion++;
    }
	
    public void run()
    {
		try
		{	
			reader = new BufferedReader(new InputStreamReader(unSocket.getInputStream()));
			
			username = reader.readLine();
			VerifierLongueurUsername();
			uneLigne = username + " viens de joindre la conversation.";
			
			do
			{
				uneLigne = reader.readLine();
				VerifierLongueurLigne();
				
				if(!uneLigne.isEmpty())
					uneLigne = username + ": " + uneLigne;
				else
					quitter = true;
					
			}while(!quitter);
		}
		catch(IOException ioe)
		{
			System.err.println("Fermeture innattendue de session sans fermer la connexion.");
			System.exit(1);
		}		
		catch(NullPointerException e)
        {
			System.err.println("Fermeture innattendue. Pointeur null rencontre.");
			System.exit(1);
        }
		finally
		{
			try
			{
				reader.close();
				unSocket.close();
				nbreInstanceConnexion --;
				System.out.println("Client deconnecte");
			}
			catch(IOException ioe)
			{ 
				
			}
		}
	}
	
	public void VerifierLongueurUsername()
	{
		if(username.length() >  MAX_USERNAME)
			username = username.substring(0, MAX_USERNAME);
		else if(username.length() <= MIN_USERNAME)
			username = unSocket.getInetAddress().getHostAddress();
	}
	
	public void VerifierLongueurLigne()
	{
		if(uneLigne.length() > MAX_CHAR)
		{
			uneLigne = uneLigne.substring(MIN_CHAR, MAX_CHAR);	
		}
	}
}

