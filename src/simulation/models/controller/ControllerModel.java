package simulation.models.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.AbstractSimulationReport;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;
import simulation.events.controller.ResumeFridgeEvent;
import simulation.events.controller.StartEngineGeneratorEvent;
import simulation.events.controller.StartWashingMachineEvent;
import simulation.events.controller.StopEngineGeneratorEvent;
import simulation.events.controller.StopWashingMachineEvent;
import simulation.events.controller.SuspendFridgeEvent;
import simulation.events.electricmeter.ConsumptionEvent;
import simulation.events.enginegenerator.EngineGeneratorProductionEvent;
import simulation.events.windturbine.WindTurbineProductionEvent;
import simulation.tools.controller.Decision;
import simulation.tools.enginegenerator.EngineGeneratorState;
import simulation.tools.fridge.FridgeConsumption;
import simulation.tools.washingmachine.WashingMachineState;
import wattwatt.tools.URIS;

@ModelExternalEvents(imported = { ConsumptionEvent.class, EngineGeneratorProductionEvent.class,
		WindTurbineProductionEvent.class }, exported = { StartEngineGeneratorEvent.class,
				StopEngineGeneratorEvent.class, SuspendFridgeEvent.class, ResumeFridgeEvent.class,
				StartWashingMachineEvent.class, StopWashingMachineEvent.class })
public class ControllerModel extends AtomicModel {
	// -------------------------------------------------------------------------
	// Inner classes
	// -------------------------------------------------------------------------

	public static class DecisionPiece {
		public final double first;
		public final double last;
		public final Decision d;

		public DecisionPiece(double first, double last, Decision d) {
			super();
			this.first = first;
			this.last = last;
			this.d = d;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "(" + this.first + ", " + this.last + ", " + this.d + ")";
		}
	}

	public static class ControllerModelReport extends AbstractSimulationReport {
		private static final long serialVersionUID = 1L;

		public ControllerModelReport(String modelURI) {
			super(modelURI);
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "ControllerModel(" + this.getModelURI() + ")";
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * URI used to create instances of the model; assumes a singleton, otherwise a
	 * different URI must be given to each instance.
	 */
	public static final String URI = URIS.CONTROLLER_MODEL_URI;

	private static final String PRODUCTION = "production";
	public static final String PRODUCTION_SERIES = "production-series";

	private static final String ENGINE_GENERATOR = "engine-generator";
	public static final String ENGINE_GENERATOR_SERIES = "engine-generator-series";

	private static final String FRIDGE = "frigde";
	public static final String FRIDGE_SERIES = "fridge-series";
	
	private static final String WASHING_MACHINE = "washing-machine";
	public static final String WASHING_MACHINE_SERIES = "washing-machine-series";

	private static final String CONTROLLER_STUB = "controller-stub";
	public static final String CONTROLLER_STUB_SERIES = "controller-stub-series";

	protected double consumption;
	protected boolean mustTransmitDecision;
	protected double productionEngineGenerator;
	protected double productionWindTurbine;

	protected EngineGeneratorState EGState;
	protected FridgeConsumption FridgeState;
	protected WashingMachineState WMState;

	protected Decision triggeredDecisionEngineGenerator;
	protected Decision lastDecisionEngineGenerator;
	protected double lastDecisionTimeEngineGenerator;
	protected final Vector<DecisionPiece> decisionFunctionEngineGenerator;

	protected Decision triggeredDecisionFridge;
	protected Decision lastDecisionFridge;
	protected double lastDecisionTimeFridge;
	protected final Vector<DecisionPiece> decisionFunctionFridge;
	
	protected Decision triggeredDecisionWashingMachine;
	protected Decision lastDecisionWashingMachine;
	protected double lastDecisionTimeWashingMachine;
	protected final Vector<DecisionPiece> decisionFunctionWashingMachine;

	protected XYPlotter productionPlotter;

	protected final Map<String, XYPlotter> modelsPlotter;

	protected EmbeddingComponentAccessI componentRef;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public ControllerModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);

		this.decisionFunctionEngineGenerator = new Vector<>();
		this.decisionFunctionFridge = new Vector<>();
		this.decisionFunctionWashingMachine = new Vector<>();
		this.modelsPlotter = new HashMap<String, XYPlotter>();

		// this.setLogger(new StandardLogger());
	}

	// -------------------------------------------------------------------------
	// Simulation protocol and related methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void setSimulationRunParameters(Map<String, Object> simParams) throws Exception {
		String vname = this.getURI() + ":" + ControllerModel.PRODUCTION_SERIES + ":"
				+ PlotterDescription.PLOTTING_PARAM_NAME;
		PlotterDescription pd1 = (PlotterDescription) simParams.get(vname);
		this.productionPlotter = new XYPlotter(pd1);
		this.productionPlotter.createSeries(ControllerModel.PRODUCTION);

		vname = this.getURI() + ":" + ControllerModel.CONTROLLER_STUB_SERIES + ":"
				+ PlotterDescription.PLOTTING_PARAM_NAME;
		// if this key is in simParams, it's the MIL that's running
		if (simParams.containsKey(vname)) {
			PlotterDescription pd2 = (PlotterDescription) simParams.get(vname);
			this.modelsPlotter.put(ControllerModel.CONTROLLER_STUB, new XYPlotter(pd2));
			this.modelsPlotter.get(ControllerModel.CONTROLLER_STUB).createSeries(ControllerModel.CONTROLLER_STUB);
		} else {
			vname = this.getURI() + ":" + ControllerModel.ENGINE_GENERATOR_SERIES + ":"
					+ PlotterDescription.PLOTTING_PARAM_NAME;
			PlotterDescription pd2 = (PlotterDescription) simParams.get(vname);
			this.modelsPlotter.put(ControllerModel.ENGINE_GENERATOR, new XYPlotter(pd2));
			this.modelsPlotter.get(ControllerModel.ENGINE_GENERATOR).createSeries(ControllerModel.ENGINE_GENERATOR);

			vname = this.getURI() + ":" + ControllerModel.FRIDGE_SERIES + ":" + PlotterDescription.PLOTTING_PARAM_NAME;
			PlotterDescription pd3 = (PlotterDescription) simParams.get(vname);
			this.modelsPlotter.put(ControllerModel.FRIDGE, new XYPlotter(pd3));
			this.modelsPlotter.get(ControllerModel.FRIDGE).createSeries(ControllerModel.FRIDGE);
			
			vname = this.getURI() + ":" + ControllerModel.WASHING_MACHINE_SERIES + ":" + PlotterDescription.PLOTTING_PARAM_NAME;
			PlotterDescription pd4 = (PlotterDescription) simParams.get(vname);
			this.modelsPlotter.put(ControllerModel.WASHING_MACHINE, new XYPlotter(pd4));
			this.modelsPlotter.get(ControllerModel.WASHING_MACHINE).createSeries(ControllerModel.WASHING_MACHINE);
		}

		// The reference to the embedding component
		this.componentRef = (EmbeddingComponentAccessI) simParams.get(URIS.CONTROLLER_URI);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void initialiseState(Time initialTime) {
		super.initialiseState(initialTime);

		this.mustTransmitDecision = false;

		if (this.componentRef == null) {
			this.consumption = 0.0;
			this.productionEngineGenerator = 0.0;
			this.productionWindTurbine = 0.0;
			this.EGState = EngineGeneratorState.OFF;
			this.FridgeState = FridgeConsumption.RESUMED;
			this.WMState = WashingMachineState.OFF;
		} else {
			try {
				this.consumption = (double) this.componentRef.getEmbeddingComponentStateValue("consumption");
				this.productionEngineGenerator = (double) this.componentRef
						.getEmbeddingComponentStateValue("productionEG");
				this.productionWindTurbine = (double) this.componentRef.getEmbeddingComponentStateValue("productionWT");
				this.EGState = (EngineGeneratorState) this.componentRef.getEmbeddingComponentStateValue("stateEG");
				this.FridgeState = (FridgeConsumption) this.componentRef.getEmbeddingComponentStateValue("stateFridge");
				this.WMState = (WashingMachineState) this.componentRef.getEmbeddingComponentStateValue("stateWM");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		this.triggeredDecisionEngineGenerator = Decision.STOP_ENGINE;
		this.lastDecisionEngineGenerator = Decision.STOP_ENGINE;
		this.lastDecisionTimeEngineGenerator = initialTime.getSimulatedTime();
		this.decisionFunctionEngineGenerator.clear();

		this.triggeredDecisionFridge = Decision.RESUME_FRIDGE;
		this.lastDecisionFridge = Decision.RESUME_FRIDGE;
		this.lastDecisionTimeFridge = initialTime.getSimulatedTime();
		decisionFunctionFridge.clear();
		
		this.triggeredDecisionWashingMachine = Decision.STOP_WASHING;
		this.lastDecisionWashingMachine = Decision.STOP_WASHING;
		this.lastDecisionTimeWashingMachine = initialTime.getSimulatedTime();
		decisionFunctionWashingMachine.clear();

		if (this.productionPlotter != null) {
			this.productionPlotter.initialise();
			this.productionPlotter.showPlotter();
			this.productionPlotter.addData(ControllerModel.PRODUCTION, this.getCurrentStateTime().getSimulatedTime(),
					0.0);
		}

		for (Map.Entry<String, XYPlotter> elt : modelsPlotter.entrySet()) {
			String URI = elt.getKey();
			XYPlotter plotter = elt.getValue();
			if (plotter != null) {
				plotter.initialise();
				plotter.showPlotter();
				if (URI == ControllerModel.ENGINE_GENERATOR) {
					plotter.addData(URI, this.getCurrentStateTime().getSimulatedTime(),
							this.decisionToInteger(this.lastDecisionEngineGenerator));
				} else if (URI == ControllerModel.FRIDGE) {
					plotter.addData(URI, this.getCurrentStateTime().getSimulatedTime(),
							this.decisionToInteger(this.lastDecisionFridge));
				} 
				 else if (URI == ControllerModel.WASHING_MACHINE) {
						plotter.addData(URI, this.getCurrentStateTime().getSimulatedTime(),
								this.decisionToInteger(this.lastDecisionWashingMachine));
					}
				else {
					assert URI.equals(ControllerModel.CONTROLLER_STUB);
					plotter.addData(URI, this.getCurrentStateTime().getSimulatedTime(),
							this.decisionToInteger(this.lastDecisionEngineGenerator));
				}
			}
		}
	}

	/**
	 * return an integer representation to ease the plotting.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	s != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param s
	 *            a state for the controller.
	 * @return an integer representation to ease the plotting.
	 */
	protected int decisionToInteger(Decision d) {
		assert d != null;

		if (d == Decision.START_ENGINE) {
			return 1;
		} else if (d == Decision.STOP_ENGINE) {
			return 0;
		} else if (d == Decision.RESUME_FRIDGE) {
			return 1;
		} else if (d == Decision.SUSPEND_FRIDGE) {
			return 0;
		} 
		else if (d == Decision.START_WASHING) {
			return 1;
		} else if (d == Decision.STOP_WASHING) {
			return 0;
		}
		else {
			// Need to add other decisions
			return -1;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public ArrayList<EventI> output() {
		if (componentRef == null) {
			// if (this.hasDebugLevel(1)) {
			// this.logMessage("output|"
			// + this.lastDecisionEngineGenerator + " "
			// + this.triggeredDecisionEngineGenerator) ;
			// }

			ArrayList<EventI> ret = null;
			ret = new ArrayList<EventI>(1);

			assert ret != null;

			if (this.triggeredDecisionEngineGenerator == Decision.START_ENGINE) {
				ret.add(new StartEngineGeneratorEvent(this.getCurrentStateTime()));
			} else if (this.triggeredDecisionEngineGenerator == Decision.STOP_ENGINE) {
				ret.add(new StopEngineGeneratorEvent(this.getCurrentStateTime()));
			} else if (this.triggeredDecisionFridge == Decision.SUSPEND_FRIDGE) {
				ret.add(new SuspendFridgeEvent(this.getCurrentStateTime()));
			} else if (this.triggeredDecisionFridge == Decision.RESUME_FRIDGE) {
				ret.add(new ResumeFridgeEvent(this.getCurrentStateTime()));
			}
			else if (this.triggeredDecisionWashingMachine == Decision.START_WASHING) {
				ret.add(new StartWashingMachineEvent(this.getCurrentStateTime()));
			} else if (this.triggeredDecisionWashingMachine == Decision.STOP_WASHING) {
				ret.add(new StopWashingMachineEvent(this.getCurrentStateTime()));
			}

			this.decisionFunctionEngineGenerator.add(new DecisionPiece(this.lastDecisionTimeEngineGenerator,
					this.getCurrentStateTime().getSimulatedTime(), this.lastDecisionEngineGenerator));

			this.decisionFunctionFridge.add(new DecisionPiece(this.lastDecisionTimeFridge,
					this.getCurrentStateTime().getSimulatedTime(), this.lastDecisionFridge));
			
			this.decisionFunctionWashingMachine.add(new DecisionPiece(this.lastDecisionTimeWashingMachine,
					this.getCurrentStateTime().getSimulatedTime(), this.lastDecisionWashingMachine));

			this.lastDecisionEngineGenerator = this.triggeredDecisionEngineGenerator;
			this.lastDecisionTimeEngineGenerator = this.getCurrentStateTime().getSimulatedTime();

			this.lastDecisionFridge = this.triggeredDecisionFridge;
			this.lastDecisionTimeFridge = this.getCurrentStateTime().getSimulatedTime();
			
			this.lastDecisionWashingMachine = this.triggeredDecisionWashingMachine;
			this.lastDecisionTimeWashingMachine = this.getCurrentStateTime().getSimulatedTime();

			this.mustTransmitDecision = false;
			return ret;
		} else {
			try {
				if (this.triggeredDecisionEngineGenerator != this.lastDecisionEngineGenerator) {
					if (this.triggeredDecisionEngineGenerator == Decision.START_ENGINE) {
						System.out.println("1");
						this.componentRef.setEmbeddingComponentStateValue("startEngine", null);
					} else if (this.triggeredDecisionEngineGenerator == Decision.STOP_ENGINE) {
						System.out.println("2");
						this.componentRef.setEmbeddingComponentStateValue("stopEngine", null);
					}
				} else if (this.triggeredDecisionFridge != this.lastDecisionFridge) {
					if (this.triggeredDecisionFridge == Decision.SUSPEND_FRIDGE) {
						System.out.println("3");
						this.componentRef.setEmbeddingComponentStateValue("suspendFridge", null);
					} else if (this.triggeredDecisionFridge == Decision.RESUME_FRIDGE) {
						System.out.println("4");
						this.componentRef.setEmbeddingComponentStateValue("resumeFridge", null);
					}
				}
				 else if (this.triggeredDecisionWashingMachine != this.lastDecisionWashingMachine) {
						if (this.triggeredDecisionWashingMachine == Decision.START_WASHING) {
							System.out.println("5");
							this.componentRef.setEmbeddingComponentStateValue("startWM", null);
						} else if (this.triggeredDecisionWashingMachine == Decision.STOP_WASHING) {
							System.out.println("6");
							this.componentRef.setEmbeddingComponentStateValue("stopWM", null);
						}
					}
			} catch (Exception e) {
				e.printStackTrace();
			}
			this.decisionFunctionEngineGenerator.add(new DecisionPiece(this.lastDecisionTimeEngineGenerator,
					this.getCurrentStateTime().getSimulatedTime(), this.lastDecisionEngineGenerator));

			this.decisionFunctionFridge.add(new DecisionPiece(this.lastDecisionTimeFridge,
					this.getCurrentStateTime().getSimulatedTime(), this.lastDecisionFridge));
			
			this.decisionFunctionWashingMachine.add(new DecisionPiece(this.lastDecisionTimeWashingMachine,
					this.getCurrentStateTime().getSimulatedTime(), this.lastDecisionWashingMachine));

			this.lastDecisionEngineGenerator = this.triggeredDecisionEngineGenerator;
			this.lastDecisionTimeEngineGenerator = this.getCurrentStateTime().getSimulatedTime();

			this.lastDecisionFridge = this.triggeredDecisionFridge;
			this.lastDecisionTimeFridge = this.getCurrentStateTime().getSimulatedTime();
			
			this.lastDecisionWashingMachine = this.triggeredDecisionWashingMachine;
			this.lastDecisionTimeWashingMachine = this.getCurrentStateTime().getSimulatedTime();
			
			this.mustTransmitDecision = false;
			return null;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration timeAdvance() {
		if (this.mustTransmitDecision) {
			return Duration.zero(this.getSimulatedTimeUnit());
		} else {
			return Duration.INFINITY;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void userDefinedExternalTransition(Duration elapsedTime) {
		if (componentRef == null) {
			// if (this.hasDebugLevel(1)) {
			// this.logMessage("userDefinedExternalTransition|"
			// + this.EGState + ">>>>>>>>>>>>>>>") ;
			// }

			ArrayList<EventI> current = this.getStoredEventAndReset();

			for (int i = 0; i < current.size(); i++) {

				if (current.get(i) instanceof EngineGeneratorProductionEvent) {
					this.productionEngineGenerator = ((EngineGeneratorProductionEvent.Reading) ((EngineGeneratorProductionEvent) current
							.get(i)).getEventInformation()).value;
					this.logMessage("userDefinedExternalTransition|" + this.getCurrentStateTime() + "|EG production = "
							+ this.productionEngineGenerator);
				} else if (current.get(i) instanceof WindTurbineProductionEvent) {
					this.productionWindTurbine = ((WindTurbineProductionEvent.Reading) ((WindTurbineProductionEvent) current
							.get(i)).getEventInformation()).value;
					this.logMessage("userDefinedExternalTransition|" + this.getCurrentStateTime() + "|WT production = "
							+ this.productionWindTurbine);
				} else if (current.get(i) instanceof ConsumptionEvent) {
					this.consumption = ((ConsumptionEvent.Reading) ((ConsumptionEvent) current.get(i))
							.getEventInformation()).value;
				}
			}
			// GroupeElectrogeneState oldState = this.EGState ;
			double production = this.productionEngineGenerator + this.productionWindTurbine;

			if (this.EGState == EngineGeneratorState.ON) {
				if (production > this.consumption) {
					// on l'eteint
					this.triggeredDecisionEngineGenerator = Decision.STOP_ENGINE;
					this.EGState = EngineGeneratorState.OFF;

					this.mustTransmitDecision = true;
				}
			} else {
				assert this.EGState == EngineGeneratorState.OFF;
				if (production <= this.consumption) {
					// on l'allume
					if (production <= this.consumption - 20) {
						this.triggeredDecisionEngineGenerator = Decision.START_ENGINE;
						this.EGState = EngineGeneratorState.ON;
						this.mustTransmitDecision = true;
					}

				}
			}
			if (this.FridgeState == FridgeConsumption.SUSPENDED) {
				if (production > this.consumption) {
					this.triggeredDecisionFridge = Decision.RESUME_FRIDGE;
					this.FridgeState = FridgeConsumption.RESUMED;

					this.mustTransmitDecision = true;
				}
			} else {
				assert this.FridgeState == FridgeConsumption.RESUMED;
				if (production <= this.consumption) {
					this.triggeredDecisionFridge = Decision.SUSPEND_FRIDGE;
					this.FridgeState = FridgeConsumption.SUSPENDED;
					this.mustTransmitDecision = true;
				}
			}
			
			if (this.WMState == WashingMachineState.ON) {
				if (production <= this.consumption) {
					this.triggeredDecisionWashingMachine = Decision.STOP_WASHING;
					this.WMState = WashingMachineState.OFF;

					this.mustTransmitDecision = true;
				}
			} if(this.WMState == WashingMachineState.WORKING) {
				if (production <= this.consumption) {
					this.triggeredDecisionWashingMachine = Decision.STOP_WASHING;
					this.WMState = WashingMachineState.OFF;

					this.mustTransmitDecision = true;
				}
			}
			else {
				assert this.WMState == WashingMachineState.OFF;
				if (production > this.consumption + 20) {
					this.triggeredDecisionWashingMachine = Decision.START_WASHING;
					this.WMState = WashingMachineState.ON;

					this.mustTransmitDecision = true;
				}
			}


			this.productionPlotter.addData(PRODUCTION, this.getCurrentStateTime().getSimulatedTime(), production);
			this.productionPlotter.addData(PRODUCTION, this.getCurrentStateTime().getSimulatedTime(), production);

			for (Map.Entry<String, XYPlotter> elt : modelsPlotter.entrySet()) {
				String URI = elt.getKey();
				XYPlotter plotter = elt.getValue();
				if (plotter != null) {
					if (URI == ControllerModel.ENGINE_GENERATOR) {
						plotter.addData(URI, this.getCurrentStateTime().getSimulatedTime(),
								this.decisionToInteger(this.lastDecisionEngineGenerator));
					} else if (URI == ControllerModel.FRIDGE) {
						plotter.addData(URI, this.getCurrentStateTime().getSimulatedTime(),
								this.decisionToInteger(this.lastDecisionFridge));
					}else if (URI == ControllerModel.WASHING_MACHINE) {
						plotter.addData(URI, this.getCurrentStateTime().getSimulatedTime(),
								this.decisionToInteger(this.lastDecisionWashingMachine));
					}  
					else {
						assert URI.equals(ControllerModel.CONTROLLER_STUB);
						plotter.addData(URI, this.getCurrentStateTime().getSimulatedTime(),
								this.decisionToInteger(this.lastDecisionEngineGenerator));
					}
				}
			}
			// if (this.hasDebugLevel(1)) {
			// this.logMessage("userDefinedExternalTransition|"
			// + this.EGState + "<<<<<<<<<<<<<<<<<<<") ;
			// }
		} else {
			try {
				this.consumption = (double) this.componentRef.getEmbeddingComponentStateValue("consumption");
				this.productionEngineGenerator = (double) this.componentRef
						.getEmbeddingComponentStateValue("productionEG");
				this.productionWindTurbine = (double) this.componentRef.getEmbeddingComponentStateValue("productionWT");
				this.EGState = (EngineGeneratorState) this.componentRef.getEmbeddingComponentStateValue("stateEG");
				this.FridgeState = (FridgeConsumption) this.componentRef.getEmbeddingComponentStateValue("stateFridge");
				this.WMState = (WashingMachineState) this.componentRef.getEmbeddingComponentStateValue("stateWM");
			} catch (Exception e) {
				e.printStackTrace();
			}
			double production = this.productionEngineGenerator + this.productionWindTurbine;

			if (this.EGState == EngineGeneratorState.ON) {
				if (production > this.consumption) {
					// on l'eteint
					this.triggeredDecisionEngineGenerator = Decision.STOP_ENGINE;
					this.EGState = EngineGeneratorState.OFF;

					this.mustTransmitDecision = true;
				}
			} else {
				assert this.EGState == EngineGeneratorState.OFF;
				if (production <= this.consumption) {
					// on l'allume
					if (production <= this.consumption - 20) {
						this.triggeredDecisionEngineGenerator = Decision.START_ENGINE;
						this.EGState = EngineGeneratorState.ON;
						this.mustTransmitDecision = true;
					}

				}
			}
			if (this.FridgeState == FridgeConsumption.SUSPENDED) {
				if (production > this.consumption) {
					this.triggeredDecisionFridge = Decision.RESUME_FRIDGE;
					this.FridgeState = FridgeConsumption.RESUMED;

					this.mustTransmitDecision = true;
				}
			} else {
				assert this.FridgeState == FridgeConsumption.RESUMED;
				if (production <= this.consumption) {
					this.triggeredDecisionFridge = Decision.SUSPEND_FRIDGE;
					this.FridgeState = FridgeConsumption.SUSPENDED;
					this.mustTransmitDecision = true;
					
				}
			}
			if (this.WMState == WashingMachineState.ON) {
				if (production <= this.consumption) {
					this.triggeredDecisionWashingMachine = Decision.STOP_WASHING;
					this.WMState = WashingMachineState.OFF;

					this.mustTransmitDecision = true;
				}
			} if(this.WMState == WashingMachineState.WORKING) {
				if (production <= this.consumption) {
					this.triggeredDecisionWashingMachine = Decision.STOP_WASHING;
					this.WMState = WashingMachineState.OFF;

					this.mustTransmitDecision = true;
				}
			}
			else {
				assert this.WMState == WashingMachineState.OFF;
				if (production > this.consumption + 20) {
					this.triggeredDecisionWashingMachine = Decision.START_WASHING;
					this.WMState = WashingMachineState.ON;

					this.mustTransmitDecision = true;
				}
			}

			this.productionPlotter.addData(PRODUCTION, this.getCurrentStateTime().getSimulatedTime(), production);
			this.productionPlotter.addData(PRODUCTION, this.getCurrentStateTime().getSimulatedTime(), production);

			for (Map.Entry<String, XYPlotter> elt : modelsPlotter.entrySet()) {
				String URI = elt.getKey();
				XYPlotter plotter = elt.getValue();
				if (plotter != null) {
					if (URI == ControllerModel.ENGINE_GENERATOR) {
						plotter.addData(URI, this.getCurrentStateTime().getSimulatedTime(),
								this.decisionToInteger(this.lastDecisionEngineGenerator));
					} else if (URI == ControllerModel.FRIDGE) {
						plotter.addData(URI, this.getCurrentStateTime().getSimulatedTime(),
								this.decisionToInteger(this.lastDecisionFridge));
					}else if (URI == ControllerModel.WASHING_MACHINE) {
						plotter.addData(URI, this.getCurrentStateTime().getSimulatedTime(),
								this.decisionToInteger(this.lastDecisionWashingMachine));
					}  
					else {
						assert URI.equals(ControllerModel.CONTROLLER_STUB);
						plotter.addData(URI, this.getCurrentStateTime().getSimulatedTime(),
								this.decisionToInteger(this.lastDecisionEngineGenerator));
					}
				}
			}

		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void endSimulation(Time endTime) throws Exception {
		if (this.productionPlotter != null) {
			this.productionPlotter.addData(ControllerModel.PRODUCTION, this.getCurrentStateTime().getSimulatedTime(),
					this.productionEngineGenerator + this.productionWindTurbine);
		}

		for (Map.Entry<String, XYPlotter> elt : modelsPlotter.entrySet()) {
			String URI = elt.getKey();
			XYPlotter plotter = elt.getValue();
			if (plotter != null) {
				if (URI == ControllerModel.ENGINE_GENERATOR) {
					plotter.addData(URI, this.getCurrentStateTime().getSimulatedTime(),
							this.decisionToInteger(this.lastDecisionEngineGenerator));
				} else if (URI == ControllerModel.FRIDGE) {
					plotter.addData(URI, this.getCurrentStateTime().getSimulatedTime(),
							this.decisionToInteger(this.lastDecisionFridge));
				}else if (URI == ControllerModel.WASHING_MACHINE) {
					plotter.addData(URI, this.getCurrentStateTime().getSimulatedTime(),
							this.decisionToInteger(this.lastDecisionWashingMachine));
				}  
				else {
					assert URI.equals(ControllerModel.CONTROLLER_STUB);
					plotter.addData(URI, this.getCurrentStateTime().getSimulatedTime(),
							this.decisionToInteger(this.lastDecisionEngineGenerator));
				}
			}
		}
		super.endSimulation(endTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#getFinalReport()
	 */
	@Override
	public SimulationReportI getFinalReport() throws Exception {
		return new ControllerModelReport(this.getURI());
	}

	public void setConsumption(double consumption) {
		this.consumption = consumption;
	}
}
// -----------------------------------------------------------------------------
