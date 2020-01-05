package wattwatt.composants.appareils.suspensible.refrigerateur;

import java.util.HashMap;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentStateAccessI;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import fr.sorbonne_u.utils.PlotterDescription;
import simulation.deployment.WattWattMain;
import simulation.equipements.refrigerateur.components.RefrigerateurSimulatorPlugin;
import simulation.equipements.refrigerateur.models.RefrigerateurCoupledModel;
import simulation.equipements.refrigerateur.models.RefrigerateurModel;
import simulation.equipements.refrigerateur.models.RefrigerateurSensorModel;
import simulation.equipements.refrigerateur.models.RefrigerateurUserModel;
import simulation.equipements.refrigerateur.tools.RefrigerateurConsommation;
import simulation.equipements.refrigerateur.tools.RefrigerateurPorte;
import simulation.equipements.sechecheveux.models.SecheCheveuxModel;
import wattwatt.interfaces.appareils.suspensible.refrigerateur.IRefrigerateur;
import wattwatt.interfaces.controleur.IControleur;
import wattwatt.ports.appareils.suspensible.refrigerateur.RefrigerateurInPort;
import wattwatt.tools.refrigerateur.RefrigerateurReglage;

@OfferedInterfaces(offered = IRefrigerateur.class)
@RequiredInterfaces(required = IControleur.class)
public class Refrigerateur extends AbstractCyPhyComponent implements EmbeddingComponentStateAccessI {

	protected RefrigerateurConsommation consumptionState;
	protected RefrigerateurPorte currentDoorState;
	protected double intensity;
	protected double temperature;

	protected double tempH;
	protected double tempB;

	protected boolean isOn;
	protected boolean isWorking;
	protected int conso;

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	protected RefrigerateurInPort refrin;
	protected RefrigerateurSimulatorPlugin asp;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	protected Refrigerateur(String uri, String refriIn) throws Exception {
		super(uri, 2, 1);
		this.initialise();
		this.refrin = new RefrigerateurInPort(refriIn, this);
		this.refrin.publishPort();

		this.tempH = RefrigerateurReglage.TEMP_H_INIT;
		this.tempB = RefrigerateurReglage.TEMP_B_INIT;

		this.consumptionState = RefrigerateurConsommation.SUSPENDED;
		this.currentDoorState = RefrigerateurPorte.CLOSED;
		this.intensity = 0.0;
		this.temperature = (double) this.asp.getModelStateValue(RefrigerateurModel.URI, "temperature");

		this.tracer.setRelativePosition(1, 0);
	}

	protected void initialise() throws Exception {
		// The coupled model has been made able to create the simulation
		// architecture description.
		Architecture localArchitecture = this.createLocalArchitecture(null);
		// Create the appropriate DEVS simulation plug-in.
		this.asp = new RefrigerateurSimulatorPlugin();
		// Set the URI of the plug-in, using the URI of its associated
		// simulation model.
		this.asp.setPluginURI(localArchitecture.getRootModelURI());
		// Set the simulation architecture.
		this.asp.setSimulationArchitecture(localArchitecture);
		// Install the plug-in on the component, starting its own life-cycle.
		this.installPlugin(this.asp);

		// Toggle logging on to get a log on the screen.
		this.toggleLogging();
	}

	public double getTempHaut() {
		return this.tempH;
	}

	public double getTempBas() {
		return this.tempB;
	}

	public void suspend() {
		this.isWorking = false;
	}

	public void resume() {
		if (this.isOn) {
			this.isWorking = true;
		} else {
			this.isWorking = false;
		}

	}

	public void on() {
		this.isOn = true;
		this.isWorking = true;
	}

	public void off() {
		this.isOn = false;
		this.isWorking = false;
	}

	public boolean isWorking() {
		return this.isWorking;
	}

	public boolean isOn() {
		return this.isOn;
	}

	public int giveConso() {
		return conso;
	}

	public void setDoorState(RefrigerateurPorte door) {
		this.currentDoorState = door;
	}

	public void setConsumptionState(RefrigerateurConsommation consumption) {
		this.consumptionState = consumption;
	}

	public void setIntensity(double intensity) {
		this.intensity = intensity;
	}

	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

	public void regule() {
		if (this.isOn) {
			if (this.isWorking) {
				if (this.tempH > RefrigerateurReglage.TEMP_H_MIN) {
					this.tempH--;
				}
				if (this.tempB > RefrigerateurReglage.TEMP_B_MIN) {
					this.tempB--;
				}
				this.conso += RefrigerateurReglage.CONSOMMATION_ACTIVE;
			} else {
				if (this.tempH < RefrigerateurReglage.TEMP_H_MAX) {
					this.tempH++;
				}
				if (this.tempB < RefrigerateurReglage.TEMP_B_MAX) {
					this.tempB++;
				}
				if (this.conso - RefrigerateurReglage.CONSOMMATION_PASSIVE <= 0) {
					this.conso = 0;
				} else {
					this.conso -= RefrigerateurReglage.CONSOMMATION_PASSIVE;
				}

			}
		} else {
			if (this.conso - RefrigerateurReglage.CONSOMMATION_PASSIVE <= 0) {
				this.conso = 0;
			} else {
				this.conso -= RefrigerateurReglage.CONSOMMATION_PASSIVE;
			}
		}
	}

	public void printState() {
		this.logMessage(">>> isOn : [" + this.isOn + "] Working : [" + this.isWorking + "] Temp Haut : ["
				+ this.getTempHaut() + " ] Temp Bas : [" + this.getTempBas() + " ] \n>>> Conso depuis le debut : ["
				+ this.giveConso() + " ]\n");
	}

	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.logMessage("Refrigerateur starting");
		try {
			Thread.sleep(10);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void execute() throws Exception {
		super.execute();
		SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 10L;
		// To give an example of the embedding component access facility, the
		// following lines show how to set the reference to the embedding
		// component or a proxy responding to the access calls.
		HashMap<String, Object> simParams = new HashMap<String, Object>();
		simParams.put("componentRef", this);
		simParams.put(RefrigerateurUserModel.URI + ":" + RefrigerateurUserModel.MTBI, 200.0) ;
		simParams.put(RefrigerateurUserModel.URI + ":" + RefrigerateurUserModel.MID, 10.0) ;
		simParams.put(
				RefrigerateurUserModel.URI + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription(
						"RefrigerateurUserModel",
						"Time (sec)",
						"Opened/Closed",
						WattWattMain.ORIGIN_X,
						WattWattMain.ORIGIN_Y,
						WattWattMain.getPlotterWidth(),
						WattWattMain.getPlotterHeight())) ;
		
		simParams.put(
				RefrigerateurModel.URI + ":" + RefrigerateurModel.MAX_TEMPERATURE, 10.0) ;
		simParams.put(
				RefrigerateurModel.URI + ":" + RefrigerateurModel.MIN_TEMPERATURE, 1.0) ;
		simParams.put(RefrigerateurModel.URI + ":" + RefrigerateurModel.BAAR, 1.75) ;
		simParams.put(RefrigerateurModel.URI + ":" + RefrigerateurModel.BBAR, 1.75) ;
		simParams.put(RefrigerateurModel.URI + ":" + RefrigerateurModel.BMASSF, 1.0/11.0) ;
		simParams.put(RefrigerateurModel.URI + ":" + RefrigerateurModel.BIS, 0.5) ;
		simParams.put(
				RefrigerateurModel.URI + ":" + RefrigerateurModel.TEMPERATURE + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription(
						"RefrigerateurModel",
						"Time (sec)",
						"Temperature (°C)",
						WattWattMain.ORIGIN_X,
						WattWattMain.ORIGIN_Y +
						WattWattMain.getPlotterHeight(),
						WattWattMain.getPlotterWidth(),
						WattWattMain.getPlotterHeight())) ;
		simParams.put(
				RefrigerateurModel.URI + ":"  + RefrigerateurModel.INTENSITY + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription(
						"RefrigerateurModel",
						"Time (sec)",
						"Intensity (Watt)",
						WattWattMain.ORIGIN_X + WattWattMain.getPlotterWidth(),
						WattWattMain.ORIGIN_Y +
						WattWattMain.getPlotterHeight(),
						WattWattMain.getPlotterWidth(),
						WattWattMain.getPlotterHeight())) ;

		simParams.put(
				RefrigerateurSensorModel.URI + ":" + RefrigerateurModel.MAX_TEMPERATURE, 10.0) ;
		simParams.put(
				RefrigerateurSensorModel.URI + ":" + RefrigerateurModel.MIN_TEMPERATURE, 1.0) ;
		simParams.put(
				RefrigerateurSensorModel.URI + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription(
						"RefrigerateurSensorModel",
						"Time (sec)",
						"Temperature (°C)",
						WattWattMain.ORIGIN_X,
						WattWattMain.ORIGIN_Y +
						2*WattWattMain.getPlotterHeight(),
						WattWattMain.getPlotterWidth(),
						WattWattMain.getPlotterHeight())) ;
		this.asp.setSimulationRunParameters(simParams);
		// Start the simulation.
		this.runTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try {
					asp.doStandAloneSimulation(0.0, 1000.0);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
		this.runTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try {
					while (true) {
						((Refrigerateur) this.getTaskOwner()).setDoorState(
								(((RefrigerateurPorte) asp.getModelStateValue(SecheCheveuxModel.URI, "door"))));
						((Refrigerateur) this.getTaskOwner()).setConsumptionState((((RefrigerateurConsommation) asp
								.getModelStateValue(SecheCheveuxModel.URI, "consumption"))));
						((Refrigerateur) this.getTaskOwner()).setTemperature(
								((double) asp.getModelStateValue(SecheCheveuxModel.URI, "temperature")));
						((Refrigerateur) this.getTaskOwner())
								.setIntensity(((double) asp.getModelStateValue(SecheCheveuxModel.URI, "intensity")));
						Thread.sleep(1000);
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
	}

	@Override
	public void shutdown() throws ComponentShutdownException {
		this.logMessage("Refrigerateur shutdown");
		try {
			this.refrin.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.shutdown();
	}

	@Override
	public void finalise() throws Exception {
		super.finalise();
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent#createLocalArchitecture(java.lang.String)
	 */
	@Override
	protected Architecture createLocalArchitecture(String architectureURI) throws Exception {
		return RefrigerateurCoupledModel.build();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentStateAccessI#getEmbeddingComponentStateValue(java.lang.String)
	 */
	@Override
	public Object getEmbeddingComponentStateValue(String name) throws Exception {
		if (name.equals("door")) {
			return this.asp.getModelStateValue(SecheCheveuxModel.URI, "door");
		} else if (name.equals("consumption")) {
			return this.asp.getModelStateValue(SecheCheveuxModel.URI, "consumption");
		} else if (name.equals("temperature")) {
			return this.asp.getModelStateValue(SecheCheveuxModel.URI, "temperature");
		} else {
			assert name.equals("intensity");
			return this.asp.getModelStateValue(SecheCheveuxModel.URI, "intensity");
		}
	}
}
