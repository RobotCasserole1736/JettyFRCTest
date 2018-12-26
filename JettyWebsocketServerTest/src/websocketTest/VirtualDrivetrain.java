package websocketTest;

public class VirtualDrivetrain implements DrivetrainInterface {

    public DrivetrainMode mode;

    public double leftMotorCmd;
    public double rightMotorCmd;

    public double leftMotorSpeed_ftpsec;
    public double rightMotorSpeed_ftpsec;

    public double leftCmdMotorSpeed_ftpsec;
    public double rightCmdMotorSpeed_ftpsec;

    public double heading_deg;
    public double headingDes_deg;

    //Constants for a very crude aproximation of a drivetrain plant.
    public final double LOOP_TIME_S = 0.02; //20ms update loop
    public final double MAX_VEL_FTPSEC = 15; //robot can't go faster than 15 ft/sec
    public final double TIME_TO_MAX_VEL_S = 3; //From a standstill, takes 3 seconds to get to max speed, best case scenario.
    public final double FRICTIONAL_DECAY_FACTOR_PER_SEC = 0.5; //Every second of no power applied, the drivetrain looses half its velocity
    public final double VIRTUAL_DRIVEBASE_WIDTH_FT = 2.0; //The effective distance between left and right wheels, as it relates to rotating the robot.

    //Calculated constants
    public final double MAX_VEL_DELTA_PER_LOOP = MAX_VEL_FTPSEC/TIME_TO_MAX_VEL_S/LOOP_TIME_S;
    public final double FRICTIONAL_DECAY_FACTOR_PER_LOOP = FRICTIONAL_DECAY_FACTOR_PER_SEC / LOOP_TIME_S;

    public VirtualDrivetrain(){
        mode = DrivetrainMode.OPEN_LOOP;
        leftMotorCmd = 0;
        rightMotorCmd = 0;
        heading_deg = 0;

    }

    @Override
    public void update() {
        switch(mode){
            case OPEN_LOOP:
                //VERY Rough model of what's happening in the drivetrain. Some would say unacceptably inaccurate.
                //Decay speed due to friction
                leftMotorSpeed_ftpsec  *= FRICTIONAL_DECAY_FACTOR_PER_LOOP;
                rightMotorSpeed_ftpsec *= FRICTIONAL_DECAY_FACTOR_PER_LOOP;

                //Change speed due to motor command
                leftMotorSpeed_ftpsec  += leftMotorCmd * MAX_VEL_DELTA_PER_LOOP;
                rightMotorSpeed_ftpsec += rightMotorCmd * MAX_VEL_DELTA_PER_LOOP;

                //Cap motor speed at max
                leftMotorSpeed_ftpsec = limit(leftMotorSpeed_ftpsec, MAX_VEL_FTPSEC);
                rightMotorSpeed_ftpsec = limit(rightMotorSpeed_ftpsec, MAX_VEL_FTPSEC);

                //Update Heading
                heading_deg += (rightMotorSpeed_ftpsec - leftMotorSpeed_ftpsec)/2 * Math.PI * VIRTUAL_DRIVEBASE_WIDTH_FT * LOOP_TIME_S;

            break;
            case CLOSED_LOOP_SEEK_TO_HEADING:

                if(headingDes_deg - heading_deg > 0.5){
                    leftMotorSpeed_ftpsec = -1.0*MAX_VEL_FTPSEC;
                    rightMotorSpeed_ftpsec = MAX_VEL_FTPSEC;
                } else if (headingDes_deg - heading_deg < -0.5){
                    leftMotorSpeed_ftpsec = MAX_VEL_FTPSEC;
                    rightMotorSpeed_ftpsec = -1.0*MAX_VEL_FTPSEC;
                } else {
                    leftMotorSpeed_ftpsec = 0;
                    rightMotorSpeed_ftpsec = 0;
                }
                //Update Heading
                heading_deg += (rightMotorSpeed_ftpsec - leftMotorSpeed_ftpsec)/2 * Math.PI * VIRTUAL_DRIVEBASE_WIDTH_FT * LOOP_TIME_S;

            break;
            case CLOSED_LOOP_VELOCITY:

                //Update Heading
                heading_deg += (rightMotorSpeed_ftpsec - leftMotorSpeed_ftpsec)/2 * Math.PI * VIRTUAL_DRIVEBASE_WIDTH_FT * LOOP_TIME_S;

            break;
            case CLOSED_LOOP_VELOCITY_WITH_HEADING:
                //Nothing to do, all calcualtion done when closed loop commands are set.
            break;
            default:
                //Default to no calculation?
            break;
        }
    }

    @Override
    public void setOpenLoopCmd(double fwdRevCmd, double rotateCmd) {
        mode = DrivetrainMode.OPEN_LOOP;
        leftMotorCmd = limit(fwdRevCmd - rotateCmd);
        rightMotorCmd = limit(fwdRevCmd + rotateCmd);
    }

    @Override
    public void setClosedLoopCmd(double leftDtSpeedCmd_ftpersec, double rightDtSpeedCmd_ftpersec) {
        mode = DrivetrainMode.CLOSED_LOOP_VELOCITY;
        //Cap motor speed at max
        leftMotorSpeed_ftpsec = limit(leftDtSpeedCmd_ftpersec, MAX_VEL_FTPSEC);
        rightMotorSpeed_ftpsec = limit(rightDtSpeedCmd_ftpersec, MAX_VEL_FTPSEC);
    }

    @Override
    public void setClosedLoopCmd(double leftSpeedCmd_ftpersec, double rightSpeedCmd_ftpersec, double desHeading_deg) {
        mode = DrivetrainMode.CLOSED_LOOP_VELOCITY_WITH_HEADING;

    }

    @Override
    public void setRotateInPlaceHeadingTgt(double desHeading_deg) {
        mode = DrivetrainMode.CLOSED_LOOP_SEEK_TO_HEADING;

    }

    @Override
    public double getActLeftSpeed_ftpersec() {
        return leftMotorSpeed_ftpsec;
    }

    @Override
    public double getActRightSpeed_ftpersec() {
        return rightMotorSpeed_ftpsec;
    }

    @Override
    public double getActHeading_deg() {
        return heading_deg;
    }

    @Override
    public double getHeadingErr_deg() {
        return heading_deg - headingDes_deg;
    }

    private double limit(double in){
        return limit(in, -1.0, 1.0);
    }

    private double limit(double in, double mag){
        return limit(in, -mag, mag);
    }

    private double limit(double in, double lower, double upper){
        if(in > upper){
            return upper;
        } else if (in < lower) {
            return lower;
        } else {
            return in;
        }
            
    }

}