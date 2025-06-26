package ar.edu.unlu.poo.vista;

import ar.edu.unlu.poo.controlador.Controlador;
import ar.edu.unlu.poo.modelo.Jugador;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class MenuLogginJugador extends JFrame {

    private final Controlador controller;
    private Jugador jugadorLocal;
    private JPanel panelPrincipal;
    private JTextPane imagenDeFondo;
    private JButton btnUnirmeAlLobby;
    private JButton btnCrearUnJugador;
    private JButton btnCargarUnJugadorDeMemoria;
    private JScrollPane scrollerPanel;
    private Image icono;
    private String tipoInterfaz;
    private Boolean visibilidad;

    public MenuLogginJugador(Controlador controller, String interfaz){
        this.controller = controller;
        controller.establecerVistaInicio(this);
        this.tipoInterfaz = interfaz;
        visibilidad = true;
        iniciarFavIcon();
        insertarImagenTextPane();
        setSize(1240, 720);
        setContentPane(panelPrincipal);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setVisible(visibilidad);

        setTitle(".                                                                                                           PANTALLA DE JUGADORES");
        btnUnirmeAlLobby.setText("UNIRSE AL LOBBY DEL BLACK JACK");
        btnCrearUnJugador.setText("CREAR UN NUEVO JUGADOR");
        btnCargarUnJugadorDeMemoria.setText("CARGAR JUGADOR GUARDADO");

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                int confirmacion = JOptionPane.showConfirmDialog(JOptionPane.getRootFrame(),"",
                        "Confirmar salida", JOptionPane.YES_NO_OPTION);

                if (confirmacion == JOptionPane.YES_OPTION) {

                    if (controller != null) {
                        controller.cerrarLaAplicacion();
                    }
                    System.exit(0);
                }
            }
        });

        btnUnirmeAlLobby.addActionListener(e -> {
            if(!controller.poseeJugador()){
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), " Usted todavia no cargo ningun jugador," +
                                "por favor, cargue un jugador \nantes de poder realizar esta accion.",
                        "MENSAJE DE ERROR!", JOptionPane.INFORMATION_MESSAGE);
            }

            else{

                try {
                    controller.establecerVistaLobby(tipoInterfaz);
                }

                catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }

                cambiarVisibilidad(visibilidad);
            }
        });

        btnCrearUnJugador.addActionListener(e -> {

            boolean validacion;

            if(!controller.poseeJugador()) {
                String nombre = JOptionPane.showInputDialog(null, "Ingrese nombre del nuevo jugador: ");
                validacion = controller.validarNombre(nombre);

                if(nombre == null){
                    return;
                }

                if (!validacion) {
                    JOptionPane.showMessageDialog(null,
                            "Nombre invalido! por favor ingrese un nombre como corresponde!.",
                            "ERROR DE INGRESO", JOptionPane.ERROR_MESSAGE);

                }

                else {
                    nombre = controller.generarIDUnico(nombre);
                    jugadorLocal = new Jugador(nombre, 1000.0);

                    JOptionPane.showMessageDialog(null, "Jugador creado correctamente!.",
                            "VALIDACION", JOptionPane.INFORMATION_MESSAGE);

                    controller.establecerJugador(jugadorLocal);
                }
            }

            else{
                JOptionPane.showMessageDialog(null, "Usted ya cargo un jugador!.",
                        "ACCION INVALIDA!", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCargarUnJugadorDeMemoria.addActionListener(e -> {

            if(!controller.poseeJugador()) {
                ArrayList<Jugador> jugadores = controller.obtenerJugadoresGuardados();

                if (jugadores.isEmpty()) {
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), " No hay jugadores guardados aun, debera crear" +
                                    "un jugador nuevo!.",
                            "MENSAJE DE ERROR!", JOptionPane.INFORMATION_MESSAGE);
                }

                else {

                    jugadorLocal = (Jugador) JOptionPane.showInputDialog(
                            null,
                            "Seleccione al jugador que representara: ", "JUGADORES GUARDADOS",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            jugadores.toArray(),
                            null
                    );

                    if(jugadorLocal == null){
                        return;
                    }

                    controller.eliminarJugadorGuardado(jugadorLocal);

                    controller.establecerJugador(jugadorLocal);
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), " Jugador cargado con exito!.",
                            "VALIDACION", JOptionPane.INFORMATION_MESSAGE);
                }
            }

            else{
                JOptionPane.showMessageDialog(null, "Usted ya cargo un jugador!.",
                        "ACCION INVALIDA!", JOptionPane.ERROR_MESSAGE);
            }
        });

    }

    public void cambiarVisibilidad(Boolean visibilidad) {
        visibilidad = !visibilidad;
        setVisible(visibilidad);
    }

    private void iniciarFavIcon(){
        icono = new ImageIcon(getClass().getResource("/ar/edu/unlu/poo/imagenes/images.png")).getImage();
        icono = icono.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
    }

    private void insertarImagenTextPane(){
        imagenDeFondo.setEditable(true);

        StyledDocument doc = imagenDeFondo.getStyledDocument();
        Style st = imagenDeFondo.addStyle("IconStyle", null);

        try {
            doc.insertString(doc.getLength(), "\n\n\n\n\n\n\n\n\n\n\n\n.    \t\t                                   ", null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        ImageIcon fondo = new ImageIcon(getClass().getResource("/ar/edu/unlu/poo/imagenes/images.png"));
        Image image = fondo.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
        fondo = new ImageIcon(image);

        StyleConstants.setIcon(st, fondo);
        try {
            doc.insertString(doc.getLength(), " ", st);
        }

        catch (BadLocationException e) {
            e.printStackTrace();
        }

        scrollerPanel.setViewportView(imagenDeFondo);
        scrollerPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollerPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        imagenDeFondo.setEditable(false);
    }
}
