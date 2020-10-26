package salesAgent;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class DrawIt {

	public static final int WIDTH = 600;
	public static final int HEIGHT = 400;

	private static final int NUM_CITIES = 10;
	private static final int CITY_SIZE = 10;
	private static final int SLEEP_TIME = 200;

	private City [] currentSearch;

	private JFrame f;
	private DrBsCanvas c;
	private JTextField numCitiesField;

	public DrawIt() {

		f = new JFrame("Dr Bs City Demo");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize((int)(WIDTH*1.2), (int)(HEIGHT*1.2));

		c = new DrBsCanvas();
		remakeCities(DrawIt.NUM_CITIES);


		f.setLayout(new BorderLayout());
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(new JLabel("Number of cities"));
		numCitiesField = new JTextField(NUM_CITIES + "", 5);
		buttonPanel.add(numCitiesField);

		JButton redraw = new JButton("(Re)make Map");
		redraw.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int x = Integer.parseInt(numCitiesField.getText());
				remakeCities(x);	
			}
		});
		buttonPanel.add(redraw);

		JButton travel = new JButton("Search");
		travel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				c.clearSearches();
				c.addSearchStep(currentSearch);
				ArrayList<City []> thingsToDisplay = Search.search(currentSearch);
				if(thingsToDisplay != null && thingsToDisplay.size() > 0) {
					for(int i = 0; i < thingsToDisplay.size(); i++) {
						c.addSearchStep(thingsToDisplay.get(i));
					}
				}
				else {
					c.addSearchStep(null);
				}
			}
		});
		buttonPanel.add(travel);


		f.add(buttonPanel, BorderLayout.NORTH);
		f.add(c, BorderLayout.CENTER);


		f.setVisible(true);
	}

	public void remakeCities(int numCities) {
		currentSearch = new City[numCities];
		for(int i = 0; i < numCities; i++) {
			currentSearch[i] = new City();
		}
		c.clearSearches();
		c.addSearchStep(currentSearch);
	}

	public void addPossibleSollution(City [] c) {
		this.c.addSearchStep(c);
	}


	@SuppressWarnings("serial")
	class DrBsCanvas extends JPanel{
		private Queue<City []> thingsToDraw;

		public DrBsCanvas() {
			Dimension d = new Dimension(WIDTH,HEIGHT);
			super.setSize(d);
			super.setMinimumSize(d);
			super.setPreferredSize(d);
			this.setDoubleBuffered(true);

			thingsToDraw = new LinkedList<City []>();
		}

		public void clearSearches() {
			thingsToDraw = new LinkedList<City []>();
		}


		public void addSearchStep(City[] c) {
			thingsToDraw.add(c);
			repaint();
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);

			if(thingsToDraw.size() > 0) {
				City [] c = thingsToDraw.remove();
				if(c != null && c.length >= 2) {
					//erase the background
					g.setColor(Color.RED);
					g.fillRect(0, 0, this.getWidth(), this.getHeight());
					//draw the connections
					g.setColor(Color.BLUE);
					for(int i = 1; i < c.length; i++) {
						City a = c[i-1];
						City b = c[i];
						g.drawLine(a.getX(), a.getY(), b.getX(), b.getY());
					}
					//draw the final connection
					g.drawLine(c[0].getX(), c[0].getY(), c[c.length-1].getX(), c[c.length-1].getY());

					//Draw the cities
					g.setColor(Color.BLACK);
					int halfCity = CITY_SIZE/2;
					for(int i = 0; i < c.length; i++) {
						City a = c[i];
						g.fillOval(a.getX()-halfCity, a.getY()-halfCity, CITY_SIZE, CITY_SIZE);
					}

					try {
						Thread.sleep(SLEEP_TIME);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else {
					//erase the background
					g.setColor(Color.CYAN);
					g.fillRect(0, 0, this.getWidth(), this.getHeight());					
				}
				
				if(thingsToDraw.size() > 0) {
					repaint();
				}
			}
			else {
				//erase the background
				g.setColor(Color.CYAN);
				g.fillRect(0, 0, this.getWidth(), this.getHeight());
			}
		}

	}

	public static void main(String[] args) {

		new DrawIt();

	}

}
