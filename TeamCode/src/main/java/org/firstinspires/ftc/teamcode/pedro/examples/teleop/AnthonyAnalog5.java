package org.firstinspires.ftc.teamcode.pedro.examples.teleop;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.pedro.examples.subclasses.IntakeSubclass;
import org.firstinspires.ftc.teamcode.pedro.examples.subclasses.ServoSubclass;
import org.firstinspires.ftc.teamcode.pedro.examples.subclasses.ShooterSubclass;
import org.firstinspires.ftc.teamcode.pedro.examples.subclasses.TransferSubclass;

@TeleOp(name = "Anthony's Analog 5V2")
public class AnthonyAnalog5 extends OpMode {

    private Follower follower;

    //private IntakeSubclass intake;
    private ShooterSubclass shooter;
    private TransferSubclass transfer;
    private ServoSubclass servo;

    private ElapsedTime servoTimer = new ElapsedTime();

    private final PoseFactory p = PoseFactory.degrees();
    private Pose startPose = p.of(130, 32, 180);

    @Override
    public void init() {
        follower = Constants.create(hardwareMap);
        follower.setPose(startPose);

        //intake = new IntakeSubclass();
        shooter = new ShooterSubclass();
        transfer = new TransferSubclass();
        servo = new ServoSubclass();

        //intake.init(hardwareMap);
        shooter.init(hardwareMap);
        transfer.init(hardwareMap);
        servo.init(hardwareMap);
        servoTimer.reset();
    }

    @Override
    public void loop() {

        if (gamepad1.dpad_up) {
            follower.setPose(startPose);
        }
        if (gamepad1.dpad_down) {
            follower.hold(startPose);
        }

        follower.manual(
                -gamepad1.left_stick_y,
                -gamepad1.left_stick_x,
                -gamepad1.right_stick_x
        );

        follower.update();

        // Intake + Transfer
        if (gamepad1.left_trigger > 0.05) {
            //intake.setIntakePower(gamepad1.left_trigger);
            //transfer.setTransferPower(gamepad1.left_trigger);
            transfer.transferOn();
        } else if (gamepad1.left_bumper) {
            //intake.intakeReverse();
            transfer.transferReverse();
        } else {
            //intake.intakeOff();
            transfer.transferOff();
        }

        // Shooter
        if (gamepad1.right_trigger > 0.05) {
            shooter.shooterOn();
            // Only feed once the flywheel is up to speed
            if (shooter.isAtVelocity()) {
                servo.servoShoot();
            } else {
                servo.servoBlock();
            }
        } else {
            shooter.shooterOff();
            servo.servoBlock();
            servoTimer.reset();
        }

        // MUST run every loop or the shooter motors get no power
        shooter.update();

        telemetry.addData("Target RPM", ShooterSubclass.targetRPM);
        telemetry.addData("Actual RPM", "%.0f", shooter.getRPM());
        telemetry.addData("RPM Error", "%.0f", ShooterSubclass.targetRPM - shooter.getRPM());
        telemetry.addData("RPM 1 / 2", "%.0f / %.0f", shooter.getRPM1(), shooter.getRPM2());
        telemetry.addData("Power", "%.2f", shooter.getPower());
        telemetry.addData("At Velocity", shooter.isAtVelocity());

        telemetry.addData("kP", ShooterSubclass.kP);
        telemetry.addData("kI", ShooterSubclass.kI);
        telemetry.addData("kD", ShooterSubclass.kD);
        telemetry.addData("kF", ShooterSubclass.kF);

        telemetry.addData("Servo Pos", servo.getPosition());

        telemetry.update();
    }
}