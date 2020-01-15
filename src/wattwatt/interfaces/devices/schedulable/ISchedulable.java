package wattwatt.interfaces.devices.schedulable;

import wattwatt.interfaces.devices.IDevices;

public interface ISchedulable extends IDevices{
		
	public boolean isWorking() throws Exception;
	
	public boolean canDelay(int delay) throws Exception;
	public boolean canAdvance(int advance) throws Exception;
	
	public int durationWork() throws Exception;
	
	public int startingTime() throws Exception;
	public int endingTime() throws Exception;
	
	public void endBefore(int end) throws Exception; //time is an int for now
	public void startAt(int debut) throws Exception;
	public void late(int delay) throws Exception;
	public void advance(int advance) throws Exception;

}
