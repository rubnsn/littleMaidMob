package mmm.lib;

import mmm.littleMaidMob.entity.EntityLittleMaidBase;
import net.minecraft.world.World;

public class EntitySelect extends EntityLittleMaidBase {
    public String name;

    //  public int color;
    //  public int textureIndex[] = new int[] { 0, 0 };
    //  public MMM_TextureBoxBase textureBox[] = new MMM_TextureBoxBase[] {
    //          new MMM_TextureBox(), new MMM_TextureBox() };
    //  public boolean contract;
    //  public ResourceLocation textures[][] = new ResourceLocation[][] {
    //          { null, null },
    //          { null, null , null , null },
    //          { null, null , null , null },
    //          { null, null , null , null },
    //          { null, null , null , null }
    //  };

    public EntitySelect(World par1World) {
        super(par1World);
    }

    @Override
    protected void entityInit() {
        // Select用だから、これ別にいらんけどな。
        super.entityInit();
        // color
        //dataWatcher.addObject(19, Integer.valueOf(0));
        // 20:選択テクスチャインデックス
        //dataWatcher.addObject(20, Integer.valueOf(0));
    }

    /*
        @Override
        public int getMaxHealth() {
            return 20;
        }
    */
    @Override
    public float getBrightness(float par1) {
        return worldObj == null ? 0.0F : super.getBrightness(par1);
    }

    // EntityCaps

    @Override
    public int getBrightnessForRender(float par1) {
        // 一定の明るさを返す
        return 0x00f000f0;
    }

    public void setTextureNames(String string) {
        this.name = string;
    }

    public void setTextureNames() {
    }

}
