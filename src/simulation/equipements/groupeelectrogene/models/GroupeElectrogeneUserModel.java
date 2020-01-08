package simulation.equipements.groupeelectrogene.models;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.random.RandomDataGenerator;

import fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ImportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.AbstractSimulationReport;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;
import simulation.equipements.groupeelectrogene.models.events.ReplenishEvent;
import simulation.equipements.groupeelectrogene.models.events.StartEvent;
import simulation.equipements.groupeelectrogene.models.events.StopEvent;
import simulation.equipements.groupeelectrogene.tools.GroupeElectrogeneUserBehaviour;

@ModelExternalEvents(exported = { StartEvent.class, ReplenishEvent.class,
		 StopEvent.class})
public class GroupeElectrogeneUserModel extends AtomicES_Model {

	public static class GroupeElectrogeneUserModelReport extends AbstractSimulationReport {
		private static final long serialVersionUID = 1L;

		public GroupeElectrogeneUserModelReport(String modelURI) {
			super(modelURI);
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "GroupeElectrogeneUserModelReport(" + this.getModelURI() + ")";
		}
	}

	private static final long serialVersionUID = 1L;
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	public static final String ACTION = "START/STOP/REFILL";
	/** URI to be used when creating the model. */
	public static final String URI = "GroupeElectrogeneUserModel";
	/**
	 * name of the run parameter defining the mean time between interruptions.
	 */
	public static final String MTBU = "mtbu";
	/**
	 * name of the run parameter defining the mean duration of interruptions.
	 */
	public static final String MTW = "mtw";
	
	public static final String MTR = "mtr";
	

	// Model simulation implementation variables
	/** initial delay before sending the first switch on event. */
	protected double initialDelay;

	/** delay between uses of the hair dryer from one day to another. */
	protected double interdayDelay;

	/** mean time between uses of the hair dryer in the same day. */
	protected double meanTimeBetweenUsages;

	/** during one use, mean time the hair dryer is at high temperature. */
	protected double meanTimeWorking;

	/** during one use, mean time the hair dryer is at low temperature. */
	protected double meanTimeAtRefill;

	/** next event to be sent. */
	protected Class<?> nextEvent;

	/** a random number generator from common math library. */
	protected final RandomDataGenerator rg;

	@ImportedVariable(type = Double.class)
	protected Value<Double> fuelCapacity;

	protected XYPlotter plotter;

	public GroupeElectrogeneUserModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine)
			throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		this.rg = new RandomDataGenerator();

		// create a standard logger (logging on the terminal)
		this.setLogger(new StandardLogger());
	}
	
	@Override
	public void initialiseState(Time initialTime) {
		this.initialDelay = GroupeElectrogeneUserBehaviour.INITIAL_DELAY;
		this.interdayDelay = GroupeElectrogeneUserBehaviour.INTER_DAY_DELAY;
//		this.meanTimeBetweenUsages = GroupeElectrogeneUserBehaviour.MEAN_TIME_BETWEEN_USAGES;
//		this.meanTimeWorking = GroupeElectrogeneUserBehaviour.MEAN_TIME_WORKING;
//		this.meanTimeAtRefill = GroupeElectrogeneUserBehaviour.MEAN_TIME_AT_REFILL;

		this.rg.reSeedSecure();

		super.initialiseState(initialTime);

		Duration d1 = new Duration(this.initialDelay, this.getSimulatedTimeUnit());
		Duration d2 = new Duration(2.0 * this.meanTimeBetweenUsages * this.rg.nextBeta(1.75, 1.75),this.getSimulatedTimeUnit());
		Time t = this.getCurrentStateTime().add(d1).add(d2);
		this.scheduleEvent(new StartEvent(t));
		


		this.nextTimeAdvance = this.timeAdvance();
		this.timeOfNextEvent = this.getCurrentStateTime().add(this.nextTimeAdvance);
		if (this.plotter != null) {
			this.plotter.initialise() ;
			this.plotter.showPlotter() ;
		}

		try {
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws Exception
	{
		String vname = this.getURI() + ":" + MTR ;
		this.meanTimeAtRefill = (double) simParams.get(vname) ;
		
		vname = this.getURI() + ":" + MTBU ;
		this.meanTimeBetweenUsages = (double) simParams.get(vname) ;
		
		vname = this.getURI() + ":" + MTW ;
		this.meanTimeWorking = (double) simParams.get(vname) ;
		
		vname = this.getURI() + ":" + GroupeElectrogeneUserModel.ACTION + ":"+ PlotterDescription.PLOTTING_PARAM_NAME ;
		PlotterDescription pdTemperature = (PlotterDescription) simParams.get(vname) ;
		this.plotter = new XYPlotter(pdTemperature) ;
		this.plotter.createSeries(GroupeElectrogeneUserModel.ACTION) ;
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model#timeAdvance()
	 */
	@Override
	public Duration timeAdvance() {
		Duration d = super.timeAdvance();
		this.logMessage("GroupeElectrogeneUserModel::timeAdvance() 1 " + d + " " + this.eventListAsString());
		return d;
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model#output()
	 */
	@Override
	public Vector<EventI> output() {
		
		assert !this.eventList.isEmpty();
		Vector<EventI> ret = super.output();
		assert ret.size() == 1;

		
		this.nextEvent = ret.get(0).getClass();

		this.logMessage("GroupeElectrogeneUserModel::output() " + this.nextEvent.getCanonicalName());
		return ret;
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void userDefinedInternalTransition(Duration elapsedTime) {

		Duration d;
		
		if (this.nextEvent.equals(StartEvent.class) ) {

			d = new Duration(2.0 * this.meanTimeBetweenUsages * this.rg.nextBeta(1.75, 1.75), this.getSimulatedTimeUnit());
			Time t = this.getCurrentStateTime().add(d);

			this.scheduleEvent(new StopEvent(t));
			if (this.plotter != null) {
				this.plotter.addData(
						ACTION,
						this.getCurrentStateTime().getSimulatedTime(),
						0.0) ;
				this.plotter.addData(
						ACTION,
						this.getCurrentStateTime().getSimulatedTime(),
						1.0) ;
			}

		} else if (this.nextEvent.equals(StopEvent.class)) {

			d = new Duration(2.0 * this.meanTimeAtRefill * this.rg.nextBeta(1.75, 1.75), this.getSimulatedTimeUnit());
			this.scheduleEvent(new ReplenishEvent(this.getCurrentStateTime().add(d)));
			if (this.plotter != null) {
				this.plotter.addData(
						ACTION,
						this.getCurrentStateTime().getSimulatedTime(),
						1.0) ;
				this.plotter.addData(
						ACTION,
						this.getCurrentStateTime().getSimulatedTime(),
						2.0) ;
			}
			
		} else if (this.nextEvent.equals(ReplenishEvent.class)) {

			d = new Duration(2.0 * this.meanTimeWorking * this.rg.nextBeta(1.75, 1.75), this.getSimulatedTimeUnit());
			this.scheduleEvent(new StartEvent(this.getCurrentStateTime().add(d)));
			if (this.plotter != null) {
				this.plotter.addData(
						ACTION,
						this.getCurrentStateTime().getSimulatedTime(),
						2.0) ;
				this.plotter.addData(
						ACTION,
						this.getCurrentStateTime().getSimulatedTime(),
						0.0) ;
			}
			
		}

	}
	
	@Override
	public SimulationReportI getFinalReport() throws Exception {
		return new GroupeElectrogeneUserModelReport(this.getURI());
	}

	

}
