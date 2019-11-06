package fr.sorbonne_u.devs_simulation.examples.molene.wm;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a
// basic component programming model to program with components
// distributed applications in the Java programming language.
//
// This software is governed by the CeCILL-C license under French law and
// abiding by the rules of distribution of free software.  You can use,
// modify and/ or redistribute the software under the terms of the
// CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
// URL "http://www.cecill.info".
//
// As a counterpart to the access to the source code and  rights to copy,
// modify and redistribute granted by the license, users are provided only
// with a limited warranty  and the software's author,  the holder of the
// economic rights,  and the successive licensors  have only  limited
// liability. 
//
// In this respect, the user's attention is drawn to the risks associated
// with loading,  using,  modifying and/or developing or reproducing the
// software by the user in light of its specific status of free software,
// that may mean  that it is complicated to manipulate,  and  that  also
// therefore means  that it is reserved for developers  and  experienced
// professionals having in-depth computer knowledge. Users are therefore
// encouraged to load and test the software's suitability as regards their
// requirements in conditions enabling the security of their systems and/or 
// data to be ensured and,  more generally, to use and operate it in the 
// same conditions as regards security. 
//
// The fact that you are presently reading this means that you have had
// knowledge of the CeCILL-C license and that you accept its terms.

import java.util.Map;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.examples.molene.wbm.WiFiBandwidthModel.WiFiBandwidthReport;
import fr.sorbonne_u.devs_simulation.examples.molene.wbsm.WiFiBandwidthSensorModel.WiFiBandwidthSensorReport;
import fr.sorbonne_u.devs_simulation.examples.molene.wdm.WiFiDisconnectionModel.WiFiDisconnectionReport;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.StaticVariableDescriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.CoupledModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.AbstractSimulationReport;

// -----------------------------------------------------------------------------
/**
 * The class <code>WiFiModel</code> implements a coupled model used to gather
 * together all of the modes concurring to represent the behaviour of the WiFi
 * network.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2018-07-17</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class				WiFiModel
extends		CoupledModel
{
	// -------------------------------------------------------------------------
	// Inner classes
	// -------------------------------------------------------------------------

	/**
	 * The class <code>WiFiModelReport</code> implements the simulation report
	 * for the WiFi model.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><strong>Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant		true
	 * </pre>
	 * 
	 * <p>Created on : 2018-10-09</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	public static class	WiFiModelReport
	extends		AbstractSimulationReport
	{
		private static final long		serialVersionUID = 1L ;
		public WiFiBandwidthReport		wifiBandwidthReport ;
		public WiFiDisconnectionReport	wifiDisconnectionReport ;
		public WiFiBandwidthSensorReport	wifiBandwidthSensorReport ;

		public			WiFiModelReport(
			String modelURI,
			SimulationReportI[] reports
			)
		{
			super(modelURI) ;
			assert	reports.length == 4 ;

			for (int i = 0 ; i < reports.length ; i++) {
				if (reports[i] instanceof WiFiBandwidthReport) {
					this.wifiBandwidthReport =
									(WiFiBandwidthReport) reports[i] ;
				} else if (reports[i] instanceof WiFiBandwidthSensorReport) {
					this.wifiBandwidthSensorReport =
									(WiFiBandwidthSensorReport) reports[i] ;
				} else if (reports[i] instanceof WiFiDisconnectionReport) {
					this.wifiDisconnectionReport =
									(WiFiDisconnectionReport) reports[i] ;
				}
			}
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String	toString()
		{
			String ret = "-----------------------------------------\n" ;
			ret += "WiFi model report\n" ;
			ret += this.wifiDisconnectionReport ;
			ret += this.wifiBandwidthReport ;
			ret += this.wifiBandwidthSensorReport ;
			return ret ;
		}
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	public static final String	URI = "WifiModel" ;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public				WiFiModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		SimulatorI simulationEngine,
		ModelDescriptionI[] submodels,
		Map<Class<? extends EventI>, EventSink[]> imported,
		Map<Class<? extends EventI>, ReexportedEvent> reexported,
		Map<EventSource, EventSink[]> connections,
		Map<StaticVariableDescriptor, VariableSink[]> importedVars,
		Map<VariableSource, StaticVariableDescriptor> reexportedVars,
		Map<VariableSource, VariableSink[]> bindings
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine, submodels,
			  imported, reexported, connections,
			  importedVars, reexportedVars, bindings);
	}

	// -------------------------------------------------------------------------
	// Simulation protocol and related methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getFinalReport()
	 */
	@Override
	public SimulationReportI		getFinalReport() throws Exception
	{
		SimulationReportI[] reports =
							new SimulationReportI[this.submodels.length] ;
		for (int i = 0 ; i < this.submodels.length ; i++) {
			reports[i] = this.submodels[i].getFinalReport() ;
		}
		return new WiFiModelReport(this.getURI(), reports) ;
	}
}
// -----------------------------------------------------------------------------
