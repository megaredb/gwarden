package com.megared.gwarden.utils;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ParticleSpawner {
    public static void line(
            ServerPlayerEntity player,
            ParticleEffect particle,
            ServerWorld world,
            BlockPos startPos,
            BlockPos endPos,
            Vec3d delta,
            double speed,
            double step) {
        Vec3d start = new Vec3d(startPos.getX(), startPos.getY(), startPos.getZ());
        Vec3d end = new Vec3d(endPos.getX(), endPos.getY(), endPos.getZ());
        double distance = start.distanceTo(end);
        Vec3d direction = end.subtract(start).normalize();

        for (double i = 0; i <= distance; i += step) {
            Vec3d position = start.add(direction.multiply(i));
            world.spawnParticles(player, particle, false, position.x, position.y, position.z, 1, 0, 0, 0, 1.0);
        }
    }
}
