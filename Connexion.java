import  java.io.*;
import java.net.*;
import java.lang.Object;

public class Connexion implements Runnable
{
	public static final int NBCONN = 2;
	public static int cCourante = 0;
	public String uneLigne = null;
	public String username = null;
	public static final int MAX_USERNAME = 8;
	public static final int MIN_USERNAME = 1;
	public static final int MAX_CHAR = 80;
	public static final int MIN_CHAR = 0;
	public BufferedReader reader;
	public PrintWriter writer;
	public Socket unSocket = null; 
	public ServeurEcho uneInstanceDeServeur;
	
	
    public Connexion(Socket unSocketUtilise, ServeurEcho unServeur)
    {		
		unSocket = unSocketUtilise;
		uneInstanceDeServeur = unServeur;
		try
		{
			writer = new PrintWriter(new OutputStreamWriter(unSocket.getOutputStream()));
			reader = new BufferedReader(new InputStreamReader(unSocket.getInputStream()));
		}
		catch(IOException ioe)
		{
			System.err.println(ioe);
			System.exit(1);
		}
		cCourante++;
    }
	
    public void run()
    {
		boolean quitter = false; 
		boolean envoyer = true;
		try
		{	
			writer.print("Entrez votre nom d'utilisateur: ");
			writer.flush();
         
			username = reader.readLine();
			   if(username.length() >  MAX_USERNAME)
					username = username.substring(0, MAX_USERNAME);
				else if(username.length() <= MIN_USERNAME)
					username = unSocket.getInetAddress().getHostAddress();

			uneInstanceDeServeur.EcrireDesMessages(username + " viens de joindre la conversation.");
			do
			{
				uneLigne = reader.readLine();
            envoyer = true;
				if(uneLigne.length() > MAX_CHAR)
				{
					uneLigne = uneLigne.substring(MIN_CHAR, MAX_CHAR);	
				}
				else if(uneLigne.isEmpty())
				{
					quitter = true;
					envoyer = false;
				}
				else if(uneLigne.trim().length() == MIN_CHAR)
				{
					envoyer = false;
				}
				
				if(envoyer)
					uneInstanceDeServeur.EcrireDesMessages(username + ": " + uneLigne);
					
			}while(!quitter);
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
				uneInstanceDeServeur.EcrireDesMessages(username + " viens de se deconnecter.");
				reader.close();
				unSocket.close();
				cCourante --;
				System.out.println("Client deconnecte");
			}
			catch(IOException ioe)
			{ 
				
			}
		}
	}
	
	public void EcrireLeMessage(String Message)
	{
		writer.println(Message);
		writer.flush();
	}
}

