package wattwatt.ports.controller;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import wattwatt.interfaces.controller.IController;

/**
 * The class <code>ControllerOutPort</code>
 *
 * <p><strong>Description</strong></p>
 *  The OutBound port of the controller component
 * 
 * <p>Created on : 2020-01-27</p>
 * 
 * @author	<p>Bah Thierno, Zheng Pascal</p>
 */
public class ControllerOutPort extends AbstractOutboundPort implements IController {

	private static final long serialVersionUID = 1L;

	public ControllerOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, IController.class, owner);
	}

	

}
