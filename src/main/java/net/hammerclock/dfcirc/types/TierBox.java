package net.hammerclock.dfcirc.types;

public enum TierBox {
   WOODEN(3),
   IRON(2),
   GOLD(1);

   private int numVal;

   private TierBox(int numVal) {
      this.numVal = numVal;
   }

   public int getNumVal() {
      return this.numVal;
   }
}
