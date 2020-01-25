package simulation.models.washingmachine;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.random.RandomDataGenerator;

import fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model;
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
import simulation.events.washingmachine.EcoModeEvent;
import simulation.events.washingmachine.PremiumModeEvent;
import simulation.events.washingmachine.StartAtEvent;
import simulation.tools.washingmachine.WashingMachineUserBehaviour;

@ModelExternalEvents(exported = { StartAtEvent.class, 
								  EcoModeEvent.class, 
								  PremiumModeEvent.class})
public class WashingMachineUserModel extends AtomicES_Model {

	public static class WashingMachineUserModelReport extends AbstractSimulationReport {
		private static final long serialVersionUID = 1L;

		public WashingMachineUserModelReport(String modelURI) {
			super(modelURI);
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "LaveLingeUserModelReport(" + this.getModelURI() + ")";
		}
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	
	public static final String ACTION = "START_AT/ECO/PREMIUM";
	/** URI to be used when creating the model. */
	public static final String URI = "WashingMachineUserModel";
	/**
	 * name of the run parameter defining the mean time between interruptions.
	 */
	public static final String MTBU = "mtbu";
	/**
	 * name of the run parameter defining the mean duration of interruptions.
	 */
	public static final String MTWE = "mtwe";
	
	public static final String MTWP = "mtwp";
	
	public static final String STD = "std";
	

	// Model simulation implementation variables
	protected double initialDelay;

	protected double interdayDelay;

	protected double meanTimeBetweenUsages;

	protected double meanTimeWorkingEco;

	protected double meanTimeWorkingPremium;
	
	protected double startingTimeDelay;

	/** next event to be sent. */
	protected Class<?> nextEvent;

	/** a random number generator from common math library. */
	protected final RandomDataGenerator rg;

	protected XYPlotter plotter;
	
	

	public WashingMachineUserModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		this.rg = new RandomDataGenerator();
		this.setLogger(new StandardLogger());
	}
	
	@Override
	public void initialiseState(Time initialTime) {
		this.initialDelay = WashingMachineUserBehaviour.INITIAL_DELAY;
		this.interdayDelay = WashingMachineUserBehaviour.INTER_DAY_DELAY;

		this.rg.reSeedSecure();

		super.initialiseState(initialTime);

		Duration d1 = new Duration(this.initialDelay, this.getSimulatedTimeUnit());
		Duration d2 = new Duration(2.0 * this.meanTimeBetweenUsages * this.rg.nextBeta(1.75, 1.75),this.getSimulatedTimeUnit());
		Time t = this.getCurrentStateTime().add(d1).add(d2);
		this.scheduleEvent(new StartAtEvent(t,this.startingTimeDelay));
		


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
		String vname = this.getURI() + ":" + MTWE ;
		this.meanTimeWorkingEco = (double) simParams.get(vname) ;
		
		vname = this.getURI() + ":" + MTBU ;
		this.meanTimeBetweenUsages = (double) simParams.get(vname) ;
		
		vname = this.getURI() + ":" + MTWP ;
		this.meanTimeWorkingPremium = (double) simParams.get(vname) ;
		
		vname = this.getURI() + ":" + STD ;
		this.startingTimeDelay = (double) simParams.get(vname) ;
		
		vname = this.getURI() + ":" + WashingMachineUserModel.ACTION + ":"+ PlotterDescription.PLOTTING_PARAM_NAME ;
		PlotterDescription pdTemperature = (PlotterDescription) simParams.get(vname) ;
		this.plotter = new XYPlotter(pdTemperature) ;
		this.plotter.createSeries(WashingMachineUserModel.ACTION) ;
	}
	
	@Override
	public Duration timeAdvance() {
		Duration d = super.timeAdvance();
		this.logMessage("LaveLingeUserModel::timeAdvance() 1 " + d + " " + this.eventListAsString());
		return d;
	}

	@Override
	public ArrayList<EventI> output() {
		assert !this.eventList.isEmpty();
		ArrayList<EventI> ret = super.output();
		assert ret.size() == 1;

		
		this.nextEvent = ret.get(0).getClass();

		this.logMessage("LaveLingeUserModel::output() " + this.nextEvent.getCanonicalName());
		return ret;
	}
	
	@Override
	public void userDefinedInternalTransition(Duration elapsedTime) {

		Duration d;
		
		if (this.nextEvent.equals(StartAtEvent.class) ) {

			

			Random r = new Random();
			if(r.nextFloat() > 0.75) {
				d = new Duration(2.0 * this.meanTimeWorkingPremium + this.startingTimeDelay * this.rg.nextBeta(1.75, 1.75), this.getSimulatedTimeUnit());
				Time t = this.getCurrentStateTime().add(d);
				this.scheduleEvent(new PremiumModeEvent(t));
				if (this.plotter != null) {
					this.plotter.addData(
							ACTION,
							this.getCurrentStateTime().getSimulatedTime(),
							0.0) ;
					this.plotter.addData(
							ACTION,
							this.getCurrentStateTime().getSimulatedTime(),
							2.0) ;
				}
			}
			else {
				d = new Duration(2.0 * this.meanTimeWorkingEco  + this.startingTimeDelay * this.rg.nextBeta(1.75, 1.75), this.getSimulatedTimeUnit());
				Time t = this.getCurrentStateTime().add(d);
				this.scheduleEvent(new EcoModeEvent(t));
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
			}
			

		} else if (this.nextEvent.equals(PremiumModeEvent.class)) {
			d = new Duration(2.0 * this.meanTimeBetweenUsages * this.rg.nextBeta(1.75, 1.75), this.getSimulatedTimeUnit());
			Time t = this.getCurrentStateTime().add(d);
			
			this.scheduleEvent(new StartAtEvent(t,this.startingTimeDelay));
			
			Random r = new Random();
			this.startingTimeDelay = this.meanTimeBetweenUsages + r.nextInt(2800) - 1400;
			
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
			
		} else if (this.nextEvent.equals(EcoModeEvent.class)) {
			d = new Duration(2.0 * this.meanTimeBetweenUsages * this.rg.nextBeta(1.75, 1.75), this.getSimulatedTimeUnit());
			
			this.scheduleEvent(new StartAtEvent(this.getCurrentStateTime().add(d),this.startingTimeDelay));
			
			Random r = new Random();
			this.startingTimeDelay = this.meanTimeBetweenUsages + r.nextInt(1000) - 500;
			
			if (this.plotter != null) {
				this.plotter.addData(
						ACTION,
						this.getCurrentStateTime().getSimulatedTime(),
						1.0) ;
				this.plotter.addData(
						ACTION,
						this.getCurrentStateTime().getSimulatedTime(),
						0.0) ;
			}
			
		}

	}
	
	@Override
	public void endSimulation(Time endTime) throws Exception {
		this.plotter.addData(ACTION, endTime.getSimulatedTime(), 0.0);
		super.endSimulation(endTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#getFinalReport()
	 */
	@Override
	public SimulationReportI getFinalReport() throws Exception {
		return new WashingMachineUserModelReport(this.getURI());
	}

}
