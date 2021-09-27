package igra;

import java.awt.*;
import java.awt.event.*;

public class Igra extends Frame implements Runnable {
	
	private Mreza mreza;
	private Generator generator = new Generator();
	private Panel upravljanje = new Panel();
	private Panel traka = new Panel(new FlowLayout(Label.CENTER, 0, 0));
	private Thread nit = new Thread(this);
	private double kvota, dobitak, balans, ulog = 100;
	private Label Balans = new Label("Balans: " + balans), Ulog = new Label("Ulog: ");
	private Label Kvota = new Label("Kvota: " + kvota), Dobitak = new Label("Dobitak: " + dobitak);
	private TextField poljeUlog = new TextField("100", 4);
	private Button igraj = new Button("Igraj");
	private Label brojTraka = new Label("");
	boolean promena;
	
	synchronized void kreni() {
		promena = true;
		notify();
	}
	
	private void azuriraj() {
		if (mreza.dohvListu().size() > 0) {
			kvota = (double)(mreza.m * mreza.n) / (mreza.dohvListu().size());
			dobitak = kvota * ulog;
			Kvota.setText("Kvota: " + String.format("%.2f", kvota));
			Dobitak.setText("Dobitak: " + String.format("%.2f", dobitak));
			igraj.setEnabled(true);
		}
		else {
			kvota = 0.0;
			dobitak = 0.0;
			Kvota.setText("Kvota: " + String.format("%.2f", kvota));
			Dobitak.setText("Dobitak: " + String.format("%.2f", dobitak));
			igraj.setEnabled(false);
		}
	}
	
	private void dodajCentar() {
		this.add(mreza, BorderLayout.WEST);
	}
	private void dodajIstok() {
		upravljanje.setLayout(new GridLayout(0, 1, 3, 0));
		upravljanje.setBackground(Color.LIGHT_GRAY);
		upravljanje.add(Balans);
		Panel panel1 = new Panel(new FlowLayout(10, 0, 20));
		panel1.add(Ulog);
		panel1.add(poljeUlog);
		upravljanje.add(panel1);
		upravljanje.add(Kvota);
		upravljanje.add(Dobitak);
		Panel panel2 = new Panel();
		panel2.add(igraj);
		upravljanje.add(panel2);
		this.add(upravljanje, BorderLayout.CENTER);
	}
	private void dodajJug() {
		traka.setBackground(Color.GRAY);
		brojTraka.setBackground(Color.GRAY);
		traka.add(brojTraka);
		traka.setPreferredSize(new Dimension(this.getWidth(), 25));
		this.add(traka, BorderLayout.SOUTH);
	}
	
	private void igraj() {
		int broj = generator.generisi(0, mreza.m * mreza.n - 1);
		for (int b : mreza.dohvSkup()) {
			if (broj == b) {
				traka.setBackground(Color.GREEN);
				brojTraka.setBackground(Color.GREEN);
				brojTraka.setFont(new Font(null, Font.BOLD, 16));
				brojTraka.setText("" + broj);
				balans += dobitak;
				traka.revalidate();
				Balans.setText("Balans: " + String.format("%.2f", balans));
				return;
			}
		}
		traka.setBackground(Color.RED);
		brojTraka.setBackground(Color.RED);
		brojTraka.setFont(new Font(null, Font.BOLD, 16));
		brojTraka.setText("" + broj);
		balans -= dobitak;
		traka.revalidate();
		Balans.setText("Balans: " + String.format("%.2f", balans));
	}
	
	private void dodajOsluskivace() {
		poljeUlog.addTextListener(te -> {
			ulog = Integer.parseInt(poljeUlog.getText());
			this.azuriraj();
		});
		
		igraj.addActionListener(ae -> {
			this.igraj();
		});
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				mreza.prekini();
				if (nit != null)
					nit.interrupt();
				dispose();
			}
		});
	}
	
	public Igra() {
		this.setBounds(600, 300, 550, 400);
		this.setResizable(false);
		this.setTitle("Igra");
		mreza = new Mreza(this);
		igraj.setEnabled(false);
		
		this.dodajCentar();
		this.dodajIstok();
		this.dodajJug();
		this.dodajOsluskivace();
		
		this.setVisible(true);
		nit.start();
	}
	
	public Mreza dohvMrezu() {
		return mreza;
	}

	@Override
	public void run() {
		try {
			while(!Thread.interrupted()) {
				synchronized(this) {
					while(!promena)
						wait();
				}
				this.azuriraj();
				promena = false;
			}
		} catch (InterruptedException e) {}
	}
	
	public static void main(String args[]) {
		new Igra();
	}
}
