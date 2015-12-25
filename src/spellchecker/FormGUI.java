/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spellchecker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Hashim
 */
public class FormGUI extends JFrame {
    JTextField path;
    JTextField search;
    JTextField suggestion;
    JButton ok;
    JButton close;
    JButton Clear;
    JButton browse;
    
    JFileChooser jfile = new JFileChooser("Select Fies");
    Trie trie;        
    
    FormGUI(){
        this.setTitle("Spell Checker/Suggestion");
        setSize(550,150);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.GREEN);
        //setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("logo.png")));
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2,dim.height/2-this.getSize().height/2);
        
        jfile.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jfile.setVisible(false);
        jfile.setDialogType(JFileChooser.OPEN_DIALOG);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files","txt");
        jfile.setFileFilter(filter);
        
        
        Font f = new Font("Times New Roman",Font.BOLD,18);
        setLayout(new FlowLayout());
        JLabel l1 = new JLabel("File Path   ");
        l1.setFont(f);
        add(l1);
        path = new JTextField("",25);
        path.setPreferredSize(new Dimension(25,25));
        path.setEditable(false);
        path.setFont(f);
        add(path);
        
        
        JLabel l3 = new JLabel("Enter Word");
        l3.setFont(f);
        add(l3);
        search = new JTextField("",25);
        search.setPreferredSize(new Dimension(25,25));
        search.setFont(f);
        
        add(search);
        
        
        
        
        
        
        
        JLabel l2 = new JLabel("Suggestion");
        l2.setFont(f);
        add(l2);
        suggestion = new JTextField("",25);
        suggestion.setPreferredSize(new Dimension(25,25));
        suggestion.setFont(f);
        suggestion.setEditable(false);
        add(suggestion);
        
        MyHandler handler = new MyHandler();
        browse = new JButton("Browse");
        browse.addActionListener(handler);
        add(browse);
        
        ok = new JButton("Check");
        ok.addActionListener(handler);
        add(ok);
        
        Clear = new JButton("Clear");
        Clear.addActionListener(handler);
        add(Clear);
        
        close = new JButton("Exit");
        close.addActionListener(handler);
        add(close);
        ;
        
        
        
    
    }
    
    public class MyHandler implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getActionCommand().equalsIgnoreCase("Exit")){
                System.exit(0);
            }else if(e.getActionCommand().equalsIgnoreCase("clear")){
                //path.setText("");
                suggestion.setText("");
                search.setText("");
            }else if(e.getActionCommand().equalsIgnoreCase("check")){
                if(path.getText().isEmpty()){
                    JOptionPane.showMessageDialog(null,"Enter File/Dictionary Path ","Error",0);
                
                }else if(search.getText().isEmpty()){
                    JOptionPane.showMessageDialog(null,"Enter word to check ","Error",0);
                }else{
                     //trie = new Trie(path.getText());
                     String suggest = trie.suggestCorrection(search.getText().trim());
                    
                     suggestion.setText(suggest.trim());
                }
                
            }else if(e.getActionCommand().equalsIgnoreCase("browse")){
                jfile.setVisible(true);
                if(jfile.showOpenDialog(null)==JFileChooser.APPROVE_OPTION){
                    //System.out.println(jfile.getSelectedFile().getAbsolutePath()); 
                    //search.setText("Please wait.....");
                    jfile.setVisible(false);
                    path.setText(jfile.getSelectedFile().getAbsolutePath());
                    setFocusable(true);
                    
                    trie = new Trie(path.getText());
                    //search.setText("");
                }
            
            }
        }
    
    
    }
    
}
