package wattwattReborn.main;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import wattwattReborn.composants.DynamicAssembler;

public class DynamicCVM extends AbstractCVM {

	public DynamicCVM() throws Exception {
		super();
	}

	// single-JVM execution
	protected static String ASSEMBLER_JVM_URI = AbstractCVM.thisJVMURI;
	protected static String COMPTEUR_JVM_URI = AbstractCVM.thisJVMURI;
	protected static String CONTROLEUR_JVM_URI = AbstractCVM.thisJVMURI;
	
	@Override
	public void			deploy() throws Exception
	{
		@SuppressWarnings("unused")
		String daURI =
			AbstractComponent.createComponent(
					DynamicAssembler.class.getCanonicalName(),
					new Object[]{CONTROLEUR_JVM_URI, COMPTEUR_JVM_URI}) ;

		// deployment done
		super.deploy() ;
	}
	
	public static void	main(String[] args)
	{
		try {
			DynamicCVM c = new DynamicCVM() ;
			c.startStandardLifeCycle(15000) ;
			Thread.sleep(5000L) ;
			System.exit(0) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

}
