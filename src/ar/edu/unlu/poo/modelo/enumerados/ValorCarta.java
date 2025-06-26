package ar.edu.unlu.poo.modelo.enumerados;

public enum ValorCarta {
    DOS(2),
    TRES(3),
    CUATRO(4),
    CINCO(5),
    SEIS(6),
    SIETE(7),
    OCHO(8),
    NUEVE(9),
    DIEZ(10),
    J(10),
    Q(10),
    K(10),
    A(11);

    private final int valor;

    ValorCarta(int v){
        this.valor = v;
    }


    //metodo: informa si el TIPO del enumerado es AS.
    public boolean tipoAs(){
        return (this == A);
    }


    //metodo: devuelve el VALOR del enumerado.
    public int getValor(){
        return valor;
    }
}
