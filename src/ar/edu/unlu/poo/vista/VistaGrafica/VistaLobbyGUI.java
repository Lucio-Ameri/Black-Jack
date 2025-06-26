package ar.edu.unlu.poo.vista.VistaGrafica;

import ar.edu.unlu.poo.controlador.Controlador;
import ar.edu.unlu.poo.interfaces.IVista;
import ar.edu.unlu.poo.modelo.enumerados.Mensajes;

import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.List;


public class VistaLobbyGUI extends JFrame implements IVista {
    private Controlador controlador;
    private boolean visibilidad;
    private JPanel panelPrincipal;
    private Image icono;
    private JTextArea listaJugadoresConectados;
    private JTextArea datosDelJugadorLocal;
    private JButton btnIrmeLobby;
    private JButton btnRankingMundial;
    private JButton btnUnirmeRonda;
    private JButton btnUnirmeListaDeEspera;
    private JButton btnRetirarSolicitudDeEspera;
    private JLabel tituloMenu;
    private JLabel notificaciones;
    private JScrollPane scrollPaneConectados;
    private JScrollPane scrollPaneDatosJugador;


    public VistaLobbyGUI(Controlador c) throws RemoteException {
        this.controlador = c;
        visibilidad = true;
        iniciarFavIcon();
        setSize(1240, 720);
        setContentPane(panelPrincipal);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setVisible(visibilidad);

        setTitle(".                     \t\t\t\t                                                                                      BLACKJACK LOBBY");
        btnIrmeLobby.setText("SALIR DEL LOBBY");
        btnRankingMundial.setText("RANKING MUNDIAL");
        btnUnirmeRonda.setText("UNIRSE A LA MESA");
        btnUnirmeListaDeEspera.setText("UNIRSE A LA LISTA DE ESPERA");
        btnRetirarSolicitudDeEspera.setText("SALIR DE LA LISTA DE ESPERA");
        notificaciones.setText("");
        tituloMenu.setText("BIENVENIDO AL LOBBY - BLACKJACK -");

        listaJugadoresConectados.setEditable(false);
        datosDelJugadorLocal.setEditable(false);
        establecerDatosJugador();

        btnIrmeLobby.addActionListener(e ->{
            cambiarVisibilidad(true);

            try{
                controlador.persistirJugadorEIrme();
            }

            catch (RemoteException ex){
                throw new RuntimeException();
            }
        });

        btnUnirmeRonda.addActionListener(e -> {
            String monto = JOptionPane.showInputDialog(null, "Ingrese el monto que apostara: ");
            switch (controlador.montoValido(monto)) {

                case NO_NUMERICO -> {
                    JOptionPane.showMessageDialog(null,
                            "Lo que ingreso no es un numero!.",
                            "ERROR DE INGRESO", JOptionPane.ERROR_MESSAGE);
                }

                case SIN_SALDO -> {
                    JOptionPane.showMessageDialog(null,
                            "Usted no posee saldo para jugar! deberia irse del casino!.",
                            "ADVERTENCIA", JOptionPane.ERROR_MESSAGE);
                }

                case SALDO_INSUFICIENTE -> {
                    JOptionPane.showMessageDialog(null,
                            "Usted no pose esa cantidad de dinero!.",
                            "ERROR DE INGRESO", JOptionPane.ERROR_MESSAGE);
                }

                case NUMERO_CORRECTO -> {
                    Mensajes m;

                    try{
                        m = controlador.unirJugadorALaMesa(Double.parseDouble(monto));
                    }
                    catch (RemoteException ex){
                        throw new RuntimeException();
                    }

                    switch (m){
                        case UNIRSE_LISTA_ESPERA -> {
                            JOptionPane.showMessageDialog(null,
                                    "Hay jugadores en lista de espera! por favor, si desea ingresar a la mesa\n" +
                                            "debe hacer fila uniendose a la 'LISTA DE ESPERA'!",
                                    "ERROR AL INTENTAR INGRESAR", JOptionPane.ERROR_MESSAGE);
                        }

                        case LA_RONDA_YA_EMPEZO -> {
                            JOptionPane.showMessageDialog(null,
                                    "La mesa ya esta en juego, por lo que deberia ingresar a la lista de espera\n" +
                                            "para poder ingresar lo antes posible!.",
                                    "ERROR AL INTENTAR INGRESAR", JOptionPane.ERROR_MESSAGE);
                        }

                        case NO_QUEDAN_LUGARES -> {
                            JOptionPane.showMessageDialog(null,
                                    "La mesa se quedo sin lugares disponibles! por favor, unase a la lista de espera\n" +
                                            "para que esto no vuelva a ocurrir y pueda jugar lo antes posible!",
                                    "ERROR AL INTENTAR INGRESAR", JOptionPane.ERROR_MESSAGE);
                        }

                        case ACCION_REALIZADA_EXITOSAMENTE -> {
                            cambiarVisibilidad(true);
                        }
                    }
                }
            }
        });

        btnUnirmeListaDeEspera.addActionListener(e -> {
            String monto = JOptionPane.showInputDialog(null, "Ingrese el monto que apostara: ");
            switch (controlador.montoValido(monto)) {

                case NO_NUMERICO -> {
                    JOptionPane.showMessageDialog(null,
                            "Lo que ingreso no es un numero!.",
                            "ERROR DE INGRESO", JOptionPane.ERROR_MESSAGE);
                }

                case SIN_SALDO -> {
                    JOptionPane.showMessageDialog(null,
                            "Usted no posee saldo para jugar! deberia irse del casino!.",
                            "ADVERTENCIA", JOptionPane.ERROR_MESSAGE);
                }

                case SALDO_INSUFICIENTE -> {
                    JOptionPane.showMessageDialog(null,
                            "Usted no pose esa cantidad de dinero!.",
                            "ERROR DE INGRESO", JOptionPane.ERROR_MESSAGE);
                }

                case NUMERO_CORRECTO -> {
                    Mensajes m;

                    try {
                        m = controlador.unirJugadorListaDeEspera(Double.parseDouble(monto));
                    } catch (RemoteException ex) {
                        throw new RuntimeException();
                    }

                    switch (m) {
                        case RONDA_ACEPTANDO_INSCRIPCIONES -> {
                            JOptionPane.showMessageDialog(null,
                                    "La mesa acepta incripciones! apurate.",
                                    "ERROR AL INTENTAR INGRESAR", JOptionPane.ERROR_MESSAGE);
                        }

                        case JUGADOR_EN_SALA_RONDA -> {
                            JOptionPane.showMessageDialog(null,
                                    "Usted ya se encuentra en la mesa. No haga trampa!",
                                    "ERROR AL INTENTAR INGRESAR", JOptionPane.ERROR_MESSAGE);
                        }

                        case JUGADOR_YA_EN_LISTA -> {
                            JOptionPane.showMessageDialog(null,
                                    "Usted ya posee una solicitud en la lista de espera!",
                                    "ERROR AL INTENTAR INGRESAR", JOptionPane.ERROR_MESSAGE);
                        }

                        case ACCION_REALIZADA_EXITOSAMENTE -> {
                            establecerDatosJugador();

                            try {
                                datosDelJugadorLocal.append("POSICION EN LA LISTA DE ESPERA: [" + controlador.posicionEspera() + "]\n");
                                notificaciones.setText("... ENTRO EN LA LISTA DE ESPERA SATISFACTORIAMENTE ...");
                            }

                            catch (RemoteException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                    }
                }
            }
        });

        btnRetirarSolicitudDeEspera.addActionListener(e ->{

            try {
                if(!controlador.removerSolicitud()){
                    JOptionPane.showMessageDialog(null,
                            "Usted no tiene una solicitud en la lista de espera!",
                            "ERROR", JOptionPane.ERROR_MESSAGE);
                }
                else{
                    establecerDatosJugador();
                    notificaciones.setText("... SALIO CORRECTAMENTE DE LA LISTA DE ESPERA ...");
                }
            }
            catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        });

        btnRankingMundial.addActionListener(e ->{
            List<String> ranking = controlador.getRankingHistorico();

            if(ranking.isEmpty()){
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), " Todavia no hay nadie que halla obtenido un puntaje" +
                                "mayor\n a 1300 puntos como para figurar en el mismo!.",
                        "MENSAJE INFORMATIVO", JOptionPane.INFORMATION_MESSAGE);
            }

            else {
                JFrame v = new JFrame("----- RANKING TOP MUNDIAL -----");
                new VentanaRanking(ranking, v);
            }
        });
    }

    private void iniciarFavIcon(){
        icono = new ImageIcon(getClass().getResource("/ar/edu/unlu/poo/imagenes/images.png")).getImage();
        icono = icono.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
    }

    @Override
    public void cambiarVisibilidad(Boolean visibilidad) {
        visibilidad = !visibilidad;
        setVisible(visibilidad);
    }

    @Override
    public void cambiarVisibilidad() {

    }

    @Override
    public void actualizarConectados(List<String> jugadoresConectados) {

        listaJugadoresConectados.setText("");
        listaJugadoresConectados.append("\t\t ---- JUGADORES CONECTADOS EN EL JUEGO ----\n");
        for(String s: jugadoresConectados){
            listaJugadoresConectados.append("\t\t" + s + "\n");
        }

        scrollPaneConectados.setViewportView(listaJugadoresConectados);
    }

    @Override
    public void actualizarLabelNotificador(String s) {
        notificaciones.setText(s);
    }

    @Override
    public void actualizarSaldoJugador() {
        establecerDatosJugador();
    }

    @Override
    public void actualizarTablero() throws RemoteException {

    }

    @Override
    public void ventanaResultados() {

    }

    @Override
    public void ventanaReInscripcion() {

    }

    private void establecerDatosJugador() {
        datosDelJugadorLocal.setText("");
        List<String> datos = controlador.getJugadorLocal();

        for(String s: datos){
            datosDelJugadorLocal.append("\t\t" + s + "\n");
        }

        scrollPaneDatosJugador.setViewportView(datosDelJugadorLocal);
    }

    private static class VentanaRanking extends JDialog{

        public VentanaRanking(java.util.List<String> ranking, JFrame ventana) {
            super(ventana, "---- RANKING TOP MUNDIAL ----", true);
            setSize(400, 300);
            setLocationRelativeTo(ventana);

            String[] r = ranking.toArray(new String [0]);

            JList<String> listaRankings = new JList<>(r);
            listaRankings.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


            JScrollPane scrollPane = new JScrollPane(listaRankings);
            scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


            JButton btnCerrar = new JButton("Cerrar");
            btnCerrar.addActionListener(e -> dispose());

            JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            panelBoton.add(btnCerrar);


            setLayout(new BorderLayout());
            add(scrollPane, BorderLayout.CENTER);
            add(panelBoton, BorderLayout.SOUTH);

            setVisible(true);
        }
    }
}

