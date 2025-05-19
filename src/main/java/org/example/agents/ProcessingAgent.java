package org.example.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ProcessingAgent extends Agent {
    @Override
    protected void setup() {
        System.out.println(getLocalName() + ": started");

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    System.out.println(getLocalName() + ": Received message");

                    // Espera un JSON como contenido
                    String parametrosJson = msg.getContent();
                    if (parametrosJson == null || parametrosJson.isEmpty()) {
                        System.out.println("No JSON content received");
                        return;
                    }

                    try {
                        // Ejecutar script Python con el JSON como argumento
                        ProcessBuilder pb = new ProcessBuilder("python", "buscar_recetas.py", parametrosJson);
                        pb.redirectErrorStream(true); // Combina stderr y stdout

                        Process process = pb.start();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                        String line;
                        System.out.println("Respuesta del script Python:");
                        while ((line = reader.readLine()) != null) {
                            System.out.println(line);
                        }

                        process.waitFor();

                    } catch (Exception e) {
                        System.err.println("Error al ejecutar el script Python: " + e.getMessage());
                        e.printStackTrace();
                    }

                } else {
                    block();
                }
            }
        });
    }
}

