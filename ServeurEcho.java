import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.ListIterator;

public class ServeurEcho
{	
	// un tableau de tout les connexions 
    ArrayList<Connexion> MesConnexions = new ArrayList<>();
   
   // Methode qui envoit en console le message ecrit par un user a tout les 
   // utilisateurs connectés dans l'array list
	public static void EcrireDesMessages(String unMessage)
	{
		for(int cpt = 0 ; cpt < MesConnexions.size() ; cpt++ )
		{
			MesConnexions.get(cpt).EcrireLeMessage(unMessage);
		}
	}
   
	public void lancerServeur(int port)
	{
		try
		{
			Socket unSocket = null;
			ServerSocket socketServeur = null;
			
			Terminateur unTerminateur = new Terminateur();
			Thread unDeTerminateur = new Thread(unTerminateur);
			unDeTerminateur.setDaemon(true);
			unDeTerminateur.start();
			
			socketServeur = new ServerSocket(port);
			socketServeur.setSoTimeout(1000);
			
			
			while(unDeTerminateur.isAlive())
			{
				try
				{
					if(Connexion.cCourante <= Connexion.nbConn)
					{
						unSocket = socketServeur.accept();
						System.out.println("Connexion du client.");
						
						Connexion uneConnexion = new Connexion(unSocket);
						Thread unDeConnexion = new Thread(uneConnexion);
						unDeConnexion.setDaemon(true);
						unDeConnexion.start();
						
						MesConnexions.add(uneConnexion);
					}
				}
				catch (SocketTimeoutException  e)
                {
				
                }
			}	
			
			if(unSocket != null)
			{
				unSocket.close();
				socketServeur.close();
			}
		
			System.exit(1);
		}
		catch(IOException ioe)
		{
			System.err.println(ioe);
			System.exit(1);
		}
	}

	public static void main(String args[]) throws IOException
	{
		ServeurEcho unBeauServeur = new ServeurEcho();
		unBeauServeur.lancerServeur(50000);
	}
}
