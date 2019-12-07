package wattwattReborn.connecteurs.sources;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import wattwattReborn.interfaces.sources.ISources;

public abstract class SourceConnector extends AbstractConnector implements ISources {

	@Override
	public int getEnergie() throws Exception {
		return ((ISources) this.offering).getEnergie();
	}

}
