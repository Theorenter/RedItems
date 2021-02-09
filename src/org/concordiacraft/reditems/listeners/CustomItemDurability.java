package org.concordiacraft.reditems.listeners;

import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.concordiacraft.reditems.main.RedItems;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Theorenter
 * The listener required to work with custom strength.
 * Works when interacting with the values of the "UNQ-DURABILITY" item tag.
 */
public class CustomItemDurability implements Listener {
    RedItems plugin;
    public CustomItemDurability(RedItems plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCustomItemDamage(PlayerItemDamageEvent e) {
        ItemStack i = e.getItem();
        ItemMeta im = i.getItemMeta();
        PersistentDataContainer data = im.getPersistentDataContainer();
        NamespacedKey nSK = new NamespacedKey(plugin, "UNQ-DURABILITY");
        if (!data.has(nSK, PersistentDataType.INTEGER_ARRAY)) {
            return;
        }
        e.setCancelled(true);

        //valS[0] - Current durability
        //valS[1] - Max durability

        int[] valS = data.get(nSK, PersistentDataType.INTEGER_ARRAY);

        // Decrease custom durability
        valS[0] = valS[0] - 1;
        data.set(nSK, PersistentDataType.INTEGER_ARRAY, valS);


        // Display real durability - lore
        List<String> lore;
        if (im.hasLore()) {
            lore = i.getItemMeta().getLore();
            int lastStrIndex = lore.size() - 1;
            String durabilityStat = lore.get(lastStrIndex);
            if (durabilityStat.startsWith("§x§F§F§F§F§F§FДействительная прочность: ")) {
                lore.set(lastStrIndex, "§x§F§F§F§F§F§FДействительная прочность: " + valS[0] + " / " + valS[1]);
            }
        } else {
            lore = new ArrayList();
            lore.add("§x§F§F§F§F§F§FДействительная прочность: " + valS[0] + " / " + valS[1]);
        }
        im.setLore(lore);


        Float per = Float.valueOf(valS[0]) / Float.valueOf(valS[1]);
        e.getPlayer().sendMessage(per.toString());
        ((Damageable) im).setDamage(
                i.getType().getMaxDurability() -
                        (int) (i.getType().getMaxDurability() * per)
        ); i.setItemMeta(im);
        if (valS[0] == 0) {
            e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_ITEM_BREAK, 1f, 1f);
            e.getPlayer().getInventory().setItemInMainHand(null); }


        // TODO добавить учёт зачарований предмета на прочность.
    }
}
