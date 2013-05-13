package uibk.autonom.ps.navigation;

import org.opencv.core.Point;
import org.opencv.core.Scalar;

public class Marker {
	private Scalar color;
	/**
	 * Point corresponding to virtual system
	 * 
	 * 			100
	 * -------------------------------------|
	 * |1               2                  3|
	 * |                                    | 100
	 * |6               5                  4|
	 * -------------------------------------
	 */
	private Point position;
	
	public Marker(Scalar color, Point position){
		this.color = color;
		this.position = position;
	}
	
	
}
