package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.FtcRobotControllerActivity;
import com.qualcomm.ftcrobotcontroller.R;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.robocol.Telemetry;

/**
 * Created by Dylan on 11/21/15.
 */
public class dylansTeleOp extends OpMode {

    DcMotor rf;
    DcMotor rb;
    DcMotor lf;
    DcMotor lb;
    DcMotor lift2;
    DcMotor lift1;
    DcMotor harvester;
    boolean pickup = false;
    //Servo hook1;
    Servo hook2;
    Servo basket;
    Servo door;
    int harvstart = 0;
    boolean harvrun = false;
    boolean isMacroRunning = false;
    int highzone = 1400;
    int midzone = 1000;
    int lowzone = 799;

    @Override
    public void init() {
        rf = hardwareMap.dcMotor.get("rf"); //right
        rb = hardwareMap.dcMotor.get("rb");
        lf = hardwareMap.dcMotor.get("lf"); //left
        lb = hardwareMap.dcMotor.get("lb");
        lift1 = hardwareMap.dcMotor.get("lift"); //RIGHT lift
        lift2 = hardwareMap.dcMotor.get("lift2"); //LEFT lift
        harvester = hardwareMap.dcMotor.get("harvester");
        lift2.setDirection(DcMotor.Direction.REVERSE);
        //hook1 = hardwareMap.servo.get("hook1"); //servo hooks
        hook2 = hardwareMap.servo.get("hook2");
        basket = hardwareMap.servo.get("basket"); //basket servo
        door = hardwareMap.servo.get("door");
        //hook1.setPosition(1.0);
//        hook2.setPosition(0.7);
        /*
        lift1.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
        lift2.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
        lift1.setChannelMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        lift2.setChannelMode(DcMotorController.RunMode.RUN_USING_ENCODERS);*/
    }

    @Override
    public void loop()
    {
        if(!isMacroRunning) {
            float left = gamepad1.left_stick_y;
            float right = gamepad1.right_stick_y;
            float right2 = gamepad2.right_stick_y;
            float left2 = gamepad2.left_stick_x;
            boolean grab = gamepad1.right_bumper;
            boolean reverse = gamepad1.left_bumper;
            boolean rb2 = gamepad2.right_bumper;
            boolean x = gamepad1.x;
            int harvend = 60;
            //float rekt = gamepad1.right_trigger;
            //Add variables for other buttons

            //harvester toggle
            if (grab == true) {
                harvrun = true;
            }
            if(harvrun)
                harvstart+=1;
            if (harvstart >= harvend) {
                pickup = !pickup;
                harvstart = 0;
                harvrun = false;
            }
            if (pickup == true) {
                harvester.setPower(1.0);
            } else if (pickup == false)
                harvester.setPower(0.0);
            if (reverse == true)
                harvester.setPower(-1.0);

            //basket door
            if (rb2 == true) {
                if (door.getPosition() < 0.2)
                    door.setPosition(0.9);
                else if (door.getPosition() > 0.4)
                    door.setPosition(0.0);
            }

            //servo hooks
            if (x) {
                if (hook2.getPosition() < 0.4) {
                    hook2.setPosition(1 - 0.5);
                    //hook1.setPosition(0.6);
                } else {
                    hook2.setPosition(1 - 0.65);
                    //hook1.setPosition(1.0);
                }
            }



            //drive train
            rb.setPower(right);
            rf.setPower(right);
            lb.setPower(-left);
            lf.setPower(-left);

            //lift NO MACROS
            lift1.setPower(right2);
            lift2.setPower(right2);
            //automatically open/close basket door
            //if(lift1.getCurrentPosition() > 420 && lift1.getPower() > 0)
            //door.setPosition(0.8);
            //else if(lift1.getCurrentPosition() < 1300 && lift1.getPower() < 0)
            //basket.setPosition(0.0);

            //lift macros
            if (gamepad2.x) //high
            {
                isMacroRunning= true;
                liftmacro(highzone);
                isMacroRunning = false;
            } else if (gamepad2.y) //mid
            {
                //Add isMacroRunning
                isMacroRunning = true;
                liftmacro(midzone);
                isMacroRunning = false;
            } else if (gamepad2.b) //low
            {
                isMacroRunning = true;
                liftmacro(lowzone);
                isMacroRunning = false;
            }

            telemetry.addData("Text", "*** Robot Data***");
            telemetry.addData("Lift1", "" + lift1.getCurrentPosition());
            telemetry.addData("Lift2", "" + lift2.getCurrentPosition());
            telemetry.addData("hook", "" + hook2.getPosition());
            telemetry.addData("Drive Train", "\nLeft Stick: " + left + " Right Stick: " + right);
            telemetry.addData("Basket position", "" + basket.getPosition());
            telemetry.addData("Door", "" + door.getPosition());
            telemetry.addData("reading", "x: " + grab + " " + pickup);
            telemetry.addData("second stick", "\nright2: " + right2 + " left2: " + left2);
        }

    }

    public void sleep(int milli)
    {
        try{
            Thread.sleep(milli);
        }
        catch(Exception e){}
    }

    //sets lift to certain height and dumps
    public void liftmacro(int distance)
    {
        //Add auto stop wheels
         lift1.setPower(0.8); //Move lift
         lift2.setPower(0.8);
         while(lift1.getCurrentPosition() < 420){sleep(20);}
         door.setPosition(.8); // starts to retract door after it passes gear
         while(lift1.getCurrentPosition() < distance){sleep(20);}
         lift1.setPower(0.0);
         lift2.setPower(0.0);
         basket.setPosition(0.5);
         door.setPosition(0.0);
         while(basket.getPosition() != 0.5){sleep(20);}
        sleep(3000);
        basket.setPosition(0.0);
         while(basket.getPosition() != 0.0){sleep(20);}
         lift1.setPower(-0.7);
         lift2.setPower(-0.7);
         door.setPosition(0.8);
         while(lift1.getCurrentPosition()> 1200){sleep(20);} //wait to reopen door
         door.setPosition(0.0);
         lift1.setPower(-0.5);
         lift2.setPower(-0.5);
         while(lift1.getCurrentPosition() > 0){sleep(20);}
         lift1.setPower(0.0);
         lift2.setPower(0.0);

    }
}
