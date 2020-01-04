package simulation.equipements.compteur.models;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentStateAccessI;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOAwithEquations;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.AbstractSimulationReport;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;
import simulation.equipements.compteur.models.events.ConsommationEvent;

@ModelExternalEvents(imported = { ConsommationEvent.class })
public class CompteurModel extends		AtomicHIOAwithEquations
{
	// -------------------------------------------------------------------------
	// Inner classes and types
	// -------------------------------------------------------------------------

	public static class		CompteurModelReport
	extends		AbstractSimulationReport
	{
		private static final long serialVersionUID = 1L;
		
		public			CompteurModelReport(String modelURI)
		{
			super(modelURI);
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String	toString()
		{
			return "CompteurModelReport(" + this.getModelURI() + ")" ;
		}
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long		serialVersionUID = 1L ;
	/** URI used to create instances of the model; assumes a singleton,
	 *  otherwise a different URI must be given to each instance.			*/
	public static final String		URI = "CompteurModel" ;

	private static final String		SERIES = "consommation" ;

	protected Value<Double>	consommationTotale = new Value<Double>(this, 0.0);

	/** plotter for the intensity level over time.							*/
	protected XYPlotter				consommationPlotter ;

	/** reference on the object representing the component that holds the
	 *  model; enables the model to access the state of this component.		*/
	protected EmbeddingComponentStateAccessI componentRef ;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a hair dryer model instance.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	uri != null
	 * pre	simulatedTimeUnit != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param uri				URI of the model.
	 * @param simulatedTimeUnit	time unit used for the simulation time.
	 * @param simulationEngine	simulation engine to which the model is attached.
	 * @throws Exception		<i>to do.</i>
	 */
	public				CompteurModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		SimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine) ;

		// creation of a plotter to show the evolution of the intensity over
		// time during the simulation.
		PlotterDescription pd =
				new PlotterDescription(
						"Total consommation",
						"Time (sec)",
						"Consommation (kW)",
						100,
						400,
						600,
						400) ;
		this.consommationPlotter = new XYPlotter(pd) ;
		this.consommationPlotter.createSeries(SERIES) ;

		// create a standard logger (logging on the terminal)
		this.setLogger(new StandardLogger()) ;
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws Exception
	{
		// The reference to the embedding component
		this.componentRef =
			(EmbeddingComponentStateAccessI) simParams.get("componentRef") ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		// initialisation of the intensity plotter 
		this.consommationPlotter.initialise() ;
		// show the plotter on the screen
		this.consommationPlotter.showPlotter() ;

		try {
			// set the debug level triggering the production of log messages.
			this.setDebugLevel(1) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}

		super.initialiseState(initialTime) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseVariables(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	protected void		initialiseVariables(Time startTime)
	{
		// first data in the plotter to start the plot.
		this.consommationPlotter.addData(
				SERIES,
				this.getCurrentStateTime().getSimulatedTime(),
				this.getConsommation());

		super.initialiseVariables(startTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public Vector<EventI>	output()
	{
		// the model does not export any event.
		return null ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration		timeAdvance()
	{
		if (this.componentRef == null) {
			// the model has no internal event, however, its state will evolve
			// upon reception of external events.
			return Duration.INFINITY ;
		} else {
			// This is to test the embedding component access facility.
			return new Duration(10.0, TimeUnit.SECONDS) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		if (this.componentRef != null) {
			// This is an example showing how to access the component state
			// from a simulation model; this must be done with care and here
			// we are not synchronising with other potential component threads
			// that may access the state of the component object at the same
			// time.
			try {
				this.logMessage("component state = " +
						componentRef.getEmbeddingComponentStateValue("consommation")) ;
			} catch (Exception e) {
				throw new RuntimeException(e) ;
			}
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		Vector<EventI> currentEvents = this.getStoredEventAndReset() ;

		assert	currentEvents != null && currentEvents.size() == 1 ;

		Event ce = (Event) currentEvents.get(0) ;

		assert ce instanceof ConsommationEvent;
		
		this.consommationPlotter.addData(
				SERIES,
				this.getCurrentStateTime().getSimulatedTime(),
				this.getConsommation());

		ce.executeOn(this) ;
		// add a new data on the plotter; this data will open a new piece
				
		this.consommationPlotter.addData(
				SERIES,
				this.getCurrentStateTime().getSimulatedTime(),
				this.getConsommation());
				
		super.userDefinedExternalTransition(elapsedTime) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime) throws Exception
	{
		this.consommationPlotter.addData(
				SERIES,
				endTime.getSimulatedTime(),
				this.getConsommation()) ;

		super.endSimulation(endTime) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#getFinalReport()
	 */
	@Override
	public SimulationReportI	getFinalReport() throws Exception
	{
		return new CompteurModelReport(this.getURI()) ;
	}

	// ------------------------------------------------------------------------
	// Model-specific methods
	// ------------------------------------------------------------------------
	
	public double		getConsommation()
	{
		return this.consommationTotale.v;
	}
	
	public void		setConsommation(double c)
	{
		this.consommationTotale.v += c;
	}
}
