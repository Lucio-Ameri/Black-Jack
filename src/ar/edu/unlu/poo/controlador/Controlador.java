package ar.edu.unlu.poo.controlador;

import ar.edu.unlu.poo.interfaces.ILobby;
import ar.edu.unlu.poo.interfaces.IVista;
import ar.edu.unlu.poo.modelo.*;
import ar.edu.unlu.poo.modelo.enumerados.*;
import ar.edu.unlu.poo.persistencia.RecordsHistoricos;
import ar.edu.unlu.poo.persistencia.Serializador;
import ar.edu.unlu.poo.rmimvc.cliente.IControladorRemoto;
import ar.edu.unlu.poo.rmimvc.observer.IObservableRemoto;
import ar.edu.unlu.poo.vista.MenuLogginJugador;
import ar.edu.unlu.poo.vista.VistaConsolaMejorada.VistaLobbyCM;
import ar.edu.unlu.poo.vista.VistaConsolaMejorada.VistaRondaCM;
import ar.edu.unlu.poo.vista.VistaGrafica.VistaLobbyGUI;


import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Controlador implements IControladorRemoto {

    private ILobby modelo;
    private MenuLogginJugador vistaInicio;
    private IVista vistaLobby;
    private IVista vistaRonda;

    private SalaRonda salaJuego;
    private Jugador jugadorLocal;
    private boolean enListaDeEspera;

    public Controlador(){
    }



    @Override
    public <T extends IObservableRemoto> void setModeloRemoto(T modeloRemoto) throws RemoteException {
        this.modelo = (ILobby) modeloRemoto;
        this.salaJuego = modelo.getSalaRonda();
    }


    @Override
    public void actualizar(IObservableRemoto observable, Object o) throws RemoteException {
        if (o instanceof Eventos) {

            switch ((Eventos) o) {
                case MESA_ACTUALIZADA -> {
                    vistaRonda.actualizarTablero();
                }

                case ACTUALIZAR_LISTA_DE_ESPERA -> {
                    if (salaJuego.jugadorInscripto(jugadorLocal)) {
                        salaJuego.agregarObservador(this);

                        if(vistaRonda == null){
                            if(vistaLobby instanceof VistaLobbyGUI){
                                //vistaRonda = new VistaRondaGUI(this);
                            }

                            else{
                                vistaRonda = new VistaRondaCM((this));
                            }
                        }
                    }

                    vistaLobby.actualizarLabelNotificador("... ACTUALIZACION EN LA LISTA DE ESPERA ...");
                }

                case CAMBIO_JUGADOR_CON_TURNO -> {
                    vistaRonda.actualizarLabelNotificador("... CAMBIO EL TURNO DEL JUGADOR! ...");
                    vistaRonda.actualizarTablero();
                }

                case ACTUALIZAR_LISTA_JUGADORES -> {
                    List<String> lista = new ArrayList<String>();
                    List<Jugador> jugadors = modelo.getJugadoresConectados();

                    for(Jugador j: jugadors){
                        lista.add(j.getNombre());
                    }

                    vistaLobby.actualizarConectados(lista);
                    vistaLobby.actualizarLabelNotificador("... ACTUALIZACION EN LA LISTA DE CONECTADOS ...");
                }

                case INICIA_NUEVA_RONDA -> {
                    vistaLobby.actualizarLabelNotificador("... LA MESA ESTA ACEPTANDO INSCRIPCIONES ...");
                }

                case RESULTADOS_OBTENIDOS -> {
                    vistaRonda.actualizarLabelNotificador("... EL DEALER TERMINO SU TURNO, VEAN SUS RESULTADOS! ...");
                    vistaRonda.actualizarTablero();
                    vistaRonda.ventanaResultados();

                    salaJuego.pasarAReInscripciones(jugadorLocal);
                }

                case REPARTIR_CARTAS_INICIALES -> {
                    vistaRonda.actualizarLabelNotificador("... INICIO EL JUEGO! REPARTIENDO CARTAS! ...");
                    vistaRonda.actualizarTablero();
                    salaJuego.controladorRepartirCartasIniciales(jugadorLocal);
                }

                case RE_INSCRIPCION -> {
                    vistaRonda.actualizarLabelNotificador("... MOMENTO DE RE INSCRIBIRSE! ...");
                    vistaRonda.actualizarTablero();
                    vistaRonda.ventanaReInscripcion();
                }

                case TURNO_JUGADORES -> {
                    vistaRonda.actualizarLabelNotificador("... INICIA EL TURNO DE LOS JUGADORES! ...");
                    vistaRonda.actualizarTablero();
                }

                case CARTAS_REPARTIDAS -> {
                    vistaRonda.actualizarLabelNotificador("... CARTAS REPARTIDAS! ...");
                    vistaRonda.actualizarTablero();
                }

                case EMPEZAR_TURNO_DEALER -> {
                    vistaRonda.actualizarLabelNotificador("... INICIA EL TURNO DEL DEALER ...");
                    salaJuego.controladorArrancarTurnoDealer(jugadorLocal);
                }
            }
        }
    }


    public boolean validarNombre(String nombre) {
        String regex = "^(?=.{1,8}$)[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ]+(?:[\\s'-][a-zA-ZáéíóúÁÉÍÓÚñÑüÜ]+)*$";

        if((nombre == null) || (nombre.trim().isEmpty()) || (!nombre.matches(regex))){
            return false;
        }

        return true;
    }

    public String generarIDUnico(String nombre) {
        ArrayList<String> nombresGuardados = Serializador.cargarListaNombresUsados();

        Random random = new Random();
        String aux;

        do{

            aux = "#" + (random.nextInt(1000) + 1);
        }
        while(nombresGuardados.contains(nombre + aux));

        nombresGuardados.add(nombre + aux);
        Serializador.guardarListaNombresUsados(nombresGuardados);

        return  nombre + aux;
    }

    public Mensajes montoValido(String monto) {
        String regex = "^[1-9]\\d*$|^0$";
        if((monto == null) || (monto.trim().isEmpty()) || (!monto.matches(regex))){
            return Mensajes.NO_NUMERICO;
        }

        else {

            if (!jugadorLocal.poseeSaldo()) {
                return Mensajes.SIN_SALDO;
            }

            else {
                if (jugadorLocal.getSaldo().transferenciaRealizable(Double.parseDouble(monto))) {
                    return Mensajes.NUMERO_CORRECTO;
                }

                else{
                    return Mensajes.SALDO_INSUFICIENTE;
                }
            }
        }
    }

    public ArrayList<Jugador> obtenerJugadoresGuardados() {
        return Serializador.cargarJugadoresGuardados();
    }


    public void eliminarJugadorGuardado(Jugador jugadorLocal) {
        Serializador.eliminarJugadorGuardado(jugadorLocal);
    }

    public void establecerJugador(Jugador jugadorLocal) {
        this.jugadorLocal = jugadorLocal;
    }

    public void establecerVistaInicio(MenuLogginJugador menuLogginJugador) {
        this.vistaInicio = menuLogginJugador;
    }

    public void cerrarLaAplicacion() {
        try {
            if (modelo != null) {
                modelo.removerObservador(this);
            }
        }

        catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public boolean poseeJugador() {
        return jugadorLocal != null;
    }

    public void establecerVistaLobby(String tipoInterfaz) throws RemoteException {
        if(vistaLobby == null){
            if(tipoInterfaz.equals("Vista Consola Mejorada")){
                vistaLobby = new VistaLobbyCM(this);
            }

            else{
                vistaLobby = new VistaLobbyGUI(this);
            }
        }

        else{
            vistaLobby.cambiarVisibilidad(false);
        }
        enListaDeEspera = false;
        modelo.unirmeLobby(jugadorLocal);
    }

    public void persistirJugadorEIrme() throws RemoteException {
        if(jugadorLocal.poseeSaldo()){
            jugadorLocal.guardarJugador();
        }

        modelo.irmeLobby(jugadorLocal);
        jugadorLocal = null;
        vistaInicio.cambiarVisibilidad(false);
    }


    public Mensajes unirJugadorALaMesa(double monto) throws RemoteException {
        Mensajes m = modelo.unirmeSalaRonda(jugadorLocal, monto);

        if(m == Mensajes.ACCION_REALIZADA_EXITOSAMENTE){
            enListaDeEspera = false;

            /*
            if(vistaLobby instanceof VistaLobbyGUI){
                //vistaRonda = new VistaRondaGUI(this);
                vistaRonda = new ar.edu.unlu.poo.vista.VistaRondaGUI(this);
            }

            else{
                vistaRonda = new VistaRondaCM(this);
            }
             */

            vistaRonda = new VistaRondaCM(this);

            vistaLobby.actualizarSaldoJugador();
            vistaLobby.cambiarVisibilidad(true);
            salaJuego.agregarObservador(this);
        }

        return m;
    }

    public Mensajes unirJugadorListaDeEspera(double monto) throws RemoteException {
        Mensajes m = modelo.unirmeListaDeEspera(jugadorLocal, monto);

        if(m == Mensajes.ACCION_REALIZADA_EXITOSAMENTE){
            enListaDeEspera = true;
        }

        return m;
    }

    public List<String> getJugadorLocal() {
        List<String> datos = new ArrayList<String>();
        datos.add("--------- DATOS DEL JUGADOR ----------");
        datos.add( "NOMBRE: " + jugadorLocal.getNombre());
        datos.add("SALDO ACTUAL: " + jugadorLocal.getSaldo().getMonto());
        datos.add("SALDO MAXIMO HISTORICO: " + jugadorLocal.getMaximoHistorico());

        return datos;
    }

    public int posicionEspera() throws RemoteException {
        return modelo.cualEsMiPosicionEnLaListaDeEspera(jugadorLocal);
    }

    public boolean removerSolicitud() throws RemoteException {
        if(modelo.tengoSolicitudEnEspera(jugadorLocal)){
            enListaDeEspera = false;
            modelo.salirListaDeEspera(jugadorLocal);
            return true;
        }
        return false;
    }

    public List<String> getRankingHistorico() {
        RecordsHistoricos r = Serializador.cargarRecordsHistoricos();
        List<String> records = r.obtenerListaTopHistorico();

        r.guardarRecords();
        return records;
    }

    private ManoJugador obtenerManoTurno(){
        List<ManoJugador> manos = jugadorLocal.getManos();

        ManoJugador actual = null;

        for (ManoJugador m : manos) {
            EstadoDeLaMano est = m.getEstadoDeLaMano();
            if (est == EstadoDeLaMano.EN_JUEGO || est == EstadoDeLaMano.TURNO_INICIAL) {
                actual = m;
                break;
            }
        }

        return actual;
    }

    public List<ManoJugador> obtenerManosJugador() {
        return jugadorLocal.getManos();
    }

    public boolean jugadorConfirmo() throws RemoteException {
        return salaJuego.confirmoElJugador(jugadorLocal);
    }

    public Mensajes borrarMano(ManoJugador mano) throws RemoteException {
        return salaJuego.retirarUnaMano(jugadorLocal, mano);
    }

    public Mensajes separarManoUsuario() throws RemoteException {
        if(salaJuego.esMiTurno(jugadorLocal)) {

            ManoJugador actual = obtenerManoTurno();

            if(actual == null){
                return Mensajes.NO_TIENE_MAS_MANOS;
            }
            return salaJuego.jugarTurno(AccionesJugador.SEPARAR_MANO, jugadorLocal, actual);
        }

        return Mensajes.NO_ES_SU_TURNO;
    }


    public Mensajes pedirCarta() throws RemoteException {
        if(salaJuego.esMiTurno(jugadorLocal)) {
            ManoJugador actual = obtenerManoTurno();

            if (actual == null) {
                return Mensajes.NO_TIENE_MAS_MANOS;
            }

            return salaJuego.jugarTurno(AccionesJugador.PEDIR_CARTA, jugadorLocal, actual);
        }

        return Mensajes.NO_ES_SU_TURNO;
    }

    public Mensajes pasarTurno() throws RemoteException {
        if(salaJuego.esMiTurno(jugadorLocal)) {
            ManoJugador actual = obtenerManoTurno();

            return salaJuego.jugarTurno(AccionesJugador.PASAR_TURNO, jugadorLocal, actual);
        }

        return Mensajes.NO_ES_SU_TURNO;
    }


    public Mensajes irmeDeLaMesa() throws RemoteException {
        Mensajes m = salaJuego.retirarmeDeLaSalaRonda(jugadorLocal);

        if(m == Mensajes.ACCION_REALIZADA_EXITOSAMENTE){
            salaJuego.removerObservador(this);

            vistaLobby.cambiarVisibilidad();
            vistaLobby.actualizarSaldoJugador();
        }

        return m;
    }

    public Mensajes doblarMano() throws RemoteException {
        if(salaJuego.esMiTurno(jugadorLocal)) {
            ManoJugador actual = obtenerManoTurno();

            if (actual == null) {
                return Mensajes.NO_TIENE_MAS_MANOS;
            }

            return salaJuego.jugarTurno(AccionesJugador.DOBLAR_MANO, jugadorLocal, actual);
        }

        return Mensajes.NO_ES_SU_TURNO;
    }

    public Mensajes rendirMano() throws RemoteException {
        if(salaJuego.esMiTurno(jugadorLocal)) {
            ManoJugador actual = obtenerManoTurno();

            if (actual == null) {
                return Mensajes.NO_TIENE_MAS_MANOS;
            }

            return salaJuego.jugarTurno(AccionesJugador.RENDIRME, jugadorLocal, actual);
        }

        return Mensajes.NO_ES_SU_TURNO;
    }

    public Mensajes quedarMano() throws RemoteException {
        if(salaJuego.esMiTurno(jugadorLocal)) {
            ManoJugador actual = obtenerManoTurno();

            if (actual == null) {
                return Mensajes.NO_TIENE_MAS_MANOS;
            }

            return salaJuego.jugarTurno(AccionesJugador.QUEDARME, jugadorLocal, actual);
        }

        return Mensajes.NO_ES_SU_TURNO;
    }

    public Mensajes confirmarPArticipacion() throws RemoteException {
        if(salaJuego.getEstadoSalaRonda() == EstadoDeLaSalaRonda.ACEPTANDO_INSCRIPCIONES) {
            if (!salaJuego.confirmoElJugador(jugadorLocal)) {
                salaJuego.confirmarParticipacion(jugadorLocal);
                return Mensajes.ACCION_REALIZADA_EXITOSAMENTE;
            }

            return Mensajes.JUGADOR_YA_CONFIRMADO;
        }

        return Mensajes.LA_RONDA_YA_EMPEZO;
    }

    public Mensajes asegurarse() throws RemoteException {
        if(salaJuego.esMiTurno(jugadorLocal)) {
            ManoJugador actual = obtenerManoTurno();

            if (actual == null) {
                return Mensajes.NO_TIENE_MAS_MANOS;
            }

            return salaJuego.jugarTurno(AccionesJugador.ASEGURARME, jugadorLocal, actual);
        }

        return Mensajes.NO_ES_SU_TURNO;
    }

    public Mensajes apostarOtraMano(double monto) throws RemoteException {
        return salaJuego.apostarOtraMano(jugadorLocal, monto);
    }

    public List<Jugador> getListaJugadores() throws RemoteException {
        return salaJuego.aquellosQueJuegan();
    }

    public List<ManoJugador> getListaDeManos(Jugador j) {
        return j.getManos();
    }

    public String getNombre(Jugador j) {
        return j.getNombre();
    }

    public String getNombreTurnoActual() throws RemoteException {
       return salaJuego.getJugadorTurnoActual();
    }


    public List<Carta> getCartasMano(ManoJugador m) {
        return m.getCartasDeLaMano();
    }

    public PaloCarta getPaloDeLaCarta(Carta c) {
        return c.getPaloDeCarta();
    }

    public ValorCarta getValorDeLaCarta(Carta c) {
        return c.getTipoDeValor();
    }

    public EstadoDeLaMano getEstadoMano(ManoJugador m) {
        return m.getEstadoDeLaMano();
    }

    public int getTotalMano(ManoJugador m) {
        return m.getTotalMano();
    }

    public List<Carta> getManoDealer() throws RemoteException {
        return salaJuego.getManoDealer();
    }

    public int getTotalManoDealer() throws RemoteException {
        return salaJuego.getTotalDealer();
    }

    public boolean getEstadCarta(Carta c) {
        return c.cartaVisible();
    }

    public void reInscripcion(double monto, boolean situacion) throws RemoteException {
        salaJuego.confirmarNuevaParticipacion(jugadorLocal, monto, situacion);

        if(!situacion){
            vistaRonda.cambiarVisibilidad();
            salaJuego.removerObservador(this);
        }
    }

    public List<String> obtenerResultadosDeLaRonda() throws RemoteException {
        List<ManoJugador> manos = jugadorLocal.getManos();
        List<String> resultados = new ArrayList<String>();

        for(ManoJugador m: manos){
            Apuesta envite = m.getEnvite();
            String resultado = "RESULTADO: " + envite.getEstado();
            String ganancia = ".        GANANCIAS: " + envite.getGanancias();
            String total = resultado + ganancia;
            resultados.add(total);
        }

        return resultados;
    }
}
