import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

/**
 * Created by 201029426 on 2015-04-17.
 */
public class Chat {
    private Timer ResterConnecter;
    private JTextField TB_AdresseIP;
    private JTextField TB_Username;
    private JCheckBox CKB_Connection;
    private JTextField TB_Message;
    private JButton BTN_Envoyer;
    private JButton BTN_Quitter;
    private JLabel LB_AdresseIP;
    private JTextArea TA_Chat;
    private JLabel LB_Usager;
    private JLabel LB_Message;
    private JButton BTN_Connexion;
    private JPanel rootPanel;
    boolean connecter;
    private final int MAX_IPADDRESS = 15; //123.456.789.123
    final int TIMEOUT = 5000;
    final int MAX_MESSAGE = 80;
    final int MAX_USERNAME = 8;
    Socket unSocket = null;
    PrintWriter writer = null;
    BufferedReader reader = null;
    Thread message = null;
    SwingWorker<Boolean, String> workerMessage;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Chatroom");
        frame.setContentPane(new Chat().rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public Chat() {
        TA_Chat.setEnabled(false);

        BTN_Quitter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (connecter) {
                    int reponse = JOptionPane.showConfirmDialog(null,
                            "Vous êtes connecté. Êtes-vous sur de vouloir quitter ?", "Attention !",
                            JOptionPane.YES_NO_OPTION);

                    if (reponse == JOptionPane.YES_OPTION) {
                        Deconnexion();
                        System.exit(1);
                    }
                } else {
                    System.exit(1);
                }
            }
        });

        BTN_Envoyer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EnvoyerMessage();
            }
        });

        ResterConnecter = new Timer(TIMEOUT, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(CKB_Connection.isSelected() && unSocket != null && !unSocket.isClosed())
                {
                    writer.println(" ");
                    writer.flush();
                }
            }
        });
        ResterConnecter.start();

        TB_Message.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    BTN_Envoyer.doClick();
                }
                super.keyTyped(e);
            }
        });

        BTN_Connexion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (BTN_Connexion.getText() == "Connexion") {
                    if (VerificationConnexion()) {
                        Connexion();
                    }
                } else {
                    if (connecter) {
                        Deconnexion();

                    }
                }
            }
        });

    }

    public boolean VerificationConnexion()
    {
        boolean valide = true;

        if(TB_AdresseIP.getText().isEmpty() || TB_AdresseIP.getText().length() > MAX_IPADDRESS)
        {
            JOptionPane.showMessageDialog(rootPanel,
                    "Adresse IP invalide", "Attention !",
                    JOptionPane.WARNING_MESSAGE );
            valide = false;
        }
        else if(TB_Username.getText().isEmpty())
        {
            JOptionPane.showMessageDialog(rootPanel,
                    "Entrez un nom d'utilisateur pour se connecter.", "Attention !",
                    JOptionPane.WARNING_MESSAGE );
            valide = false;
        }
        return valide;
    }

    public void Connexion()
    {
        TB_AdresseIP.setEnabled(false);
        TB_Username.setEnabled(false);
        BTN_Connexion.setText("Deconnection");
        connecter = true;
        InetSocketAddress address = null;

        if(unSocket == null || unSocket.isClosed())
        {
            try
            {
                address = new InetSocketAddress(TB_AdresseIP.getText(), 50000);
                unSocket = new Socket();

                unSocket.connect(address);

                writer = new PrintWriter(new OutputStreamWriter(unSocket.getOutputStream()));
                reader = new BufferedReader(new InputStreamReader(unSocket.getInputStream()));

                writer.println(TB_Username.getText());
                writer.flush();

                message = new Thread(new GetMessageServeur(reader, TA_Chat));
                message.setDaemon(true);

                demarrer();
            }
            catch(IOException ex)
            {
                JOptionPane.showMessageDialog(rootPanel,
                        "Impossible d'établir une connexion", "Attention !",
                        JOptionPane.WARNING_MESSAGE );
                unSocket = null;
            }
        }
        else
        {
            JOptionPane.showMessageDialog(rootPanel,
                    "Vous êtes déjà connecté", "Attention !",
                    JOptionPane.WARNING_MESSAGE );
        }
    }


    public void demarrer()
    {
        // la tâche produira un résultat final de type Boolean et des
        // résultats intermédiaires de type Integer
        SwingWorker<Boolean, String> workerFetchMessages
                = new SwingWorker<Boolean, String>() {

            // c'est ici que s'exécute la tâche trop longue pour
            // le thread de l'IUG
            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    String texte = null;
                    while (!Thread.interrupted() && (texte = reader.readLine()) != null && texte.trim().length() != 0) {
                        TA_Chat.setText(TA_Chat.getText() + texte + "\n");
                    }
                } catch (IOException ex) {
                    System.err.println(ex);
                }
                // résultat final du traitement (un boolean qui sera mis`
                // en boîte dans un Boolean
                return true;
            }

            // méthode exécutée à la fin du traitement et qui peut
            // modifier sans danger l'aspect de l'IUG
            protected void done() {
            }

            // reçoit les valeurs intermédiaires et met à jour sans
            // danger l'aspect de l'IUG
            @Override
            protected void process(List<String> chunks) {
                // les valeurs publiées sont passés dans une liste étant
                // donné qu'ils peuvent arriver en "chunks"
                for (int i = 0; i < chunks.size(); i++) {
                    TA_Chat.setText(TA_Chat.getText() + chunks.get(i) + "\n");
                }

                // met à jour le widget
            }
        };

        // démarrage du thread
        workerFetchMessages.execute();
    }

    public void Deconnexion()
    {
        BTN_Connexion.setText("Connexion");
        connecter = false;

        writer.println("");
        writer.flush();

        TB_AdresseIP.setEnabled(true);
        TB_Username.setEnabled(true);
    }

    public void EnvoyerMessage()
    {
        if (connecter) {
            if (TB_Message.getText().isEmpty()) {
                JOptionPane.showMessageDialog(rootPanel,
                        "Un message vide ne peut être envoyé.", "Attention !",
                        JOptionPane.WARNING_MESSAGE);
                try
                {
                    message.interrupt();
                    unSocket.close();
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            } else if (TB_Message.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(rootPanel,
                        "Un message sans contenu ne peut être envoyé.", "Attention !",
                        JOptionPane.WARNING_MESSAGE);
                try
                {
                    message.interrupt();
                    unSocket.close();
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
            else
            {
                if(TB_Message.getText().length() > MAX_MESSAGE)
                {
                    int reponse = JOptionPane.showConfirmDialog(null,
                            "Votre message est trop long, il va être raccourci. Continuer ?", "Attention !",
                            JOptionPane.YES_NO_OPTION);

                    if (reponse == JOptionPane.YES_OPTION) {
                        writer.println(TB_Message.getText());
                        writer.flush();
                    }
                }
                else
                {
                    writer.println(TB_Message.getText());
                    writer.flush();
                }
            }
        } else {
            JOptionPane.showMessageDialog(rootPanel,
                    "Vous ne pouvez envoyer de message si vous n'êtes pas connecté.", "Attention !",
                    JOptionPane.WARNING_MESSAGE);
        }
        TB_Message.setText("");
        TB_Message.grabFocus();
    }

}


