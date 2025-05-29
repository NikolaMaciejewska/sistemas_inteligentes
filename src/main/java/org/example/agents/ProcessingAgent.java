package org.example.agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import java.io.*;

public class ProcessingAgent extends Agent {
    @Override
    protected void setup() {
        System.out.println(getLocalName() + ": started");

        // 🟢 REGISTRO DEL SERVICIO EN EL DF
        try {
            // Descripción del agente
            DFAgentDescription dfd = new DFAgentDescription();
            dfd.setName(getAID()); // Identificador del agente

            // Descripción del servicio
            ServiceDescription sd = new ServiceDescription();
            sd.setName("Clasificacion de recetas"); // Nombre del servicio
            sd.setType("recipe-classification");     // Tipo de servicio (puede usarse para búsquedas)

            // Se añade el servicio al descriptor del agente
            dfd.addServices(sd);

            // Registro en el DF
            DFService.register(this, dfd);
            System.out.println("Servicio 'Clasificacion de recetas' registrado en DF");

        } catch (FIPAException e) {
            System.err.println("Error al registrar el servicio en el DF: " + e.getMessage());
            e.printStackTrace();
        }

        // 🔁 COMPORTAMIENTO PRINCIPAL
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


    // 🔻 Este método se ejecuta cuando se elimina el agente
    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this); // Se elimina el registro del agente del DF
            System.out.println(getLocalName() + ": Servicio desregistrado del DF.");
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }


}
