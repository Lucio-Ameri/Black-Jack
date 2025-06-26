package ar.edu.unlu.poo.persistencia;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecordsHistoricos implements Serializable {
    @Serial
    private static final long serialVersionUID = 12L;

    private List<String> jugadores;
    private HashMap<String, Double> puntajes;

    public RecordsHistoricos(){
        this.jugadores = new ArrayList<String>();
        this.puntajes = new HashMap<String, Double>();
    }


    //metodo: funcion que indica si un jugador se encuentra o no en la lista.
    private boolean jugadorEnRecordHistorico(String nombre){
        return jugadores.contains(nombre);
    }


    //metodo: actualiza la lista de puntajes, tanto con jugadores nuevos como con viejos.
    public void actualizarLista(String nombre, double maximo){
        if(jugadorEnRecordHistorico(nombre)){

            if(puntajes.get(nombre) < maximo){
                puntajes.replace(nombre, maximo);
            }
        }

        else{

            if(maximo > 1300.0) {
                jugadores.add(nombre);
                puntajes.put(nombre, maximo);
            }
        }
    }


    //metodo: obtengo una lista de puntajes maximos historicos de cada jugador.
    public List<String> obtenerListaTopHistorico(){
        if(!jugadores.isEmpty()) {
            List<String> listaOrdenada = new ArrayList<String>(jugadores);
            listaOrdenada.sort((j1, j2) -> Double.compare(puntajes.get(j1), puntajes.get(j2)));

            List<String> listaTopDefinitiva = new ArrayList<>();
            int i = 1;
            for (String s : listaOrdenada) {
                listaTopDefinitiva.add("TOP " + i + ": --- " + s + " --- " + puntajes.get(s));
                i ++;
            }

            return listaTopDefinitiva;
        }

        return new ArrayList<String>();
    }


    public void guardarRecords(){
        Serializador.guardarRecordsHistoricos(this);
    }

}
