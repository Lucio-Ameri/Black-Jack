package ar.edu.unlu.poo.modelo;

import ar.edu.unlu.poo.interfaces.ISalaRonda;
import ar.edu.unlu.poo.modelo.enumerados.*;
import ar.edu.unlu.poo.persistencia.RecordsHistoricos;
import ar.edu.unlu.poo.persistencia.Serializador;
import ar.edu.unlu.poo.rmimvc.observer.ObservableRemoto;

import java.io.Serial;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SalaRonda extends ObservableRemoto implements ISalaRonda, Serializable{
    @Serial
    private static final long serialVersionUID = 11L;
    private static int index;
    private List<Jugador> inscriptos;
    private HashMap<String, Boolean> confirmacion;
    private EstadoDeLaSalaRonda estado;
    private Dealer dealer;
    private String turnoActual;
    private int lugaresDisponibles;

    public SalaRonda(){
        this.inscriptos = new ArrayList<Jugador>();
        this.confirmacion = new HashMap<String, Boolean>();
        this.estado = EstadoDeLaSalaRonda.ACEPTANDO_INSCRIPCIONES;
        this.dealer = new Dealer();
        this.turnoActual = "";
        this.lugaresDisponibles = 7;
        index = -1;
    }



    private void cambiarEstadoDeLaSalaRonda(EstadoDeLaSalaRonda en) {
        estado = en;
    }

    @Override
    public boolean hayLugaresDisponibles() throws RemoteException {
        return lugaresDisponibles > 0;
    }

    private void reiniciarConfirmados(){
        if(!confirmacion.isEmpty()) {
            confirmacion.keySet().forEach(jugador -> confirmacion.put(jugador, false));
        }
    }

    @Override
    public void confirmarParticipacion(Jugador j) throws RemoteException{
        confirmacion.replace(j.getNombre(), false, true);
        actualizarEstadoRonda();
    }

    private boolean confirmaronTodos(){
        return !confirmacion.isEmpty() && confirmacion.values().stream().allMatch(Boolean::booleanValue);
    };

    private void actualizarEstadoRonda() throws RemoteException {
        if(estado == EstadoDeLaSalaRonda.ACEPTANDO_INSCRIPCIONES){

            if((!hayLugaresDisponibles() || confirmaronTodos()) && !inscriptos.isEmpty()){
                cambiarEstadoDeLaSalaRonda(EstadoDeLaSalaRonda.REPARTIENDO_CARTAS);

                reiniciarConfirmados();
                notificarObservadores(Eventos.REPARTIR_CARTAS_INICIALES);
            }
        }

        else if(estado == EstadoDeLaSalaRonda.REPARTIENDO_CARTAS && (!inscriptos.isEmpty())){
            cambiarEstadoDeLaSalaRonda(EstadoDeLaSalaRonda.TURNO_JUGADORES);
            reiniciarConfirmados();

            turnoActual = inscriptos.get(0).getNombre();
            notificarObservadores(Eventos.CAMBIO_JUGADOR_CON_TURNO);
        }

        else if(estado == EstadoDeLaSalaRonda.TURNO_JUGADORES){
            cambiarEstadoDeLaSalaRonda(EstadoDeLaSalaRonda.TURNO_DEALER);
            reiniciarConfirmados();

            if(lugaresDisponibles > 7){
                lugaresDisponibles = 7;
            }
        }

        /*
         * En esta parte, los controladores van a mostrar los resultados del jugador propicio en la vista, cuando esa ventana,
         * se cierre, el controlador volvera a confirmar para pasar a la siguiente instancia de la ronda.
         * */
        else if(estado == EstadoDeLaSalaRonda.TURNO_DEALER){
            cambiarEstadoDeLaSalaRonda(EstadoDeLaSalaRonda.RESULTADOS);
            reiniciarConfirmados();
        }

        else if(estado == EstadoDeLaSalaRonda.RESULTADOS){
            cambiarEstadoDeLaSalaRonda(EstadoDeLaSalaRonda.RE_INSCRIPCIONES);
            reiniciarConfirmados();
        }

        else if(estado == EstadoDeLaSalaRonda.RE_INSCRIPCIONES){
            cambiarEstadoDeLaSalaRonda(EstadoDeLaSalaRonda.ACEPTANDO_INSCRIPCIONES);
            reiniciarConfirmados();
            index = -1;
            notificarObservadores(Eventos.INICIA_NUEVA_RONDA);
        }

        else{
            cambiarEstadoDeLaSalaRonda(estado);
        }
    }

    @Override
    public void controladorRepartirCartasIniciales(Jugador j) throws RemoteException{
        confirmacion.replace(j.getNombre(), false, true);

        if(confirmaronTodos()){
            repartirCartasIniciales();
            notificarObservadores(Eventos.CARTAS_REPARTIDAS);

            actualizarEstadoRonda();
        }
    }

    private void repartirCartasIniciales(){

        for(int i = 0; i < 2; i++){
            for(Jugador j: inscriptos){
                List<ManoJugador> manos = j.getManos();

                for(ManoJugador m: manos){
                    m.recibirCarta(dealer.repartirCarta());
                }
            }

            dealer.getManoDealer().recibirCarta(dealer.repartirCarta());
        }
    }

    private void pasarTurno() throws RemoteException {
        index++;

        if(inscriptos.size() == index){
            turnoActual = "";

            actualizarEstadoRonda();
            notificarObservadores(Eventos.EMPEZAR_TURNO_DEALER);
        }

        else{
            confirmacion.replace(turnoActual, false, true);
            turnoActual = inscriptos.get(index).getNombre();
            notificarObservadores(Eventos.CAMBIO_JUGADOR_CON_TURNO);
        }
    }

    @Override
    public void controladorArrancarTurnoDealer(Jugador j) throws RemoteException{
        confirmacion.replace(j.getNombre(), false, true);

        if(confirmacion.values().stream().allMatch(Boolean::booleanValue)){

            jugarTurnoDealer();
            actualizarEstadoRonda();

            notificarObservadores(Eventos.RESULTADOS_OBTENIDOS);
        }
    }

    private void jugarTurnoDealer(){
        dealer.revelar();
        ManoDealer manoD = dealer.getManoDealer();

        while(manoD.getEstadoDeLaMano() == EstadoDeLaMano.TURNO_INICIAL){
            manoD.recibirCarta(dealer.repartirCarta());
        }

        dealer.calcularResultados(inscriptos);
        RecordsHistoricos r = Serializador.cargarRecordsHistoricos();

        for(Jugador j: inscriptos) {
            dealer.repartirGanancia(j);
            j.actualizarMaximoHistorico();
            r.actualizarLista(j.getNombre(), j.getMaximoHistorico());
        }

        Serializador.guardarRecordsHistoricos(r);
    }

    @Override
    public void pasarAReInscripciones(Jugador j) throws RemoteException{
        confirmacion.replace(j.getNombre(), false, true);

        if(confirmaronTodos()){
            actualizarEstadoRonda();
            notificarObservadores(Eventos.RE_INSCRIPCION);
        }
    }

    @Override
    public void confirmarNuevaParticipacion(Jugador j, double monto, boolean situacion) throws RemoteException{

        if (situacion) {
            dealer.eliminarManos(j);

            j.getSaldo().actualizarMonto( - monto);
            j.getManos().add(new ManoJugador(monto));

            confirmarParticipacion(j);
        }

        else {
            lugaresDisponibles += j.getManos().size();
            dealer.eliminarManos(j);

            inscriptos.remove(j);
            confirmacion.remove(j);

            actualizarEstadoRonda();
        }
    }

    @Override
    public Mensajes inscribirJugador(Jugador j, double monto) throws RemoteException{
        if(!jugadorInscripto(j)) {

            if(estado == EstadoDeLaSalaRonda.ACEPTANDO_INSCRIPCIONES) {

                if(hayLugaresDisponibles()) {

                    lugaresDisponibles--;

                    inscriptos.add(j);
                    confirmacion.put(j.getNombre(), false);

                    j.getSaldo().actualizarMonto(- monto);
                    j.agregarMano(new ManoJugador(monto));

                    actualizarEstadoRonda();
                    return Mensajes.ACCION_REALIZADA_EXITOSAMENTE;
                }

                return Mensajes.NO_QUEDAN_LUGARES;
            }

            return Mensajes.LA_RONDA_YA_EMPEZO;
        }

        return Mensajes.JUGADOR_YA_ESTA_INSCRIPTO;
    }


    @Override
    public Mensajes apostarOtraMano(Jugador j, double monto) throws RemoteException{
        if(!confirmoElJugador(j)) {

            if(estado == EstadoDeLaSalaRonda.ACEPTANDO_INSCRIPCIONES) {

                if(hayLugaresDisponibles()) {

                    lugaresDisponibles--;

                    j.getSaldo().actualizarMonto(- monto);
                    j.agregarMano(new ManoJugador(monto));

                    actualizarEstadoRonda();
                    return Mensajes.ACCION_REALIZADA_EXITOSAMENTE;
                }

                return Mensajes.NO_QUEDAN_LUGARES;
            }

            return Mensajes.LA_RONDA_YA_EMPEZO;
        }

        return Mensajes.JUGADOR_YA_CONFIRMADO;
    }

    @Override
    public Mensajes retirarUnaMano(Jugador j, ManoJugador mano) throws RemoteException{
        if(!confirmoElJugador(j)) {

            if(estado == EstadoDeLaSalaRonda.ACEPTANDO_INSCRIPCIONES) {

                if(j.getManos().size() > 1) {
                    dealer.devolverDineroMano(j, mano);
                    j.getManos().remove(mano);

                    lugaresDisponibles ++;
                    return Mensajes.ACCION_REALIZADA_EXITOSAMENTE;
                }

                return Mensajes.ACCION_INVALIDA_ULTIMA_MANO;
            }

            return Mensajes.LA_RONDA_YA_EMPEZO;
        }

        return Mensajes.JUGADOR_YA_CONFIRMADO;
    }

    @Override
    public Mensajes retirarmeDeLaSalaRonda(Jugador j) throws RemoteException{
        if(!confirmoElJugador(j)) {

            if(estado == EstadoDeLaSalaRonda.ACEPTANDO_INSCRIPCIONES) {
                lugaresDisponibles += j.getManos().size();

                dealer.devolverDineroManos(j);
                dealer.eliminarManos(j);

                inscriptos.remove(j);
                confirmacion.remove(j);


                actualizarEstadoRonda();
                return Mensajes.ACCION_REALIZADA_EXITOSAMENTE;
            }

            return Mensajes.LA_RONDA_YA_EMPEZO;
        }

        return Mensajes.JUGADOR_YA_CONFIRMADO;
    }


    @Override
    public Mensajes jugarTurno(AccionesJugador accion, Jugador j, ManoJugador m) throws RemoteException{
        Dinero saldo = j.getSaldo();

        switch(accion){
            case PEDIR_CARTA -> {
                m.recibirCarta(dealer.repartirCarta());
                notificarObservadores(Eventos.MESA_ACTUALIZADA);
                return Mensajes.ACCION_REALIZADA_EXITOSAMENTE;
            }

            case QUEDARME -> {
                m.quedarme();
                notificarObservadores(Eventos.MESA_ACTUALIZADA);
                return Mensajes.ACCION_REALIZADA_EXITOSAMENTE;
            }

            case ASEGURARME -> {

                if(m.manoTurnoInicial()) {

                    if(dealer.condicionSeguro()) {

                        if(saldo.transferenciaRealizable(m.getMontoApostado() / 2.0)) {
                            if(m.asegurarme()){

                                saldo.actualizarMonto(- (m.getMontoApostado() / 2.0) );
                                return Mensajes.ACCION_REALIZADA_EXITOSAMENTE;
                            }

                            return Mensajes.ACCION_YA_REALIZADA;
                        }

                        return Mensajes.SALDO_INSUFICIENTE;
                    }

                    return Mensajes.DEALER_SIN_AS_PRIMERA;
                }

                return Mensajes.NO_ES_TURNO_INICIAL;
            }

            case SEPARAR_MANO -> {
                if(m.manoTurnoInicial()) {

                    if(m.condicionSepararMano()) {

                        if(saldo.transferenciaRealizable(m.getMontoApostado())) {

                            saldo.actualizarMonto(- m.getMontoApostado());

                            ManoJugador nm = m.separar();

                            m.recibirCarta(dealer.repartirCarta());
                            nm.recibirCarta(dealer.repartirCarta());

                            j.agregarMano(nm, j.getManos().indexOf(m) + 1);
                            notificarObservadores(Eventos.MESA_ACTUALIZADA);
                            return Mensajes.ACCION_REALIZADA_EXITOSAMENTE;
                        }

                        return Mensajes.SALDO_INSUFICIENTE;
                    }

                    return Mensajes.NO_CUMPLE_CONDICION_SEPARAR;
                }

                return Mensajes.NO_ES_TURNO_INICIAL;
            }

            case DOBLAR_MANO -> {
                if(m.manoTurnoInicial()) {

                    if(saldo.transferenciaRealizable(m.getMontoApostado())) {
                        saldo.actualizarMonto(-m.getMontoApostado());
                        m.doblar(dealer.repartirCarta());
                        notificarObservadores(Eventos.MESA_ACTUALIZADA);
                        return Mensajes.ACCION_REALIZADA_EXITOSAMENTE;
                    }

                    return Mensajes.SALDO_INSUFICIENTE;
                }

                return Mensajes.NO_ES_TURNO_INICIAL;
            }

            case RENDIRME -> {
                if(m.manoTurnoInicial()) {
                    m.rendirme();
                    notificarObservadores(Eventos.MESA_ACTUALIZADA);
                    return Mensajes.ACCION_REALIZADA_EXITOSAMENTE;
                }

                return Mensajes.NO_ES_TURNO_INICIAL;
            }

            case PASAR_TURNO -> {
                pasarTurno();
                return Mensajes.ACCION_REALIZADA_EXITOSAMENTE;
            }
        }

        return Mensajes.ALGO_PASO;
    }


    @Override
    public List<Carta> getManoDealer() throws RemoteException{
        return dealer.getManoDealer().getCartasDeLaMano();
    }

    @Override
    public int getTotalDealer() throws RemoteException{
        return dealer.getManoDealer().getTotalMano();
    }

    @Override
    public EstadoDeLaSalaRonda getEstadoSalaRonda() throws RemoteException{
        return estado;
    }

    @Override
    public String getJugadorTurnoActual() throws RemoteException{
        if(turnoActual.equals("")){
            return "";
        }

        return turnoActual;
    }

    @Override
    public boolean jugadorInscripto(Jugador j) throws RemoteException{
        return (inscriptos.contains(j));
    }

    @Override
    public boolean confirmoElJugador(Jugador j) throws RemoteException {
        Boolean confirmado = confirmacion.get(j.getNombre());
        return confirmado != null && confirmado;
    }

    @Override
    public List<Jugador> aquellosQueJuegan() throws RemoteException{
        if(estado == EstadoDeLaSalaRonda.ACEPTANDO_INSCRIPCIONES || estado == EstadoDeLaSalaRonda.RE_INSCRIPCIONES){
            return new ArrayList<Jugador>();
        }

        return inscriptos;
    }

    @Override
    public boolean esMiTurno(Jugador j) throws RemoteException{
        return j.getNombre().equals(turnoActual);
    }
}