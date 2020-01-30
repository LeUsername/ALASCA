package simulation.models.hairdryer;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;
import fr.sorbonne_u.devs_simulation.examples.molene.tic.TicEvent;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOAwithEquations;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.AbstractSimulationReport;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;
import simulation.events.hairdryer.AbstractHairDryerEvent;
import simulation.events.hairdryer.DecreasePowerEvent;
import simulation.events.hairdryer.HairDryerConsumptionEvent;
import simulation.events.hairdryer.IncreasePowerEvent;
import simulation.events.hairdryer.SwitchModeEvent;
import simulation.events.hairdryer.SwitchOffEvent;
import simulation.events.hairdryer.SwitchOnEvent;
import simulation.tools.hairdryer.HairDryerPowerLevel;
import simulation.tools.hairdryer.HairDryerState;
import wattwatt.tools.URIS;
import wattwatt.tools.hairdryer.HairDryerMode;
import wattwatt.tools.hairdryer.HairDryerSetting;

@ModelExternalEvents(imported = { SwitchOnEvent.class, 
								  SwitchOffEvent.class, 
								  SwitchModeEvent.class,
								  IncreasePowerEvent.class, 
								  DecreasePowerEvent.class,
								  TicEvent.class}, 
					 exported = { HairDryerConsumptionEvent.class })
public class HairDryerModel extends AtomicHIOAwithEquations {

	// -------------------------------------------------------------------------
	// Inner classes and types
	// -------------------------------------------------------------------------

	public static class HairDryerModelReport extends AbstractSimulationReport {
		private static final long serialVersionUID = 1L;

		public HairDryerModelReport(String modelURI) {
			super(modelURI);
			
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "HairDryerModelReport(" + this.getModelURI() + ")";
		}
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	/**
	 * URI used to create instances of the model; assumes a singleton, otherwise a
	 * different URI must be given to each instance.
	 */
	public static final String URI = URIS.HAIR_DRYER_MODEL_URI;

	private static final String SERIES = "intensity";
	public static final String CONSUMPTION_SERIES = "intensity-series";

	/** nominal tension (in Volts) of the hair dryer. */
	protected static final double TENSION = 220.0; // Volts

	/** true when a external event triggered a reading. */
	protected boolean triggerReading;
	
	/** plotter for the intensity level over time. */
	protected XYPlotter consumptionPlotter;

	/**
	 * reference on the object representing the component that holds the model;
	 * enables the model to access the state of this component.
	 */
	protected EmbeddingComponentAccessI componentRef;
	
	/** current intensity in Amperes; intensity is power/tension. */
	//@ExportedVariable(type = Double.class)
	protected double currentConsumption;
	
	/** Mode dans lequel est le seche cheveux (HOT_AIR, COLD_AIR) */
	protected HairDryerMode mode;
	
	/** Etat dans lequel se trouve le seche cheveux */
	protected HairDryerState state;

	protected HairDryerPowerLevel powerLvl;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public HairDryerModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void setSimulationRunParameters(Map<String, Object> simParams) throws Exception {
		
		// Initialise the look of the plotter
		String vname = this.getURI() + ":" + HairDryerModel.CONSUMPTION_SERIES + ":" + PlotterDescription.PLOTTING_PARAM_NAME ;
		PlotterDescription pd = (PlotterDescription) simParams.get(vname) ;
		this.consumptionPlotter = new XYPlotter(pd) ;
		this.consumptionPlotter.createSeries(SERIES) ;
		
		// The reference to the embedding component
		this.componentRef =
			(EmbeddingComponentAccessI) simParams.get(URIS.HAIR_DRYER_URI) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void initialiseState(Time initialTime) {
		if(componentRef == null) {
			this.mode = HairDryerMode.COLD_AIR;
			this.powerLvl = HairDryerPowerLevel.MEDIUM;
			this.state = HairDryerState.OFF;
		} else {
			try {
				this.mode = (HairDryerMode)this.componentRef.getEmbeddingComponentStateValue("mode");
				this.powerLvl = (HairDryerPowerLevel)this.componentRef.getEmbeddingComponentStateValue("powerLevel");
				this.state = (HairDryerState)this.componentRef.getEmbeddingComponentStateValue("isOn");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		this.triggerReading = false;

		// initialisation of the intensity plotter on the screen
		if(this.consumptionPlotter != null) {
			this.consumptionPlotter.initialise();
			this.consumptionPlotter.showPlotter();
		}

		super.initialiseState(initialTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseVariables(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	protected void initialiseVariables(Time startTime) {
		// as the hair dryer starts in mode OFF, its power consumption should be 0
		
		
		
		if(componentRef == null) {
			this.currentConsumption = 0.0;
		} else {
			try {
				this.currentConsumption =  (Double)this.componentRef.getEmbeddingComponentStateValue("consumption");;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// first data in the plotter to start the plot.
		this.consumptionPlotter.addData(SERIES, this.getCurrentStateTime().getSimulatedTime(), this.getIntensity());

		super.initialiseVariables(startTime);
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration timeAdvance() {
		
		if (!this.triggerReading) {
			return Duration.INFINITY;
		} else {
			return Duration.zero(this.getSimulatedTimeUnit());
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public ArrayList<EventI> output() {
		if (this.triggerReading) {
			double reading = this.currentConsumption; // Watt

			ArrayList<EventI> ret = new ArrayList<EventI>(1);
			Time currentTime = this.getCurrentStateTime().add(this.getNextTimeAdvance());
			HairDryerConsumptionEvent consommation = new HairDryerConsumptionEvent(currentTime, reading);
			ret.add(consommation);

			this.triggerReading = false;
			return ret;
		} else {
			return null;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void userDefinedInternalTransition(Duration elapsedTime) {

		if(this.componentRef != null) {
			this.consumptionPlotter.addData(
					SERIES,
					this.getCurrentStateTime().getSimulatedTime(),
					this.currentConsumption);
			updateIntensity();
			this.consumptionPlotter.addData(
					SERIES,
					this.getCurrentStateTime().getSimulatedTime(),
					this.currentConsumption);
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void userDefinedExternalTransition(Duration elapsedTime) {
		super.userDefinedExternalTransition(elapsedTime);
		if(this.componentRef == null) {

			ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
			assert currentEvents != null;
	
			Event ce = (Event) currentEvents.get(0);
			
			this.consumptionPlotter.addData(SERIES, this.getCurrentStateTime().getSimulatedTime(), this.currentConsumption);
	
			if (ce instanceof TicEvent) {
				this.triggerReading = true;
			} else {
				assert ce instanceof AbstractHairDryerEvent;
				ce.executeOn(this);
			}
			this.consumptionPlotter.addData(SERIES, this.getCurrentStateTime().getSimulatedTime(), this.currentConsumption);
	
		} else {
			ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
			assert currentEvents != null;
	
			Event ce = (Event) currentEvents.get(0);
			
			assert ce instanceof TicEvent;
			this.triggerReading = true;
			
			try {
				this.state = (HairDryerState) this.componentRef.getEmbeddingComponentStateValue("isOn");
				this.mode = (HairDryerMode)this.componentRef.getEmbeddingComponentStateValue("mode");
				this.powerLvl = (HairDryerPowerLevel)this.componentRef.getEmbeddingComponentStateValue("powerLevel");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void endSimulation(Time endTime) throws Exception {
		this.consumptionPlotter.addData(SERIES, endTime.getSimulatedTime(), this.currentConsumption);
		super.endSimulation(endTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#getFinalReport()
	 */
	@Override
	public SimulationReportI getFinalReport() throws Exception {
		return new HairDryerModelReport(this.getURI());
	}

	// ------------------------------------------------------------------------
	// Model-specific methods
	// ------------------------------------------------------------------------

	public void switchOn() {
		this.state = HairDryerState.ON;
		updateIntensity();
	}

	public void switchOff() {
		this.state = HairDryerState.OFF;
		this.currentConsumption = 0.0;
	}

	public void switchMode() {
		if (this.mode == HairDryerMode.COLD_AIR) {
			this.mode = HairDryerMode.HOT_AIR;
		} else {
			this.mode =HairDryerMode.COLD_AIR;
		}
		updateIntensity();
	}

	public HairDryerMode getMode() {
		return this.mode;
	}

	public double getIntensity() {
		return this.currentConsumption;
	}


	public void increasePower() {
		if (this.powerLvl == HairDryerPowerLevel.LOW) {
			this.powerLvl = HairDryerPowerLevel.MEDIUM;
		} else {
			if (this.powerLvl == HairDryerPowerLevel.MEDIUM) {
				this.powerLvl = HairDryerPowerLevel.HIGH;
			}
		}
		updateIntensity();
	}
	
	public boolean isOn() {
		return this.state == HairDryerState.ON;
	}

	public void decreasePower() {
		if (this.powerLvl == HairDryerPowerLevel.HIGH) {
			this.powerLvl = HairDryerPowerLevel.MEDIUM;
		} else {
			if (this.powerLvl == HairDryerPowerLevel.MEDIUM) {
				this.powerLvl = HairDryerPowerLevel.LOW;
			}
		}
		updateIntensity();
	}

	public void updateIntensity() {
		if(this.state == HairDryerState.ON) {
			switch (this.getMode()) {
			case HOT_AIR:
				this.currentConsumption = HairDryerSetting.CONSO_HOT_MODE * this.powerLvl.getValue() / TENSION;
				break;
			case COLD_AIR:
				this.currentConsumption = HairDryerSetting.CONSO_COLD_MODE * this.powerLvl.getValue() / TENSION;
				break;
	
			}
		} else {
			this.currentConsumption = 0.0;
		}
	}

	public HairDryerPowerLevel getPowerLevel() {
		return this.powerLvl;
	}
}
