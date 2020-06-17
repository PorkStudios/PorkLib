/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.minecraft.item;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.misc.Cloneable;
import net.daporkchop.lib.logging.format.component.TextComponent;
import net.daporkchop.lib.minecraft.block.BlockState;
import net.daporkchop.lib.minecraft.tileentity.TileEntity;
import net.daporkchop.lib.minecraft.util.Identifier;
import net.daporkchop.lib.primitive.map.IntIntMap;
import net.daporkchop.lib.primitive.map.ObjIntMap;

import java.awt.Color;
import java.util.List;
import java.util.Set;

/**
 * Optional additional information used to describe an item.
 * <p>
 * See <a href="https://minecraft.gamepedia.com/Player.dat_format#Item_structure">Item Structure on the Minecraft Wiki</a>.
 * <p>
 * This class is split into sections. Different item types may only respect properties from certain sections.
 *
 * @author DaPorkchop_
 */
@ToString
@Getter
@Setter
@Accessors(fluent = true, chain = true)
public class ItemMeta implements Cloneable<ItemMeta> {
    // general

    /**
     * If {@code true}, the item will not lose durability when used in survival mode.
     */
    protected boolean unbreakable = false;

    /**
     * A {@link Set} of the block IDs that may be destroyed by this item in adventure mode.
     * <p>
     * Empty or {@code null} values will be treated as unset.
     */
    protected Set<Identifier> canDestroy = null;

    /**
     * A value used in the {@code custom_model_data} item tag in the overrides of item models.
     */
    protected float customModelData;

    // block

    /**
     * A {@link Set} of the block IDs that this block may be placed against in adventure mode.
     * <p>
     * Empty or {@code null} values will be treated as unset.
     */
    protected Set<Identifier> canPlaceOn = null;

    /**
     * Contains the {@link TileEntity} data attached to this item, for example banner data or blocks with tile entities that were Ctrl+picked in
     * creative mode.
     */
    protected TileEntity tileEntity = null;

    /**
     * A custom {@link BlockState} which should be used in favor of the default state for this item's ID.
     * <p>
     * Default or {@code null} values will be treated as unset.
     * <p>
     * Values that do not correspond to this item's ID will be silently serialized, and may cause unexpected behavior when loaded.
     */
    protected BlockState blockState = null;

    // enchantment

    /**
     * A map of enchantment IDs to their corresponding levels.
     * <p>
     * Empty or {@code null} values will be treated as unset.
     */
    protected ObjIntMap<Identifier> enchantments = null;

    /**
     * Exactly the same as {@link #enchantments}, but used for enchantments stored in an enchanted book.
     */
    protected ObjIntMap<Identifier> storedEnchantments = null;

    /**
     * The number of additional XP levels required to process this item in an anvil.
     * <p>
     * Negative values will be treated as {@code 0}.
     */
    protected int repairCost = 0;

    // potion

    /**
     * The {@link Identifier} of the default potion effect to apply.
     * <p>
     * {@code null} values will be treated as unset.
     */
    protected Identifier defaultEffect = null;

    /**
     * Additional, custom potion effects applied by this item.
     * <p>
     * Empty or {@code null} values will be treated as unset.
     */
    protected List<PotionEffect> customEffects = null;

    /**
     * A custom {@link Color} to be used by the potion.
     * <p>
     * {@code null} values will be treated as unset.
     * <p>
     * The color's alpha channel will be ignored.
     */
    protected Color customPotionColor = null;

    // crossbow

    /**
     * A list of items that the crossbow has charged.
     * <p>
     * Empty or {@code null} values will be treated as unset.
     */
    protected List<ItemStack> chargedProjectiles = null;

    /**
     * Whether or not this crossbow is charged.
     */
    protected boolean charged = false;

    //display

    /**
     * A custom text component shown in place of this item's name.
     * <p>
     * {@code null} values will be treated as unset.
     */
    protected TextComponent name = null;

    /**
     * The lines of text to display as this item's lore.
     * <p>
     * Empty or {@code null} values will be treated as unset.
     */
    protected List<String> lore = null;

    /**
     * The color that the leather armor was dyed with.
     * <p>
     * {@code null} values will be treated as unset.
     * <p>
     * The color's alpha channel will be ignored.
     */
    protected Color armorColor = null;

    /**
     * The map color.
     * <p>
     * Negative values will be treated as unset.
     */
    protected int mapColor = -1;

    /**
     * A bitmask indicating which parts of an item tooltip should not be rendered.
     * <p>
     * See the Minecraft Wiki page for more information on this field.
     * <p>
     * {@code 0} values will be treated as unset.
     */
    protected int hideFlags = 0;

    // book

    /**
     * Whether or not the book has been opened.
     */
    protected boolean bookResolved = false;

    /**
     * The copy tier of this book.
     * <p>
     * {@code 0} values will be treated as unset.
     */
    protected int bookGeneration = 0;

    /**
     * The name of the author of this book.
     * <p>
     * {@code null} values will be treated as unset.
     */
    protected String bookAuthor = null;

    /**
     * The title of this book.
     * <p>
     * {@code null} values will be treated as unset.
     */
    protected String bookTitle = null;

    /**
     * The pages in the book. Each {@link TextComponent} represents a single page.
     * <p>
     * Empty or {@code null} values will be treated as unset.
     */
    protected List<TextComponent> bookPages = null;

    /**
     * The pages in the book. Each {@link String} represents a single page.
     * <p>
     * Published books do not use this field, it is only used by the Book and Quill item.
     * <p>
     * Empty or {@code null} values will be treated as unset.
     */
    protected List<String> bookPagesEditable = null;

    // firework

    /**
     * The firework explosion type. Only used by a firework star.
     * <p>
     * {@code null} values will be treated as unset.
     */
    protected FireworkExplosion fireworkExplosion = null;

    /**
     * The number of gunpowder used to craft this firework rocket.
     */
    protected int fireworkFlight = 0;

    /**
     * All of the firework explosions that belong to this firework rocket.
     * <p>
     * Empty or {@code null} values will be treated as unset.
     */
    protected List<FireworkExplosion> fireworkExplosions = null;

    // map

    /**
     * The map number.
     * <p>
     * Negative values will be treated as unset.
     */
    protected int mapId = -1;

    /**
     * All of the decorations to display on this map.
     * <p>
     * Empty or {@code null} values will be treated as unset.
     */
    protected List<MapDecoration> mapDecorations = null;

    // suspicious stew

    /**
     * The status effects that this suspicious stew has.
     * <p>
     * Empty or {@code null} values will be treated as unset.
     */
    protected IntIntMap stewEffects = null;

    //TODO: attribute modifiers

    //TODO: skulls

    //TODO: entities, required for armor stands and spawn eggs

    //TODO: compasses track the position of their lodestone block

    @Override
    public ItemMeta clone() {
        return new ItemMeta()
                .unbreakable(this.unbreakable)
                .canDestroy(this.canDestroy)
                .customModelData(this.customModelData)
                .canPlaceOn(this.canPlaceOn)
                .tileEntity(this.tileEntity)
                .blockState(this.blockState)
                .enchantments(this.enchantments)
                .storedEnchantments(this.storedEnchantments)
                .repairCost(this.repairCost)
                .defaultEffect(this.defaultEffect)
                .customEffects(this.customEffects)
                .customPotionColor(this.customPotionColor)
                .chargedProjectiles(this.chargedProjectiles)
                .charged(this.charged)
                .name(this.name)
                .lore(this.lore)
                .armorColor(this.armorColor)
                .mapColor(this.mapColor)
                .hideFlags(this.hideFlags)
                .bookResolved(this.bookResolved)
                .bookAuthor(this.bookAuthor)
                .bookTitle(this.bookTitle)
                .bookPages(this.bookPages)
                .bookPagesEditable(this.bookPagesEditable)
                .fireworkExplosion(this.fireworkExplosion)
                .fireworkFlight(this.fireworkFlight)
                .fireworkExplosions(this.fireworkExplosions)
                .mapId(this.mapId)
                .mapDecorations(this.mapDecorations)
                .stewEffects(this.stewEffects);
    }
}
