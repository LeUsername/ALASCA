package interfaces.appareils.planifiables;

import interfaces.appareils.IAppareil;

/**
 * L'interface <code>IPlanifiable</code>
 * 
 * <p>
 * Created on : 2019-10-02
 * </p>
 * 
 * @author 3408625
 *
 */
public interface IPlanifiable extends IAppareil {
	/**
	 * Fixe une heure a laquelle demarrer l'appareil
	 * 
	 * @throws Exception
	 */
	public void setStart(int date) throws Exception;

	/**
	 * Fixe une heure a laquelle stopper l'appareil
	 * 
	 * @throws Exception
	 */
	public void setStop(int date) throws Exception;
}
