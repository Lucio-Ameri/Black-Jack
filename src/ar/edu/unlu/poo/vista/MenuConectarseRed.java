package ar.edu.unlu.poo.vista;

import ar.edu.unlu.poo.servidor.ClienteApp;
import ar.edu.unlu.poo.servidor.ServidorApp;

import javax.swing.*;
import java.rmi.RemoteException;
import java.awt.*;


public class MenuConectarseRed extends JFrame {
    private JPanel panelPrincipal;
    private Image icono;
    private JLabel titulo;
    private JButton btnCrearServidor;
    private JButton btnUnirseServidor;
    private JButton btnSaberMas1;
    private JButton btnSaberMas2;

    public MenuConectarseRed(){
        iniciarFavIcon();
        setContentPane(panelPrincipal);
        setIconImage(icono);
        setVisible(true);
        setSize(700, 350);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle(".                                                                                     BLACK JACK");

        titulo.setText("MENU    -  UNIRSE AL JUEGO  -");
        btnCrearServidor.setText("CREAR NUEVO SERVIDOR");
        btnUnirseServidor.setText("UNIRSE A UN SERVIDOR EXISTENTE");
        btnSaberMas1.setText("?");
        btnSaberMas2.setText("?");


        btnCrearServidor.addActionListener(e -> {
            dispose();
            try {
                new ServidorApp();
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
            new ClienteApp();
        });

        btnUnirseServidor.addActionListener(e -> {
            dispose();
            new ClienteApp();
        });

        btnSaberMas1.addActionListener(e -> {
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "¡IMPORTANTE! : Al realizar esta accion, se creara un servidor " +
                            "con las especificaciones indicadas.\n por favor, recuerde los datos colocados del servidor, para poder ponerlos" +
                            " cuando tengamos\n que indicarlos luego al crear el cliente correspondiente.",
                    "INFORMACION SOBRE CREAR UN SERVIDOR", JOptionPane.INFORMATION_MESSAGE);
        });

        btnSaberMas2.addActionListener(e -> {
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "¡IMPORTANTE! : Al realizar esta accion, solo crearemos el cliente" +
                            " por lo que \ndeberia conocer los datos del servidor al que nos incluiremos. Por favor, coloquelos \nen sus lugares" +
                            " correspondientes para poder dar inicio al juego.",
                    "INFORMACION SOBRE UNIRSE A UN SERVIDOR", JOptionPane.INFORMATION_MESSAGE);
        });


    }

    private void iniciarFavIcon(){
        icono = new ImageIcon(getClass().getResource("/ar/edu/unlu/poo/imagenes/images.png")).getImage();
        icono = icono.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
    }
}
