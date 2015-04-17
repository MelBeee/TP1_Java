import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
        BTN_Quitter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(1);
            }
        });

        BTN_Envoyer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(TB_Message.getText().isEmpty())
                {
                    JOptionPane.showMessageDialog(rootPanel,
                            "Un message vide ne peut être envoyé.", "Attention !",
                            JOptionPane.WARNING_MESSAGE);
                }
                else if(TB_Message.getText().trim().isEmpty())
                {
                    // Ici on "reset" notre timeout
                }
                else
                {
                    // Ici on écrit le message dans le textbox
                }
            }
        });

        BTN_Connexion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(BTN_Connexion.getText() == "Connexion")
                {
                    if(TB_AdresseIP.getText().isEmpty() || TB_AdresseIP.getText().length() > MAX_IPADDRESS)
                    {
                        JOptionPane.showMessageDialog(rootPanel,
                                "Adresse IP invalide", "Attention !",
                                JOptionPane.WARNING_MESSAGE );
                    }
                    else if(TB_Username.getText().isEmpty())
                    {
                        JOptionPane.showMessageDialog(rootPanel,
                                "Entrez un nom d'utilisateur pour se connecter.", "Attention !",
                                JOptionPane.WARNING_MESSAGE );
                    }
                    else
                    {
                        if(!VerifierNomUser(TB_Username.getText()))
                        {
                            BTN_Connexion.setText("Deconnection");
                            connecter = true;

                            // ici on se connecte
                        }
                        else
                        {
                            JOptionPane.showMessageDialog(rootPanel,
                                    "Nom d'utilisateur déjà utilisé.", "Attention !",
                                    JOptionPane.WARNING_MESSAGE );
                        }
                    }
                }
                else
                {
                    if(connecter)
                    {
                        BTN_Connexion.setText("Connexion");
                        connecter = false;

                        // ici on se deconnecte
                    }
                    else
                    {
                        // ici yé pas senser avoir d'erreur
                    }
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
}


