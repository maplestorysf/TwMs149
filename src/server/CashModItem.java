package server;
import java.util.LinkedList;
import java.util.List;
/**
 *
 * @author pungin
 */
public class CashModItem extends CashItem {
    public List<CashItemFlag> flags = new LinkedList();
    private boolean main;
    private String note;
    public CashModItem(int sn, String note, int itemId, int count, int price, int period, int gender, int Class, boolean sale, boolean main) {
        super(sn, itemId, count, price, period, gender, Class, sale);
        this.note = note;
        this.main = main;
    }
    public void initFlags(CashItem ci) {
        if (ci != null && ci.getSN() != getSN()) {
            return;
        }
        if (ci == null || ci.getId() != getId()) {
            flags.add(CashItemFlag.ITEMID);
        }
        if (ci == null || ci.getCount() != getCount()) {
            flags.add(CashItemFlag.COUNT);
        }
        if (ci == null || ci.getPrice() != getPrice()) {
            flags.add(CashItemFlag.PRICE);
        }
        if (ci == null || ci.getPeriod() != getPeriod()) {
            flags.add(CashItemFlag.PERIOD);
        }
        if (ci == null || ci.getGender() != getGender()) {
            flags.add(CashItemFlag.GENDER);
        }
        if (ci == null || ci.isOnSale() != isOnSale()) {
            flags.add(CashItemFlag.ONSALE);
        }
      //  if (ci == null || ci.getFlage() != getFlage()) {
          //  flags.add(CashItemFlag.FLAGE);
        //}
    }
    public void setMainItem(boolean main) {
        this.main = main;
    }
    public boolean isMainItem() {
        return main;
    }
    public void setNote(String note) {
        this.note = note;
    }
    public String getNote() {
        return note;
    }
}