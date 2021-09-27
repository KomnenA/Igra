package igra;

public class Generator {
	
	public int generisi(int donja, int gornja) {
		return (int)(Math.random()*(gornja - donja + 1)) + donja;
	}
}
