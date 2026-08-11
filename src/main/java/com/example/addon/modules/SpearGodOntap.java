package com.example.addon.modules;

import com.example.addon.ExampleAddon;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

public class SpearGodOntap extends Module {
    // Tạo nhóm cài đặt
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // Cài đặt phạm vi tấn công (mặc định 10 blocks)
    private final Setting<Double> range = sgGeneral.add(new DoubleSetting.Builder()
        .name("range")
        .description("Phạm vi tấn công của giáo (blocks).")
        .defaultVal(10.0)
        .min(1.0)
        .max(20.0)
        .build()
    );

    public SpearGodOntap() {
        super(ExampleAddon.CATEGORY, "spear-god-ontap", "Tự động xoay và tấn công mục tiêu trong phạm vi bằng giáo.");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null) return;

        // Kiểm tra xem có đang cầm giáo (Trident) không
        if (mc.player.getMainHandStack().getItem() != Items.TRIDENT) return;

        Entity target = null;
        double minDst = range.get();

        // Tìm thực thể (Entity) gần nhất
        for (Entity entity : mc.world.getEntities()) {
            if (entity == mc.player || !entity.isAlive()) continue;
            if (entity instanceof LivingEntity) {
                double dist = mc.player.distanceTo(entity);
                if (dist <= minDst) {
                    minDst = dist;
                    target = entity;
                }
            }
        }

        // Nếu tìm thấy mục tiêu, thực hiện xoay và tấn công
        if (target != null) {
            Rotations.rotate(Rotations.getYaw(target), Rotations.getPitch(target), 10, () -> {
                mc.interactionManager.attackEntity(mc.player, target);
                mc.player.swingHand(Hand.MAIN_HAND);
            });
        }
    }
}
