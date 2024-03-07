package com.tac.guns.util.math;

import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import javax.annotation.Nonnull;

public class MathUtil {
    public static double magnificationToFovMultiplier(double magnification, double currentFov) {
        double currentTan = Math.tan(Math.toRadians(currentFov / 2));
        double newTan = currentTan / magnification;
        double newFov = Math.toDegrees(Math.atan(newTan)) * 2;
        return newFov / currentFov;
    }

    public static double fovToMagnification(double currentFov, double originFov) {
        return Math.tan(Math.toRadians(originFov / 2)) / Math.tan(Math.toRadians(currentFov / 2));
    }

    public static float[] toQuaternion(float roll, float pitch, float yaw) {
        double cy = Math.cos(yaw * 0.5);
        double sy = Math.sin(yaw * 0.5);
        double cp = Math.cos(pitch * 0.5);
        double sp = Math.sin(pitch * 0.5);
        double cr = Math.cos(roll * 0.5);
        double sr = Math.sin(roll * 0.5);
        return new float[]{
                (float) (cy * cp * sr - sy * sp * cr),
                (float) (sy * cp * sr + cy * sp * cr),
                (float) (sy * cp * cr - cy * sp * sr),
                (float) (cy * cp * cr + sy * sp * sr)
        };
    }

    public static Quaternion slerp(Quaternion from, Quaternion to, float alpha) {
        float ax = from.i();
        float ay = from.j();
        float az = from.k();
        float aw = from.r();
        float bx = to.i();
        float by = to.j();
        float bz = to.k();
        float bw = to.r();

        float dot = ax * bx + ay * by + az * bz + aw * bw;
        if (dot < 0) {
            bx = -bx;
            by = -by;
            bz = -bz;
            bw = -bw;
            dot = -dot;
        }
        float epsilon = 1e-6f;
        float s0, s1;
        if ((1.0 - dot) > epsilon) {
            float omega = (float) Math.acos(dot);
            float invSinOmega = 1.0f / (float) Math.sin(omega);
            s0 = (float) Math.sin((1.0 - alpha) * omega) * invSinOmega;
            s1 = (float) Math.sin(alpha * omega) * invSinOmega;
        } else {
            s0 = 1.0f - alpha;
            s1 = alpha;
        }
        float rx = s0 * ax + s1 * bx;
        float ry = s0 * ay + s1 * by;
        float rz = s0 * az + s1 * bz;
        float rw = s0 * aw + s1 * bw;
        return new Quaternion(rx, ry, rz, rw);
    }

    public static Vector3f getEulerAngles(Matrix4f matrix){
        double sy = Math.sqrt(matrix.m00 * matrix.m00 + matrix.m10 * matrix.m10);
        boolean singular = sy < 1e-6;
        double x, y, z;
        if (!singular)
        {
            x = Math.atan2(matrix.m21, matrix.m22);
            y = Math.atan2(-matrix.m20, sy);
            z = Math.atan2(matrix.m10, matrix.m00);
        }
        else
        {
            x = Math.atan2(-matrix.m12, matrix.m11);
            y = Math.atan2(-matrix.m20, sy);
            z = 0;
        }
        return new Vector3f((float) x, (float) y, (float) z);
    }
}
