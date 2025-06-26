package ar.edu.unlu.poo.modelo;

import ar.edu.unlu.poo.modelo.enumerados.PaloCarta;
import ar.edu.unlu.poo.modelo.enumerados.ValorCarta;
import ar.edu.unlu.poo.modelo.enumerados.VisibilidadCarta;

import java.io.Serial;
import java.io.Serializable;

public class Carta implements Serializable {
    @Serial
    private static final long serialVersionUID = 3L;

    private final PaloCarta palo;
    private final ValorCarta valor;
    private VisibilidadCarta estado;

    public Carta(PaloCarta p, ValorCarta v){
        this.palo = p;
        this.valor = v;
        this.estado = VisibilidadCarta.OCULTA;
    }


    //metodo: devuelve el PALO de la carta.
    public PaloCarta getPaloDeCarta(){
        return palo;
    }


    //metodo: devuelve el TIPO de la carta
    public ValorCarta getTipoDeValor(){
        return valor;
    }


    //metodo: devuelve el VALOR NUMERICO de la carta.
    public int getValorNumericoCarta(){
        return valor.getValor();
    }


    //metodo: informa si la carta es VISIBLE o no.
    public boolean cartaVisible(){
        return (estado == VisibilidadCarta.VISIBLE);
    }


    //metodo: revela la carta si es que esta OCULTA.
    public void revelarCarta(){
        if(!cartaVisible()) {
            estado = VisibilidadCarta.VISIBLE;
        }
    }


    //metodo: informa si la carta es del TIPO AS.
    public boolean esUnAs(){
        return valor.tipoAs();
    }


    //metodo: informa si las cartas son similares (mismo TIPO)
    public boolean cartasSimilares(Carta c){
        return valor == c.getTipoDeValor();
    }
}
