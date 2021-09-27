package igra;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;

import igra.Polje.Status;

public class Mreza extends Panel implements Runnable {

	private static final int razmak = 3;
	private Thread nit = new Thread(this);
	int m, n;
	private Polje[][] polja;
	private ArrayList<Polje> izabranaPolja = new ArrayList<>();
	private Igra igra;
	private HashSet<Integer> skup = new HashSet<>();
	boolean radi;
	
	void prekini() {
		for (int i = 0; i<m; i++) {
			for (int j = 0; j<n; j++)
				polja[i][j].prekini();
		}
		if (nit != null)
			nit.interrupt();
	}
	synchronized void kreni() {
		radi = true;
		notify();
	}
	
	private void dodajPolja() {
		int brojac = 0;
		for (int i = 0; i<m; i++) {
			for (int j = 0; j<n; j++) {
				polja[i][j] = new Polje(this, brojac);
				brojac++;
			}
		}
	}
	
	private void dodajMrezu() {
		this.setBackground(Color.BLACK);
		this.setLayout(new GridLayout(m, n, razmak, razmak));
		for (int i = 0; i<m; i++) {
			for (int j = 0; j<n; j++)
				this.add(polja[i][j]);
		}
	}
	
	private void azuriraj() {
		for (int i = 0; i<m; i++) {
			for (int j = 0; j<n; j++) {
				if (polja[i][j].dohvStatus() == Status.IZABRANO && !izabranaPolja.contains(polja[i][j])) {
					izabranaPolja.add(polja[i][j]);
					skup.add(polja[i][j].dohvBroj());
				}
				else if (polja[i][j].dohvStatus() == Status.SLOBODNO && izabranaPolja.contains(polja[i][j])) {
					izabranaPolja.remove(polja[i][j]);
					skup.remove(polja[i][j].dohvBroj());
				}
			}
		}
	}
	
	public Mreza(Igra igra) {
		this(4, 5, igra);
	}
	public Mreza(int m, int n, Igra igra) {
		this.m = m;
		this.n = n;
		this.igra = igra;
		polja = new Polje[m][n];
		
		this.dodajPolja();
		this.dodajMrezu();
		
		nit.start();
	}
	
	public synchronized ArrayList<Polje> dohvListu() {
		return izabranaPolja;
	}
	public synchronized HashSet<Integer> dohvSkup() {
		return skup;
	}

	@Override
	public void run() {
		try {
			while(!Thread.interrupted()) {
				synchronized(this) {
					while (!radi)
						wait();
				}
				this.azuriraj();
				igra.kreni();
				radi = false;
			}
		} catch (InterruptedException e) {}
	}	
}
