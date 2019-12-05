package wattwattReborn.composants;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import wattwattReborn.interfaces.compteur.ICompteur;
import wattwattReborn.interfaces.controleur.IControleurLaunch;
import wattwattReborn.ports.controleur.ControleurLaunchInBoundPort;
import wattwattReborn.ports.controleur.ControleurOutPort;

@RequiredInterfaces(required = { ICompteur.class })
@OfferedInterfaces(offered = { IControleurLaunch.class })
public class DynamicControleur extends AbstractComponent {

	protected ControleurLaunchInBoundPort clin;
	protected ControleurOutPort clout;
	
	protected DynamicControleur() throws Exception {
		super(0, 0);
		
		this.clout = new ControleurOutPort(this);
		this.clout.publishPort();
		
		this.clin = new ControleurLaunchInBoundPort(this);
		this.clin.publishPort();
		
		this.tracer.setTitle("dynamic controleur") ;
		this.tracer.setRelativePosition(1, 1) ;
	}
	
	
	@Override
	public void			finalise() throws Exception
	{
		this.doPortDisconnection(this.clout.getPortURI()) ;
		super.finalise();
	}
	
	@Override
	public void			shutdown() throws ComponentShutdownException
	{
		try {
			this.clout.unpublishPort() ;
			this.clin.unpublishPort() ;
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		
		super.shutdown();
	}
	
	@Override
	public void			shutdownNow() throws ComponentShutdownException
	{
		try {
			this.clout.unpublishPort() ;
			this.clin.unpublishPort() ;
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		
		super.shutdownNow();
	}
	
		public void printConso() {
			System.out.println("hihi test dynamique dans controleur");
		}

}
