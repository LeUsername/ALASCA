package wattwatt.interfaces.appareils.suspensibles;

/**
 * L'interface <code>IRefrigerateur</code>
 * 
 * <p>
 * Created on : 2019-10-02
 * </p>
 * 
 * @author 3408625
 *
 */

public interface IRefrigerateur extends ISuspensible {

	/**
	 * 
	 * @return Temperature du refrigerateur en °C
	 * @throws Exception
	 */
	public double verifTemperature() throws Exception;

	/**
	 * 
	 * @throws Exception
	 */
	public void augmenterTemperature() throws Exception;

	/**
	 * 
	 * @throws Exception
	 */
	public void diminuerTemperature() throws Exception;
}
