package ar.edu.unlu.poo.modelo;

import ar.edu.unlu.poo.modelo.enumerados.EstadoDeLaMano;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ManoDealer extends Mano implements Serializable {
    @Serial
    private static final long serialVersionUID = 9L;

    public ManoDealer(){
        super();
    }


    //metodo: informa si la primer carta del dealer esdel TIPO AS.
    public boolean primeraEsAs(){
        List<Carta> cartas = getCartasDeLaMano();
        return cartas.get(0).esUnAs();
    }


    //metodo: revela la mano del dealer y calcula su nuevo total.
    protected void revelarMano(){
        List<Carta> cartas = getCartasDeLaMano();

        for(Carta c: cartas){
            if(!c.cartaVisible()){
                c.revelarCarta();
            }
        }

        calcularTotal();
    }


    //metodo: actualiza el estado de la mano segun el total que posee.
    @Override
    protected void actualizarEstadoMano(int total) {
        if(total > 21){
            cambiarEstadoDeLaMano(EstadoDeLaMano.SE_PASO);
        }

        else if(total > 16){
            if(manoTurnoInicial() && total == 21){
                cambiarEstadoDeLaMano(EstadoDeLaMano.BLACKJACK);
            }

            else{
                cambiarEstadoDeLaMano(EstadoDeLaMano.SE_QUEDO);
            }
        }

        else{
            cambiarEstadoDeLaMano(getEstadoDeLaMano());
        }
    }


    //metodo: recibe una carta, revelandola en caso de que no sea la segunda, y calcula el total.
    @Override
    public void recibirCarta(Carta c) {
        List<Carta> cartasD = getCartasDeLaMano();

        if(cartasD.size() != 1){
            c.revelarCarta();
        }

        cartasD.add(c);
        calcularTotal();
    }
}
