//package ports;
//
//import composants.Compteur;
//import fr.sorbonne_u.components.ComponentI;
//import fr.sorbonne_u.components.ports.AbstractInboundPort;
//import interfaces.ICompteur;
//
///***
// * La classe <code>CompteurInBoundPort</code>
// * 
// * <p>
// * Created on : 2019-10-17
// * </p>
// * 
// * @author 3408625
// *
// */
//
//@SuppressWarnings("serial")
//public class CompteurInBoundPort extends AbstractInboundPort implements ICompteur {
//
//	public CompteurInBoundPort(String uri, ComponentI owner) throws Exception {
//		super(uri, ICompteur.class, owner);
//	}
//
//	public CompteurInBoundPort(ComponentI owner) throws Exception {
//		super(ICompteur.class, owner);
//	}
//
//	@Override
//	public int getAllConsommation() throws Exception {
//		return ((Compteur)this.getOwner()).getAllConsommation() ;
//	}
//
//	@Override
//	public int getAllProductionsAleatoires() throws Exception {
//		return ((Compteur)this.getOwner()).getAllProductionsAleatoires() ;
//	}
//
//	@Override
//	public int getAllProductionsIntermittentes() throws Exception {
//		return ((Compteur)this.getOwner()).getAllProductionsIntermittentes() ;
//	}
//
//	@Override
//	public void reset() throws Exception {
//		((Compteur)this.getOwner()).reset();
//	}
//
//}
