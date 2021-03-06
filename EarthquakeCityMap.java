package module3;

//Java utilities libraries
import java.util.ArrayList;
import java.util.List;

//Processing library
import processing.core.PApplet;

//Unfolding libraries
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.providers.Microsoft;
import de.fhpotsdam.unfolding.utils.MapUtils;

//Parsing library
import parsing.ParseFeed;

/** EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 * Author: UC San Diego Intermediate Software Development MOOC team
 * @author Kamilė Medzevičiūtė
 * Date: July 17, 2015
 * */
public class EarthquakeCityMap extends PApplet {

	private static final long serialVersionUID = 1L;

	private static final boolean offline = false;
	
	// Less than this threshold is a light earthquake
	public static final float THRESHOLD_MODERATE = 5;
	// Less than this threshold is a minor earthquake
	public static final float THRESHOLD_LIGHT = 4;

	public static String mbTilesString = "blankLight-1-3.mbtiles";
	
	// The map
	private UnfoldingMap map;
	
	//feed with magnitude 2.5+ Earthquakes
	private String earthquakesURL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";

	
	public void setup() {
		size(950, 600, OPENGL);

		if (offline) {
		    map = new UnfoldingMap(this, 200, 50, 700, 500, new MBTilesMapProvider(mbTilesString));
		    earthquakesURL = "2.5_week.atom"; 	// Same feed, saved Aug 7, 2015, for working offline
		}
		else {
			map = new UnfoldingMap(this, 200, 50, 700, 500, new Microsoft.HybridProvider()); 
		}
		
	    map.zoomToLevel(2);
	    MapUtils.createDefaultEventDispatcher(this, map);	
			
	    List<Marker> markers = new ArrayList<Marker>();
	    List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	    
	    for (PointFeature eq: earthquakes) {
          SimplePointMarker marker = createMarker(eq);    
          markers.add(marker);
        }

        map.addMarkers(markers);
	}
		
	private SimplePointMarker createMarker(PointFeature feature)
	{  
		SimplePointMarker marker = new SimplePointMarker(feature.getLocation());
		
		Object magObj = feature.getProperty("magnitude");
		float mag = Float.parseFloat(magObj.toString());
		
		int yellow = color(255, 255, 0);
	    int blue = color(0, 100, 200);
	    int red = color(255, 0, 0);
		
		if (mag >= THRESHOLD_MODERATE) {
	        marker.setColor(red);
	        marker.setRadius(16);
	    } else if (mag >= THRESHOLD_LIGHT){
	        marker.setColor(yellow);
	        marker.setRadius(10);
	    } else {
	        marker.setColor(blue);
	        marker.setRadius(6);
	    }
	    
	    return marker;
	}
	
	public void draw() {
	    background(10);
	    map.draw();
	    addKey();
	}

	private void addKey() 
	{	
		//rectangular
	    fill(255, 255, 200);
	    rect(25, 50, 150, 250);
	    // heading
	    textSize(12);
	    fill(0, 0, 0);
	    text("Earthquake Key", 50, 70);
	    // marker1
	    fill(255, 0, 0);
	    ellipse(40, 130, 16, 16);
	    // text1
	    textSize(10);
	    fill(0, 0, 0);
	    text("5.0+ Magnitude", 80, 135);
	    // marker2
	    fill(255, 255, 0);
        ellipse(40, 180, 10, 10);
        // text2
        fill(0, 0, 0);
        text("4.0+ Magnitude", 80, 185);
        // marker3
        fill(0, 100, 200);
        ellipse(40, 230, 6, 6);
        // text 3
        fill(0, 0, 0);
        text("Below 4.0", 80, 235);
	
	}
}
