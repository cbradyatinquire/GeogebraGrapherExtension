package geographer;

import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;


import geogebra.plugin.GgbAPI;

import org.nlogo.api.*;
import org.nlogo.app.App;

public class GrapherExtension extends DefaultClassManager {

	public static HashMap<String, GrapherBundle> graphers = new HashMap<String, GrapherBundle>();
	public static JPanel geogebraTab = new JPanel( new GridLayout(3,0) );

	
	public GgbAPI getGgbAPI( String gname )
	{
		if (graphers.containsKey(gname) ) {
			return graphers.get(gname).getGeoGebraAPI();
		}
		return null; 
	}
	
	
	@Override
	public void load(PrimitiveManager primManager) throws ExtensionException {
		App.app().tabs().addTab("WOW", geogebraTab );
		
		primManager.addPrimitive("redraw", new RedrawGraphers() );
		primManager.addPrimitive("new-grapher-window", new NewGrapherWindow() );
		primManager.addPrimitive("set-grapher-location", new SetGrapherLocation() );
		primManager.addPrimitive("set-grapher-size", new SetGrapherSize() );	
		primManager.addPrimitive("set-grapher-window-range", new SetGrapherWindowRange() );
		primManager.addPrimitive("add-equation", new AddEquation() );
		
	}
	
	
	
	
	public static class RedrawGraphers extends DefaultCommand {

		public Syntax getSyntax() {
			return Syntax.commandSyntax(new int[] {});
		}
		
		@Override
		public void perform(Argument[] args, final Context ctxt)
				throws ExtensionException, LogoException {

			for ( GrapherBundle grapher : graphers.values() )
			{
				grapher.refreshGImage();
			}
			
			SwingUtilities.invokeLater( new Runnable() {
				public void run() { 
					BufferedImage bi = ctxt.getDrawing();
					Graphics g = bi.getGraphics();
					for ( GrapherBundle grapher : graphers.values() )
					{
						if ( grapher.getShowing() )
						{
							//grapher.refreshGImage();
							//Graphics gprimed = g.create(grapher.getLeft(), grapher.getTop(), grapher.getWidth(), grapher.getHeight());
							//grapher.paintOnto(gprimed);
							Image i = grapher.getImage();
							if (i != null )
								g.drawImage( i,  grapher.getLeft(), grapher.getTop(), grapher.getWidth(), grapher.getHeight(), null);
						}
					}
				}
			} );
			
		}
	
	}
	
	
	public static class NewGrapherWindow extends DefaultCommand {

		public Syntax getSyntax() {
			return Syntax.commandSyntax(new int[] { Syntax.StringType() });
		}
		
		@Override
		public void perform(Argument[] args, Context ctxt)
				throws ExtensionException, LogoException {

			String name = args[0].getString();
			GrapherBundle bundle = new GrapherBundle( name, geogebraTab );
			graphers.put( bundle.getName(), bundle );
		}
	
	}
	
	public static class SetGrapherLocation extends DefaultCommand {

		public Syntax getSyntax() {
			return Syntax.commandSyntax(new int[] { Syntax.StringType(), Syntax.NumberType(), Syntax.NumberType() });
		}
		
		@Override
		public void perform(Argument[] args, Context ctxt)
				throws ExtensionException, LogoException {

			String name = args[0].getString();
			int left = args[1].getIntValue();
			int top = args[2].getIntValue();
			GrapherBundle gb = graphers.get( name );
			if (gb != null)
			{
				gb.setLeft(left);
				gb.setTop(top);
			}
		}
	
	}
	
	
	
	public static class SetGrapherSize extends DefaultCommand {

		public Syntax getSyntax() {
			return Syntax.commandSyntax(new int[] { Syntax.StringType(), Syntax.NumberType(), Syntax.NumberType() });
		}
		
		@Override
		public void perform(Argument[] args, Context ctxt)
				throws ExtensionException, LogoException {

			String name = args[0].getString();
			int width = args[1].getIntValue();
			int height = args[2].getIntValue();
			if ( width <= 0 || height <= 0 || name == null) { throw new ExtensionException( "DIMENSION ERROR: graph name="+name+", request for width="+width+"; height="+height); }
			else
			{
				GrapherBundle gb = graphers.get( name );
				if (gb != null)
				{
					gb.setWidHeight(width, height);
				}
			}
		}
	
	}
	
	public static class SetGrapherWindowRange extends DefaultCommand {

		public Syntax getSyntax() {
			return Syntax.commandSyntax(new int[] { Syntax.StringType(), 
					Syntax.NumberType(), Syntax.NumberType(), Syntax.NumberType(), 
					Syntax.NumberType(), Syntax.NumberType(), Syntax.NumberType() });
		}
		
		@Override
		public void perform(Argument[] args, Context ctxt)
				throws ExtensionException, LogoException {

			String name = args[0].getString();
			
			double xmin = args[1].getDoubleValue();
			double xmax = args[2].getDoubleValue();
			double xscl = args[3].getDoubleValue();
			
			double ymin = args[4].getDoubleValue();
			double ymax = args[5].getDoubleValue();
			double yscl = args[6].getDoubleValue();
			
			GrapherBundle gb = graphers.get( name );
			if (gb != null)
			{
				gb.setCoords(xmin, xmax, xscl, ymin, ymax, yscl);
			}
		}
	
	}
	
	
	public static class AddEquation extends DefaultCommand  {

		public Syntax getSyntax() {
			return Syntax.commandSyntax(new int[] { Syntax.StringType(), Syntax.StringType(), Syntax.StringType() });
		}
		
		@Override
		public void perform(Argument[] args, Context ctxt)
				throws ExtensionException, LogoException {

			String gname = args[0].getString();
			String eqid = args[1].getString();
			String eqdef = args[2].getString();
			
			GrapherBundle gb = graphers.get( gname );
			if (gb != null)
			{
				try {
				gb.addEquation(eqid, eqdef);
				}
				catch (Exception e )
				{
					throw new ExtensionException("Exception on attempting to graph id=" + eqid + "; eq def="+eqdef+"; in grapher "+gname+".",e );
				}
			}
		}
	
	}
	

}
