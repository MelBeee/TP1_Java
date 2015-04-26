import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by M�lissa on 2015-04-25.
 */
public class GetMessageServeur  implements Runnable{
    BufferedReader reader = null;
    JTextArea TA_Chat = null;

    GetMessageServeur(BufferedReader bufferedReader, JTextArea textArea)
    {
        reader = bufferedReader;
        TA_Chat = textArea;
    }

    public synchronized void run()
    {
        try
        {
            String duText = null;
            while(!Thread.interrupted() && (duText = reader.readLine()) != null && duText.trim().length() != 0)
            {
                TA_Chat.setText(TA_Chat.getText() +  duText + "\n");
            }

            TA_Chat.setText(TA_Chat.getText() + "Vous �tes maintenant d�connect�" + "\n");
        }
        catch(IOException ex)
        {
            System.err.println(ex);
        }
    }
}
