package com.jewelcharms.client.event;

import com.jewelcharms.JewelCharms;
import com.jewelcharms.client.renderer.ToolJewelRenderer;
import com.jewelcharms.util.ToolJewelData;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderItemInFrameEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = JewelCharms.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onRenderItemInFrame(RenderItemInFrameEvent event) {
        ItemStack stack = event.getItemStack();

        if (stack.isEmpty() || ToolJewelData.getJewelCount(stack) == 0) {
            return;
        }

        ToolJewelRenderer.renderJewelOverlays(
            stack,
            event.getPoseStack(),
            event.getMultiBufferSource(),
            event.getPackedLight(),
            0
        );
    }
}
