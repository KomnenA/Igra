package igra;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Polje extends Canvas implements Runnable {

	public enum Status {SLOBODNO, IZABRANO};
	
	private Mreza mreza;
	private int broj;
	private Label natpis;
	private Status status = Status.SLOBODNO;
	private boolean radi;
	private Thread nit = new Thread(this);
	
	void prekini() {
		if (nit != null)
			nit.interrupt();
	}
	private synchronized void kreni() {
		radi = true;
		notify();
	}
	
	public Polje(Mreza mreza, int broj) {
		this.setSize(new Dimension(75, 75));
		this.mreza = mreza;
		this.broj = broj;
		natpis = new Label("" + broj);
		
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (status == Status.SLOBODNO) {
					status = Status.IZABRANO;
					kreni();
				}
				else if (status == Status.IZABRANO) {
					status = Status.SLOBODNO;
					kreni();
				}
			}
		});
		
		nit.start();
		repaint();
	}
	
	public int dohvBroj() {
		return broj;
	}
	public Status dohvStatus() {
		return status;
	}
	
	public void postaviStatus(Status s) {
		status = s;
	}
	
	@Override
	public void paint(Graphics g) {
		g.setColor(Color.ORANGE);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		int dim = this.getHeight() >= this.getWidth() ? this.getWidth()/3 : this.getHeight()/3;
		g.setFont(new Font(null, Font.BOLD, dim));
		if (status == Status.SLOBODNO) {
			g.setColor(Color.BLACK);
			if (broj < 10) {
				g.drawString(natpis.getText(), this.getWidth()/2 - dim/3, this.getHeight()/2 + dim/3);
			}
			else {
				g.drawString(natpis.getText(), this.getWidth()/2 - 2*dim/3, this.getHeight()/2 + dim/3);
			}
		}
		else if (status == Status.IZABRANO) {
			g.setColor(Color.BLUE);
			g.fillOval(0, 0, this.getWidth(), this.getHeight());
			g.setColor(Color.WHITE);
			if (broj < 10) {
				g.drawString(natpis.getText(), this.getWidth()/2 - dim/3, this.getHeight()/2 + dim/3);
			}
			else {
				g.drawString(natpis.getText(), this.getWidth()/2 - 2*dim/3, this.getHeight()/2 + dim/3);
			}
		}
	}

	@Override
	public void run() {
		try {
			while(!Thread.interrupted()) {
				synchronized(this) {
					while(!radi) 
						wait();
				}
				repaint();
				mreza.kreni();
				radi = false;
			}
		} catch(InterruptedException e) {}
	}
}
