package practica3_4;

public class Carta {
	public enum Palo{OROS, COPAS, ESPADAS, BASTOS};
	public enum Valor{AS, DOS, TRES, CUATRO, CINCO, SEIS, SIETE, SOTA, CABALLO, REY};
	
	protected String palo;
	protected String valor;
	public String getPalo() {
		return palo;
	}
	public void setPalo(String palo) {
		this.palo = palo;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	public int getValorNumerico() {
		if(valor.equals("AS")) {
			return 1;
		}else if(valor.equals("DOS")) {
			return 2;
		}else if(valor.equals("TRES")) {
			return 3;
		}else if(valor.equals("CUATRO")) {
			return 4;
		}else if(valor.equals("CINCO")) {
			return 5;
		}else if(valor.equals("SEIS")) {
			return 6;
		}else if(valor.equals("SIETE")) {
			return 7;
		}else if(valor.equals("SOTA")) {
			return 10;
		}else if(valor.equals("CABALLO")) {
			return 11;
		}else{
			return 12;
		}
	}
	public Carta(String palo, String valor) {
		super();
		this.palo = palo;
		this.valor = valor;
	}
	@Override
	public String toString() {
		return "Carta [palo=" + palo + ", valor=" + valor + "]";
	}
}
