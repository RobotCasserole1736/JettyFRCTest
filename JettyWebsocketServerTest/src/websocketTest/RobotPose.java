package websocketTest;

import org.usfirst.frc.team1736.lib.WebServer.CasseroleRobotPoseView;

public class RobotPose {

	public double leftVelosity_RPM;
	public double rightVelosity_RPM;
	public final double wheelRadius_In = 3;
	public double poseX;
	public double poseY;
	
	public void setLeftMotorSpeed(double speed) {
		leftVelosity_RPM = speed;
	}

	public void setRightMotorSpeed(double speed){
		rightVelosity_RPM = speed;
    }
	
	public void update() {
		double leftVelosity_FPS = leftVelosity_RPM * (2*3.14*wheelRadius_In / 60);
		double rightVelosity_FPS = rightVelosity_RPM * (2*3.14*wheelRadius_In / 60);
		
		
		//poseX = 0.02 * leftVelosity_FPS;
		poseY = 0.02 * rightVelosity_FPS;
		CasseroleRobotPoseView.setRobotPose(poseX, poseY, 0);
		}
	
}
