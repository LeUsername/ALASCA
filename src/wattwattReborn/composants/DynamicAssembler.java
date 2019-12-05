package wattwattReborn.composants;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.pre.dcc.connectors.DynamicComponentCreationConnector;
import fr.sorbonne_u.components.pre.dcc.interfaces.DynamicComponentCreationI;
import fr.sorbonne_u.components.pre.dcc.ports.DynamicComponentCreationOutboundPort;
import fr.sorbonne_u.components.reflection.connectors.ReflectionConnector;
import fr.sorbonne_u.components.reflection.interfaces.ReflectionI;
import fr.sorbonne_u.components.reflection.ports.ReflectionOutboundPort;
import wattwattReborn.connecteurs.CompteurConnector;
import wattwattReborn.connecteurs.ControleurLaunchConnector;
import wattwattReborn.interfaces.compteur.ICompteur;
import wattwattReborn.interfaces.controleur.IControleur;
import wattwattReborn.interfaces.controleur.IControleurLaunch;
import wattwattReborn.ports.controleur.ControleurLaunchOutBoundPort;

@RequiredInterfaces(required = { DynamicComponentCreationI.class, IControleurLaunch.class })
public class DynamicAssembler extends AbstractComponent {

	protected DynamicComponentCreationOutboundPort portToControleurJVM;
	protected DynamicComponentCreationOutboundPort portToCompteurJVM;

	protected String controleurJVMURI;
	protected String compteurJVMURI;
	protected String controleurOutboundPortURI;
	protected String compteurInboundPortURI;

	protected String controleurLaunchInboundPortURI;

	protected DynamicAssembler(String controleurJVMUri, String compteurJVMUri) throws Exception {
		super(1, 0);
		this.controleurJVMURI = controleurJVMUri;
		this.compteurJVMURI = compteurJVMUri;
	}

	@Override
	public void start() throws ComponentStartException {
		super.start();

		try {
			this.portToControleurJVM = new DynamicComponentCreationOutboundPort(this);
			this.portToControleurJVM.localPublishPort();
			
			this.doPortConnection(this.portToControleurJVM.getPortURI(),
					this.portToControleurJVM + AbstractCVM.DCC_INBOUNDPORT_URI_SUFFIX,
					DynamicComponentCreationConnector.class.getCanonicalName());

			this.portToCompteurJVM = new DynamicComponentCreationOutboundPort(this);
			this.portToCompteurJVM.localPublishPort();
			this.doPortConnection(this.portToCompteurJVM.getPortURI(),
					this.portToCompteurJVM + AbstractCVM.DCC_INBOUNDPORT_URI_SUFFIX,
					DynamicComponentCreationConnector.class.getCanonicalName());

			this.runTask(new AbstractComponent.AbstractTask() {
				@Override
				public void run() {
					try {
						((DynamicAssembler) this.getTaskOwner()).dynamicDeploy();
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			});

		} catch (Exception e) {
			throw new ComponentStartException(e);
		}
	}

	public void dynamicDeploy() throws Exception {
		assert this.portToControleurJVM != null;
		assert this.portToControleurJVM.connected();
		assert this.portToCompteurJVM != null;
		assert this.portToCompteurJVM.connected();

		// call the dynamic component creator of the provider JVM to create
		// the provider component
		String compteurRIPURI = this.portToCompteurJVM.createComponent(Compteur.class.getCanonicalName(),
				new Object[] { "osef", "osef" });
		// call the dynamic component creator of the consumer JVM to create
		// the provider component
		String controleurRIPURI = this.portToControleurJVM.createComponent(DynamicControleur.class.getCanonicalName(),
				new Object[] {});

		this.addRequiredInterface(ReflectionI.class);
		ReflectionOutboundPort rop = new ReflectionOutboundPort(this);
		rop.localPublishPort();

		// connect to the provider (server) component
		rop.doConnection(compteurRIPURI, ReflectionConnector.class.getCanonicalName());
		// toggle logging on the provider component
		rop.toggleTracing();
		// get the URI of the URI provider inbound port of the provider
		// component.
		String[] uris = rop.findInboundPortURIsFromInterface(ICompteur.class);
		assert uris != null && uris.length == 1;
		this.compteurInboundPortURI = uris[0];
		this.doPortDisconnection(rop.getPortURI());

		// connect to the consumer (client) component
		rop.doConnection(controleurRIPURI, ReflectionConnector.class.getCanonicalName());
		// toggle logging on the consumer component
		rop.toggleTracing();
		// get the URI of the launch inbound port of the consumer component.
		uris = rop.findInboundPortURIsFromInterface(IControleurLaunch.class);
		assert uris != null && uris.length == 1;
		this.controleurLaunchInboundPortURI = uris[0];
		// get the URI of the URI consumer outbound port of the consumer
		// component.
		uris = rop.findOutboundPortURIsFromInterface(IControleur.class);
		assert uris != null && uris.length == 1;
		this.controleurOutboundPortURI = uris[0];
		// connect the consumer outbound port top the provider inbound one.
		rop.doPortConnection(this.controleurOutboundPortURI, this.compteurInboundPortURI,
				CompteurConnector.class.getCanonicalName());
		this.doPortDisconnection(rop.getPortURI());
		rop.unpublishPort();

		this.runTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try {
					((DynamicAssembler) this.getTaskOwner()).launch();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
	}

	public void launch() throws Exception {
		ControleurLaunchOutBoundPort p = new ControleurLaunchOutBoundPort(this);
		p.publishPort();
		this.doPortConnection(p.getPortURI(), this.controleurLaunchInboundPortURI,
				ControleurLaunchConnector.class.getCanonicalName());
		p.printConso();
		this.doPortDisconnection(p.getPortURI());
		p.unpublishPort();
		p.destroyPort();
	}
	
	@Override
	public void			finalise() throws Exception
	{
		if (this.portToControleurJVM.connected()) {
			this.doPortDisconnection(this.portToControleurJVM.getPortURI()) ;
		}
		if (this.portToCompteurJVM.connected()) {
			this.doPortDisconnection(this.portToCompteurJVM.getPortURI()) ;
		}

		super.finalise() ;
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public void			shutdown() throws ComponentShutdownException
	{
		try {
			this.portToControleurJVM.unpublishPort() ;
			this.portToCompteurJVM.unpublishPort() ;
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}

		super.shutdown();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdownNow()
	 */
	@Override
	public void			shutdownNow() throws ComponentShutdownException
	{
		try {
			this.portToControleurJVM.unpublishPort() ;
			this.portToCompteurJVM.unpublishPort() ;
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}

		super.shutdownNow() ;
	}

}
