package websocketTest;


public interface DrivetrainInterface {
    /**
     * Periodic update call
     */
    public void update();

    /**
     * Command drivetrain with open-loop commands (ie from driver joysticks)
     * @param fwdRevCmd Forward reverse command = 0 is stop, 1 is full forward, -1 is full reverse
     * @param rotateCmd Rotation command = 0 is stop, 1 is full left turn, -1 is full right turn
     */
    public void setOpenLoopCmd(double fwdRevCmd, double rotateCmd);

    public void setClosedLoopCmd(double leftDtSpeed_ftpersec, double rightDtSpeed_ftpersec);

    public void setClosedLoopCmd(double leftSpeed_ftpersec, double rightSpeed_ftpersec, double desHeading_deg);

    public void setRotateInPlaceHeadingTgt(double desHeading_deg);

    public double getActLeftSpeed_ftpersec();
    public double getActRightSpeed_ftpersec();
    public double getActHeading_deg();
    
    public double getHeadingErr_deg();
}