package data;

import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataRequiredI;

/**
 * La classe <code>StringData</code> qui represente les donnees envoyees par la
 * compteur. On y retrouve la consommation actuelle, la quantite d'electricite
 * produite et la quantite d'electricite disponible (ces trois derniers en kWh).
 * Ces valeurs sont representees par des entiers.
 * 
 * <p>
 * Created on : 2019-10-19
 * </p>
 * 
 * @author 3408625
 *
 */

public class CompteurData implements DataOfferedI.DataI, DataRequiredI.DataI {


	private static final long serialVersionUID = 1L;

	/**
	 * La quantite d'electricite consommee (en kWh)
	 */
	private int consommation = 0;

	/**
	 * La quantite d'electricite produite (en kWh)
	 */
	private int productionAleatoire = 0;

	/**
	 * La quantite d'electricite disponible dans les batteries (en kWh)
	 */
	private int productionIntermittente = 0;

	public void setConsommation(int c) {
		this.consommation = c;
	}

	public void setProdAlea(int c) {
		this.productionAleatoire = c;
	}

	public void setProdInterm(int c) {
		this.productionIntermittente = c;
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
