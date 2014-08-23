
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import org.languagetool.JLanguageTool;
import org.languagetool.language.Spanish;
import org.languagetool.rules.RuleMatch;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author juan
 */
public class Main extends JFrame implements ActionListener {

    private JTextArea textfield1, textfield2;
    private JButton boton1;
    private JLanguageTool langTool;
    private JLabel label,label2;
    private String acu = "";

    public Main() throws IOException {
        setLayout(null);
        label = new JLabel("escriba el texto a chequear: ");
        label.setBounds(20, 0, 300, 15);
        add(label);
        textfield1 = new JTextArea();
        textfield1.setLineWrap(true);
        textfield1.setWrapStyleWord(true);
        textfield1.setBounds(20, 20, 600, 100);

        add(textfield1);
        textfield2 = new JTextArea();
        textfield2.setLineWrap(true);
        textfield1.setWrapStyleWord(true);
        
        label2 = new JLabel("correcciones sugeridas");
        label2.setBounds(20, 140, 300, 15);
        add(label2);
        
        textfield2.setBounds(20, 160, 600, 100);
        add(textfield2);
        boton1 = new JButton("verificar");
        boton1.setBounds(500, 270, 100, 50);
        add(boton1);

        langTool = new JLanguageTool(new Spanish());
        langTool.activateDefaultPatternRules();
        boton1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                List<RuleMatch> matches;
                try {
                    matches = langTool.check(textfield1.getText());
                    for (RuleMatch match : matches) {
                        System.out.println("Potential error at line "
                                + match.getLine() + ", column "
                                + match.getColumn() + ": ") ;
                                acu +=  match.getMessage()
                                +("Suggested correction: " + match.getSuggestedReplacements() + "\n ");
                        
                    }
                    textfield2.setText(acu);
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

    }

    public static void main(String[] ar) throws IOException {
        Main main = new Main();
        main.setSize(650, 370);
        main.setLocationRelativeTo(null);
        main.setVisible(true);
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
