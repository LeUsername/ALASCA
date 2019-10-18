package data;

import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataRequiredI;

public class CompteurData implements DataOfferedI.DataI, DataRequiredI.DataI{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8027716644045741525L;
	
	private int consommation = 0;
	private int productionAleatoire = 0;
	private int productionIntermittente = 0;
	
	
	public void setConsommation(int c) {
		this.consommation= c;
	}
	public void setProdAlea(int c) {
		this.productionAleatoire= c;
	}
	public void setProdInterm(int c) {
		this.productionIntermittente= c;
	}
	
	public int getProdAlea() {
		return productionAleatoire;
	}
	public int getProdInterm() {
		return productionIntermittente;
	}
	public int getConsommation() {
		return consommation;
	}

}
