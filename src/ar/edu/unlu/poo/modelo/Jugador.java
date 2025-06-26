package ar.edu.unlu.poo.modelo;

import ar.edu.unlu.poo.modelo.enumerados.Mensajes;
import ar.edu.unlu.poo.persistencia.Serializador;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Jugador implements Serializable {
    @Serial
    private static final long serialVersionUID = 6L;

    private String nombre;
    private List<ManoJugador> manos;
    private Dinero saldo;
    private double maximoHistorico;

    public Jugador(String nombre, double monto){
        this.nombre = nombre;
        this.saldo = new Dinero(monto);
        this.manos = new ArrayList<ManoJugador>();
        this.maximoHistorico = saldo.getMonto();
    }


    //metodo: devuelve el nombre del jugador.
    public String getNombre(){
        return nombre;
    }


    //metodo: devuelve el Dinero del jugador.
    public Dinero getSaldo(){
        return saldo;
    }


    //metodo: devuelve la lista de manos del jugador.
    public List<ManoJugador> getManos(){
        return manos;
    }


    //metodo: devuelve el maximo historico del jugador.
    public double getMaximoHistorico(){
        return maximoHistorico;
    }


    public void actualizarMaximoHistorico(){
        if(saldo.getMonto() > maximoHistorico){
            maximoHistorico = saldo.getMonto();
        }
    }

    //metodo: agrega una mano al final de la lista.
    public void agregarMano(ManoJugador nm){
        manos.add(nm);
    }


    //metodo: agrega una mano en la posicion x de la lista de manos.
    public void agregarMano(ManoJugador nm, int indice){
        manos.add(indice, nm);
    }


    //metodo: elimina una mano de la lista de manos que posee el jugador.
    public void retirarUnaMano(ManoJugador m){
        manos.remove(m);
    }


    //metodo: informa si el jugador tiene o no saldo, util para saber si este puede jugar o no.
    public boolean poseeSaldo(){
        return saldo.tengoDinero();
    }


    //metodo: elimina las manos que posee.
    public void resetearManos(){
        manos.clear();
    }


    //metodo: guarda al jugador en la lista de jugadores guardados.
    public Mensajes guardarJugador(){
        if(saldo.tengoDinero()){
            ArrayList<Jugador> jugadores = Serializador.cargarJugadoresGuardados();

            boolean agregar = true;

            for(Jugador j: jugadores){
                if(nombre.equals(j.getNombre())){
                    agregar = false;
                    break;
                }
            }

            if(agregar) {
                jugadores.add(this);
            }

            Serializador.guardarJugador(jugadores);

            return Mensajes.JUGADOR_GUARDADO;
        }

        return Mensajes.NO_POSEE_DINERO_PARA_GUARDAR;
    }

    @Override
    public String toString() {
        return this.nombre; // Esto hará que se muestre solo el nombre en el JOptionPane
    }
}
