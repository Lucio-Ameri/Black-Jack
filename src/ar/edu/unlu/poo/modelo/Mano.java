package ar.edu.unlu.poo.modelo;

import ar.edu.unlu.poo.modelo.enumerados.EstadoDeLaMano;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Mano implements Serializable {
    @Serial
    private static final long serialVersionUID = 8L;

    private List<Carta> cartasDeLaMano;
    private EstadoDeLaMano estado;
    private int totalMano;

    public Mano(){
        this.cartasDeLaMano = new ArrayList<Carta>();
        this.estado = EstadoDeLaMano.TURNO_INICIAL;
        this.totalMano = 0;
    }


    //metodo: devuelve las cartas de la mano.
    public List<Carta> getCartasDeLaMano(){
        return cartasDeLaMano;
    }


    //metodo: devuelve el total actual de la mano.
    public int getTotalMano(){
        return totalMano;
    }


    //metodo: devuelve el estado de la mano.
    public EstadoDeLaMano getEstadoDeLaMano(){
        return estado;
    }


    //metodo: informa si la mano se encuentra en el turno inicial.
    public boolean manoTurnoInicial(){
        return estado == EstadoDeLaMano.TURNO_INICIAL;
    }


    //metodo: calcula el total de la mano.
    protected void calcularTotal(){
        int total = 0;
        int totalAs = 0;

        for(Carta c: cartasDeLaMano){
            if(c.cartaVisible()){
                if(c.esUnAs()){
                    totalAs ++;
                }

                total += c.getValorNumericoCarta();
            }
        }

        while(totalAs > 0 && total > 21){
            totalAs --;
            total -= 10;
        }

        totalMano = total;
        actualizarEstadoMano(totalMano);
    }


    //metodo: cambia el estado de la mano segun el parametro.
    protected void cambiarEstadoDeLaMano(EstadoDeLaMano en){
        estado = en;
    }



    protected abstract void actualizarEstadoMano(int total);
    public abstract void recibirCarta(Carta c);
}
