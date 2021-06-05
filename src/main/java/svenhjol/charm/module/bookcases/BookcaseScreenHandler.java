package svenhjol.charm.module.bookcases;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import svenhjol.charm.screen.CharmScreenHandler;

public class BookcaseScreenHandler extends CharmScreenHandler {
    public BookcaseScreenHandler(int syncId, PlayerInventory player) {
        this(syncId, player, new SimpleInventory(BookcaseBlockEntity.SIZE));
    }

    public BookcaseScreenHandler(int syncId, PlayerInventory player, Inventory inventory) {
        super(Bookcases.SCREEN_HANDLER, syncId, player, inventory);
        int index = 0;

        // container's inventory slots
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new BookcaseSlot(inventory, index++, 8 + (i * 18), 18));
        }

        for (int i = 0; i < 9; ++i) {
            this.addSlot(new BookcaseSlot(inventory, index++, 8 + (i * 18), 36));
        }

        index = 9; // start of player inventory

        // player's main inventory slots
        for (int r = 0; r < 3; ++r) {
            for (int c = 0; c < 9; ++c) {
                this.addSlot(new Slot(player, index++, 8 + c * 18, 68 + r * 18));
            }
        }

        // player's hotbar
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(player, i, 8 + (i * 18), 126));
        }
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if (slotIndex > 0 && slotIndex < BookcaseBlockEntity.SIZE) {
            ItemStack stack = this.getCursorStack();
            if (!player.world.isClient && Bookcases.canContainItem(stack))
                Bookcases.triggerAddedBookToBookcase((ServerPlayerEntity) player);
        }
        super.onSlotClick(slotIndex, button, actionType, player);
    }
}