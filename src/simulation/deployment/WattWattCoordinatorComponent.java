package simulation.deployment;

import fr.sorbonne_u.components.AbstractComponent;

public class WattWattCoordinatorComponent
extends AbstractComponent
{
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create the Coordinator component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 */
	protected			WattWattCoordinatorComponent()
	{
		// a coordinator needs to have 2 threads, one to execute the simulator
		// and the other to receive parent notifications from the submodels.
		super(2, 0) ;
		this.initialise() ;
	}

	/**
	 * create the Coordinator component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	reflectionInboundPortURI != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param reflectionInboundPortURI	URI of the reflection inbound port of the created component.
	 */
	protected			WattWattCoordinatorComponent(
		String reflectionInboundPortURI
		)
	{
		// a coordinator needs to have 2 threads, one to execute the simulator
		// and the other to receive parent notifications from the submodels.
		super(reflectionInboundPortURI, 2, 0) ;
		this.initialise() ;
	}

	/**
	 * initialise the Coordinator component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 */
	protected void		initialise()
	{
		this.tracer.setTitle("WattWatt coupled model component") ;
		this.tracer.setRelativePosition(1, 4) ;
		this.toggleTracing() ;		
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public void			execute() throws Exception
	{
		super.execute() ;

		this.logMessage("Coordinator component begins execution.");
	}
}
// -----------------------------------------------------------------------------