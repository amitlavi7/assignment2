package bgu.spl.mics.application.passiveObjects;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Passive data-object representing a information about an agent in MI6.
 * You must not alter any of the given public methods of this class. 
 * <p>
 * You may add ONLY private fields and methods to this class.
 */
public class Squad {

	private Map<String, Agent> agents;
	private static  class SquadHolder {
		private static Squad instance = new Squad();
	}

	private Squad(){
		agents = new HashMap<String, Agent>();
	}

	/**
	 * Retrieves the single instance of this class.
	 */
	public static Squad getInstance() {
		return SquadHolder.instance;
	}

	/**
	 * Initializes the squad. This method adds all the agents to the squad.
	 * <p>
	 * @param agents 	Data structure containing all data necessary for initialization
	 * 						of the squad.
	 */
	public void load (Agent[] agents) {
		for (Agent agent : agents) {
			this.agents.put(agent.getSerialNumber(), agent);
		}
	}

	/**
	 * Releases agents.
	 */
	public void releaseAgents(List<String> serials){
		for (String serial: serials){
			if (agents.containsKey(serial))
				agents.get(serial).release();
		}
		notifyAll();
	}

	/**
	 * simulates executing a mission by calling sleep.
	 * @param time   milliseconds to sleep
	 */
	public void sendAgents(List<String> serials, int time){
		for(String serial : serials){
			agents.get(serial).acquire();
		}
		try{
			Thread.sleep(time);
		}
		catch(Exception e) {

		}
		for (String serial: serials){
			if (agents.containsKey(serial))
				agents.get(serial).release();
		}


	}

	/**
	 * acquires an agent, i.e. holds the agent until the caller is done with it
	 * @param serials   the serial numbers of the agents
	 * @return ‘false’ if an agent of serialNumber ‘serial’ is missing, and ‘true’ otherwise
	 */
	public boolean getAgents(List<String> serials){
		synchronized (this) {
			for (String serial : serials) {
				if (!agents.containsKey(serial) || !agents.get(serial).isAvailable())
					return false;
				agents.get(serial).acquire();
			}
		}
		return true;
	}

    /**
     * gets the agents names
     * @param serials the serial numbers of the agents
     * @return a list of the names of the agents with the specified serials.
     */
    public List<String> getAgentsNames(List<String> serials){
    	List<String> nameList = new LinkedList<>();
		for (String serial: serials){
			if (agents.containsKey(serial))
				nameList.add(agents.get(serial).getName());
		}
		return nameList;
    }

}
