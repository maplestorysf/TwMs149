package server;
/**
 *
 * @author pungin
 */
public enum CashItemFlag {
    //-----------------------------Mask[0]
    // 道具ID [0x1]
    ITEMID(0),
    // 數量 [0x2]
    COUNT(1),
    // 價錢(打折後) [0x4]
    PRICE(2),
    // 3 [0x8]    
    UNK3(3),
    // 排序 [0x10]
    PRIORITY(4),
    // 道具時間(什麼時間?限時?) [0x20]
    PERIOD(5),
    // [0x40]
    UNK6(6),
    // [0x80]
    MESO(7),
    // [0x100]
    UNK8(8),
    // 性別 [0x200]
    GENDER(9),
    // 出售中 [0x400]
    ONSALE(10),
    // 商品狀態[0x800] 0-NEW,1-SALE,2-HOT,3-EVENT,其他-無
    FLAGE(11),
    // 12 [0x1000]
    UNK12(12),
    // 13 [0x2000]
    UNK13(13),
    // 14 [0x4000]
    UNK14(14),
    // 15 [0x8000]
    UNK15(15),
    // 是否為禮包? [0x10000]
    PACKAGEZ(16),
    // 17 [0x20000]
    UNK17(17),;
    private final int code;
    private final int first;
    private CashItemFlag(int code) {
        this.code = 1 << (code % 32);
        this.first = (int) Math.floor(code / 32);
    }
    public int getPosition() {
        return first;
    }
    public int getValue() {
        return code;
    }
}