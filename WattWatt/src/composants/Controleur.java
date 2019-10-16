package composants;

import fr.sorbonne_u.components.AbstractComponent;
import interfaces.IControleur;

public class Controleur extends AbstractComponent implements IControleur {

	protected Controleur(int nbThreads, int nbSchedulableThreads) {
		super(nbThreads, nbSchedulableThreads);
	}

	@Override
	public void gestionRefigerateur() throws Exception {
		System.out.println("gestion refrigerateur");
	}

	@Override
	public void gestionLaveLinge() throws Exception {
		System.out.println("gestion lavelinge");
	}

	@Override
	public void gestionBatterie() throws Exception {
		System.out.println("gestion batterie");
	}

	@Override
	public void gestionEolienne() throws Exception {
		System.out.println("gestion eolienne");
	}

}
