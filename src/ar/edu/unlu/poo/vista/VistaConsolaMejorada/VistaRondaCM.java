package ar.edu.unlu.poo.vista.VistaConsolaMejorada;

import ar.edu.unlu.poo.controlador.Controlador;
import ar.edu.unlu.poo.interfaces.IVista;
import ar.edu.unlu.poo.modelo.Carta;
import ar.edu.unlu.poo.modelo.Jugador;
import ar.edu.unlu.poo.modelo.ManoJugador;
import ar.edu.unlu.poo.modelo.enumerados.*;

import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.List;

public class VistaRondaCM extends JFrame implements IVista {
    private Controlador controlador;
    private Image icono;
    private JPanel panelPrincipal;
    private JPanel panelNorte;
    private JLabel titulo;
    private JLabel notificaciones;
    private JPanel panelCentro;
    private JScrollPane visualizadorCartas;
    private JTextArea mesa;
    private JPanel panelSur;
    private JButton btnPedirCarta;
    private JButton btnDoblarMano;
    private JButton btnQuedarme;
    private JButton btnAsegurarme;
    private JButton btnSepararMano;
    private JButton btnRendirme;
    private JButton btnIrmeMesa;
    private JButton btnConfirmarParticipacion;
    private JButton btnRetirarMano;
    private JButton btnApostarMano;
    private JButton btnPasarTurno;
    private JTextArea datosDelJugador;
    private JScrollPane panelJugador;

    public VistaRondaCM(Controlador c) throws RemoteException {
        this.controlador = c;
        setVisible(true);
        iniciarFavIcon();
        setSize(1240, 720);
        setContentPane(panelPrincipal);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        setTitle(".\t\t\t\t\t\t\t\t\t\t  - BLACK JACK -");
        titulo.setText("- MESA DE JUEGO BLACK JACK -");
        notificaciones.setText("");
        mesa.setEditable(false);
        mesa.setBackground(Color.BLACK);
        mesa.setForeground(Color.WHITE);
        mesa.setCaretColor(Color.WHITE);
        mesa.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 20));

        btnApostarMano.setText("APOSTAR UNA MANO");
        btnAsegurarme.setText("ASEGURARSE");
        btnConfirmarParticipacion.setText("CONFIRMAR PARTICIPACION");
        btnQuedarme.setText("QUEDARME");
        btnRendirme.setText("RENDIRME");
        btnDoblarMano.setText("DOBLAR MANO");
        btnIrmeMesa.setText("IRME DE LA MESA");
        btnPedirCarta.setText("PEDIR UNA CARTA");
        btnRetirarMano.setText("RETIRAR UNA MANO");
        btnSepararMano.setText("SEPARAR MANO");
        btnPasarTurno.setText("PASAR TURNO");
        datosDelJugador.setText("");
        datosDelJugador.setEditable(false);
        actualizarSaldoJugador();
        establecerTablero();

        btnSepararMano.addActionListener(e -> {
            Mensajes m;
            try {
                m = controlador.separarManoUsuario();
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }

            switch (m){
                case NO_ES_TURNO_INICIAL -> {
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), " No puede realizar la accion ya que no es el turno inicial de la mano!.",
                            "MENSAJE DE ERROR!", JOptionPane.ERROR_MESSAGE);
                }

                case NO_ES_SU_TURNO -> {
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), " No es su turno.",
                            "MENSAJE DE ERROR!", JOptionPane.ERROR_MESSAGE);
                }

                case NO_CUMPLE_CONDICION_SEPARAR -> {
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), " Su mano no cumple las condiciones para poder separar.",
                            "MENSAJE DE ERROR!", JOptionPane.ERROR_MESSAGE);
                }

                case SALDO_INSUFICIENTE -> {
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), " No posee suficiente saldo para realizar la accion.",
                            "MENSAJE DE ERROR!", JOptionPane.ERROR_MESSAGE);
                }

                case NO_TIENE_MAS_MANOS -> {
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), " No tiene mas manos, por favor pase el turno!.",
                            "MENSAJE DE ERROR!", JOptionPane.ERROR_MESSAGE);
                }

                case ACCION_REALIZADA_EXITOSAMENTE -> {
                    actualizarSaldoJugador();
                }
            }
        });

        btnRetirarMano.addActionListener(e -> {
            try {
                if(!controlador.jugadorConfirmo()) {

                    List<ManoJugador> manos = controlador.obtenerManosJugador();
                    ManoJugador mano = (ManoJugador) JOptionPane.showInputDialog(
                            null,
                            "Seleccione la mano que desea borrar: ", "MANOS APOSTADAS",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            manos.toArray(),
                            null
                    );

                    if(mano == null){
                        return;
                    }
                    Mensajes m;
                    m = controlador.borrarMano(mano);

                    switch (m){
                        case LA_RONDA_YA_EMPEZO -> {
                            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), " La mesa ya comenzo el juego! no puede realizar esta accion.",
                                    "MENSAJE DE ERROR!", JOptionPane.ERROR_MESSAGE);
                        }

                        case ACCION_INVALIDA_ULTIMA_MANO -> {
                            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), " Es su ultima mano! no puede realizar esta accion.",
                                    "MENSAJE DE ERROR!", JOptionPane.ERROR_MESSAGE);
                        }

                        case ACCION_REALIZADA_EXITOSAMENTE -> {
                            actualizarSaldoJugador();
                        }
                    }
                }

                else{
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), " Usted ya confirmo, debe esperar a que inicie la ronda!.",
                            "MENSAJE DE ERROR!", JOptionPane.ERROR_MESSAGE);
                }

            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        });

        btnPedirCarta.addActionListener( e -> {
            Mensajes m;
            try {
                m = controlador.pedirCarta();
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }

            switch (m){
                case NO_ES_SU_TURNO -> {
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), " No es su turno.",
                            "MENSAJE DE ERROR!", JOptionPane.ERROR_MESSAGE);
                }

                case NO_TIENE_MAS_MANOS -> {
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Ya jugo todas sus manos, por favor! pase de turno.",
                            "MENSAJE DE ERROR!", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnPasarTurno.addActionListener(e -> {
            Mensajes m;
            try {
                m = controlador.pasarTurno();

            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }

                if(m == Mensajes.NO_ES_SU_TURNO){
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "No es su turno!.",
                            "MENSAJE DE ERROR!", JOptionPane.ERROR_MESSAGE);
                }
        });

        btnIrmeMesa.addActionListener(e -> {
            Mensajes m;
            try {
                m = controlador.irmeDeLaMesa();

            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }

            switch (m){
                case JUGADOR_YA_CONFIRMADO -> {
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), " Usted ya confirmo su participacion, no puede irse.",
                            "MENSAJE DE ERROR!", JOptionPane.ERROR_MESSAGE);
                }

                case LA_RONDA_YA_EMPEZO -> {
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Accion invalida! la mesa ya empezo el juego.",
                            "MENSAJE DE ERROR!", JOptionPane.ERROR_MESSAGE);
                }

                case ACCION_REALIZADA_EXITOSAMENTE -> {
                    dispose();
                }
            }
        });

        btnDoblarMano.addActionListener(e -> {
            Mensajes m;
            try {
                m = controlador.doblarMano();
            }

            catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }

            switch (m){
                case NO_ES_TURNO_INICIAL -> {
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "No es el turno inicial de la mano.",
                            "MENSAJE DE ERROR!", JOptionPane.ERROR_MESSAGE);
                }

                case NO_ES_SU_TURNO -> {
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "No es su turno.",
                            "MENSAJE DE ERROR!", JOptionPane.ERROR_MESSAGE);
                }

                case NO_TIENE_MAS_MANOS -> {
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), " Ya jugo todas sus manos, por favor pase el turno.",
                            "MENSAJE DE ERROR!", JOptionPane.ERROR_MESSAGE);
                }

                case SALDO_INSUFICIENTE -> {
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), " No posee suficiente saldo para realizar la accion.",
                            "MENSAJE DE ERROR!", JOptionPane.ERROR_MESSAGE);
                }

                case ACCION_REALIZADA_EXITOSAMENTE -> {
                    actualizarSaldoJugador();
                }
            }
        });

        btnRendirme.addActionListener(e -> {
            Mensajes m;
            try {
                m = controlador.rendirMano();
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }

            switch (m){
                case NO_TIENE_MAS_MANOS -> {
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Ya jugo todas sus manos, por favor pase el turno.",
                            "MENSAJE DE ERROR!", JOptionPane.ERROR_MESSAGE);
                }

                case NO_ES_SU_TURNO -> {
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), " No es su turno.",
                            "MENSAJE DE ERROR!", JOptionPane.ERROR_MESSAGE);
                }

                case NO_ES_TURNO_INICIAL -> {
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "No puede realizar la accion ya que no es el turno inicial de la mano.",
                            "MENSAJE DE ERROR!", JOptionPane.ERROR_MESSAGE);
                }
            }

        });

        btnQuedarme.addActionListener(e -> {
            Mensajes m;
            try {
                m = controlador.quedarMano();
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }

            switch (m){
                case NO_ES_SU_TURNO -> {
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "No es su turno.",
                            "MENSAJE DE ERROR!", JOptionPane.ERROR_MESSAGE);
                }

                case NO_TIENE_MAS_MANOS -> {
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Ya jugo todas sus manos, por favor pase el turno.",
                            "MENSAJE DE ERROR!", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnConfirmarParticipacion.addActionListener(e -> {
            Mensajes m;
            try {
                m = controlador.confirmarPArticipacion();
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }

            if(m == Mensajes.JUGADOR_YA_CONFIRMADO){
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Usted ya confirmo su participacion!.",
                        "MENSAJE DE ERROR!", JOptionPane.ERROR_MESSAGE);
            }

            else if(m == Mensajes.LA_RONDA_YA_EMPEZO){
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "La ronda ya comenzo, no puede realizar esta accion.",
                        "MENSAJE DE ERROR!", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnAsegurarme.addActionListener(e -> {
            Mensajes m;

            try {
                m = controlador.asegurarse();
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }

            switch (m){
                case NO_TIENE_MAS_MANOS -> {
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Ya jugo todas sus manos, por favor pase el turno.",
                            "MENSAJE DE ERROR!", JOptionPane.ERROR_MESSAGE);
                }

                case NO_ES_SU_TURNO -> {
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "No es su turno!.",
                            "MENSAJE DE ERROR!", JOptionPane.ERROR_MESSAGE);
                }

                case NO_ES_TURNO_INICIAL -> {
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "No es el turno inicial de la mano.",
                            "MENSAJE DE ERROR!", JOptionPane.ERROR_MESSAGE);
                }

                case DEALER_SIN_AS_PRIMERA -> {
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "El dealer no posee un As como primer carta, accion invalida!.",
                            "MENSAJE DE ERROR!", JOptionPane.ERROR_MESSAGE);
                }

                case SALDO_INSUFICIENTE -> {
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "No posee el suficiente saldo para realizar la accion.",
                            "MENSAJE DE ERROR!", JOptionPane.ERROR_MESSAGE);
                }

                case ACCION_YA_REALIZADA -> {
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Usted ya esta asegurado!.",
                            "MENSAJE DE ERROR!", JOptionPane.ERROR_MESSAGE);
                }

                case ACCION_REALIZADA_EXITOSAMENTE -> {
                    actualizarSaldoJugador();
                }
            }
        });

        btnApostarMano.addActionListener(e -> {
            Mensajes m;

            try {
                if(!controlador.jugadorConfirmo()) {
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
                            m = controlador.apostarOtraMano(Double.parseDouble(monto));

                            switch (m){
                                case ACCION_REALIZADA_EXITOSAMENTE -> {
                                    actualizarSaldoJugador();
                                }

                                case LA_RONDA_YA_EMPEZO -> {
                                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "La mesa ya inicio, no puede apostar mas manos.",
                                            "MENSAJE DE ERROR!", JOptionPane.ERROR_MESSAGE);
                                }

                                case NO_QUEDAN_LUGARES -> {
                                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "La mesa se quedo sin lugares disponibles.",
                                            "MENSAJE DE ERROR!", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        }
                    }
                }

                else{
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), " Usted ya confirmo, debe esperar a que inicie la ronda!.",
                            "MENSAJE DE ERROR!", JOptionPane.ERROR_MESSAGE);
                }
            }

            catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        });

    }



    private void establecerTablero() throws RemoteException {
        List<Jugador> jugadores = controlador.getListaJugadores();
        boolean turnoImprimido = false;
        if(jugadores.isEmpty()){
            mesa.setText("");
            mesa.append("\n\n\n\n\n\n\t\t\t\t ---- LA MESA TODAVIA NO EMPEZO ----\n");
        }

        else{

            String nombreTurnoActual = controlador.getNombreTurnoActual();
            mesa.setText("");

            for(Jugador j: jugadores){

                mesa.append("Jugador: " + controlador.getNombre(j) + "......");
                List<ManoJugador> manos = controlador.getListaDeManos(j);

                for(ManoJugador m: manos){
                    List<Carta> cartas = controlador.getCartasMano(m);
                    EstadoDeLaMano estado = controlador.getEstadoMano(m);

                    if(((estado == EstadoDeLaMano.TURNO_INICIAL) || (estado == EstadoDeLaMano.EN_JUEGO)) && (!turnoImprimido)){
                        turnoImprimido = true;
                        if(nombreTurnoActual.equals(controlador.getNombre(j))){
                            mesa.append("     MANO JUGANDO ----- > ");
                        }
                    }

                    for(Carta c: cartas){
                        boolean estadoCarta = controlador.getEstadCarta(c);

                        PaloCarta p = controlador.getPaloDeLaCarta(c);
                        ValorCarta v = controlador.getValorDeLaCarta(c);

                        mesa.append(obtenerCartaDigital(p, v, estadoCarta) + "  ");
                    }

                    mesa.append("     PUNTAJE: ");
                    int total = controlador.getTotalMano(m);
                    mesa.append("[" + total + "]\n");

                }
                mesa.append("\n\n");
            }


            List<Carta> cartas =  controlador.getManoDealer();
            for(Carta c: cartas){
                boolean estadoCarta = controlador.getEstadCarta(c);
                PaloCarta p = controlador.getPaloDeLaCarta(c);
                ValorCarta v = controlador.getValorDeLaCarta(c);

                mesa.append(obtenerCartaDigital(p, v, estadoCarta) + "  ");
            }

            mesa.append("     PUNTAJE: ");
            int total = controlador.getTotalManoDealer();
            mesa.append("[" + total + "]\n");
        }
    }

    @Override
    public void cambiarVisibilidad(Boolean visibilidad) {
    }

    @Override
    public void cambiarVisibilidad() {
        dispose();
    }

    @Override
    public void actualizarConectados(List<String> jugadoresConectados) {
    }

    @Override
    public void actualizarLabelNotificador(String s) {
        notificaciones.setText(s);
    }

    @Override
    public void actualizarSaldoJugador() {
        datosDelJugador.setText("");
        List<String> datos = controlador.getJugadorLocal();

        for(String s: datos){
            datosDelJugador.append("\t\t" + s + "\n");
        }

        panelJugador.setViewportView(datosDelJugador);
    }

    @Override
    public void actualizarTablero() throws RemoteException {
        establecerTablero();
    }

    @Override
    public void ventanaResultados() throws RemoteException {

        List<String> resultadosDeLaRonda = controlador.obtenerResultadosDeLaRonda();JOptionPane.showMessageDialog(
                null,
                resultadosDeLaRonda,
                "RESULTADOS DE LA RONDA!",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    @Override
    public void ventanaReInscripcion() throws RemoteException {
        int confirmacion = JOptionPane.showConfirmDialog(JOptionPane.getRootFrame(), "DESEA VOLVER A JUGAR EN LA MESA?",
                "RE INSCRIPCION", JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
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
                    controlador.reInscripcion(Double.parseDouble(monto), true);
                }
            }
        }

        else{
            controlador.reInscripcion(0, false);
        }
    }

    private String obtenerCartaDigital(PaloCarta p, ValorCarta v, boolean estadoCarta){
        String s = "[";
        if(estadoCarta) {
            switch (v) {
                case A -> {
                    s = s + "A";
                }

                case J -> {
                    s = s + "J";
                }

                case K -> {
                    s = s + "K";
                }

                case Q -> {
                    s = s + "Q";
                }

                case DOS -> {
                    s = s + "2";
                }

                case DIEZ -> {
                    s = s + "10";
                }

                case OCHO -> {
                    s = s + "8";
                }
                case SEIS -> {
                    s = s + "6";
                }

                case TRES -> {
                    s = s + "3";
                }

                case CINCO -> {
                    s = s + "5";
                }

                case NUEVE -> {
                    s = s + "9";
                }

                case SIETE -> {
                    s = s + "7";
                }

                case CUATRO -> {
                    s = s + "4";
                }
            }

            switch (p) {
                case PICAS -> {
                    s = s + "♠]";
                }

                case TREBOLES -> {
                    s = s + "♣]";
                }

                case CORAZONES -> {
                    s = s + "♥]";
                }

                case DIAMANTES -> {
                    s = s + "♦]";
                }
            }
        }

        else{
            s = s + "???]";
        }

        return s;
    }

    private void iniciarFavIcon(){
        icono = new ImageIcon(getClass().getResource("/ar/edu/unlu/poo/imagenes/images.png")).getImage();
        icono = icono.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
    }
}
