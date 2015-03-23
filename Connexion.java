import  java.io.*;
import java.net.*;

public class Connexion implements Runnable
{
	public Socket unSocket = null; 
    public Connexion(Socket unSocketUtilise)
    {
		unSocket = unSocketUtilise;
    }

	public static final int nbConn = 2;
	public static int cCourante = 0;
	
	//public static void decrementConnexion()
	//{
	//	cCourante--;
	//}
	
    public void run()
    {
		try
		{
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(unSocket.getOutputStream()));
			BufferedReader reader = new BufferedReader(new InputStreamReader(unSocket.getInputStream()));
			String uneLigne = new String();
			cCourante ++;
			do
			{
				uneLigne = reader.readLine();
				writer.println(uneLigne);
				writer.flush();
			}while(uneLigne != null && !uneLigne.isEmpty());
			
			writer.close();
			reader.close();

			System.out.println("Client deconnecte");
		}
		catch(IOException ioe)
		{
			System.err.println("Fermeture innattendue de session sans fermer la connexion");
			System.exit(1);
		}	
		
		finally
		{
			try
			{
				unSocket.close();
				cCourante --;
			}
			catch(IOException ioe)
			{ 
				
			}
		}
	}
}

