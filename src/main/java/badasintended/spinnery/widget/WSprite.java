package badasintended.spinnery.widget;

import badasintended.spinnery.client.utility.SpriteSheet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

/**
 * Allows you to render a sprite that's been packed into a texture atlas.
 * Specifically useful for rendering icons and other bits from the Minecraft resources.
 */
@Environment(EnvType.CLIENT)
public class WSprite extends WAbstractWidget {
    private SpriteSheet.Sprite sprite;

    public SpriteSheet.Sprite getSprite() {
        return sprite;
    }

    public <W extends WSprite> W setSprite(SpriteSheet.Sprite sprite) {
        this.sprite = sprite;
        return (W) this;
    }

    @Override
    public void draw(MatrixStack matrices, VertexConsumerProvider.Immediate provider) {
        if (isHidden()) {
            return;
        }

        getSprite().draw(matrices, provider, getX(), getY(), getZ(), getWidth(), getHeight(), false);
    }
}
