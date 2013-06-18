package uibk.autonom.ps.navigation;

import org.opencv.core.Scalar;

public interface FinishedNavigationAction {
	public void startAction(Navigator navigator);
	public void setTrackColor(Scalar color);
}
