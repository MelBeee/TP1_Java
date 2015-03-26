import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.ListIterator;

public class ServeurEcho
{	
	final int MIN_CHAR = 0;
	final int TEMPS_TIMEOUT = 50000;
	static final int PORT_SOCKET = 50000;
	PrintWriter writer = null;
	Socket unSocket = null;
	ServerSocket socketServeur = null;
	// un tableau de tout les connexions 
    static ArrayList<Connexion> MesConnexions = new ArrayList<>();
	String uneLigne = new String(); 
   
   // Methode qui envoit en console le message ecrit par un user a tout les 
   // utilisateurs connectés dans l'array list
	private synchronized void EcrireDesMessages()
	{
		if(!MesConnexions.isEmpty())
		{
			for(int cpt = 0 ; cpt < MesConnexions.size() ; cpt++ )
			{
				uneLigne = MesConnexions.get(cpt).uneLigne;
				if(uneLigne.trim().length() != MIN_CHAR)
				{
					DistribuerLeMessage();
					MesConnexions.get(cpt).uneLigne = null;
				}
				else if(uneLigne.isEmpty())
				{
					uneLigne = MesConnexions.get(cpt).username + "a quitte la conversation.";
					FermerUneSeuleConnexion(cpt);
					DistribuerLeMessage();
				}
			}
		}
	}
	
	private synchronized void DistribuerLeMessage()
	{
		try
		{
			if(uneLigne != null)
			{
				for(int cpt = 0 ; cpt < MesConnexions.size() ; cpt++ )
				{
					unSocket = MesConnexions.get(cpt).unSocket;
					writer = new PrintWriter(new OutputStreamWriter(unSocket.getOutputStream()));
					
					writer.println(uneLigne);
					writer.flush();
				}
			}
		}
		catch(IOException ioe)
		{
			
		}
	}
	
	private void FermerUneSeuleConnexion(int cpt)
	{
		try
		{
			unSocket = MesConnexions.get(cpt).unSocket;
			MesConnexions.remove(cpt);
			unSocket.close();
		}
		catch(IOException ioe)
		{
			System.err.println("Fermeture innattendue lors d'une tentative de fermeture d'une connexion.");
		}
	}
   
	public void lancerServeur(int port)
	{
		try
		{
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
					if(Connexion.nbreInstanceConnexion <= Connexion.NBRE_CONNEXION_MAX)
					{
						unSocket = socketServeur.accept();
	
						Connexion uneConnexion = new Connexion(unSocket);
						unSocket.setSoTimeout(TEMPS_TIMEOUT);
						
						Thread unDeConnexion = new Thread(uneConnexion);
						unDeConnexion.setDaemon(true);
						MesConnexions.add(uneConnexion);
						unDeConnexion.start();

						DemanderUsername();
						System.out.println("Connexion du client.");
					}
					else
					{
						System.out.println("Le maximum d'utilisateur a ete atteind. Veuillez reessayer plus tard.");
					}
				}
				catch (IOException ioe)
                {
				
                }
				catch(NullPointerException ioe)
				{
					System.err.println("Fermeture innattendue. Pointeur null rencontre.");
					System.exit(1);
				}
				finally
				{
					EcrireDesMessages();
				}
			}	
			FermerLesConnexions();
		}
		catch(IOException ioe)
		{
			System.err.println("Fermeture innattendue de session sans fermer la connexion.");
			System.exit(1);
		}		
	}
	
	private void FermerLesConnexions()
	{
		if(!MesConnexions.isEmpty())
		{
			for(int cpt = 0; cpt < MesConnexions.size() ; cpt++)
			{
				try
				{
					unSocket = MesConnexions.get(cpt).unSocket;
					unSocket.close();
				}
				catch(IOException ioe)
				{
					System.err.println("Erreur a la fermeture d'une connexion.");
				}
				catch(NullPointerException e)
				{
					System.err.println("Erreur a la fermeture d'une connexion.");
				}
			}
		}
	}
	
	private void DemanderUsername()
	{
		writer.println("Entrez votre nom d'utilisateur: ");
		writer.flush();
	}

	public static void main(String args[]) throws IOException
	{
		ServeurEcho unBeauServeur = new ServeurEcho();
		unBeauServeur.lancerServeur(PORT_SOCKET);
	}
}
