package simulation.models.fridge;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.random.RandomDataGenerator;

import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;
import fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.AbstractSimulationReport;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;
import simulation.events.fridge.CloseEvent;
import simulation.events.fridge.OpenEvent;
import simulation.tools.fridge.FridgeDoor;
import wattwatt.tools.URIS;

//-----------------------------------------------------------------------------
@ModelExternalEvents(exported = { CloseEvent.class, 
								 OpenEvent.class})
//-----------------------------------------------------------------------------
public class FridgeUserModel 
extends		AtomicES_Model
{
	// -------------------------------------------------------------------------
	// Inner classes
	// -------------------------------------------------------------------------

	public static class	RefrigerateurUserModelReport
	extends		AbstractSimulationReport
	{
		private static final long			serialVersionUID = 1L ;

		public			RefrigerateurUserModelReport(String modelURI)
		{
			super(modelURI) ;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String	toString()
		{
			return "RefrigerateurUserModelReport(" + this.getModelURI() + ")";
			
		}
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L ;
	/** name of the series used in the plotting.							*/
	private static final String	SERIES = "Refrigerateur's door opened/closed" ;
	/** URI to be used when creating the model.								*/
	public static final String	URI = URIS.FRIDGE_USER_MODEL_URI ;
	/** name of the run parameter defining the mean time between
	 *  interruptions.														*/
	public static final String	MTBI = "mtbi" ;
	/** name of the run parameter defining the mean duration of
	 *  interruptions.														*/
	public static final String	MID = "mid" ;

	// Model simulation implementation variables
	/** the time between interruptions follows an exponential
	 *  distribution with mean <code>meanTimeBetweenInterruptions</code>.	*/
	protected double					meanTimeBetweenInterruptions ;
	/** the duration of the interruptions follows an exponential
	 *  distribution with mean <code>meanInterruptionDuration</code>.		*/
	protected double					meanInterruptionDuration ;
	/**	a random number generator from common math library.					*/
	protected final RandomDataGenerator	rg ;
	/** 	the current state of the refrigerateur's door.					*/
	protected FridgeDoor currentState ;
	
	/** next event to be sent. */
	protected Class<?> nextEvent;

	// Plotting
	/** Frame used to plot the bandwidth interruption function during
	 *  the simulation.														*/
	protected XYPlotter					plotter ;
	
	/** reference on the object representing the component that holds the
	 *  model; enables the model to access the state of this component.		*/
	protected EmbeddingComponentAccessI componentRef ;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an instance of the WiFi disconnection model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	simulatedTimeUnit != null
	 * pre	simulationEngine == null ||
	 * 		    	simulationEngine instanceof HIOA_AtomicEngine
	 * post	this.getURI() != null
	 * post	uri != null implies this.getURI().equals(uri)
	 * post	this.getSimulatedTimeUnit().equals(simulatedTimeUnit)
	 * post	simulationEngine != null implies
	 * 			this.getSimulationEngine().equals(simulationEngine)
	 * </pre>
	 *
	 * @param uri					unique identifier of the model.
	 * @param simulatedTimeUnit		time unit used for the simulation clock.
	 * @param simulationEngine		simulation engine enacting the model.
	 * @throws Exception			<i>todo.</i>
	 */
	public				FridgeUserModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		SimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine) ;

		// Create the random number generators
		this.rg = new RandomDataGenerator() ;
	}

	// -------------------------------------------------------------------------
	// Simulation protocol and related methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws Exception
	{
		// Get the values of the run parameters in the map using their names
		// and set the model implementation variables accordingly
		String vname = this.getURI() + ":" + MTBI ;
		this.meanTimeBetweenInterruptions = (double) simParams.get(vname) ;
		vname = this.getURI() + ":" + MID ;
		this.meanInterruptionDuration = (double) simParams.get(vname) ;

		vname = this.getURI() + ":" + PlotterDescription.PLOTTING_PARAM_NAME ;
		// Initialise the look of the plotter
		PlotterDescription pd = (PlotterDescription) simParams.get(vname) ;
		this.plotter = new XYPlotter(pd) ;
		this.plotter.createSeries(SERIES) ;
		
		// The reference to the embedding component
		this.componentRef = (EmbeddingComponentAccessI) simParams.get(URIS.FRIDGE_URI) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{

		// initialisation of the random number generators
		this.rg.reSeedSecure() ;

		super.initialiseState(initialTime);

		// Schedule the first SwitchOn event.
		Duration d1 = new Duration(0.0, this.getSimulatedTimeUnit());
		Duration d2 = new Duration(2.0 * this.meanTimeBetweenInterruptions * this.rg.nextBeta(1.75, 1.75),
				this.getSimulatedTimeUnit());
		
		Time t = this.getCurrentStateTime().add(d1).add(d2);
		this.scheduleEvent(new OpenEvent(t));
		
		// initialisation of the event occurrences plotter on the screen
		if (this.plotter != null) {
			this.plotter.initialise() ;
			this.plotter.showPlotter() ;
		}


		// The model is set to start in the state interrupted and with a
		// resumption event that occurs at time 0.
		if(this.componentRef == null) {
			this.currentState = FridgeDoor.OPENED ;
		}
		else {
			try {
				this.currentState = (FridgeDoor) this.componentRef.getEmbeddingComponentStateValue("door");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		this.nextTimeAdvance = this.timeAdvance() ;
		this.timeOfNextEvent =
				this.getCurrentStateTime().add(this.getNextTimeAdvance()) ;
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model#timeAdvance()
	 */
	@Override
	public Duration timeAdvance() {
		Duration d = super.timeAdvance();
		return d;
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model#output()
	 */
	@Override
	public ArrayList<EventI> output() {
		if (componentRef != null) {
			this.nextEvent = super.output().get(0).getClass();
			return null;
		} else {
			assert !this.eventList.isEmpty();
			ArrayList<EventI> ret = super.output();
			assert ret.size() == 1;

			this.nextEvent = ret.get(0).getClass();

			return ret;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		if(this.componentRef == null) {
			if (this.currentState == FridgeDoor.OPENED) {
				
				this.currentState = FridgeDoor.CLOSED ;

				// compute the time of occurrence (in the future)
//				double closedTime = this.rg.nextExponential(this.meanTimeBetweenInterruptions);
//				Duration d = new Duration(closedTime, this.getSimulatedTimeUnit());
//				Time t = this.getCurrentStateTime().add(d);
//				this.scheduleEvent(new CloseEvent(t)) ;

				// Update the plotter with the next event occurrence
				if (this.plotter != null) {
					this.plotter.addData(
							SERIES,
							this.getCurrentStateTime().getSimulatedTime(),
							0.0) ;
					this.plotter.addData(
							SERIES,
							this.getCurrentStateTime().getSimulatedTime(),
							1.0) ;
				}
			} else {
				assert	this.currentState == FridgeDoor.CLOSED ;
				this.currentState = FridgeDoor.OPENED ;

				// compute the time of occurrence (in the future)
//				double openedTime = this.rg.nextExponential(this.meanInterruptionDuration);
//				Duration d = new Duration(openedTime, this.getSimulatedTimeUnit());
//				Time t = this.getCurrentStateTime().add(d);
//				this.scheduleEvent(new OpenEvent(t)) ;
//				
				if (this.plotter != null) {
					this.plotter.addData(
							SERIES,
							this.getCurrentStateTime().getSimulatedTime(),
							1.0) ;
					this.plotter.addData(
							SERIES,
							this.getCurrentStateTime().getSimulatedTime(),
							0.0) ;
				}
			}
		}else {
			try {
				this.currentState = (FridgeDoor) this.componentRef.getEmbeddingComponentStateValue("door");
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (this.currentState == FridgeDoor.OPENED) {
				try {
					this.componentRef.setEmbeddingComponentStateValue("close", null);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				double closedTime = this.rg.nextExponential(this.meanTimeBetweenInterruptions);
				Duration d = new Duration(closedTime, this.getSimulatedTimeUnit());
				Time t = this.getCurrentStateTime().add(d);
				this.scheduleEvent(new CloseEvent(t)) ;

				if (this.plotter != null) {
					this.plotter.addData(
							SERIES,
							this.getCurrentStateTime().getSimulatedTime(),
							0.0) ;
					this.plotter.addData(
							SERIES,
							this.getCurrentStateTime().getSimulatedTime(),
							1.0) ;
				}
			} else {
				assert	this.currentState == FridgeDoor.CLOSED ;
				try {
					this.componentRef.setEmbeddingComponentStateValue("open", null);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				double openedTime = this.rg.nextExponential(this.meanInterruptionDuration);
				Duration d = new Duration(openedTime, this.getSimulatedTimeUnit());
				Time t = this.getCurrentStateTime().add(d);
				this.scheduleEvent(new OpenEvent(t)) ;

				if (this.plotter != null) {
					this.plotter.addData(
							SERIES,
							this.getCurrentStateTime().getSimulatedTime(),
							1.0) ;
					this.plotter.addData(
							SERIES,
							this.getCurrentStateTime().getSimulatedTime(),
							0.0) ;
				}
			}

		}
		
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime) throws Exception
	{
		// advance the time at the current (ending) time and finish the
		// event occurrences function for the report.
		double end = endTime.getSimulatedTime() ;
		if (this.currentState == FridgeDoor.OPENED) {
			if (this.plotter != null) {
				this.plotter.addData(SERIES, end, 0.0) ;
			}
		} else {
			assert	this.currentState == FridgeDoor.CLOSED ;
			if (this.plotter != null) {
				this.plotter.addData(SERIES, end, 1.0) ;
			}
		}
		// end the simulation run for this model.
		super.endSimulation(endTime) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getFinalReport()
	 */
	@Override
	public SimulationReportI	getFinalReport()
	throws Exception
	{
		
		return new RefrigerateurUserModelReport(this.getURI()) ;
	}


}
// -----------------------------------------------------------------------------