package mmm.littleMaidMob.inventory;

import mmm.littleMaidMob.entity.EntityLittleMaidBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.MathHelper;
import net.minecraft.world.Explosion;

public class InventoryLittleMaid extends InventoryPlayer {

    public EntityLittleMaidBase maid;
    public boolean isOpen;
    ItemStack[] prevItems;

    public InventoryLittleMaid(EntityLittleMaidBase pMaid) {
        super(pMaid.avatar);
        mainInventory = new ItemStack[getInitInvSize()];
        maid = pMaid;
        isOpen = false;
        prevItems = new ItemStack[mainInventory.length + armorInventory.length];
    }

    @Override
    public boolean addItemStackToInventory(final ItemStack p_70441_1_) {
        if (player == null) {
            player = maid.avatar;
        }
        return super.addItemStackToInventory(p_70441_1_);
    }

    /***/
    public ItemStack armorItemInSlot(int p_70440_1_) {
        return this.armorInventory[p_70440_1_];
    }

    /**
     * 初期化時のインベントリサイズ
     * 
     * @return
     */
    public int getInitInvSize() {
        // メイドインベントリはプレーヤの半分
        return 18;
    }

    public static int getHotbarSize() {
        return 18;
    }

    /**
     * インベントリのどこでもカレントアイテムに成り得る
     */
    @Override
    public ItemStack getCurrentItem() {
        if (currentItem >= 0 && currentItem < mainInventory.length) {
            return mainInventory[currentItem];
        } else {
            return null;
        }
    }

    @Override
    public void decrementAnimations() {
        // 落ちないようにtryで括る
        // アニメーション処理ってか毎時処理
        for (int i = 0; i < mainInventory.length; i++) {
            try {
                if (mainInventory[i] != null)
                    mainInventory[i].updateAnimation(maid.worldObj, maid, i, currentItem == i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // EntityPlayerにしか対応してねぇ・・・
        for (int i = 0; i < armorInventory.length; i++) {
            try {
                if (armorInventory[i] != null) {
                    armorInventory[i].getItem().onArmorTick(maid.worldObj, player, armorInventory[i]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getTotalArmorValue() {
        int i = 0;
        for (int j = 0; j < armorInventory.length; j++)
            if (armorInventory[j] != null && (armorInventory[j].getItem() instanceof ItemArmor)) {
                int k = ((ItemArmor) armorInventory[j].getItem()).damageReduceAmount;
                i += k;
            }

        return i;
    }

    @Override
    public void damageArmor(float par1) {
        // ダメージを装備で分散
        par1 /= 3F;
        // 最低ダメージ保証
        if (par1 < 1.0F)
            par1 = 1.0F;
        // 各部にダメージ
        for (int i = 0; i < armorInventory.length; i++) {
            if (i == 3)
                continue;
            if (armorInventory[i] == null || !(armorInventory[i].getItem() instanceof ItemArmor))
                continue;
            armorInventory[i].damageItem((int) par1, maid);
            if (armorInventory[i].stackSize == 0)
                armorInventory[i] = null;
        }
    }

    /**
     * インベントリをブチマケロ！
     * 
     * @param detonator
     */
    public void dropAllItems(boolean detonator) {
        Explosion lexp = null;
        if (detonator) {
            // Mobによる破壊の是非
            lexp = new Explosion(maid.worldObj, maid, maid.posX, maid.posY, maid.posZ, 3F);
            lexp.isFlaming = false;
            lexp.isSmoking = maid.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing");
        }

        armorInventory[3] = null;
        for (int i = 0; i < getSizeInventory(); i++) {
            ItemStack it = getStackInSlot(i);
            if (it != null) {
                if (detonator && isItemExplord(i)) {
                    Block lblock = Block.getBlockFromItem(it.getItem());
                    if (lblock instanceof BlockTNT) {
                        BlockTNT ltnt = (BlockTNT) lblock;
                        for (int l = 0; l < it.stackSize; l++) {
                            // 爆薬ぶちまけ
                            ltnt.onBlockDestroyedByExplosion(maid.worldObj, MathHelper.floor_double(maid.posX) + maid.getRNG().nextInt(7) - 3, MathHelper.floor_double(maid.posY) + maid.getRNG().nextInt(7) - 3, MathHelper.floor_double(maid.posZ) + maid.getRNG().nextInt(7) - 3, lexp);
                        }
                    }
                } else {
                    maid.entityDropItem(it, 0F);
                }
            }
            setInventorySlotContents(i, null);
        }
        if (detonator) {
            lexp.doExplosionA();
            lexp.doExplosionB(true);
        }
    }

    @Override
    public void dropAllItems() {
        dropAllItems(false);
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer) {
        return maid.isDead ? false : par1EntityPlayer.getDistanceSqToEntity(maid) <= 64D;
    }

    // ロード
    @Override
    public void readFromNBT(NBTTagList par1nbtTagList) {
        super.readFromNBT(par1nbtTagList);
        // インベントリのサイズを変更する
        ItemStack[] lmi = new ItemStack[getInitInvSize()];
        for (int li = 0; li < lmi.length; li++) {
            lmi[li] = mainInventory[li];
        }
        mainInventory = lmi;
    }

    @Override
    public String getInventoryName() {
        return "container.insideSkirt";
    }

    // アイテムの状態識別

    /**
     * 燃えるアイテムか?
     * 
     * @param index スロット
     * @return
     */
    public boolean isItemBurned(int index) {
        return index > -1 && isItemBurned(getStackInSlot(index));
    }

    public static boolean isItemBurned(ItemStack pItemstack) {
        return (pItemstack != null && TileEntityFurnace.getItemBurnTime(pItemstack) > 0);
    }

    /**
     * 精錬アイテムか？
     * 
     * @param index スロット
     * @return
     */
    public boolean isItemSmelting(int index) {
        return isItemSmelting(getStackInSlot(index));
    }

    public static boolean isItemSmelting(ItemStack pItemstack) {
        return (pItemstack != null && FurnaceRecipes.smelting().getSmeltingResult(pItemstack) != null);
    }

    /**
     * 爆発物？
     * 
     * @param index スロット
     * @return
     */
    public boolean isItemExplord(int index) {
        return (index >= 0) && isItemExplord(getStackInSlot(index));
    }

    public static boolean isItemExplord(ItemStack pItemstack) {
        if (pItemstack == null)
            return false;
        Block lblock = Block.getBlockFromItem(pItemstack.getItem());
        return (pItemstack != null && lblock != null && lblock.getMaterial() == Material.tnt);
    }

    @Override
    public void openInventory() {
        super.openInventory();
        isOpen = true;
        maid.onGuiOpened();
    }

    @Override
    public void closeInventory() {
        super.closeInventory();
        isOpen = false;
        maid.onGuiClosed();
    }

    public void setInventoryCurrentSlotContents(ItemStack itemstack) {
        if (currentItem > -1) {
            setInventorySlotContents(currentItem, itemstack);
        }
    }

    public int getInventorySlotContainItem(Item item) {
        // 指定されたアイテムIDの物を持っていれば返す
        for (int j = 0; j < mainInventory.length; j++) {
            if (mainInventory[j] != null && mainInventory[j].getItem() == item) {
                return j;
            }
        }

        return -1;
    }

    public ItemStack getHeadMount() {
        return mainInventory[mainInventory.length - 1];
    }

    // インベントリの転送関連
    public boolean isChanged(int pIndex) {
        // 変化があったかの判定
        ItemStack lis = getStackInSlot(pIndex);
        return !ItemStack.areItemStacksEqual(lis, prevItems[pIndex]);
        // return (lis == null || prevItems[pIndex] == null) ?
        // (prevItems[pIndex] != lis) : !ItemStack.areItemStacksEqual(lis,
        // prevItems[pIndex]);
        // return prevItems[pIndex] != getStackInSlot(pIndex);
    }

    public void setChanged(int pIndex) {
        prevItems[pIndex] = new ItemStack(Items.sugar);
    }

    public void resetChanged(int pIndex) {
        // 処理済みのチェック
        ItemStack lis = getStackInSlot(pIndex);
        prevItems[pIndex] = (lis == null ? null : lis.copy());
    }

    public void clearChanged() {
        // 強制リロード用、ダミーを登録して強制的に一周させる
        ItemStack lis = new ItemStack(Items.sugar);
        for (int li = 0; li < prevItems.length; li++) {
            prevItems[li] = lis;
        }
    }

}
