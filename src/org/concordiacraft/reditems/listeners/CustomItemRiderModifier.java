package org.concordiacraft.reditems.listeners;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.concordiacraft.reditems.main.RedItems;


public class CustomItemRiderModifier implements Listener {
    RedItems plugin;

    public CustomItemRiderModifier(RedItems plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerAttackEntityWhenRide(EntityDamageByEntityEvent e) {
        if (!(e.getDamager().isInsideVehicle()) || !(e.getDamager() instanceof Player) || !(e.getEntity() instanceof LivingEntity)) { return; }
        Player pAttacker = (Player) e.getDamager();
        if (!(pAttacker.getVehicle() instanceof Horse)) { return; }
        LivingEntity eDamaged = (LivingEntity) e.getEntity();
        if (!pAttacker.getInventory().getItemInMainHand().hasItemMeta()) { return; }
        ItemStack itemRidingWeapon = pAttacker.getInventory().getItemInMainHand();
        PersistentDataContainer weaponData = itemRidingWeapon.getItemMeta().getPersistentDataContainer();
        NamespacedKey nSK = new NamespacedKey(plugin, "UNQ-RIDING-MODIFIER");
        if (!weaponData.has(nSK, PersistentDataType.DOUBLE)) { return; }
        Double damageModifier = weaponData.get(nSK, PersistentDataType.DOUBLE);
        Double weaponDamage = null;
        for (AttributeModifier am : itemRidingWeapon.getItemMeta().getAttributeModifiers(Attribute.GENERIC_ATTACK_DAMAGE)) {
            weaponDamage = am.getAmount();
        }
        if(isEntityBlockingPlayer(eDamaged)) { return; } else {
            Double finalDmg = (damageModifier * weaponDamage) - weaponDamage;
            eDamaged.damage(finalDmg);
            eDamaged.setVelocity(pAttacker.getLocation().toVector().subtract(eDamaged.getLocation().toVector()).normalize().multiply(-5));
        }
    }
    private boolean isEntityBlockingPlayer(LivingEntity eDamaged) {
        if (!(eDamaged instanceof Player)) {
            return false;
        } else {
            Player pDamaged = (Player) eDamaged;
            if (!pDamaged.isBlocking())
            {
                return false;
            } else {
                return true;
            }
        }
    }
}