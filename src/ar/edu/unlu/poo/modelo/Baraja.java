package ar.edu.unlu.poo.modelo;

import ar.edu.unlu.poo.modelo.enumerados.PaloCarta;
import ar.edu.unlu.poo.modelo.enumerados.ValorCarta;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Baraja implements Serializable {

    @Serial
    private static final long serialVersionUID = 2L;

    private List<Carta> baraja;

    public Baraja(){
        this.baraja = new ArrayList<Carta>();
        generarBaraja();
    }


    //metodo: devuelve la primera carta que posee el mazo.
    public Carta repartir(){
        return baraja.remove(0);
    }


    //metodo: informa si la baraja se encuentra vacia.
    public boolean barajaVacia(){
        return baraja.isEmpty();
    }


    //metodo: genera la baraja.
    public void generarBaraja(){
        generarCartas();
        mezclarCartas();
    }


    //metodo: genera las cartas de la baraja (8 mazos).
    private void generarCartas(){
        baraja.clear();

        ValorCarta[] valores = ValorCarta.values();
        PaloCarta[] palos = PaloCarta.values();

        for(int i = 0; i < 8; i++){
            for(PaloCarta p: palos){
                for(ValorCarta v: valores){

                    baraja.add(new Carta(p, v));
                }
            }
        }
    }


    //metodo: mezcla las cartas de la baraja.
    private void mezclarCartas(){
        Collections.shuffle(baraja);
    }
}
