package org.example.agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

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
                        ProcessBuilder pb = new ProcessBuilder("python", "buscar_recetas.py");
                        pb.redirectErrorStream(true);

                        Process process = pb.start();

                        // Enviar JSON al script
                        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
                            writer.write(parametrosJson);
                            writer.flush();
                        }

                        // Leer la salida del script Python
                        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        StringBuilder outputBuilder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            outputBuilder.append(line).append("\n");
                        }

                        process.waitFor();

                        // Enviar la salida de vuelta al agente emisor
                        ACLMessage reply = msg.createReply();
                        reply.setPerformative(ACLMessage.INFORM);
                        reply.setContent(outputBuilder.toString().trim());

                        send(reply);
                        System.out.println("Respuesta enviada al agente emisor.");

                    } catch (Exception e) {
                        System.err.println("Error al ejecutar el script Python: " + e.getMessage());
                        e.printStackTrace();
                    }

                } else {
                    block(); // Espera nuevos mensajes
                }
            }
        });
    }
}
