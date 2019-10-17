package interfaces;

import data.StringData;
import fr.sorbonne_u.components.interfaces.DataRequiredI;

/**
 * L'interface <code>ICompteur</code>
 * 
 * <p>
 * Created on : 2019-10-09
 * </p>
 * 
 * @author 3408625
 *
 */
public interface ICompteur extends  DataRequiredI {

//	/**
//	 * renvoie la consommation totale de tous les appareils allumes
//	 * 
//	 * @return
//	 * @throws Exception
//	 */
//	public int getAllConsommation() throws Exception;
//
//	/**
//	 * renvoie la production totale d'energie par les sources aleatoires
//	 * 
//	 * @return
//	 * @throws Exception
//	 */
//	public int getAllProductionsAleatoires() throws Exception;
//
//	/**
//	 * renvoie la production totale d'energie par les sources intermittentes
//	 * 
//	 * @return
//	 * @throws Exception
//	 */
//	public int getAllProductionsIntermittentes() throws Exception;
//
//	/**
//	 * Remet le compteur a zero
//	 * 
//	 * @throws Exception
//	 */
//	public void reset() throws Exception;
	
	public DataRequiredI.DataI getData(String uri) throws Exception;

	public void sendData(StringData msg) throws Exception;

}
