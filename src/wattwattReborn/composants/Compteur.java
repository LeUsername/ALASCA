package wattwattReborn.composants;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.ports.PortI;
import wattwattReborn.interfaces.compteur.ICompteur;
import wattwattReborn.interfaces.controleur.IControleur;
import wattwattReborn.ports.compteur.CompteurInPort;
import wattwattReborn.tools.compteur.CompteurReglage;

@OfferedInterfaces(offered = ICompteur.class)
@RequiredInterfaces(required = IControleur.class)
public class Compteur extends AbstractComponent {

	protected CompteurInPort cptin;

	protected int consomation;

	public Compteur(String uri, String compteurIn) throws Exception {
		super(1,1);
//		super(uri, 1, 1);

//		this.cptin = new CompteurInPort(compteurIn, this);
		this.cptin = new CompteurInPort(this);
		this.cptin.publishPort();
		
		/////////
		if (AbstractCVM.isDistributed) {
			this.executionLog.setDirectory(System.getProperty("user.dir")) ;
		} else {
			this.executionLog.setDirectory(System.getProperty("user.home")) ;
		}
		this.tracer.setTitle("compteur dynamic") ;
		/////////
		
		this.tracer.setRelativePosition(0, 1);
	}

	public int giveConso() throws Exception {
		return consomation;
	}

	public void majConso() {
		Random rand = new Random();
		this.consomation = CompteurReglage.MIN_THR_HOUSE_CONSOMMATION
				+ rand.nextInt(CompteurReglage.MAX_THR_HOUSE_CONSOMMATION - CompteurReglage.MIN_THR_HOUSE_CONSOMMATION);
	}

	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.logMessage("Compteur starting");
		try {
			Thread.sleep(10);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void execute() throws Exception {
		super.execute();

		this.scheduleTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try {
					while (true) {
						((Compteur) this.getTaskOwner()).majConso();
						Thread.sleep(CompteurReglage.MAJ_RATE);
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}, 100, TimeUnit.MILLISECONDS);
	}

	@Override
	public void shutdown() throws ComponentShutdownException {
		this.logMessage("Compteur shutdown");
		try {
			this.cptin.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.shutdown();
	}
	
	@Override
	public void			shutdownNow() throws ComponentShutdownException
	{
		try {
			PortI[] p = this.findPortsFromInterface(ICompteur.class) ;
			p[0].unpublishPort() ;
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdownNow();
	}

	@Override
	public void finalise() throws Exception {

		super.finalise();
	}

}
