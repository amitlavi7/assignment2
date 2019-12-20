package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Agent;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.Squad;
import bgu.spl.mics.application.subscribers.Intelligence;
import bgu.spl.mics.application.subscribers.M;
import bgu.spl.mics.application.subscribers.Q;
import bgu.spl.mics.application.subscribers.Moneypenny;
import bgu.spl.mics.application.publishers.TimeService;
import com.google.gson.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.JsonObject;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class MI6Runner {
    public static void main(String[] args) throws FileNotFoundException {
        if (args.length != 1){
            System.out.println("Invalid args size");
            return;
        }
        //Gson gson = new Gson();
        JsonObject obj = (JsonObject) new JsonParser().parse(new FileReader(args[0]));
        JsonArray inventory = obj.getAsJsonArray("inventory");
        JsonArray squad =  obj.getAsJsonArray("squad");
//        JsonArray intelligence = obj.getAsJsonObject("services").getAsJsonArray("intelligence");
        JsonObject services = obj.getAsJsonObject("services");
        LinkedList<M> mList = new LinkedList<>();
        LinkedList<Moneypenny> moneypennies = new LinkedList<>();
        LinkedList<Intelligence> intelligenceList = new LinkedList<>();
        loadInventory(inventory);
        loadSquad(squad);
        loadServices(services, mList, moneypennies, intelligenceList);
        List<Thread> threadsList = new LinkedList<>();
        Q q = new Q ();
        int time = services.get("time").getAsInt();
        TimeService timeService = new TimeService(time);
        for (M m : mList)
            threadsList.add(new Thread(m));
        for (Moneypenny moneypenny : moneypennies)
            threadsList.add(new Thread(moneypenny));
        for (Intelligence intelligence : intelligenceList)
            threadsList.add(new Thread(intelligence));
        threadsList.add(new Thread(q));
        threadsList.add(new Thread(timeService));
        for (Thread t : threadsList)
            t.start();
//        Inventory inv = Inventory.getInstance();
//        for(String gadget: inv.getItem()){
//
//        }
    }

    private static void loadInventory(JsonArray inventory) {
        Inventory inv = Inventory.getInstance();
        String[] itemsToLoad = new String[inventory.size()];
        for (int i = 0; i < inventory.size(); i++)
            itemsToLoad[i] = inventory.get(i).toString();
        inv.load(itemsToLoad);
    }

    private static void loadSquad(JsonArray squad) {
        Squad s = Squad.getInstance();
        Agent[] AgentsToLoad = new Agent[squad.size()];
        Agent agent = new Agent();
        for (int i = 0; i < squad.size(); i++) {
            agent.setName(((JsonObject)squad.get(i)).get("name").toString());
            agent.setSerialNumber(((JsonObject)squad.get(i)).get("serialNumber").toString());
            AgentsToLoad[i] = agent;
        }
        s.load(AgentsToLoad);
    }

    private static void loadServices(JsonObject services, LinkedList<M> mList, LinkedList<Moneypenny> moneypennies, LinkedList<Intelligence> intelligenceList){
        int msNumber = services.get("M").getAsInt();
        int moneypennyNumber = services.get("Moneypenny").getAsInt();
        JsonArray missionsInfo = services.get("intelligence").getAsJsonArray();
        for (int i = 0; i < missionsInfo.size(); i++){
            JsonObject missionObject = missionsInfo.get(i).getAsJsonObject();
            JsonArray missionsArray = missionObject.get("missions").getAsJsonArray();
            Intelligence intelligence = new Intelligence(i, missionsArray);
            intelligenceList.add(intelligence);
        }
        for (int i = 0; i < msNumber; i++) {
            M m = new M(i);
            mList.add(m);
        }
        for (int i = 0; i < moneypennyNumber; i++) {
            Moneypenny mp = new Moneypenny(i);
            moneypennies.add(mp);
        }
    }

}
