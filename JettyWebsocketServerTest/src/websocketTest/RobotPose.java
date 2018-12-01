package websocketTest;

import org.usfirst.frc.team1736.lib.DataServer.Signal;

/**
 * 
 * The following coordinate system is used:
 * 
 *   Field Centerline
 *   |
 *   V 
 * 
 * 
 *   ^ Field Y+
 *   |                    _____________________________                             
 *   |                    |      Bumper Length        |                             
 *   |                    V                           V                             
 *   |                                                                              
 *   |                    =============================                       <-|
 *   |                    |    O                 O    |       <-|               |           
 *   |                    |                           |         |               |   
 *   |                Rear|      Robot +----> Y+      |Front    |Wheel Base     | Bumper Width
 *   |                    |            |              |         |               |      
 *   |                    |    O       V X+      O    |       <-|               |                  
 *   |                    =============================                       <-|     
 *   |                                                                              
 *   |                                                                              
 *   |                                                                              
 *   |                                                                              
 *<--+------------------------------------------------------------------> Field X+    <- Your alliance wall
 *   |
 *   V
 * Positive field Theta (T)   is defined as rotation from X+ to Y+. 0 Theta points along positive X axis. 
 * Field->Robot theta defined as angle from Field X axis to Robot X axis.
 * Robot drawn at T = 270deg.
 * 

 */

public class RobotPose {

    //Robot Physical Constants
    public final double wheelRadius_In = 3;
    public final double WHEEL_BASE_WIDTH_FT = 1.75;
    public final double BUMPER_WIDTH_FT = 2.0;
    public final double SIDE_LINEAR_DISTANCE_PER_ROBOT_ROTATION_FT = WHEEL_BASE_WIDTH_FT*Math.PI; //account for wheel scrub on rotation here.
    public final double BUMPER_LENGTH_FT = 2.5;

    //Field physical Constants  - model as a rectangle for now
    public final double FIELD_UPPER_BOUNDARY_FT = 54.0;
    public final double FIELD_LOWER_BOUNDARY_FT = 0.0;
    public final double FIELD_LEFT_BOUNDARY_FT  = -13.47;
    public final double FIELD_RIGHT_BOUNDARY_FT = 13.47;

    //Robot State
    public double leftWheelSpeed_RPM;
    public double rightWheelSpeed_RPM;
    public double poseX;
    public double poseY;
    public double poseT;

    //Simulation Timing
    double prevLoopTime = 0;
    double delta_t_sec = 0.02;

    //Simulation outputs
    Signal poseXSig;
    Signal poseYSig;
    Signal poseTSig;

    public RobotPose(){
        poseXSig = new Signal("botposex", "ft");
        poseYSig = new Signal("botposey", "ft");
        poseTSig = new Signal("botposeT", "deg");
        prevLoopTime = System.currentTimeMillis();

        //Default Field start position
        poseX = 0;
        poseY = 10;
        poseT = 0;
    }
    
    public void setLeftWheelSpeed_RPM(double speed) {
        leftWheelSpeed_RPM = speed;
    }

    public void setRightWheelSpeed_RPM(double speed){
        rightWheelSpeed_RPM = speed;
    }
    
    /**
     * Slew the robot instantaneously to a new position. Usefule at the start of autonomous to
     * reset the simulation to have the robot at the proper start position.
     * @param field_x
     * @param field_y
     * @param field_t
     */
    public void setRobotPose(double field_x, double field_y, double field_t){
        poseX = field_x;
        poseY = field_y;
        poseT = field_t;
    }
    
    public void update() {
        double sampleTime = System.currentTimeMillis();

        //double delta_t_sec = (sampleTime - prevLoopTime)*0.001; //Accounts for actual variation in real-time execution
        delta_t_sec = 0.02; //Assumes perfect real-time system.
        
        updatePoseFromWheelSpeeds();
        handleFieldColission();

        //Output to webpage
        poseXSig.addSample(sampleTime, poseX);
        poseYSig.addSample(sampleTime, poseY);
        poseTSig.addSample(sampleTime, poseT);
        prevLoopTime = sampleTime;
    }
    
    private void updatePoseFromWheelSpeeds(){
        //Robot frome velocity
        double leftVelocity_FPS = leftWheelSpeed_RPM * (2*3.14*wheelRadius_In / 12.0  / 60.0);
        double rightVelocity_FPS = rightWheelSpeed_RPM * (2*3.14*wheelRadius_In / 12.0 / 60.0);
        
        //Tank-drive robot frame displacement
        double delta_y_robot_ft = (leftVelocity_FPS + rightVelocity_FPS)/2 *delta_t_sec;
        double delta_x_robot_ft = 0;
        double delta_t_robot_deg= ((-1.0 * leftVelocity_FPS) + rightVelocity_FPS) * delta_t_sec * (1/SIDE_LINEAR_DISTANCE_PER_ROBOT_ROTATION_FT) * 360.0;
        
        //Transform to field coordinates
        poseX += cos(poseT)*delta_x_robot_ft + -1.0*sin(poseT)*delta_y_robot_ft;
        poseY += sin(poseT)*delta_x_robot_ft +      cos(poseT)*delta_y_robot_ft;
        poseT += delta_t_robot_deg;
    }
    
    private void handleFieldColission(){

        //Helper calculations for distance from robot center out to sides
        final double dx = BUMPER_WIDTH_FT/2.0;
        final double dy = BUMPER_LENGTH_FT/2.0;
        final double ndx = -1.0*dx;
        final double ndy = -1.0*dy;

        //Calculate verticie locations using 2d rotation formulae https://academo.org/demos/rotation-about-point/
        double FL_Corner_X = poseX + ( ndx*cos(poseT) -  dy*sin(poseT) );
        double FL_Corner_Y = poseY + (  dy*cos(poseT) + ndx*sin(poseT) );
        double FR_Corner_X = poseX + (  dx*cos(poseT) -  dy*sin(poseT) );
        double FR_Corner_Y = poseY + (  dy*cos(poseT) +  dx*sin(poseT) );
        double RL_Corner_X = poseX + ( ndx*cos(poseT) - ndy*sin(poseT) );
        double RL_Corner_Y = poseY + ( ndy*cos(poseT) + ndx*sin(poseT) );
        double RR_Corner_X = poseX + (  dx*cos(poseT) - ndy*sin(poseT) );
        double RR_Corner_Y = poseY + ( ndy*cos(poseT) +  dx*sin(poseT) );
 
        //The extrema of the verticiecs forms the bounding box of the robot
        double robotFrontBounds = max4(FL_Corner_Y, FR_Corner_Y, RL_Corner_Y, RR_Corner_Y);
        double robotRearBounds  = min4(FL_Corner_Y, FR_Corner_Y, RL_Corner_Y, RR_Corner_Y);
        double robotRightBounds = max4(FL_Corner_X, FR_Corner_X, RL_Corner_X, RR_Corner_X);
        double robotLeftBounds  = min4(FL_Corner_X, FR_Corner_X, RL_Corner_X, RR_Corner_X);

        //If the corresponding side of the boundary box exceeds the field boundary, the robot is in colission with a wall.

        if(robotFrontBounds > FIELD_UPPER_BOUNDARY_FT){
            //Robot colliding with opposing alliance wall
            poseY -= (robotFrontBounds - FIELD_UPPER_BOUNDARY_FT); //Reset bot within field
            //System.out.println("Colission with upper field boundary");
        }

        if(robotRearBounds < FIELD_LOWER_BOUNDARY_FT){
            //Robot colliding with your alliance wall
            poseY += (FIELD_LOWER_BOUNDARY_FT - robotRearBounds); //Reset bot within field
            //System.out.println("Colission with Lower field boundary");
        }

        if(robotRightBounds > FIELD_RIGHT_BOUNDARY_FT){
            //Robot colliding with opposing alliance wall
            poseX -= (robotRightBounds - FIELD_RIGHT_BOUNDARY_FT); //Reset bot within field
            //System.out.println("Colission with Right field boundary");
        }

        if(robotLeftBounds < FIELD_LEFT_BOUNDARY_FT){
            //Robot colliding with opposing alliance wall
            poseX += (FIELD_LEFT_BOUNDARY_FT - robotLeftBounds); //Reset bot within field
            //System.out.println("Colission with Left field boundary");
        }
    }
    

    //Utility Math helper functions
    private double cos(double in_deg){
        return Math.cos(in_deg*Math.PI/180.0);
    }

    private double sin(double in_deg){
        return Math.sin(in_deg*Math.PI/180.0);
    }

    private double max4(double in1, double in2, double in3, double in4){
        return Math.max(in1, Math.max(in2, Math.max(in3, in4)));
    }

    private double min4(double in1, double in2, double in3, double in4){
        return Math.min(in1, Math.min(in2, Math.min(in3, in4)));
    }
    
}
