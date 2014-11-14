package mmm.littleMaidMob.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import mmm.lib.Client;
import mmm.lib.EntitySelect;
import mmm.lib.multiModel.MultiModelManager;
import mmm.lib.multiModel.model.AbstractModelBase;
import mmm.lib.multiModel.texture.MultiModelContainer;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

//TODO:アーマーセレクタが機能していない(modelがブランクだと表示されないため？)
public class GuiTextureSlot extends GuiSlot {

    public GuiTextureSelect owner;
    protected int selected;
    protected EntitySelect entity;
    protected List<MultiModelContainer> indexTexture;
    protected List<MultiModelContainer> indexArmor;
    public boolean mode;
    public int texsel[] = new int[2];
    public int color;
    protected int selectColor;
    private ItemStack armors[] = new ItemStack[] { new ItemStack(Items.leather_boots), new ItemStack(Items.leather_leggings), new ItemStack(Items.leather_chestplate), new ItemStack(Items.leather_helmet) };
    protected boolean isContract;
    protected static MultiModelContainer blankBox;

    public GuiTextureSlot(GuiTextureSelect pOwner) {
        super(pOwner.mc, pOwner.width, pOwner.height, 16, pOwner.height - 64, 36);
        owner = pOwner;
        entity = new EntitySelect(owner.mc.theWorld);
        color = owner.target.getMultiModel().getColor();
        selectColor = -1;
        blankBox = new MultiModelContainer();
        blankBox.defaultModel = new AbstractModelBase[] { null, null, null };

        texsel[0] = 0;//-1;
        texsel[1] = 0;//-1;
        indexTexture = new ArrayList<MultiModelContainer>();
        indexArmor = new ArrayList<MultiModelContainer>();
        isContract = true;//owner.target.isContract();
        entity.setContract(isContract);
        MultiModelContainer ltbox[] = owner.target.getMultiModel().getMultiModelContainer();
        for (Entry<String, MultiModelContainer> entry : MultiModelManager.instance.instance.textures.entrySet()) {
            MultiModelContainer lbox = entry.getValue();
            if (isContract) {
                if (lbox.getContractColorBits() > 0) {
                    indexTexture.add(lbox);
                }
            } else {
                if (lbox.getWildColorBits() > 0) {
                    indexTexture.add(lbox);
                }
            }
            if (lbox.hasArmor()) {
                indexArmor.add(lbox);
            }
            if (lbox == ltbox[0]) {
                texsel[0] = indexTexture.size() - 1;
            }
            if (lbox == ltbox[1]) {
                texsel[1] = indexArmor.size() - 1;
            }
        }
        setMode(false);
    }

    @Override
    protected int getSize() {
        return mode ? indexArmor.size() : indexTexture.size();
    }

    @Override
    protected void elementClicked(int var1, boolean var2, int var3, int var4) {
        if (mode) {
            selected = var1;
            texsel[1] = var1;
        } else {
            MultiModelContainer lbox = getSelectedBox(var1);
            if (lbox.hasColor(selectColor, isContract) && (owner.canSelectColor & (1 << selectColor)) > 0) {
                selected = var1;
                texsel[0] = var1;
                owner.selectColor = selectColor;
            } else if (lbox.hasColor(color, isContract)) {
                selected = var1;
                texsel[0] = var1;
                owner.selectColor = color;
            }
        }
    }

    @Override
    protected boolean isSelected(int var1) {
        return selected == var1;
    }

    @Override
    protected void drawBackground() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void drawSlot(int var1, int var2, int var3, int var4, Tessellator var5, int var6, int var7) {
        GL11.glPushMatrix();
        if (!mode) {
            for (int li = 0; li < 16; li++) {
                int lx = var2 + 15 + 12 * li;
                selectColor = (mouseX - (var2 + 15)) / 12;
                if ((selectColor < 0) && (selectColor > 15)) {
                    selectColor = -1;
                }
                if (color == li) {
                    owner.drawRect(lx, var3, lx + 11, var3 + 36, 0x88882222);
                } else if (owner.selectColor == li) {
                    owner.drawRect(lx, var3, lx + 11, var3 + 36, 0x88226622);
                } else if ((owner.canSelectColor & (1 << li)) > 0) {
                    owner.drawRect(lx, var3, lx + 11, var3 + 36, 0x88222288);
                }
            }
        }

        MultiModelContainer lbox;
        if (mode) {
            lbox = indexArmor.get(var1);
            entity.getMultiModel().model = blankBox;
            entity.getMultiModel().armor = lbox;
        } else {
            lbox = indexTexture.get(var1);
            entity.getMultiModel().model = lbox;
            entity.getMultiModel().armor = blankBox;
        }
        //MultiModelManager.instance.checkMultiModelContainerServer(lbox);
        GL11.glDisable(GL11.GL_BLEND);
        owner.getFontrenderObj().drawStringWithShadow(lbox.name, var2 + 16, var3 + 25, -1);
        GL11.glTranslatef(var2 + 8F, var3 + 25F, 50F);
        GL11.glScalef(12F, -12F, 12F);
        entity.renderYawOffset = 30F;
        entity.rotationYawHead = 15F;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
        if (mode) {
            // アーマー
            ResourceLocation ltxname[];
            GL11.glTranslatef(1F, 0, 0);
            Map<Integer, ResourceLocation> lmap = lbox.armors.get("default");
            if (lmap != null) {
                //              ltxname = entity.getTextures(1);
                //              ltxname[0] = ltxname[1] = ltxname[2] = ltxname[3] =
                //                      lbox.getArmorTextureName(MMM_MultiModelManager.tx_armor1, "default", 0);
                //              ltxname = entity.getTextures(2);
                //              ltxname[0] = ltxname[1] = ltxname[2] = ltxname[3] =
                //                      lbox.getArmorTextureName(MMM_MultiModelManager.tx_armor2, "default", 0);
                entity.setModel("default");
                RenderManager.instance.renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
            }
            for (String ls : RenderBiped.bipedArmorFilenamePrefix) {
                GL11.glTranslatef(1F, 0, 0);
                if (lbox.armors.containsKey(ls)) {
                    //                  ltxname = entity.getTextures(1);
                    //                  ltxname[0] = ltxname[1] = ltxname[2] = ltxname[3] =
                    //                          lbox.getArmorTextureName(MMM_MultiModelManager.tx_armor1, ls, 0);
                    //                  ltxname = entity.getTextures(2);
                    //                  ltxname[0] = ltxname[1] = ltxname[2] = ltxname[3] =
                    //                          lbox.getArmorTextureName(MMM_MultiModelManager.tx_armor2, ls, 0);
                    entity.setModel(ls);
                    RenderManager.instance.renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
                    Client.setLightmapTextureCoords(0x00f0);//61680
                }
            }
        } else {
            // テクスチャ表示
            for (int li = 0; li < 16; li++) {
                GL11.glTranslatef(1F, 0, 0);
                if (lbox.hasColor(li, isContract)) {
                    entity.getMultiModel().setColor(li);
                    entity.setContract(isContract);
                    entity.setTextureNames();
                    //                  entity.getTextures(0)[0] = lbox.getTextureName(li + (isContract ? 0 : MMM_MultiModelManager.tx_wild));
                    RenderManager.instance.renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
                    Client.setLightmapTextureCoords(0x00f0);//61680
                }
            }
        }

        GL11.glPopMatrix();
    }

    public MultiModelContainer getSelectedBox() {
        return getSelectedBox(selected);
    }

    public MultiModelContainer getSelectedBox(int pIndex) {
        return mode ? indexArmor.get(pIndex) : indexTexture.get(pIndex);
    }

    public MultiModelContainer getSelectedBox(boolean pMode) {
        return pMode ? indexArmor.get(texsel[1]) : indexTexture.get(texsel[0]);
    }

    public void setMode(boolean pFlag) {
        //this.func_148141_e(slotHeight * -getSize());
        if (pFlag) {
            selected = texsel[1];
            mode = true;
            entity.setCurrentItemOrArmor(1, armors[0]);
            entity.setCurrentItemOrArmor(2, armors[1]);
            entity.setCurrentItemOrArmor(3, armors[2]);
            entity.setCurrentItemOrArmor(4, armors[3]);
        } else {
            selected = texsel[0];
            mode = false;
            entity.setCurrentItemOrArmor(1, null);
            entity.setCurrentItemOrArmor(2, null);
            entity.setCurrentItemOrArmor(3, null);
            entity.setCurrentItemOrArmor(4, null);
        }
        //this.func_148141_e(slotHeight * selected);
    }

}