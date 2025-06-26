package ar.edu.unlu.poo.modelo;

import ar.edu.unlu.poo.interfaces.ILobby;
import ar.edu.unlu.poo.modelo.enumerados.EstadoDeLaSalaRonda;
import ar.edu.unlu.poo.modelo.enumerados.Eventos;
import ar.edu.unlu.poo.modelo.enumerados.Mensajes;
import ar.edu.unlu.poo.rmimvc.observer.IObservableRemoto;
import ar.edu.unlu.poo.rmimvc.observer.IObservadorRemoto;
import ar.edu.unlu.poo.rmimvc.observer.ObservableRemoto;

import java.io.Serial;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Lobby extends ObservableRemoto implements IObservadorRemoto, Serializable, ILobby {
    @Serial
    private static final long serialVersionUID = 7L;

    private List<Jugador> conectados;
    private List<Jugador> listaDeEspera;
    private HashMap<Jugador, Double> solicitudesEnEspera;;
    private SalaRonda sala;

    public Lobby(){
        this.conectados = new ArrayList<Jugador>();
        this.listaDeEspera = new ArrayList<Jugador>();
        this.solicitudesEnEspera = new HashMap<Jugador, Double>();
        this.sala = new SalaRonda();
    }


    @Override
    public List<Jugador> getJugadoresConectados() throws RemoteException {
        return conectados;
    }


    @Override
    public SalaRonda getSalaRonda() throws RemoteException{
        return sala;
    }


    @Override
    public int getLonguitudListaDeEspera() throws RemoteException{
        return listaDeEspera.size();
    }


    @Override
    public int cualEsMiPosicionEnLaListaDeEspera(Jugador j) throws RemoteException{
        if(hayJugadoresEsperando() && tengoSolicitudEnEspera(j)){
            return listaDeEspera.indexOf(j) + 1;
        }

        return -1;
    }

    @Override
    public boolean tengoSolicitudEnEspera(Jugador j) throws RemoteException{
        return listaDeEspera.contains(j);
    }

    private boolean hayJugadoresEsperando(){
        return (!listaDeEspera.isEmpty());
    }

    private boolean estoyConectadoEnElLobby(Jugador j){
        return conectados.contains(j);
    }


    @Override
    public Mensajes unirmeLobby(Jugador j) throws RemoteException {
        if(!estoyConectadoEnElLobby(j)){
            conectados.add(j);
            notificarObservadores(Eventos.ACTUALIZAR_LISTA_JUGADORES);

            return Mensajes.ACCION_REALIZADA_EXITOSAMENTE;
        }

        return Mensajes.JUGADOR_YA_ESTA_INSCRIPTO;
    }

    private boolean jugadorEnSalaRonda(Jugador j) throws RemoteException {
        return sala.jugadorInscripto(j);
    }


    @Override
    public Mensajes irmeLobby(Jugador j) throws RemoteException {
        if(!jugadorEnSalaRonda(j)){

            if(tengoSolicitudEnEspera(j)){
                listaDeEspera.remove(j);
                solicitudesEnEspera.remove(j);
            }

            Jugador je = null;
            for(Jugador jug: conectados){
                if(jug.getNombre().equals(j.getNombre())){
                    je = jug;
                    break;
                }
            }
            
            if(je != null){
              conectados.remove(je);  
            }


            notificarObservadores(Eventos.ACTUALIZAR_LISTA_JUGADORES);
            return Mensajes.ACCION_REALIZADA_EXITOSAMENTE;
        }

        return Mensajes.JUGADOR_EN_SALA_RONDA;
    }


    @Override
    public Mensajes unirmeListaDeEspera(Jugador j, double monto) throws RemoteException{
        if(!jugadorEnSalaRonda(j)){

            if(sala.getEstadoSalaRonda() != EstadoDeLaSalaRonda.ACEPTANDO_INSCRIPCIONES){

                if(!tengoSolicitudEnEspera(j)){
                    solicitudesEnEspera.put(j, monto);
                    listaDeEspera.add(j);

                    return Mensajes.ACCION_REALIZADA_EXITOSAMENTE;
                }

                return Mensajes.JUGADOR_YA_EN_LISTA;
            }

            return Mensajes.RONDA_ACEPTANDO_INSCRIPCIONES;
        }

        return Mensajes.JUGADOR_EN_SALA_RONDA;
    }


    @Override
    public Mensajes salirListaDeEspera(Jugador j) throws RemoteException {
        if(tengoSolicitudEnEspera(j)){

            listaDeEspera.remove(j);
            solicitudesEnEspera.remove(j);

            notificarObservadores(Eventos.ACTUALIZAR_LISTA_DE_ESPERA);
        }

        return Mensajes.NO_ESTABA_EN_LA_LISTA_DE_ESPERA;
    }


    @Override
    public Mensajes unirmeSalaRonda(Jugador j, double monto) throws RemoteException{
        if(sala.getEstadoSalaRonda() == EstadoDeLaSalaRonda.ACEPTANDO_INSCRIPCIONES){

            if(!hayJugadoresEsperando()){

                return sala.inscribirJugador(j, monto);
            }

            return Mensajes.UNIRSE_LISTA_ESPERA;
        }

        return Mensajes.LA_RONDA_YA_EMPEZO;
    }

    private boolean agregarJugadorEsperando(Jugador j, double monto) throws RemoteException {
        Mensajes resultado = sala.inscribirJugador(j, monto);
        if(resultado == Mensajes.ACCION_REALIZADA_EXITOSAMENTE){
            listaDeEspera.remove(j);
            solicitudesEnEspera.remove(j);

            return true;
        }

        return false;
    }

    @Override
    public void actualizar(IObservableRemoto observable, Object o) throws RemoteException {

        if(o instanceof Eventos){

            if((Eventos) o == Eventos.INICIA_NUEVA_RONDA){
                if(hayJugadoresEsperando()) {

                    for (Jugador j : listaDeEspera) {
                        if (!agregarJugadorEsperando(j, solicitudesEnEspera.get(j))) {
                            break;
                        }
                    }

                }

                notificarObservadores(Eventos.ACTUALIZAR_LISTA_DE_ESPERA);
            }
        }
    }
}
