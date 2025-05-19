package org.example.agents;

import javax.swing.JFrame;
import org.example.agents.*;


public class MainGUI extends Thread {
	String titulo;
	AcquisitionAgent agenteAcquisition;
	UI frame;
	//Recibe como parámetros el título de la ventana y la referencia al agente cliente
	public MainGUI(String tit, AcquisitionAgent a) {
		this.titulo= "Agente Cliente: " + tit;
		this.agenteAcquisition = a;
	}

	public void run()	{
		//El interfaz se basará en un contendor de tipo Jframe, Lo personalizamos para que pueda gestionar la información del hilo
    		JFrame jFrame;
		//Utilizamos un constructor personalizado, pasando la referencia al agente como parámetro
    		jFrame = new UI(agenteAcquisition);
    		frame = (UI) jFrame;

       		 jFrame.setTitle(titulo);
       		 jFrame.setVisible(true);
       		 jFrame.setResizable(true);
	}

	//Utilizamos este método para que el agente pueda acceder a la referencia de la ventana cuando lo necesite

    public UI getFrame()	{
		return frame;
	}

}
