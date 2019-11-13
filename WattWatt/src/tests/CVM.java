package tests;

import java.util.Vector;

import composants.Batterie;
import composants.Compteur;
import composants.Controleur;
import composants.Eolienne;
import composants.LaveLinge;
import composants.SecheCheveux;
import connecteurs.StringDataConnector;

// Copyright Jacques Malenfant, Sorbonne Universite.
//
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

import fr.sorbonne_u.components.cvm.AbstractCVM;

public class CVM extends AbstractCVM {
	/**
	 * URI of the reflection inbound port of the concurrent map component.
	 */
	protected String COMPTEUR_URI = "compteur";
	protected String CONTROLLEUR_URI = "controleur";
	protected String SECHE_CHEVEUX_URI = "secheCheveux";
	protected String LAVE_LINGE_URI = "laveLinge";
	protected String EOLIENNE_URI = "eolienne";
	protected String BATTERIE_URI = "batterie";

	protected Vector<String> uris = new Vector<>();

	Controleur cont;
	Compteur cpt;
	SecheCheveux secheCheveux;
	LaveLinge laveLinge;
	Eolienne eolienne;
	Batterie batterie;
	
	int quantiteMaxBatterie = 10;

	public CVM() throws Exception {
		super();
	}

	/**
	 * @see fr.sorbonne_u.components.cvm.AbstractCVM#deploy()
	 */
	@Override
	public void deploy() throws Exception {
		// --------------------------------------------------------------------
		// Creation phase
		// --------------------------------------------------------------------
		uris.add(COMPTEUR_URI);
		uris.add(EOLIENNE_URI);
		uris.add(LAVE_LINGE_URI);
		uris.add(SECHE_CHEVEUX_URI);
		uris.add(BATTERIE_URI);

		this.cont = new Controleur(CONTROLLEUR_URI, 1, 0, uris);
		this.cpt = new Compteur(COMPTEUR_URI, 1, 0);
		this.secheCheveux = new SecheCheveux(SECHE_CHEVEUX_URI, 1, 0);
		this.laveLinge = new LaveLinge(LAVE_LINGE_URI, 1, 0);
		this.eolienne = new Eolienne(EOLIENNE_URI, 1, 0);
		this.batterie = new Batterie(BATTERIE_URI, 1, 0, quantiteMaxBatterie);

		this.addDeployedComponent(CONTROLLEUR_URI, cont);
		this.addDeployedComponent(COMPTEUR_URI, cpt);
		this.addDeployedComponent(SECHE_CHEVEUX_URI, secheCheveux);
		this.addDeployedComponent(LAVE_LINGE_URI, laveLinge);
		this.addDeployedComponent(EOLIENNE_URI, eolienne);
		this.addDeployedComponent(BATTERIE_URI, batterie);

		this.toggleTracing(CONTROLLEUR_URI);
		this.toggleTracing(COMPTEUR_URI);
		this.toggleTracing(SECHE_CHEVEUX_URI);
		this.toggleTracing(LAVE_LINGE_URI);
		this.toggleTracing(EOLIENNE_URI);
		this.toggleTracing(BATTERIE_URI);

		this.doPortConnection(CONTROLLEUR_URI, this.cont.stringDataInPort.get(COMPTEUR_URI).getPortURI(),
				this.cpt.stringDataOutPort.getPortURI(), StringDataConnector.class.getCanonicalName());
		this.doPortConnection(COMPTEUR_URI, this.cpt.stringDataInPort.getPortURI(),
				this.cont.stringDataOutPort.get(COMPTEUR_URI).getPortURI(),
				StringDataConnector.class.getCanonicalName());

		this.doPortConnection(CONTROLLEUR_URI, this.cont.stringDataInPort.get(SECHE_CHEVEUX_URI).getPortURI(),
				this.secheCheveux.stringDataOutPort.getPortURI(), StringDataConnector.class.getCanonicalName());
		this.doPortConnection(SECHE_CHEVEUX_URI, this.secheCheveux.stringDataInPort.getPortURI(),
				this.cont.stringDataOutPort.get(SECHE_CHEVEUX_URI).getPortURI(),
				StringDataConnector.class.getCanonicalName());

		this.doPortConnection(CONTROLLEUR_URI, this.cont.stringDataInPort.get(LAVE_LINGE_URI).getPortURI(),
				this.laveLinge.stringDataOutPort.getPortURI(), StringDataConnector.class.getCanonicalName());
		this.doPortConnection(LAVE_LINGE_URI, this.laveLinge.stringDataInPort.getPortURI(),
				this.cont.stringDataOutPort.get(LAVE_LINGE_URI).getPortURI(),
				StringDataConnector.class.getCanonicalName());

		this.doPortConnection(CONTROLLEUR_URI, this.cont.stringDataInPort.get(EOLIENNE_URI).getPortURI(),
				this.eolienne.stringDataOutPort.getPortURI(), StringDataConnector.class.getCanonicalName());
		this.doPortConnection(EOLIENNE_URI, this.eolienne.stringDataInPort.getPortURI(),
				this.cont.stringDataOutPort.get(EOLIENNE_URI).getPortURI(),
				StringDataConnector.class.getCanonicalName());

		this.doPortConnection(CONTROLLEUR_URI, this.cont.stringDataInPort.get(BATTERIE_URI).getPortURI(),
				this.batterie.stringDataOutPort.getPortURI(), StringDataConnector.class.getCanonicalName());
		this.doPortConnection(BATTERIE_URI, this.batterie.stringDataInPort.getPortURI(),
				this.cont.stringDataOutPort.get(BATTERIE_URI).getPortURI(),
				StringDataConnector.class.getCanonicalName());

		super.deploy();
	}

	public static void main(String[] args) {
		try {
			CVM a = new CVM();
			a.startStandardLifeCycle(500000L);
			Thread.sleep(500000L);
			System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
// ------------------------------------------------------------------------------
