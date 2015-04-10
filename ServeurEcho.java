// ServeurEcho.java
// Fait par Melissa Boucher et Francis Thibodeau 
// 27 mars 2015 
// Collège Lionel Groulx

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.ListIterator;

public class ServeurEcho
{	
	// un tableau de tout les connexions 
    static ArrayList<Connexion> MesConnexions = new ArrayList<>();
	// port utilisé pour la connexion au serveur
	static final int PORT = 50000;
	// temps avant que le serveur kick un user
	final int TEMPS_TIMEOUT = 90000;
   
   // Methode qui envoit en console le message ecrit par un user a tout les 
   // utilisateurs connectés dans l'array list
	public static synchronized void EcrireDesMessages(String unMessage)
	{
		for(int cpt = 0 ; cpt < MesConnexions.size() ; cpt++ )
		{
			MesConnexions.get(cpt).EcrireLeMessage(unMessage);
		}
	}
   
   // Methode qui fait pas mal tout 
	public void lancerServeur(int port)
	{
		try
		{
			// Creation des sockets 
			Socket unSocket = null;
			ServerSocket socketServeur = null;
			
			// creation et initialisation et affectation du thread de serveur
			Terminateur unTerminateur = new Terminateur();
			Thread unDeTerminateur = new Thread(unTerminateur);
			unDeTerminateur.setDaemon(true);
			unDeTerminateur.start();
			
			// affectation du socket de serveur
			socketServeur = new ServerSocket(port);
			socketServeur.setSoTimeout(1000);
			
			// tant et aussi longtemps que le thread serveur est vivant 
			while(unDeTerminateur.isAlive())
			{
				try
				{
				// si le nombre maximal de connexion n'est pas atteinte
					if(Connexion.cCourante <= Connexion.NBCONN)
					{
					// on accepte un nouveau socket (un nouveau client)
						unSocket = socketServeur.accept();
						unSocket.setSoTimeout(TEMPS_TIMEOUT);
						System.out.println("Connexion du client.");
						
					// on créer une instance de connexion associé a ce socket
						Connexion uneConnexion = new Connexion(unSocket, this);
						Thread unDeConnexion = new Thread(uneConnexion);
						unDeConnexion.setDaemon(true);
						unDeConnexion.start();
						
					//on l'ajoute a notre array de connexion
						MesConnexions.add(uneConnexion);
					}
				}
				catch (SocketTimeoutException  e)
                {
				
                }
			}	
			// on ferme nos sockets
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
	
	// main qui appele lancerServeur et c'est pas mal ca
	public static void main(String args[]) throws IOException
	{
		new ServeurEcho().lancerServeur(PORT);
	}
}
