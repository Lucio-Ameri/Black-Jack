package ar.edu.unlu.poo.vista;

import ar.edu.unlu.poo.controlador.Controlador;
import ar.edu.unlu.poo.interfaces.IVista;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.rmi.RemoteException;

public class VistaRondaGUI extends JFrame implements IVista {
    private Controlador controlador;
    private boolean visibilidad;
    private Image icono;

    // Paneles principales
    private JPanel panelPrincipal;
    private JPanel mesaPanel; // Panel donde se dibuja la mesa
    private JPanel controlesPanel; // Panel con botones de control

    // Elementos de la mesa
    private JLabel titulo;
    private JLabel notificaciones;
    private JLabel saldoLabel;
    private JLabel apuestaLabel;

    // Áreas para mostrar cartas
    private JPanel dealerPanel;
    private JPanel jugadorPanel;
    private JPanel otrosJugadoresPanel;

    // Botones
    private JButton btnPedirCarta;
    private JButton btnRendirme;
    private JButton btnDoblar;
    private JButton btnQuedarme;
    private JButton btnSeparar;
    private JButton btnApostarMano;
    private JButton btnAsegurar;
    private JButton btnRetirarMano;
    private JButton btnVerTodasLasManos;
    private JButton btnIrmeDeLaMesa;
    private JButton btnConfirmarParticipacion;

    public VistaRondaGUI(Controlador c) {
        this.controlador = c;
        this.visibilidad = true;

        // Configuración básica de la ventana
        setTitle("BLACKJACK MESA");
        setSize(1240, 720);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        iniciarFavIcon();

        // Inicializar componentes
        inicializarComponentes();
        configurarAcciones();

        setContentPane(panelPrincipal);
        setVisible(visibilidad);
    }

    private void inicializarComponentes() {
        // Panel principal con BorderLayout
        panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(new Color(0, 100, 0)); // Verde de mesa de blackjack

        // Panel superior con título y notificaciones
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        titulo = new JLabel("--- MESA DE BLACKJACK ---", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);

        notificaciones = new JLabel("", JLabel.CENTER);
        notificaciones.setFont(new Font("Arial", Font.PLAIN, 16));
        notificaciones.setForeground(Color.YELLOW);

        topPanel.add(titulo, BorderLayout.NORTH);
        topPanel.add(notificaciones, BorderLayout.CENTER);

        // Panel de la mesa (centro)
        mesaPanel = new JPanel(new BorderLayout());
        mesaPanel.setOpaque(false);
        mesaPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel del dealer (parte superior de la mesa)
        dealerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        dealerPanel.setOpaque(false);
        dealerPanel.setPreferredSize(new Dimension(0, 180));

        JLabel dealerLabel = new JLabel("CRUPIER", JLabel.CENTER);
        dealerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        dealerLabel.setForeground(Color.WHITE);

        JPanel dealerContainer = new JPanel(new BorderLayout());
        dealerContainer.setOpaque(false);
        dealerContainer.add(dealerLabel, BorderLayout.NORTH);
        dealerContainer.add(dealerPanel, BorderLayout.CENTER);

        // Panel de otros jugadores (parte izquierda/derecha de la mesa)
        otrosJugadoresPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        otrosJugadoresPanel.setOpaque(false);

        // Panel del jugador principal (parte inferior de la mesa)
        jugadorPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        jugadorPanel.setOpaque(false);
        jugadorPanel.setPreferredSize(new Dimension(0, 180));

        JLabel jugadorLabel = new JLabel("TU MANO", JLabel.CENTER);
        jugadorLabel.setFont(new Font("Arial", Font.BOLD, 18));
        jugadorLabel.setForeground(Color.WHITE);

        JPanel jugadorContainer = new JPanel(new BorderLayout());
        jugadorContainer.setOpaque(false);
        jugadorContainer.add(jugadorLabel, BorderLayout.SOUTH);
        jugadorContainer.add(jugadorPanel, BorderLayout.CENTER);

        // Añadir componentes al panel de la mesa
        mesaPanel.add(dealerContainer, BorderLayout.NORTH);
        mesaPanel.add(otrosJugadoresPanel, BorderLayout.CENTER);
        mesaPanel.add(jugadorContainer, BorderLayout.SOUTH);

        // Panel de controles (sur)
        controlesPanel = new JPanel(new GridLayout(2, 1));
        controlesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        controlesPanel.setBackground(new Color(70, 70, 70));

        // Panel de información
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        infoPanel.setOpaque(false);

        saldoLabel = new JLabel("Saldo: $1000");
        saldoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        saldoLabel.setForeground(Color.WHITE);

        apuestaLabel = new JLabel("Apuesta: $0");
        apuestaLabel.setFont(new Font("Arial", Font.BOLD, 16));
        apuestaLabel.setForeground(Color.WHITE);

        infoPanel.add(saldoLabel);
        infoPanel.add(apuestaLabel);

        // Panel de botones
        JPanel botonesPanel = new JPanel(new GridLayout(2, 5, 5, 5));
        botonesPanel.setOpaque(false);

        // Inicializar botones
        btnApostarMano = crearBoton("APOSTAR MANO");
        btnAsegurar = crearBoton("ASEGURARSE");
        btnDoblar = crearBoton("DOBLAR");
        btnQuedarme = crearBoton("QUEDARSE");
        btnRendirme = crearBoton("RENDIRSE");
        btnConfirmarParticipacion = crearBoton("CONFIRMAR");
        btnIrmeDeLaMesa = crearBoton("IRSE");
        btnPedirCarta = crearBoton("PEDIR CARTA");
        btnSeparar = crearBoton("SEPARAR");
        btnVerTodasLasManos = crearBoton("VER MANOS");
        btnRetirarMano = crearBoton("RETIRAR");

        // Añadir botones al panel
        botonesPanel.add(btnApostarMano);
        botonesPanel.add(btnAsegurar);
        botonesPanel.add(btnDoblar);
        botonesPanel.add(btnQuedarme);
        botonesPanel.add(btnRendirme);
        botonesPanel.add(btnConfirmarParticipacion);
        botonesPanel.add(btnIrmeDeLaMesa);
        botonesPanel.add(btnPedirCarta);
        botonesPanel.add(btnSeparar);
        botonesPanel.add(btnVerTodasLasManos);
        botonesPanel.add(btnRetirarMano);

        controlesPanel.add(infoPanel);
        controlesPanel.add(botonesPanel);

        // Añadir todo al panel principal
        panelPrincipal.add(topPanel, BorderLayout.NORTH);
        panelPrincipal.add(mesaPanel, BorderLayout.CENTER);
        panelPrincipal.add(controlesPanel, BorderLayout.SOUTH);
    }

    private JButton crearBoton(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Arial", Font.BOLD, 12));
        boton.setBackground(new Color(50, 50, 50));
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        return boton;
    }

    private void configurarAcciones() {
        // Aquí irían las acciones de los botones, que delegarían al controlador
        // Ejemplo:
        //btnPedirCarta.addActionListener(e -> controlador.pedirCarta());
        //btnQuedarme.addActionListener(e -> controlador.quedarse());
        // ... configurar el resto de botones
    }

    // Métodos para actualizar la vista
    public void mostrarCartaDealer(ImageIcon carta, boolean bocaAbajo) {
        JLabel cartaLabel = new JLabel(bocaAbajo ? crearCartaBocaAbajo() : carta);
        dealerPanel.add(cartaLabel);
        dealerPanel.revalidate();
        dealerPanel.repaint();
    }

    public void mostrarCartaJugador(ImageIcon carta) {
        JLabel cartaLabel = new JLabel(carta);
        jugadorPanel.add(cartaLabel);
        jugadorPanel.revalidate();
        jugadorPanel.repaint();
    }

    public void limpiarMesa() {
        dealerPanel.removeAll();
        jugadorPanel.removeAll();
        // Limpiar otros paneles si es necesario
        mesaPanel.revalidate();
        mesaPanel.repaint();
    }

    private ImageIcon crearCartaBocaAbajo() {
        // Crear una imagen simple para carta boca abajo
        BufferedImage img = new BufferedImage(80, 120, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(Color.BLUE);
        g2d.fillRect(0, 0, 80, 120);
        g2d.setColor(Color.WHITE);
        g2d.drawRect(5, 5, 70, 110);
        g2d.dispose();
        return new ImageIcon(img);
    }

    private void iniciarFavIcon() {
        icono = new ImageIcon(getClass().getResource("/ar/edu/unlu/poo/imagenes/images.png")).getImage();
        icono = icono.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        setIconImage(icono);
    }

    // Implementación de métodos de la interfaz IVista
    @Override
    public void cambiarVisibilidad() {
        visibilidad = !visibilidad;
        setVisible(visibilidad);
    }

    @Override
    public void actualizarConectados(java.util.List<String> jugadoresConectados) {

    }

    @Override
    public void actualizarLabelNotificador(String s) {
        notificaciones.setText(s);
    }

    @Override
    public void actualizarSaldoJugador() {
        // Implementar actualización de saldo
    }

    @Override
    public void actualizarTablero() throws RemoteException {

    }

    @Override
    public void ventanaResultados() throws RemoteException {

    }

    @Override
    public void ventanaReInscripcion() throws RemoteException {

    }

    public void cambiarVisibilidad(Boolean visibilidad) {
        this.visibilidad = visibilidad;
        setVisible(visibilidad);
    }
}
