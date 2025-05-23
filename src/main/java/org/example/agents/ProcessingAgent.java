package org.example.agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.*;

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

                    String parametrosJson = msg.getContent();
                    if (parametrosJson == null || parametrosJson.isEmpty()) {
                        System.out.println("No JSON content received");
                        return;
                    }

                    try {
                        // Crear el proceso para ejecutar el script
                        ProcessBuilder pb = new ProcessBuilder("python", "buscar_recetas.py");
                        pb.redirectErrorStream(true);

                        Process process = pb.start();

                        // Enviar el JSON al stdin del script Python
                        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
                            writer.write(parametrosJson);
                            writer.flush();
                        }

                        // Leer la salida del script
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
