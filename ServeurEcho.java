import java.io.*;
import java.net.*;

public  class ServeurEcho
{	
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
			System.out.println("Serveur echo en attente d'une connexion.");
			
			
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
		unBeauServeur.lancerServeur(7);
	}
}













