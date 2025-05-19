package org.example.agents;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class MainContainerLauncher{
    public static void main(String[] args) {
        Runtime rt = Runtime.instance();

        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        profile.setParameter(Profile.PLATFORM_ID, "RecipePlatform");
        profile.setParameter(Profile.LOCAL_PORT, "2099");
        profile.setParameter(Profile.GUI, "true");

        AgentContainer mainContainer = rt.createMainContainer(profile);

        try {
            AgentController dataAgent = mainContainer.createNewAgent(
                    "AcquisitionAgent",
                    "org.example.agents.AcquisitionAgent",
                    null
            );

            AgentController procAgent = mainContainer.createNewAgent(
                    "ProcessingAgent",
                    "org.example.agents.ProcessingAgent",
                    null
            );

            dataAgent.start();
            procAgent.start();

        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}
