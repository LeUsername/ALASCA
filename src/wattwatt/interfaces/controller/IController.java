package wattwatt.interfaces.controller;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

public interface IController extends OfferedI, RequiredI {
	
	
	// PAS BESOIN DE CELUI LA 
	
	public int getAllConso() throws Exception;
	
	// Refrigerateur
	public void refriOn() throws Exception;
	public void refriOff() throws Exception;
	public void refriSuspend() throws Exception;
	public void refriResume() throws Exception;
	public double refriTempH() throws Exception;
	public double refriTempL() throws Exception;
	public int refriConso() throws Exception;
	//
}
