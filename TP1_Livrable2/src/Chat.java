import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Created by 201029426 on 2015-04-17.
 */
public class Chat {
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
    ServeurEcho serveur = new ServeurEcho();
    private final int MAX_IPADDRESS = 15; //123.456.789.123

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
                    Deconnexion();
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

        BTN_Connexion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (BTN_Connexion.getText() == "Connexion") {
                    if (VerificationConnexion()) {
                        if (!VerifierNomUser(TB_Username.getText())) {
                            Connexion();
                        } else {
                            JOptionPane.showMessageDialog(rootPanel,
                                    "Nom d'utilisateur déjà utilisé.", "Attention !",
                                    JOptionPane.WARNING_MESSAGE);
                        }
                    }
                } else {
                    if (connecter) {
                        Deconnexion();
                    }
                }
            }
        });

        TB_Message.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    EnvoyerMessage();
                }
            }
        });
    }

    public boolean VerifierNomUser(String nom)
    {
        boolean existe = false;

        if(!serveur.MesConnexions.isEmpty())
        {
            for(int i = 0; i < serveur.MesConnexions.size() || !existe; i++) {
                if (nom == serveur.MesConnexions.get(i).getUsername()) {
                    existe = true;
                }
            }
        }
        return existe;
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
        if(!CKB_Connection.isSelected())
        {
            //ici on set le timeout utilisateur si l'utilisateur n'a pas coché de rester connecté
        }
        // ici on se connecte
    }

    public void Deconnexion()
    {
        BTN_Connexion.setText("Connexion");
        connecter = false;

        // ici on se deconnecte
    }

    public void EnvoyerMessage()
    {
        if (connecter) {
            if (TB_Message.getText().isEmpty()) {
                JOptionPane.showMessageDialog(rootPanel,
                        "Un message vide ne peut être envoyé.", "Attention !",
                        JOptionPane.WARNING_MESSAGE);
            } else if (TB_Message.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(rootPanel,
                        "Un message sans contenu ne peut être envoyé.", "Attention !",
                        JOptionPane.WARNING_MESSAGE);
                // Ici on "reset" notre timeout
            } else {
                TA_Chat.setText(TA_Chat.getText() + "\n" + TB_Username.getText() + ": " + TB_Message.getText());
                TB_Message.setText("");

                // Ici on écrit le message dans le textbox
            }
        } else {
            JOptionPane.showMessageDialog(rootPanel,
                    "Vous ne pouvez envoyer de message si vous n'êtes pas connecté.", "Attention !",
                    JOptionPane.WARNING_MESSAGE);
            TB_Message.setText("");
        }
    }
}


