package wattwatt.composants.appareils.planifiable.lavelinge;

import java.util.HashMap;
import java.util.Random;

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
import simulation2.deployment.WattWattMain;
import simulation2.models.lavelinge.LaveLingeCoupledModel;
import simulation2.models.lavelinge.LaveLingeModel;
import simulation2.models.lavelinge.LaveLingeUserModel;
import simulation2.plugins.LaveLingeSimulatorPlugin;
import simulation2.tools.Duree;
import simulation2.tools.lavelinge.LaveLingeLavage;
import simulation2.tools.lavelinge.LaveLingeUserBehaviour;
import wattwatt.interfaces.appareils.planifiable.lavelinge.ILaveLinge;
import wattwatt.interfaces.controleur.IControleur;
import wattwatt.ports.appareils.planifiable.lavelinge.LaveLingeInPort;
import wattwatt.tools.lavelinge.LaveLingeMode;
import wattwatt.tools.lavelinge.LaveLingeReglage;

@OfferedInterfaces(offered = ILaveLinge.class)
@RequiredInterfaces(required = IControleur.class)
public class LaveLinge extends AbstractCyPhyComponent implements EmbeddingComponentStateAccessI {

	protected LaveLingeInPort lavein;

	protected LaveLingeMode mode;
	protected int startingTime;
	protected int durationWork;

	protected boolean isOn;
	protected boolean isWorking;
	protected int conso;

	protected boolean isOnSim;
	protected LaveLingeLavage state;
	protected boolean isWorkingSim;
	protected double consoSim;	
	
	protected LaveLingeSimulatorPlugin asp;
	
	protected LaveLinge(String uri, String laveIn) throws Exception {
		super(uri, 2, 1);
		this.initialise();
		this.lavein = new LaveLingeInPort(laveIn, this);
		this.lavein.publishPort();

		this.isOnSim = (boolean)this.asp.getModelStateValue(LaveLingeModel.URI, "isOn");
		this.state = (LaveLingeLavage)this.asp.getModelStateValue(LaveLingeModel.URI, "lavageMode");
		this.isWorkingSim = (boolean)this.asp.getModelStateValue(LaveLingeModel.URI, "isWorking");
		this.consoSim = (double)this.asp.getModelStateValue(LaveLingeModel.URI, "consommation");
		
		ecoLavage();
		this.startingTime = LaveLingeReglage.START;
		this.tracer.setRelativePosition(1, 2);

	}
	
	protected void initialise() throws Exception {
		// The coupled model has been made able to create the simulation
		// architecture description.
		Architecture localArchitecture = this.createLocalArchitecture(null);
		// Create the appropriate DEVS simulation plug-in.
		this.asp = new LaveLingeSimulatorPlugin();
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

	public boolean canDelay(int delay) {
		return startingTime + delay < LaveLingeReglage.END;
	}

	public boolean canAdvance(int advance) {
		return startingTime - advance >= LaveLingeReglage.START;
	}

	public int durationWork() {
		return durationWork;
	}

	public int startingTime() {
		return startingTime;
	}

	public int endingTime() {
		return startingTime + durationWork;
	}

	public void endBefore(int end) {
		assert end < LaveLingeReglage.END;
		startingTime = end;
	}

	public void startAt(int debut) {
		assert debut >= LaveLingeReglage.START;
		startingTime = debut;
	}

	public void late(int delay) {
		if (canDelay(delay)) {
			startingTime += delay;
		}
	}

	public void advance(int advance) {
		if (canAdvance(advance)) {
			startingTime -= advance;
		}
	}

	public void On() {
		this.isOn = true;
	}

	public void Off() {
		this.isOn = false;
	}

	public boolean isOn() {
		return this.isOn;
	}

	public boolean isWorking() {
		return this.isWorking;
	}

	public int giveConso() {
		if (isOn()) {
			return conso;
		} else {
			return 0;
		}
	}

	public void ecoLavage() {
		mode = LaveLingeMode.ECO;
		conso = LaveLingeReglage.CONSO_ECO_MODE;
		durationWork = LaveLingeReglage.DURATION_ECO_MODE;
	}

	public void premiumLavage() {
		mode = LaveLingeMode.PREMIUM;
		conso = LaveLingeReglage.CONSO_PREMIUM_MODE;
		durationWork = LaveLingeReglage.CONSO_PREMIUM_MODE;
	}

	public LaveLingeMode getMode() {
		return mode;
	}

	public void behave(Random rand) throws InterruptedException {
		if (this.isOn) {
			this.isWorking = true;
			if (getMode() == LaveLingeMode.ECO) {
				this.logMessage(
						"Washing machine starting eco mode at: " + this.startingTime + " for " + this.durationWork);
				Thread.sleep(this.durationWork / 2);
				this.logMessage("Still working for " + this.durationWork / 2);
				Thread.sleep(this.durationWork / 2);
			} else {
				this.logMessage(
						"Washing machine starting premium mode at: " + this.startingTime + " for " + this.durationWork);
				Thread.sleep(this.durationWork / 2);
				this.logMessage("Still working for " + this.durationWork / 2);
				Thread.sleep(this.durationWork / 2);
			}
			this.isWorking = false;
		}
	}

	public void printState() {
		this.logMessage(">>> isOn : [" + this.isOn + "] Mode : [" + this.getMode()
				+ " ] \n>>> Conso depuis le debut : [" + this.giveConso() + " ]\n");
	}

	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.logMessage("LaveLinge starting");
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
		simParams.put(LaveLingeUserModel.URI + ":" + LaveLingeUserModel.MTBU,
				LaveLingeUserBehaviour.MEAN_TIME_BETWEEN_USAGES);
		simParams.put(LaveLingeUserModel.URI + ":" + LaveLingeUserModel.MTWE,
				LaveLingeUserBehaviour.MEAN_TIME_WORKING_ECO);
		simParams.put(LaveLingeUserModel.URI + ":" + LaveLingeUserModel.MTWP,
				LaveLingeUserBehaviour.MEAN_TIME_WORKING_PREMIUM);
		simParams.put(LaveLingeUserModel.URI + ":" + LaveLingeUserModel.STD,
				10.0);
		
		simParams.put(LaveLingeModel.URI + ":" + LaveLingeModel.CONSUMPTION_ECO,
				LaveLingeReglage.CONSO_ECO_MODE_SIM);
		simParams.put(LaveLingeModel.URI + ":" + LaveLingeModel.CONSUMPTION_PREMIUM,
				LaveLingeReglage.CONSO_PREMIUM_MODE_SIM);
		simParams.put(LaveLingeModel.URI + ":" + LaveLingeUserModel.STD,
				10.0);
		
		simParams.put(
				LaveLingeUserModel.URI + ":" + LaveLingeUserModel.ACTION + ":"
						+ PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription("LaveLingeUserModel", "Time (min)", "User actions",
						0,
						WattWattMain.getPlotterHeight(),
						WattWattMain.getPlotterWidth(),
						WattWattMain.getPlotterHeight()));

		simParams.put(
				LaveLingeModel.URI + ":" + LaveLingeModel.INTENSITY_SERIES + ":"
						+ PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription("LaveLingeModel", "Time (min)", "Consommation (W)",
						0,
						2*WattWattMain.getPlotterHeight(),
						WattWattMain.getPlotterWidth(),
						WattWattMain.getPlotterHeight()));
		
		this.asp.setSimulationRunParameters(simParams);
		// Start the simulation.
		this.runTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try {
					asp.doStandAloneSimulation(0.0, Duree.DUREE_SEMAINE);
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
						((LaveLinge) this.getTaskOwner()).isOnSim = 
								(((boolean) asp.getModelStateValue(LaveLingeModel.URI, "isOn")));
						((LaveLinge) this.getTaskOwner()).isWorkingSim = 
								(((boolean) asp.getModelStateValue(LaveLingeModel.URI, "isWorking")));
						((LaveLinge) this.getTaskOwner()).consoSim = 
								(((double) asp.getModelStateValue(LaveLingeModel.URI, "consomation")));
						((LaveLinge) this.getTaskOwner()).state = 
								(((LaveLingeLavage) asp.getModelStateValue(LaveLingeModel.URI, "lavageMode")));
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
		this.logMessage("LaveLinge shutdown");
		try {
			this.lavein.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.shutdown();
	}

	@Override
	public void finalise() throws Exception {
		super.finalise();
	}

	@Override
	public Object getEmbeddingComponentStateValue(String name) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Architecture createLocalArchitecture(String architectureURI) throws Exception {
		return LaveLingeCoupledModel.build();
	}

}
