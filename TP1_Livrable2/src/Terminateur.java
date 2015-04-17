// Terminateur.java
// 
// Fait par Melissa Boucher et Francis Thibodeau 
// 20 Fevrier 2015
import java.io.*;

public class Terminateur implements Runnable
{
    public void run()
    {
        try
        {
            // variable qui va contenir le readline
            String keyPress = new String();
            // buffer pour lire l'entrée au clavier
            BufferedReader reader = new BufferedReader(new InputStreamReader( System.in ) );

            // on lit se qui est entré au clavier tant que l'utilisateur n'entre pas Q ou q
            while(!keyPress.toLowerCase().equals("q"))
            {
                keyPress = reader.readLine();
            }
        }
        catch(IOException e)
        {
            System.err.println(e);
            System.exit(1);
        }
    }
}