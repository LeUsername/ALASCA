package simulation.plugins;

import java.util.Map;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.cyphy.plugins.devs.SupervisorPluginI;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelArchitectureI;
import fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorPluginManagementCI;
import fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SupervisorNotificationCI;
import fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SupervisorPluginManagementCI;
import fr.sorbonne_u.components.cyphy.plugins.devs.ports.SimulatorPluginManagementOutboundPort;
import fr.sorbonne_u.components.cyphy.plugins.devs.ports.SupervisorNotificationInboundPort;
import fr.sorbonne_u.components.cyphy.plugins.devs.ports.SupervisorPluginManagementInboundPort;
import fr.sorbonne_u.components.reflection.interfaces.ReflectionI;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class WattWattSupervisorPlugin 
	extends		AbstractPlugin
	implements	SupervisorPluginI
	{
		private static final long serialVersionUID = 1L;

		// -------------------------------------------------------------------------
		// Plug-in internal constants and variables
		// -------------------------------------------------------------------------

		/** the global simulation architecture associated to the plug-in.		*/
		protected ComponentModelArchitectureI			architecture ;
		/** port through which other components can manage the simulations. 	*/
		protected SupervisorPluginManagementInboundPort	smip ;
		/** port through which simulators can notify back their report after
		 *  each simulation run.												*/
		protected SupervisorNotificationInboundPort		snip ;
		/** simulation management outbound port of the root model component
		 *  allowing this supervisor component to manage the simulation runs.	*/
		protected SimulatorPluginManagementOutboundPort	rootModelSmop ;
		/** variable in which the simulation report can be found after each
		 *  simulation run.														*/
		protected SimulationReportI						report ;

		// -------------------------------------------------------------------------
		// Constructor
		// -------------------------------------------------------------------------

		/**
		 * create a supervisor plug-in with the given global simulation
		 * architecture.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	architecture != null
		 * post	true			// no postcondition.
		 * </pre>
		 *
		 * @param architecture	architecture of the simulation model to be created.
		 * @throws Exception	<i>to do.</i>
		 */
		public				WattWattSupervisorPlugin(
			ComponentModelArchitectureI architecture
			) throws Exception
		{
			assert	architecture != null ;
			this.architecture = architecture ;		
		}

		// -------------------------------------------------------------------------
		// Plug-in generic methods
		// -------------------------------------------------------------------------

		/**
		 * @see fr.sorbonne_u.components.AbstractPlugin#installOn(fr.sorbonne_u.components.ComponentI)
		 */
		@Override
		public void			installOn(ComponentI owner) throws Exception
		{
			assert	owner != null ;
			assert	this.getPluginURI() != null ;

			super.installOn(owner) ;

			if (!owner.isRequiredInterface(ReflectionI.class)) {
				this.addRequiredInterface(ReflectionI.class) ;
			}
			this.addOfferedInterface(SupervisorNotificationCI.class) ;
			this.addOfferedInterface(SupervisorPluginManagementCI.class) ;
			this.addRequiredInterface(SimulatorPluginManagementCI.class) ;
		}

		/**
		 * @see fr.sorbonne_u.components.AbstractPlugin#initialise()
		 */
		@Override
		public void			initialise() throws Exception
		{
			super.initialise() ;

			this.snip = new SupervisorNotificationInboundPort(this.getPluginURI(),
															  this.owner) ;
			this.snip.publishPort() ;

			this.smip = new SupervisorPluginManagementInboundPort(
														this.getPluginURI(),
														owner) ;
			this.smip.publishPort() ;
		}

		/**
		 * @see fr.sorbonne_u.components.AbstractPlugin#isInitialised()
		 */
		@Override
		public boolean		isInitialised() throws Exception
		{
			return super.isInitialised() &&
								this.snip != null && this.smip != null ;
		}

		/**
		 * @see fr.sorbonne_u.components.AbstractPlugin#finalise()
		 */
		@Override
		public void			finalise() throws Exception
		{
			if (this.rootModelSmop != null && this.rootModelSmop.connected()) {
				this.owner.doPortDisconnection(this.rootModelSmop.getPortURI()) ;
			}
			super.finalise() ;
		}

		/**
		 * @see fr.sorbonne_u.components.AbstractPlugin#uninstall()
		 */
		@Override
		public void			uninstall() throws Exception
		{
			this.snip.unpublishPort() ;
			this.snip.destroyPort() ;
			this.removeOfferedInterface(SupervisorNotificationCI.class) ;

			this.smip.unpublishPort() ;
			this.smip.destroyPort() ;
			this.removeOfferedInterface(SupervisorPluginManagementCI.class) ;

			if (this.rootModelSmop != null) {
				this.rootModelSmop.unpublishPort() ;
				this.rootModelSmop.destroyPort() ;
			}
	 		this.removeRequiredInterface(SimulatorPluginManagementCI.class) ;

			super.uninstall();
		}

		// -------------------------------------------------------------------------
		// Plug-in specific methods
		// -------------------------------------------------------------------------

		/**
		 * return the outbound port connected to the root model component, or
		 * null if none is connected.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	true			// no precondition.
		 * post	true			// no postcondition.
		 * </pre>
		 *
		 * @return	the simulation management reference of the root model, or null if none is connected.
		 */
		protected SimulatorPluginManagementOutboundPort	getRootSmop()
		{
			return this.rootModelSmop ;
		}

		/**
		 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#getTimeOfStart()
		 */
		@Override
		public Time			getTimeOfStart() throws Exception
		{
			return this.getRootSmop().getTimeOfStart() ;
		}

		/**
		 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#getSimulationEndTime()
		 */
		@Override
		public Time			getSimulationEndTime() throws Exception
		{
			return this.getRootSmop().getSimulationEndTime() ;
		}

//		/**
//		 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#readyForSimulation()
//		 */
//		@Override
//		public boolean		readyForSimulation() throws Exception
//		{
//			return this.getRootSmop() != null &&
//						this.getRootSmop().connected() &&
//										this.getRootSmop().readyForSimulation() ;
//		}

		/**
		 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#setSimulationRunParameters(java.util.Map)
		 */
		@Override
		public void			setSimulationRunParameters(
			Map<String, Object> simParams
			) throws Exception
		{
			assert	simParams != null ;

			this.getRootSmop().setSimulationRunParameters(simParams) ;
		}

		/**
		 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#doStandAloneSimulation(double, double)
		 */
		@Override
		public void			doStandAloneSimulation(
			double startTime,
			double simulationDuration
			) throws Exception
		{
			this.getRootSmop().doStandAloneSimulation(
												startTime, simulationDuration) ;
		}

//		/**
//		 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#startCollaborativeSimulation(double, double)
//		 */
//		@Override
//		public void			startCollaborativeSimulation(
//			double startTime,
//			double simulationDuration
//			) throws Exception
//		{
//			this.getRootSmop().startCollaborativeSimulation(
//											startTime, simulationDuration) ;
//		}

		/**
		 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#isSimulationRunning()
		 */
		@Override
		public boolean		isSimulationRunning() throws Exception
		{
			return this.getRootSmop().isSimulationRunning() ;
		}

		/**
		 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#stopSimulation()
		 */
		@Override
		public void			stopSimulation() throws Exception
		{
			this.getRootSmop().stopSimulation() ;
		}

		/**
		 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#getFinalReport()
		 */
		@Override
		public SimulationReportI	getFinalReport() throws Exception
		{
			return this.getRootSmop().getFinalReport() ;
		}

		/**
		 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SupervisorPluginManagementI#connectRootSimulatorComponent()
		 */
		@Override
		public void			connectRootSimulatorComponent()
		throws Exception
		{
			this.logMessage("connecting supervisor to " +
											this.architecture.getRootModelURI()) ;

			this.rootModelSmop = this.architecture.connectRootModelComponent(
												(AbstractComponent)this.owner) ;
			this.rootModelSmop.connectSupervision(this.snip.getPortURI()) ;
		}

		/**
		 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SupervisorPluginManagementI#createSimulator()
		 */
		public void			createSimulator() throws Exception
		{
			assert	this.architecture != null && this.architecture.isComplete() ;

			if (this.rootModelSmop == null) {
				this.connectRootSimulatorComponent() ;
			}

			// TODO: does not work for simulation architectures containing only
			// one atomic model that is root model by default.
			this.getRootSmop().compose(architecture) ;
		}

		/**
		 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SupervisorNotificationI#acceptSimulationReport(fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI)
		 */
		@Override
		public void			acceptSimulationReport(SimulationReportI report)
		{
			// memorise the notified report.
			this.report = report ;
			this.logMessage("" + report) ;
		}

		@Override
		public void startRealTimeSimulation(double startTime, double simulationDuration) throws Exception {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void finaliseSimulation() throws Exception {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void resetArchitecture(ComponentModelArchitectureI architecture) throws Exception {
			// TODO Auto-generated method stub
			
		}
	}
	// -----------------------------------------------------------------------------