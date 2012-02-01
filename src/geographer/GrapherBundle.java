package geographer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import geogebra.GeoGebraPanel;
import geogebra.plugin.GgbAPI;
import geographer.GrapherExtension.TabPanel;

public class GrapherBundle {

	private String name = "default";
	private GeoGebraPanel grapher;
	public GeoGebraPanel getPanel() { return grapher; }
	private boolean showing = true; 
	
	private int top = 0;
	private int left = 40;
	public int getTop() {	return top; }
	public void setTop(int top) {		this.top = top;}
	public int getLeft() {		return left; }
	public void setLeft(int left) {		this.left = left; }
	
	private int physWid = 1000;
	private int physHt = 1030;
	public void setWidHeight( int wid, int ht ) { 
		physWid = wid;
		physHt = ht;
		grapher.setBounds(0, 0, physWid, physHt + 30);
		getGeoGebraAPI().setCoordSystem(coords[0], coords[1], coords[2], coords[3]);
		//refreshGImage();
	}
	public int getWidth() { return physWid; }
	public int getHeight() { return physHt; }
	
	private boolean ready = false;
	

	
	//private BufferedImage gimage;
	//public BufferedImage getImage() { if (gimage == null) { refreshGImage(); } return gimage; }
	private Image gimage;
	public Image getImage() { if (gimage == null) { refreshGImage(); } return gimage; }
	
	public String getName() { return name; }
	public void setName( String nname ) { name = nname; }
	public boolean getShowing() { return showing; }
	public void setShowing( boolean toshow) { showing = toshow; }
	
	private double[] coords = {-10.0, 10.0, -10.0, 10.0 };
	public double[] getCoords() { return coords; }
	private double xscl, yscl;
	public void setCoords( double xmin, double xmax, double xscl, double ymin, double ymax, double yscl )
	{
		coords[0] = xmin;
		coords[1] = xmax;
		coords[2] = ymin;
		coords[3] = ymax;
		this.xscl = xscl;
		this.yscl = yscl;
		getGeoGebraAPI().getApplication().getEuclidianView().setAutomaticAxesNumberingDistance(false, 0);
		getGeoGebraAPI().getApplication().getEuclidianView().setAutomaticAxesNumberingDistance(false, 1);
		getGeoGebraAPI().getApplication().getEuclidianView().setAxesNumberingDistance(xscl, 0);
		getGeoGebraAPI().getApplication().getEuclidianView().setAxesNumberingDistance(yscl, 1);
		getGeoGebraAPI().setCoordSystem(coords[0], coords[1], coords[2], coords[3]);
		
		//refreshGImage();
	}
		
	public void updateBounds()
	{
		grapher.setBounds(left, top, physWid, physHt);
	}

	public GrapherBundle( String name, final TabPanel pane )
	{
		if (name != null )  //send in null for default name
			this.name = name;
		grapher = new GeoGebraPanel();
		grapher.setBackground(Color.DARK_GRAY);
		grapher.setBounds(0, 0, physWid, physHt);
		grapher.setShowAlgebraView(false);
		grapher.setShowMenubar(false);
		grapher.setShowAlgebraInput(false);
		grapher.setShowToolbar(false);
		grapher.setShowSpreadsheetView(false);
		getGeoGebraAPI().setErrorDialogsActive(false);
		
		grapher.getGeoGebraAPI().getApplication().getEuclidianView().setAutomaticAxesNumberingDistance(true, 0);
		grapher.getGeoGebraAPI().getApplication().getEuclidianView().setAutomaticAxesNumberingDistance(true, 1);
		getGeoGebraAPI().setCoordSystem(coords[0], coords[1], coords[2], coords[3]);
		grapher.buildGUI();
		
		
		
		SwingUtilities.invokeLater( new Runnable() {
			public void run() { 
				setWidHeight(physWid,physHt);
				ready = true;
				pane.addGrapherPane(grapher);
			}});
	}
		
	
	public void addEquation( String identifier, String definition )
	{
		getGeoGebraAPI().evalCommand(identifier+"="+definition);
		refreshGImage();
	}
	
	
	public GgbAPI getGeoGebraAPI()
	{
		return grapher.getGeoGebraAPI();
	}
	
	
	public void refreshGImage( )
	{
		if (ready )
		{
			int wid = grapher.getGeoGebraAPI().getApplication().getEuclidianView().getWidth();
			int ht = grapher.getGeoGebraAPI().getApplication().getEuclidianView().getHeight();
			if (wid > 0 && ht > 0)
				gimage = grapher.getGeoGebraAPI().getApplication().getEuclidianView().getExportImage(1.0);
		}
	}
	
	
}
