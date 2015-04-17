// Connexion.java
// Fait par Melissa Boucher et Francis Thibodeau 
// 27 mars 2015 
// Collège Lionel Groulx

import  java.io.*;
import java.net.*;
import java.lang.Object;

public class Connexion implements Runnable
{
    // nbre de connexion maximale
    public static final int NBCONN = 4;
    // nbre de connexion présentement utilisé
    public static int cCourante = 0;
    // variable utilisé pour stocké ce que l'utilisateur veux écrire
    private String uneLigne = null;
    // username de l'instance de la connexion
    private String username = null;
    // variable constante pour les limites du username et d'une ligne
    private static final int MAX_USERNAME = 8;
    private static final int MIN_USERNAME = 1;
    private static final int MAX_CHAR = 80;
    private static final int MIN_CHAR = 0;
    // writer et reader qui permettre d'ecrire ou de lire en console
    private BufferedReader reader;
    private PrintWriter writer;
    // socket de l'utilisateur
    private Socket unSocket = null;
    private ServeurEcho uneInstanceDeServeur;
    // est-ce qu'on quitte ou non
    boolean quitter = false;
    // est-ce qu'on envoye ou non
    boolean envoyer = true;

    public String getUsername()
    {
        return username;
    }

    //---- Constructeur de Connexion ----\\
    // prend en parametere un socket et une instance de ServeurEcho pour avoir accès au méthode de cette derniere.
    public Connexion(Socket unSocketUtilise, ServeurEcho unServeur)
    {
        // affectation des variables
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
        // on augmente le nombre d'instance de connexion
        cCourante++;
    }

    //---- Verification du username ----\\
    // Sert a verifier que notre username est correctement entré.
    private void VerifierUsername()
    {
        if(username.length() >  MAX_USERNAME) // si ca depasse la limite, on tronque
            username = username.substring(0, MAX_USERNAME);
        else if(username.length() <= MIN_USERNAME) // si c'est sous la limite, on utilise l'adresse ip
            username = unSocket.getInetAddress().getHostAddress();
    }

    //---- Verification du message ----\\
    // Sert a verifier que notre message est correctement ecrit
    private void VerifierLigne()
    {
        envoyer = true;
        if(uneLigne.length() > MAX_CHAR) // si ca depasse la limite, on tronque
        {
            uneLigne = uneLigne.substring(MIN_CHAR, MAX_CHAR);
        }
        else if(uneLigne.isEmpty()) // si l'utilisateur entre une ligne vide, ca veux dire qu'il veux quitter
        {
            quitter = true; // on veux quitter
            envoyer = false; // on envoit pas
        }
        else if(uneLigne.trim().length() == MIN_CHAR) // si une ligne est composé d'espace seulement on envoit pas
        {
            envoyer = false; // on envoit pas
        }
    }

    //---- Ecrire le message ----\\
    // Écrit le message envoyer pas un autre utilisateur dans la console de putty (dans ce cas ci)
    public void EcrireLeMessage(String Message)
    {
        writer.println(Message);
        writer.flush();
    }

    public void run()
    {
        try
        {
            //demande du nom d'utilisateur
            writer.print("Entrez votre nom d'utilisateur: ");
            writer.flush();

            username = reader.readLine();
            VerifierUsername();

            // message d'acceuil dans le channel
            uneInstanceDeServeur.EcrireDesMessages(username + " viens de joindre la conversation.");

            do // tant que l'utilisateur ne quitte pas, on attend qu'il écrit un message
            {
                // get le message
                uneLigne = reader.readLine();
                VerifierLigne(); // verifie le message

                if(envoyer)  // si il est correctement écrit on l'envoit
                    uneInstanceDeServeur.EcrireDesMessages(username + ": " + uneLigne);

            }while(!quitter);
        }
        catch(IOException ioe)
        {

        }
        catch(NullPointerException ioe)
        {

        }
        finally
        {
            try
            {
                // message de depart d'un utilisateur dans le chat
                uneInstanceDeServeur.EcrireDesMessages(username + " viens de se deconnecter.");
                // fermeture des writer/reader et socket
                reader.close();
                writer.close();
                unSocket.close();
                // decremente le nombre d'instance de connexion
                cCourante --;
                // affichage dans la console serveur qu'un client s'est déconnecté
                System.out.println("Client deconnecte");
            }
            catch(IOException ioe)
            {

            }
        }
    }
}

