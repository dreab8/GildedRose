import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

//- All items have a SellIn value which denotes the number of days we have to sell the item
//- All items have a Quality value which denotes how valuable the item is
//- At the end of each day our system lowers both values for every item
//
//Pretty simple, right? Well this is where it gets interesting:
//
//- Once the sell by date has passed, Quality degrades twice as fast
//- The Quality of an item is never negative
//- "Aged Brie" actually increases in Quality the older it gets
//- The Quality of an item is never more than 50
//- "Sulfuras", being a legendary item, never has to be sold or decreases in Quality
//- "Backstage passes", like aged brie, increases in Quality as it's SellIn value approaches; Quality increases by 2 when there are 10 days or less and by 3 when there are 5 days or less but Quality drops to 0 after the concert
//
//We have recently signed a supplier of conjured items. This requires an update to our system:
//
//- "Conjured" items degrade in Quality twice asl fast as normal items

public class GildedRoseTest {

    private static final int REGULAR_DECREASING = 1;

    public static class ItemBuilder {

        private int sellWithin;
        private int quality;
        private String name;

        private ItemBuilder(int sellWithin, int quality, String name) {
            super();
            this.sellWithin = sellWithin;
            this.quality = quality;
            this.name = name;
        }

        public static ItemBuilder anItem() {
            ItemBuilder builder = new ItemBuilder(0, 0, "");
            return builder;
        }

        public ItemBuilder toSellWithin(int days) {
            return new ItemBuilder(days, this.quality, this.name);
        }

        public ItemBuilder withQuality(int quality) {

            return new ItemBuilder(this.sellWithin, quality, this.name);
        }

        public ItemBuilder named(String name) {
            return new ItemBuilder(this.sellWithin, this.quality, name);
        }

        public Item build() {
            Item item = new Item(this.name, this.sellWithin, this.quality);
            return item;
        }

    }

    private final static int REGULAR_STARTING_QUALITY = 10;
    private final static int REGULAR_STARTING_SELLIN = 10;

    ItemBuilder regularItemPrototype = ItemBuilder.anItem().named("apple").toSellWithin(REGULAR_STARTING_SELLIN)
            .withQuality(REGULAR_STARTING_QUALITY);

    @Test
    public void regular_item_quality_degrades_by_1_every_time_quality_is_updated() {

        Item regularItem = regularItemPrototype.build();
        GildedRose.addItem(regularItem);
        GildedRose.updateQuality();
        assertEquals(REGULAR_STARTING_QUALITY - REGULAR_DECREASING, regularItem.quality);
    }
    
    @Test
    public void after_selling_date_quality_decreases_twice_as_normal(){
        Item itemPastSellDate = regularItemPrototype.toSellWithin(-1).build();
        
        GildedRose.addItem(itemPastSellDate);
        GildedRose.updateQuality();
        assertEquals(REGULAR_STARTING_QUALITY - 2*REGULAR_DECREASING, itemPastSellDate.quality);
    }

    @Test
    public void on_selling_date_quality_decreases_twice_as_normal(){
        Item itemOnSellByDate = regularItemPrototype.toSellWithin(0).build();
        
        GildedRose.addItem(itemOnSellByDate);
        GildedRose.updateQuality();
        assertEquals(REGULAR_STARTING_QUALITY - 2 * REGULAR_DECREASING, itemOnSellByDate.quality);
    }
    

    @Test
    public void quality_wont_go_below_zero(){
        Item itemOfZeroQuality = regularItemPrototype.withQuality(0).build();
        
        GildedRose.addItem(itemOfZeroQuality);
        GildedRose.updateQuality();
        assertEquals(0, itemOfZeroQuality.quality);
    }
    
    @Test
    public void agedBrie_Item_increase_in_quality_with_age(){
        Item agedBrie = regularItemPrototype.named("Aged Brie").build();
        GildedRose.addItem(agedBrie);
        GildedRose.updateQuality();
        assertEquals(REGULAR_STARTING_QUALITY + REGULAR_DECREASING, agedBrie.quality);      
    }
    
    @Test
    public void inn_warehouse_remains_even_if_new_inn_created() {
        GildedRose.addItem(new Item("", 1, 1));
        GildedRose differentInnInAChain = new GildedRose();
        assertEquals(1, differentInnInAChain.getNumberOfItemsInStore());
    }

    @Before
    public void cleanup_before_test() {
        GildedRose.clearWarehouse();
    }
}
