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
        profile.setParameter(Profile.GUI, "true");
        profile.setParameter(Profile.PLATFORM_ID, "RecipePlatform");
        profile.setParameter(Profile.LOCAL_PORT, "1099");


        AgentContainer mainContainer = rt.createMainContainer(profile);

        try {
            AgentController uiAgent = mainContainer.createNewAgent(
                    "UIAgent",
                    "org.example.agents.UIAgent",
                    null
            );

            AgentController finderAgent = mainContainer.createNewAgent(
                    "RecipeFinderAgent",
                    "org.example.agents.RecipeFinderAgent",
                    null
            );

            AgentController prefAgent = mainContainer.createNewAgent(
                    "PreferenceAgent",
                    "org.example.agents.PreferenceAgent",
                    null
            );

            uiAgent.start();
            finderAgent.start();
            prefAgent.start();

        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}
