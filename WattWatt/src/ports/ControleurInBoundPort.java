//package ports;
//
//import fr.sorbonne_u.components.ComponentI;
//import fr.sorbonne_u.components.interfaces.DataOfferedI.DataI;
//import fr.sorbonne_u.components.ports.AbstractDataInboundPort;
//import interfaces.IControleur;
//
///***
// * La classe <code>ControleurInBoundPort</code> 
// * 
// * <p>
// * Created on : 2019-10-17
// * </p>
// * 
// * @author 3408625
// *
// */
//
//public class ControleurInBoundPort extends AbstractDataInboundPort {
//
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = -4459008402756365871L;
//
//	public ControleurInBoundPort(String uri, ComponentI owner) throws Exception {
//		super(uri, IControleur.class, owner);
//	}
//
//	public ControleurInBoundPort(ComponentI owner) throws Exception {
//		super(IControleur.class, owner);
//	}
//
//	@Override
//	public DataI get() throws Exception {
//		return null;
//	}
//
//}
