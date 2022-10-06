package server;

import database.DatabaseConnection;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import tools.Pair;

public class CashItemFactory {

    private final static CashItemFactory instance = new CashItemFactory();
    private final Map<Integer, CashItem> itemStats = new HashMap<>();
    private final Map<Integer, List<Integer>> itemPackage = new HashMap<>();
    private final Map<Integer, CashModItem> itemMods = new HashMap<>();
    private final Map<Integer, List<Integer>> openBox = new HashMap<>();
    private final Map<Integer, List<Pair<Integer, Integer>>> unkCoupon = new LinkedHashMap();
    private final Map<Integer, List<List<Pair<Integer, Integer>>>> unkCoupon2 = new LinkedHashMap();
    private final Map<Integer, Pair<List<Integer>, List<Integer>>> royaCoupon = new LinkedHashMap();
    private final MapleDataProvider data = MapleDataProviderFactory.getDataProvider("/Etc.wz");

    public static CashItemFactory getInstance() {
        return instance;
    }

    public void initialize(boolean reload) {
        if (reload) {
            itemStats.clear();
            itemPackage.clear();
            itemMods.clear();
            openBox.clear();
            unkCoupon.clear();
            unkCoupon2.clear();
            royaCoupon.clear();
        }
        if (!itemStats.isEmpty() || !itemPackage.isEmpty() || !itemMods.isEmpty() || !openBox.isEmpty() || !unkCoupon.isEmpty() || !unkCoupon2.isEmpty() || !royaCoupon.isEmpty()) {
            return;
        }
        final List<MapleData> cccc = data.getData("Commodity.img").getChildren();
        for (MapleData field : cccc) {
            final int SN = MapleDataTool.getIntConvert("SN", field, 0);

            final CashItem stats = new CashItem(SN,
                    MapleDataTool.getIntConvert("ItemId", field, 0),
                    MapleDataTool.getIntConvert("Count", field, 1),
                    MapleDataTool.getIntConvert("Price", field, 0),
                    MapleDataTool.getIntConvert("Period", field, 0),
                    MapleDataTool.getIntConvert("Gender", field, 2),
                    MapleDataTool.getIntConvert("Class", field, -1),
                    MapleDataTool.getIntConvert("OnSale", field, 0) > 0 && MapleDataTool.getIntConvert("Price", field, 0) > 0);
            if (SN > 0) {
                itemStats.put(SN, stats);
            }
        }

        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM cashshop_modified_items"); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CashModItem ret = new CashModItem(
                            rs.getInt("serial"),
                            "",
                            rs.getInt("itemid"),
                            rs.getInt("count"),
                            rs.getInt("discount_price"),
                            rs.getInt("period"),
                            rs.getInt("gender"),
                            rs.getInt("mark"),
                            rs.getInt("showup") > 0,
                            false
                    );
                    itemMods.put(ret.getSN(), ret);
                    if (ret.isOnSale()) {
                        final CashItem cc = itemStats.get(ret.getSN());
                        if (cc != null) {
                            ret.initFlags(cc); //init
                        }
                    }
                }
            }
        } catch (SQLException e) {
        }

        final MapleData b = data.getData("CashPackage.img");
        for (MapleData c : b.getChildren()) {
            if (c.getChildByPath("SN") == null) {
                continue;
            }
            final List<Integer> packageItems = new ArrayList<>();
            for (MapleData d : c.getChildByPath("SN").getChildren()) {
                packageItems.add(MapleDataTool.getIntConvert(d));
            }
            itemPackage.put(Integer.parseInt(c.getName()), packageItems);
        }

        List<Integer> availableSN = new LinkedList<>();
        availableSN.add(20001141);
        availableSN.add(20001142);
        availableSN.add(20001143);
        availableSN.add(20001144);
        availableSN.add(20001145);
        availableSN.add(20001146);
        availableSN.add(20001147);
        openBox.put(5533003, availableSN); // 七色彩紅帽箱

        availableSN = new LinkedList<>();
        availableSN.add(20000462);
        availableSN.add(20000463);
        availableSN.add(20000464);
        availableSN.add(20000465);
        availableSN.add(20000466);
        availableSN.add(20000467);
        availableSN.add(20000468);
        availableSN.add(20000469);
        openBox.put(5533000, availableSN); // 戰國壽司帽箱子

        availableSN = new LinkedList<>();
        availableSN.add(20800259);
        availableSN.add(20800260);
        availableSN.add(20800263);
        availableSN.add(20800264);
        availableSN.add(20800265);
        availableSN.add(20800267);
        openBox.put(5533001, availableSN); // 天使光射武器箱

        availableSN = new LinkedList<>();
        availableSN.add(20800270);
        availableSN.add(20800271);
        availableSN.add(20800272);
        availableSN.add(20800273);
        availableSN.add(20800274);
        openBox.put(5533002, availableSN); // 騎士團長的武器

        if (!unkCoupon.containsKey(5680157)) { // 楓之谷皇家風格
            unkCoupon.put(5680157, Arrays.asList(new Pair[]{
                new Pair(1702523, 1702523), // [晴天彩虹, 晴天彩虹]
                new Pair(1072934, 1072934), // [彩虹運動鞋, 彩虹運動鞋]
                new Pair(1082588, 1082588), // [彩虹彈珠, 彩虹彈珠]
                new Pair(1062207, 1062207), // [齁一波一短褲, 齁一波一短褲]
                new Pair(1042319, 1042319), // [齁一波一T恤, 齁一波一T恤]
                new Pair(1004180, 1004180), // [齁一波一帽子, 齁一波一帽子]
                new Pair(1702512, 1702512), // [主角的權杖, 主角的權杖]
                new Pair(1102688, 1102688), // [爆爆鞭炮, 爆爆鞭炮]
                new Pair(1071078, 1071078), // [玻璃高跟鞋, 玻璃高跟鞋]
                new Pair(1070061, 1070061), // [玻璃運動鞋, 玻璃運動鞋]
                new Pair(1051392, 1051392), // [派對公主, 派對公主]
                new Pair(1050322, 1050322), // [派對王子, 派對王子]
                new Pair(1004158, 1004158), // [LED老鼠髮圈, LED老鼠髮圈]
                new Pair(1702503, 1702503), // [氣泡氣泡 泡泡射擊, 氣泡氣泡 泡泡射擊]
                new Pair(1102674, 1102674), // [飯團大逃出騷動, 飯團大逃出騷動]
                new Pair(1071076, 1071076), // [花色郊遊鞋子, 花色郊遊鞋子]
                new Pair(1070059, 1070059), // [彩色郊遊鞋子, 彩色郊遊鞋子]
                new Pair(1051390, 1051390), // [迎春花郊遊, 迎春花郊遊]
                new Pair(1050319, 1050319), // [天空郊遊, 天空郊遊]
                new Pair(1001097, 1001097), // [花遊貝雷帽, 花遊貝雷帽]
                new Pair(1000074, 1000074), // [春遊貝雷帽, 春遊貝雷帽]
                new Pair(1702485, 1702485), // [今日的一袋子, 今日的一袋子]
                new Pair(1102669, 1102669), // [狂購神妖精, 狂購神妖精]
                new Pair(1072897, 1072897), // [藍色莫卡辛, 藍色莫卡辛]
                new Pair(1051382, 1051382), // [可愛的指引, 可愛的指引]
                new Pair(1050310, 1050310), // [閃亮指引, 閃亮指引]
                new Pair(1001095, 1001095), // [粉色咚咚, 粉色咚咚]
                new Pair(1000072, 1000072), // [藍色咚咚, 藍色咚咚]
                new Pair(1702486, 1702486), // [冬柏飄散著, 冬柏飄散著]
                new Pair(1102667, 1102667), // [雲上充滿月亮, 雲上充滿月亮]
                new Pair(1072901, 1072901), // [五色珠子, 五色珠子]
                new Pair(1051383, 1051383), // [佳人隱月, 佳人隱月]
                new Pair(1050311, 1050311), // [月光隱月, 月光隱月]
                new Pair(1001092, 1001092), // [隱月花簪子, 隱月花簪子]
                new Pair(1000069, 1000069), // [隱月花帽, 隱月花帽]
                new Pair(1702468, 1702468), // [巧克力棒, 巧克力棒]
                new Pair(1082565, 1082565), // [巧克力蝴蝶裝飾, 巧克力蝴蝶裝飾]
                new Pair(1072876, 1072876), // [小熊鞋, 小熊鞋]
                new Pair(1051372, 1051372), // [巧克女孩, 巧克女孩]
                new Pair(1050304, 1050304), // [巧克男孩, 巧克男孩]
                new Pair(1003998, 1003998), // [白巧克力兔耳, 白巧克力兔耳]
                new Pair(1702473, 1702473), // [黯影執行者, 黯影執行者]
                new Pair(1102632, 1102632), // [暗影贖罪者, 暗影贖罪者]
                new Pair(1071074, 1071074), // [黯影長靴, 黯影長靴]
                new Pair(1070057, 1070057), // [黯影羅馬鞋, 黯影羅馬鞋]
                new Pair(1051373, 1051373), // [血色黑大衣, 血色黑大衣]
                new Pair(1050305, 1050305), // [血腥黑外套, 血腥黑外套]
                new Pair(1004002, 1004002), // [黯影頭巾, 黯影頭巾]
                new Pair(1702464, 1702464), // [閃亮亮的朋友, 閃亮亮的朋友]
                new Pair(1102621, 1102621), // [嘮叨喇叭, 嘮叨喇叭]
                new Pair(1072868, 1072868), // [制服皮鞋, 制服皮鞋]
                new Pair(1051369, 1051369), // [女僕的矜持, 女僕的矜持]
                new Pair(1050302, 1050302), // [管家的品格, 管家的品格]
                new Pair(1003972, 1003972), // [藍蝴蝶結花邊帽子, 藍蝴蝶結花邊帽子]
                new Pair(1003971, 1003971), // [藍蝴蝶結禮帽, 藍蝴蝶結禮帽]
                new Pair(1702457, 1702457), // [美味冰斧, 美味冰斧]
                new Pair(1102619, 1102619), // [美味冰企鵝, 美味冰企鵝]
                new Pair(1072862, 1072862), // [愛心布丁托鞋, 愛心布丁托鞋]
                new Pair(1051367, 1051367), // [涼爽冰塊, 涼爽冰塊]
                new Pair(1050300, 1050300), // [涼爽冰塊, 涼爽冰塊]
                new Pair(1003958, 1003958), // [粉紅糯米冰, 粉紅糯米冰]
                new Pair(1003957, 1003957), // [冰薄荷綠糯米, 冰薄荷綠糯米]
                new Pair(1702451, 1702451), // [超級明星麥克風, 超級明星麥克風]
                new Pair(1102608, 1102608), // [超級明星鏡球, 超級明星鏡球]
                new Pair(1072852, 1072852), // [超級明星鞋, 超級明星鞋]
                new Pair(1051362, 1051362), // [超級明星洋裝, 超級明星洋裝]
                new Pair(1050296, 1050296), // [超級明星套裝, 超級明星套裝]
                new Pair(1003945, 1003945), // [超級明星王冠, 超級明星王冠]
                new Pair(1702442, 1702442), // [棒球棒, 棒球棒]
                new Pair(1102593, 1102593), // [飄浮棒球, 飄浮棒球]
                new Pair(1072836, 1072836), // [棒球鞋, 棒球鞋]
                new Pair(1051357, 1051357), // [粉紅棒球服, 粉紅棒球服]
                new Pair(1050291, 1050291), // [汀奇棒球, 汀奇棒球]
                new Pair(1003909, 1003909), // [粉紅蘇打帽, 粉紅蘇打帽]
                new Pair(1702433, 1702433), // [塞里曼德, 塞里曼德]
                new Pair(1102583, 1102583), // [小恐龍波比, 小恐龍波比]
                new Pair(1072831, 1072831), // [火焰靴, 火焰靴]
                new Pair(1051352, 1051352), // [泰勒米多勒, 泰勒米多勒]
                new Pair(1050285, 1050285), // [泰勒米多勒, 泰勒米多勒]
                new Pair(1003892, 1003892), // [躍升之鑽, 躍升之鑽]
                new Pair(1702424, 1702424), // [時尚鋼鐵, 時尚鋼鐵]
                new Pair(1082527, 1082527), // [高爾夫手套, 高爾夫手套]
                new Pair(1072823, 1072823), // [高爾夫運動鞋, 高爾夫運動鞋]
                new Pair(1042264, 1042264), // [卡拉高爾夫T恤, 卡拉高爾夫T恤]
                new Pair(1061206, 1061206), // [高爾夫短裙, 高爾夫短裙]
                new Pair(1060182, 1060182), // [高爾夫短褲, 高爾夫短褲]
                new Pair(1003867, 1003867), // [神射帽, 神射帽]
                new Pair(1702415, 1702415), // [作夢的糖果枕頭, 作夢的糖果枕頭]
                new Pair(1072808, 1072808), // [小羊拖鞋, 小羊拖鞋]
                new Pair(1082520, 1082520), // [小羊毛手套, 小羊毛手套]
                new Pair(1052605, 1052605), // [小羊睡袍, 小羊睡袍]
                new Pair(1003831, 1003831), // [小羊髮夾, 小羊髮夾]
                new Pair(1702406, 1702406), // [殞落魔法方塊, 殞落魔法方塊]
                new Pair(1102537, 1102537), // [魔法之星斗篷, 魔法之星斗篷]
                new Pair(1051347, 1051347), // [魔法之星洋裝, 魔法之星洋裝]
                new Pair(1050283, 1050283), // [魔法之星套裝, 魔法之星套裝]
                new Pair(1003809, 1003809), // [神秘黑蝴蝶結, 神秘黑蝴蝶結]
                new Pair(1003808, 1003808), // [神秘黑絲帽, 神秘黑絲帽]
            }));
        }
        if (!unkCoupon.containsKey(5680159)) { // 楓之谷皇家風格
            unkCoupon.put(5680159, Arrays.asList(new Pair[]{
                new Pair(1702523, 1702523), // [晴天彩虹, 晴天彩虹]
                new Pair(1072934, 1072934), // [彩虹運動鞋, 彩虹運動鞋]
                new Pair(1082588, 1082588), // [彩虹彈珠, 彩虹彈珠]
                new Pair(1062207, 1062207), // [齁一波一短褲, 齁一波一短褲]
                new Pair(1042319, 1042319), // [齁一波一T恤, 齁一波一T恤]
                new Pair(1004180, 1004180), // [齁一波一帽子, 齁一波一帽子]
                new Pair(1052762, 1052762), // [香蕉背帶, 香蕉背帶]
                new Pair(1072838, 1072838), // [貓熊拖鞋, 貓熊拖鞋]
                new Pair(1062175, 1062175), // [撲通撲通貼身牛仔褲, 撲通撲通貼身牛仔褲]
                new Pair(1062208, 1062208), // [膝蓋兔寶貝褲子, 膝蓋兔寶貝褲子]
                new Pair(1042216, 1042216), // [楓葉小隊棒球上衣, 楓葉小隊棒球上衣]
                new Pair(1042125, 1042125), // [小兔包包黃色T恤, 小兔包包黃色T恤]
                new Pair(1003807, 1003807), // [愛心墨鏡, 愛心墨鏡]
                new Pair(1002598, 1002598), // [可愛兔子耳朵, 可愛兔子耳朵]
                new Pair(1003912, 1003912), // [今天是小狗！, 今天是小狗！]
                new Pair(1003586, 1003586), // [薄荷星馬藍帽, 薄荷星馬藍帽]
                new Pair(1004181, 1004181), // [糖果派對色帶髮圈, 糖果派對色帶髮圈]
                new Pair(1702525, 1702525), // [料理的完成, 料理的完成]
                new Pair(1702405, 1702405), // [星光愛心魔術棒, 星光愛心魔術棒]
                new Pair(1702443, 1702443), // [人偶師的詛咒, 人偶師的詛咒]
                new Pair(1102700, 1102700), // [小希爾, 小希爾]
                new Pair(1102644, 1102644), // [小精靈, 小精靈]
                new Pair(1102547, 1102547), // [紫水晶的夢想, 紫水晶的夢想]
                new Pair(1012437, 1012437), // [手掌臉部裝飾, 手掌臉部裝飾]
                new Pair(1022230, 1022230), // [巴尼玻璃, 巴尼玻璃]
                new Pair(5530638, 5530638), // [[30天]水墨畫泡泡戒指交換券, [30天]水墨畫泡泡戒指交換券]
                new Pair(5530637, 5530637), // [[30天]水墨畫名牌戒指交換券, [30天]水墨畫名牌戒指交換券]
                new Pair(5680262, 5680262), // [我愛法國效果道具交換券, 我愛法國效果道具交換券]
                new Pair(5065100, 5065100), // [特別版連發煙火, 特別版連發煙火]
                new Pair(5121033, 5121033), // [星星糖果, 星星糖果]
                new Pair(5450010, 5450010), // [貓商人奈洛, 貓商人奈洛]
                new Pair(5390009, 5390009), // [名叫朋友的喇叭, 名叫朋友的喇叭]
                new Pair(5390000, 5390000), // [炎熱喇叭, 炎熱喇叭]
            }));
        }
        if (!unkCoupon2.containsKey(5069001)) { // 優質大師零件
            unkCoupon2.put(5069001, Arrays.asList(new List[]{
                Arrays.asList(new Pair[]{
                    new Pair(5530697, 5530698), // [閃亮蝴蝶美髮券(男)交換券, 閃亮蝴蝶美髮券(女)交換券]
                    new Pair(1102729, 1102729), // [閃耀燈籠, 閃耀燈籠]
                    new Pair(1102748, 1102748), // [兔子熊露營袋, 兔子熊露營袋]
                    new Pair(1102712, 1102712), // [輾轉不寐的度假勝地, 輾轉不寐的度假勝地]
                    new Pair(1102706, 1102706), // [叮咚歐洛拉, 叮咚歐洛拉]
                    new Pair(1082588, 1082588), // [彩虹彈珠, 彩虹彈珠]
                    new Pair(1102688, 1102688), // [爆爆鞭炮, 爆爆鞭炮]
                    new Pair(1102674, 1102674), // [飯團大逃出騷動, 飯團大逃出騷動]
                    new Pair(1102669, 1102669), // [狂購神妖精, 狂購神妖精]
                    new Pair(1102667, 1102667), // [雲上充滿月亮, 雲上充滿月亮]
                    new Pair(1082565, 1082565), // [巧克力蝴蝶裝飾, 巧克力蝴蝶裝飾]
                }),
                Arrays.asList(new Pair[]{
                    new Pair(5530697, 5530698), // [閃亮蝴蝶美髮券(男)交換券, 閃亮蝴蝶美髮券(女)交換券]
                    new Pair(1000076, 1001098), // [赤紅黃昏, 藍色新野]
                    new Pair(1004279, 1004279), // [松鼠帽, 松鼠帽]
                    new Pair(1004213, 1004213), // [呼啦呼啦羽毛裝飾, 呼啦呼啦羽毛裝飾]
                    new Pair(1004192, 1004192), // [DoReMi耳機, DoReMi耳機]
                    new Pair(1004180, 1004180), // [齁一波一帽子, 齁一波一帽子]
                    new Pair(1004158, 1004158), // [LED老鼠髮圈, LED老鼠髮圈]
                    new Pair(1000074, 1001097), // [春遊貝雷帽, 花遊貝雷帽]
                    new Pair(1000072, 1001095), // [藍色咚咚, 粉色咚咚]
                    new Pair(1000069, 1001092), // [隱月花帽, 隱月花簪子]
                    new Pair(1003998, 1003998), // [白巧克力兔耳, 白巧克力兔耳]
                }),
                Arrays.asList(new Pair[]{
                    new Pair(5530697, 5530698), // [閃亮蝴蝶美髮券(男)交換券, 閃亮蝴蝶美髮券(女)交換券]
                    new Pair(1702538, 1702538), // [露水燈籠, 露水燈籠]
                    new Pair(1702540, 1702540), // [在這裡！手電筒, 在這裡！手電筒]
                    new Pair(1702535, 1702535), // [呼啦呼啦小企鵝, 呼啦呼啦小企鵝]
                    new Pair(1702528, 1702528), // [木琴旋律, 木琴旋律]
                    new Pair(1702523, 1702523), // [晴天彩虹, 晴天彩虹]
                    new Pair(1702512, 1702512), // [主角的權杖, 主角的權杖]
                    new Pair(1702503, 1702503), // [氣泡氣泡 泡泡射擊, 氣泡氣泡 泡泡射擊]
                    new Pair(1702485, 1702485), // [今日的一袋子, 今日的一袋子]
                    new Pair(1702486, 1702486), // [冬柏飄散著, 冬柏飄散著]
                    new Pair(1702468, 1702468), // [巧克力棒, 巧克力棒]
                }),
                Arrays.asList(new Pair[]{
                    new Pair(5530697, 5530698), // [閃亮蝴蝶美髮券(男)交換券, 閃亮蝴蝶美髮券(女)交換券]
                    new Pair(1050339, 1051408), // [光輝燈籠, 燦爛燈籠]
                    new Pair(1050341, 1051410), // [叢林露營造型, 叢林露營造型]
                    new Pair(1050337, 1051406), // [夏威夷情侶, 夏威夷情侶]
                    new Pair(1050335, 1051405), // [旋律少年, 旋律少女]
                    new Pair(1062207, 1062207), // [齁一波一短褲, 齁一波一短褲]
                    new Pair(1042319, 1042319), // [齁一波一T恤, 齁一波一T恤]
                    new Pair(1050322, 1051392), // [派對王子, 派對公主]
                    new Pair(1050319, 1051390), // [天空郊遊, 迎春花郊遊]
                    new Pair(1050310, 1051382), // [閃亮指引, 可愛的指引]
                    new Pair(1050311, 1051383), // [月光隱月, 佳人隱月]
                    new Pair(1050304, 1051372), // [巧克男孩, 巧克女孩]
                }),
                Arrays.asList(new Pair[]{
                    new Pair(5530697, 5530698), // [閃亮蝴蝶美髮券(男)交換券, 閃亮蝴蝶美髮券(女)交換券]
                    new Pair(1072978, 1072978), // [燈籠唐鞋, 燈籠唐鞋]
                    new Pair(1072998, 1072998), // [兔子熊拖鞋, 兔子熊拖鞋]
                    new Pair(1072951, 1072951), // [呼啦呼啦串珠腳環, 呼啦呼啦串珠腳環]
                    new Pair(1072943, 1072943), // [合鳴鞋, 合鳴鞋]
                    new Pair(1072934, 1072934), // [彩虹運動鞋, 彩虹運動鞋]
                    new Pair(1070061, 1071078), // [玻璃運動鞋, 玻璃高跟鞋]
                    new Pair(1070059, 1071076), // [彩色郊遊鞋子, 花色郊遊鞋子]
                    new Pair(1072897, 1072897), // [藍色莫卡辛, 藍色莫卡辛]
                    new Pair(1072901, 1072901), // [五色珠子, 五色珠子]
                    new Pair(1072876, 1072876), // [小熊鞋, 小熊鞋]
                }),}));
        }
        if (!unkCoupon2.containsKey(5069000)) { // 大師零件
            unkCoupon2.put(5069000, Arrays.asList(new List[]{
                Arrays.asList(new Pair[]{
                    new Pair(5530697, 5530698), // [閃亮蝴蝶美髮券(男)交換券, 閃亮蝴蝶美髮券(女)交換券]
                    new Pair(1102729, 1102729), // [閃耀燈籠, 閃耀燈籠]
                    new Pair(1102748, 1102748), // [兔子熊露營袋, 兔子熊露營袋]
                    new Pair(1102712, 1102712), // [輾轉不寐的度假勝地, 輾轉不寐的度假勝地]
                    new Pair(1102706, 1102706), // [叮咚歐洛拉, 叮咚歐洛拉]
                    new Pair(1082588, 1082588), // [彩虹彈珠, 彩虹彈珠]
                    new Pair(1102688, 1102688), // [爆爆鞭炮, 爆爆鞭炮]
                    new Pair(1102674, 1102674), // [飯團大逃出騷動, 飯團大逃出騷動]
                    new Pair(1102669, 1102669), // [狂購神妖精, 狂購神妖精]
                    new Pair(1102667, 1102667), // [雲上充滿月亮, 雲上充滿月亮]
                    new Pair(1082565, 1082565), // [巧克力蝴蝶裝飾, 巧克力蝴蝶裝飾]
                }),
                Arrays.asList(new Pair[]{
                    new Pair(5530697, 5530698), // [閃亮蝴蝶美髮券(男)交換券, 閃亮蝴蝶美髮券(女)交換券]
                    new Pair(1000076, 1001098), // [赤紅黃昏, 藍色新野]
                    new Pair(1004279, 1004279), // [松鼠帽, 松鼠帽]
                    new Pair(1004213, 1004213), // [呼啦呼啦羽毛裝飾, 呼啦呼啦羽毛裝飾]
                    new Pair(1004192, 1004192), // [DoReMi耳機, DoReMi耳機]
                    new Pair(1004180, 1004180), // [齁一波一帽子, 齁一波一帽子]
                    new Pair(1004158, 1004158), // [LED老鼠髮圈, LED老鼠髮圈]
                    new Pair(1000074, 1001097), // [春遊貝雷帽, 花遊貝雷帽]
                    new Pair(1000072, 1001095), // [藍色咚咚, 粉色咚咚]
                    new Pair(1000069, 1001092), // [隱月花帽, 隱月花簪子]
                    new Pair(1003998, 1003998), // [白巧克力兔耳, 白巧克力兔耳]
                }),
                Arrays.asList(new Pair[]{
                    new Pair(5530697, 5530698), // [閃亮蝴蝶美髮券(男)交換券, 閃亮蝴蝶美髮券(女)交換券]
                    new Pair(1702538, 1702538), // [露水燈籠, 露水燈籠]
                    new Pair(1702540, 1702540), // [在這裡！手電筒, 在這裡！手電筒]
                    new Pair(1702535, 1702535), // [呼啦呼啦小企鵝, 呼啦呼啦小企鵝]
                    new Pair(1702528, 1702528), // [木琴旋律, 木琴旋律]
                    new Pair(1702523, 1702523), // [晴天彩虹, 晴天彩虹]
                    new Pair(1702512, 1702512), // [主角的權杖, 主角的權杖]
                    new Pair(1702503, 1702503), // [氣泡氣泡 泡泡射擊, 氣泡氣泡 泡泡射擊]
                    new Pair(1702485, 1702485), // [今日的一袋子, 今日的一袋子]
                    new Pair(1702486, 1702486), // [冬柏飄散著, 冬柏飄散著]
                    new Pair(1702468, 1702468), // [巧克力棒, 巧克力棒]
                }),
                Arrays.asList(new Pair[]{
                    new Pair(5530697, 5530698), // [閃亮蝴蝶美髮券(男)交換券, 閃亮蝴蝶美髮券(女)交換券]
                    new Pair(1050339, 1051408), // [光輝燈籠, 燦爛燈籠]
                    new Pair(1050341, 1051410), // [叢林露營造型, 叢林露營造型]
                    new Pair(1050337, 1051406), // [夏威夷情侶, 夏威夷情侶]
                    new Pair(1050335, 1051405), // [旋律少年, 旋律少女]
                    new Pair(1062207, 1062207), // [齁一波一短褲, 齁一波一短褲]
                    new Pair(1042319, 1042319), // [齁一波一T恤, 齁一波一T恤]
                    new Pair(1050322, 1051392), // [派對王子, 派對公主]
                    new Pair(1050319, 1051390), // [天空郊遊, 迎春花郊遊]
                    new Pair(1050310, 1051382), // [閃亮指引, 可愛的指引]
                    new Pair(1050311, 1051383), // [月光隱月, 佳人隱月]
                    new Pair(1050304, 1051372), // [巧克男孩, 巧克女孩]
                }),
                Arrays.asList(new Pair[]{
                    new Pair(5530697, 5530698), // [閃亮蝴蝶美髮券(男)交換券, 閃亮蝴蝶美髮券(女)交換券]
                    new Pair(1072978, 1072978), // [燈籠唐鞋, 燈籠唐鞋]
                    new Pair(1072998, 1072998), // [兔子熊拖鞋, 兔子熊拖鞋]
                    new Pair(1072951, 1072951), // [呼啦呼啦串珠腳環, 呼啦呼啦串珠腳環]
                    new Pair(1072943, 1072943), // [合鳴鞋, 合鳴鞋]
                    new Pair(1072934, 1072934), // [彩虹運動鞋, 彩虹運動鞋]
                    new Pair(1070061, 1071078), // [玻璃運動鞋, 玻璃高跟鞋]
                    new Pair(1070059, 1071076), // [彩色郊遊鞋子, 花色郊遊鞋子]
                    new Pair(1072897, 1072897), // [藍色莫卡辛, 藍色莫卡辛]
                    new Pair(1072901, 1072901), // [五色珠子, 五色珠子]
                    new Pair(1072876, 1072876), // [小熊鞋, 小熊鞋]
                }),}));
        }
        if (!royaCoupon.containsKey(5150115)) { // 聖誕節美髮券
            royaCoupon.put(5150115, new Pair(
                    Arrays.asList(new Integer[]{ // 男
                33260, // 黑色那因哈特造型
                30850, // 黑色玉米鬚造型
                30810, // 黑色火爆浪子造型
                30950, // 黑色波浪劉海造型
                33000, // 黑色金賢重造型
                33080, // 黑色狂狼勇士造型
                33160, // 黑色莉琳造型
            }),
                    Arrays.asList(new Integer[]{ // 女
                34090, // 黑色狂狼勇士造型
                34160, // 黑色莉琳造型
                34110, // 黑色幻影造型
                34330, // 黑色嬌小紮尾造型
                32140, // 黑色髮尾大捲造型
                34120, // 黑色波希米亞造型
                37630, // 黑色貴族千金造型
            })
            ));
        }
        if (!royaCoupon.containsKey(5150067)) { // 頂級皇家美髮券
            royaCoupon.put(5150067, new Pair(
                    Arrays.asList(new Integer[]{ // 男
                33750, // 黑色簡約瀏海造型
                33720, // 黑色側分飄逸造型
                33680, // 黑色皇族紮髮造型
                33710, // 黑色活力小紮造型
                30530, // 黑色高額頭造型
                30620, // 黑色日本武士造型
                30840, // 黑色朱里昂造型
                30790, // 黑色獅子短鬃造型
            }),
                    Arrays.asList(new Integer[]{ // 女
                34660, // 黑色雙角捲造型
                34750, // 黑色蝴蝶側尾造型
                34450, // 黑色垂耳小貓造型
                34720, // 黑色蓬鬆髮辮造型
                34590, // 黑色清新小紮造型
                34350, // 黑色桂冠髮圈造型
                34630, // 黑色優雅女皇造型
                34680, // 黑色熱舞少女造型
            })
            ));
        }
        if (!royaCoupon.containsKey(5150043)) { // 通用高級美髮券
            royaCoupon.put(5150043, new Pair(
                    Arrays.asList(new Integer[]{ // 男
                30160, // 黑色山本頭造型
                30910, // 黑色短小捲造型
                30200, // 黑色微捲設計造型
                30210, // 黑色露耳清爽造型
                30220, // 黑色貓王造型
                30260, // 黑色左旋旁分造型
                30300, // 黑色齊眉自然造型
                30310, // 黑色西瓜皮造型
                30320, // 黑色黑人微捲造型
                30410, // 黑色順風微捲造型
                30460, // 黑色旋風捲造型
                30470, // 黑色天然短捲造型
                30480, // 黑色時髦順髮造型
                30490, // 黑色運動短髮造型
                30510, // 黑色捲翹瀏海造型
                30630, // 黑色蓬鬆散羽造型
                30730, // 黑色迎風飄逸造型
                30760, // 黑色馬桶蓋造型
                30830, // 黑色亞里斯造型
                30880, // 黑色時尚瀏海造型
            }),
                    Arrays.asList(new Integer[]{ // 女
                31020, // 黑色大波浪捲造型
                31060, // 黑色髮尾上翹造型
                31110, // 黑色波浪馬尾造型
                31140, // 黑色傳統蒼龍造型
                31150, // 黑色挑染長髮造型
                31160, // 黑色青春小髻造型
                31220, // 黑色瀏海半遮造型
                31230, // 黑色俏麗捲辮造型
                31240, // 黑色披頭散髮造型
                31880, // 黑色堅強少女造型
                31330, // 黑色濃密直髮造型
                31470, // 黑色中國女孩造型
                31610, // 黑色瑞迪亞造型
                31460, // 黑色洋蔥短剪造型
                31340, // 黑色層次短剪造型
                31490, // 黑色隨意盤髮造型
                31310, // 黑色齊眉波浪造型
                31290, // 黑色活潑短髮造型
                31640, // 黑色迎風柔順造型
                31810, // 黑色蘋果短髮造型
            })
            ));
        }
        if (!royaCoupon.containsKey(5152083)) { // 風暴整型卷
            royaCoupon.put(5152083, new Pair(
                    Arrays.asList(new Integer[]{ // 男
                20030, // 黑色有自信臉型
                20051, // 黑色狐狸眼臉型
                20040, // 黑色明亮的臉型
                20033, // 黑色很搶眼臉型
                20021, // 黑色笑咪咪臉型
                20050, // 黑色看不見臉型
                20038, // 黑色好俏皮臉型
            }),
                    Arrays.asList(new Integer[]{ // 女
                21078, // 黑色寧靜的臉型
                21064, // 黑色扁嘴的臉型
                21045, // 黑色卡通化臉型
                21038, // 黑色明亮的臉型
                21009, // 黑色不理人臉型
                21022, // 黑色丹鳳眼臉型
                21043, // 黑色很叛逆臉型
            })
            ));
        }
        if (!royaCoupon.containsKey(5152203)) { // 中秋節整型券
            royaCoupon.put(5152203, new Pair(
                    Arrays.asList(new Integer[]{ // 男
                20090, // 黑色張望的臉型
                20081, // 黑色很純粹臉型
                20077, // 黑色天使臉型
                20080, // 黑色閃星光臉型
                20088, // 黑色讀書人臉型
                20033, // 黑色很搶眼臉型
                20030, // 黑色有自信臉型
            }),
                    Arrays.asList(new Integer[]{ // 女
                21091, // 黑色玻璃眼臉型
                21065, // 黑色西格諾斯臉型
                21064, // 黑色扁嘴的臉型
                21077, // 黑色放大眼臉型
                21078, // 黑色寧靜的臉型
                21063, // 黑色超新星臉型
                21062, // 黑色聰明的臉型
            })
            ));
        }
        if (!royaCoupon.containsKey(5150084)) { // 風暴髮型卷
            royaCoupon.put(5150084, new Pair(
                    Arrays.asList(new Integer[]{ // 男
                30810, // 黑色火爆浪子造型
                33090, // 黑色獅子長鬃造型
                33140, // 黑色帥氣小髻造型
                33160, // 黑色莉琳造型
                33200, // 黑色影武者造型
                33640, // 黑色酷帥逆風造型
                33950, // 黑色斜遮短髮造型
            }),
                    Arrays.asList(new Integer[]{ // 女
                34090, // 黑色狂狼勇士造型
                34150, // 黑色帥氣小髻造型
                34110, // 黑色幻影造型
                34240, // 黑色蘋果長髮造型
                31370, // 黑色浪漫編織造型
                34910, // 黑色俏麗流行造型
                34560, // 黑色自然梳理造型
            })
            ));
        }
        if (!royaCoupon.containsKey(5152084)) { // 春節整型卷
            royaCoupon.put(5152084, new Pair(
                    Arrays.asList(new Integer[]{ // 男
                20077, // 黑色天使臉型
                20045, // 黑色很叛逆臉型
                20043, // 黑色雙色瞳臉型
                20030, // 黑色有自信臉型
                20011, // 黑色粗眉毛臉型
                20050, // 黑色看不見臉型
                20010, // 黑色不耐煩臉型
            }),
                    Arrays.asList(new Integer[]{ // 女
                21077, // 黑色放大眼臉型
                21063, // 黑色超新星臉型
                21042, // 黑色惹人愛臉型
                21028, // 黑色好清純臉型
                21010, // 黑色不耐煩臉型
                21048, // 黑色看不見臉型
                21036, // 黑色好俏皮臉型
            })
            ));
        }
        if (!royaCoupon.containsKey(5152092)) { // 端午節整型卷
            royaCoupon.put(5152092, new Pair(
                    Arrays.asList(new Integer[]{ // 男
                20070, // 黑色那因哈特臉型
                20081, // 黑色很純粹臉型
                20046, // 黑色朦朧美臉型
                20030, // 黑色有自信臉型
                20010, // 黑色不耐煩臉型
            }),
                    Arrays.asList(new Integer[]{ // 女
                21078, // 黑色寧靜的臉型
                21065, // 黑色西格諾斯臉型
                21063, // 黑色超新星臉型
                21062, // 黑色聰明的臉型
                21058, // 黑色乖女孩臉型
            })
            ));
        }
        if (!royaCoupon.containsKey(5150108)) { // 中秋節髮型券
            royaCoupon.put(5150108, new Pair(
                    Arrays.asList(new Integer[]{ // 男
                36580, // 黑色棉花糖造型
                33000, // 黑色金賢重造型
                33520, // 黑色花花公子造型
                33470, // 黑色帥氣日系造型
                33780, // 黑色自然梳理造型
                36130, // 黑色史烏造型
                36390, // 黑色蓬鬆有型造型
            }),
                    Arrays.asList(new Integer[]{ // 女
                37630, // 黑色貴族千金造型
                34100, // 黑色蝴蝶髮束造型
                34780, // 黑色希拉造型
                37030, // 黑色花香波浪造型
                37000, // 黑色殺人鯨造型
                31940, // 黑色小紮波浪造型
                34210, // 黑色單紮波浪造型
            })
            ));
        }
        if (!royaCoupon.containsKey(5150140)) { // 逆轉髮型券
            royaCoupon.put(5150140, new Pair(
                    Arrays.asList(new Integer[]{ // 男
                40140, // 黑色酸毛線造型
                40150, // 黑色可愛風格頭
                40160, // 黑色蓬鬆束髮造型
                40130, // 黑色水晶造型
                40170, // 黑色星星仙女造型
                40180, // 黑色甜姐兒造型
                40190, // 黑色雙束造型
                40200, // 黑色小紮波浪造型
                40210, // 黑色單辮瀏海造型
                40220, // 黑色華爾滋造型
                40230, // 黑色飄逸內捲造型
            }),
                    Arrays.asList(new Integer[]{ // 女
                41230, // 黑海造型
                41240, // 黑色那因哈特造型
                41250, // 黑色飛翹年輕造型
                41260, // 黑色藝術氣息造型
                41270, // 黑色酷帥逆風造型
                41280, // 黑色隨風飛舞造型
                41290, // 黑色蜜糖男孩造型
                41300, // 黑色瑪哈造型
                41310, // 黑色染尾亂捲造型
                41320, // 黑色層次散羽造型
                41330, // 黑色橡果造型
            })
            ));
        }
        if (!royaCoupon.containsKey(5152204)) { // 橘子節整形卷
            royaCoupon.put(5152204, new Pair(
                    Arrays.asList(new Integer[]{ // 男
                20081, // 黑色很純粹臉型
                20080, // 黑色閃星光臉型
                20043, // 黑色雙色瞳臉型
                20011, // 黑色粗眉毛臉型
                20050, // 黑色看不見臉型
                20097, // 黑色很憂慮臉型
                20095, // 黑色勾魂眼臉型
                20070, // 黑色那因哈特臉型
                20051, // 黑色狐狸眼臉型
                20090, // 黑色張望的臉型
            }),
                    Arrays.asList(new Integer[]{ // 女
                21011, // 黑色大眼睛臉型
                21041, // 黑色雙色瞳臉型
                21058, // 黑色乖女孩臉型
                21063, // 黑色超新星臉型
                21065, // 黑色西格諾斯臉型
                21091, // 黑色玻璃眼臉型
                21084, // 黑色大小姐臉型
                21078, // 黑色寧靜的臉型
                21043, // 黑色很叛逆臉型
                21092, // 黑色輕柔的臉型
            })
            ));
        }
        if (!royaCoupon.containsKey(5150085)) { // 春節髮型卷
            royaCoupon.put(5150085, new Pair(
                    Arrays.asList(new Integer[]{ // 男
                33250, // 黑色妖精翁造型
                33310, // 黑色飆風狂野造型
                36020, // 黑色強力雕塑造型
                33780, // 黑色自然梳理造型
                36160, // 黑色遮頭蓋面造型
                33650, // 黑色浪漫義式造型
                33940, // 黑色古惑性格造型
            }),
                    Arrays.asList(new Integer[]{ // 女
                32150, // 黑色雪莉造型
                34440, // 黑色嬰兒捲造型
                34720, // 黑色蓬鬆髮辮造型
                34750, // 黑色蝴蝶側尾造型
                31680, // 黑色長髮娃娃造型
                34330, // 黑色嬌小紮尾造型
                34270, // 黑色馬奧奈特造型
            })
            ));
        }
        if (!royaCoupon.containsKey(5152053)) { // 皇家整形券
            royaCoupon.put(5152053, new Pair(
                    Arrays.asList(new Integer[]{ // 男
                23018, // 黑色奧妙的臉
                23025, // 黑色小嘟嘴臉型
                20033, // 黑色很搶眼臉型
                20097, // 黑色很憂慮臉型
                23003, // 黑色倫多臉型
                20021, // 黑色笑咪咪臉型
            }),
                    Arrays.asList(new Integer[]{ // 女
                24058, // 黑色莉莉臉
                24026, // 黑色甜蜜的臉型
                21010, // 黑色不耐煩臉型
                21033, // 黑色水汪汪臉型
                21054, // 黑色娃娃臉臉型
                21093, // 黑色閃亮亮臉型
            })
            ));
        }
        if (!royaCoupon.containsKey(5150117)) { // 冬季髮型券
            royaCoupon.put(5150117, new Pair(
                    Arrays.asList(new Integer[]{ // 男
                33150, // 黑色龍魔導士造型
                33160, // 黑色莉琳造型
                33240, // 黑色自然短髮造型
                33310, // 黑色飆風狂野造型
                33320, // 黑色狂豹獵人造型
                32140, // 黑色髮尾大捲造型
                33740, // 黑色柔順輕揚造型
            }),
                    Arrays.asList(new Integer[]{ // 女
                31990, // 黑色龍魔導士造型
                34160, // 黑色莉琳造型
                34210, // 黑色單紮波浪造型
                34290, // 黑色煉獄巫師造型
                34300, // 黑色狂豹獵人造型
                32140, // 黑色髮尾大捲造型
                34770, // 黑色艾麗亞造型
            })
            ));
        }
        if (!royaCoupon.containsKey(5152205)) { // 聖誕節美容券
            royaCoupon.put(5152205, new Pair(
                    Arrays.asList(new Integer[]{ // 男
                20080, // 黑色閃星光臉型
                20082, // 黑色守護者臉型
                20090, // 黑色張望的臉型
                20070, // 黑色那因哈特臉型
                20045, // 黑色很叛逆臉型
                20038, // 黑色好俏皮臉型
                20088, // 黑色讀書人臉型
            }),
                    Arrays.asList(new Integer[]{ // 女
                21011, // 黑色大眼睛臉型
                21038, // 黑色明亮的臉型
                21045, // 黑色卡通化臉型
                21058, // 黑色乖女孩臉型
                21063, // 黑色超新星臉型
                21065, // 黑色西格諾斯臉型
                21078, // 黑色寧靜的臉型
            })
            ));
        }
        if (!royaCoupon.containsKey(5150086)) { // 匈奴族長髮型卷
            royaCoupon.put(5150086, new Pair(
                    Arrays.asList(new Integer[]{ // 男
                32430, // 黑色雙奶油捲造型
                32450, // 黑色頂尖山峰造型
                32440, // 黑色飄逸海草造型
                32460, // 黑色核爆事故造型
                32470, // 黑色雙奶油捲造型
                32480, // 黑色飄逸海草造型
                32490, // 黑色頂尖山峰造型
                32500, // 黑色核爆事故造型
            }),
                    Arrays.asList(new Integer[]{ // 女
                32430, // 黑色雙奶油捲造型
                32450, // 黑色頂尖山峰造型
                32440, // 黑色飄逸海草造型
                32460, // 黑色核爆事故造型
                32470, // 黑色雙奶油捲造型
                32480, // 黑色飄逸海草造型
                32490, // 黑色頂尖山峰造型
                32500, // 黑色核爆事故造型
            })
            ));
        }
        if (!royaCoupon.containsKey(5150102)) { // 閃亮髮型卷
            royaCoupon.put(5150102, new Pair(
                    Arrays.asList(new Integer[]{ // 男
                33750, // 黑色簡約瀏海造型
                36390, // 黑色蓬鬆有型造型
                36340, // 黑色復古瀏海造型
                36070, // 黑色伊卡勒特造型
                36350, // 黑色閃亮瀏海造型
                36130, // 黑色史烏造型
                33930, // 黑色粗曠造型
                36160, // 黑色遮頭蓋面造型
                33800, // 黑色火焰瀏海造型
            }),
                    Arrays.asList(new Integer[]{ // 女
                37220, // 黑色包脖長髮造型
                37100, // 黑色桃樂絲造型
                37310, // 黑色珍珠小結造型
                34940, // 黑色西格諾斯造型
                34160, // 黑色莉琳造型
                34150, // 黑色帥氣小髻造型
                34100, // 黑色蝴蝶髮束造型
                31680, // 黑色長髮娃娃造型
                34270, // 黑色馬奧奈特造型
            })
            ));
        }
        if (!royaCoupon.containsKey(5152206)) { // 冬季整形券
            royaCoupon.put(5152206, new Pair(
                    Arrays.asList(new Integer[]{ // 男
                20043, // 黑色雙色瞳臉型
                20044, // 黑色惹人愛臉型
                20047, // 黑色卡通化臉型
                20080, // 黑色閃星光臉型
                20040, // 黑色明亮的臉型
                20049, // 黑色高傲精靈臉型
                20048, // 黑色惡魔殺手臉型
            }),
                    Arrays.asList(new Integer[]{ // 女
                21041, // 黑色雙色瞳臉型
                21042, // 黑色惹人愛臉型
                21045, // 黑色卡通化臉型
                21064, // 黑色扁嘴的臉型
                21038, // 黑色明亮的臉型
                21047, // 黑色高傲精靈臉型
                21046, // 黑色惡魔殺手臉型
            })
            ));
        }
        if (!royaCoupon.containsKey(5150142)) { // 曉之陣改編紀念髮型券
            royaCoupon.put(5150142, new Pair(
                    Arrays.asList(new Integer[]{ // 男
                40070, // null
                40080, // null
                35820, // 陰陽師長髮造型(黑)
                35830, // 陰陽師馬尾造型(黑)
            }),
                    Arrays.asList(new Integer[]{ // 女
                38890, // 女劍客長髮造型(黑)
                38900, // 女劍客短髮造型(黑)
                41170, // null
                41180, // null
            })
            ));
        }
        if (!royaCoupon.containsKey(5152071)) { // 頂級皇家整型券
            royaCoupon.put(5152071, new Pair(
                    Arrays.asList(new Integer[]{ // 男
                20032, // 黑色平靜的臉型
                20023, // 
                20046, // 黑色朦朧美臉型
                20053, // 黑色克勞烏臉型
                20011, // 黑色粗眉毛臉型
                20030, // 黑色有自信臉型
                20050, // 黑色看不見臉型
                20051, // 黑色狐狸眼臉型
            }),
                    Arrays.asList(new Integer[]{ // 女
                21021, // 黑色鬥雞眼臉型
                21023, // 黑色很鎮定臉型
                21044, // 黑色朦朧美臉型
                21052, // 黑色雪莉臉型
                21042, // 黑色惹人愛臉型
                21045, // 黑色卡通化臉型
                21049, // 黑色狐狸眼臉型
                21050, // 黑色仁王眼臉型
            })
            ));
        }
        if (!royaCoupon.containsKey(5150095)) { // 戰國髮型卷
            royaCoupon.put(5150095, new Pair(
                    Arrays.asList(new Integer[]{ // 男
                33970, // 黑色個性大紮造型
                36070, // 黑色伊卡勒特造型
                32140, // 黑色髮尾大捲造型
                33440, // 黑色勁舞巨星造型
                30920, // 黑色陽光小尾造型
                33390, // 黑色費奇造型
            }),
                    Arrays.asList(new Integer[]{ // 女
                37110, // 黑色小捲髮結造型
                34940, // 黑色西格諾斯造型
                34770, // 黑色艾麗亞造型
                37090, // 黑色捲馬尾造型
                34630, // 黑色優雅女皇造型
                34640, // 黑色厚髮上盤造型
            })
            ));
        }
        if (!royaCoupon.containsKey(5150071)) { // 超級偶像美髮券
            royaCoupon.put(5150071, new Pair(
                    Arrays.asList(new Integer[]{ // 男
                33990, // 黑色火焰造型
                33720, // 黑色側分飄逸造型
                32160, // 黑色克勞烏造型
                33210, // 黑色擴散鬃毛造型
                33250, // 黑色妖精翁造型
                33640, // 黑色酷帥逆風造型
                33650, // 黑色浪漫義式造型
            }),
                    Arrays.asList(new Integer[]{ // 女
                34360, // 黑色夢想少女造型
                32150, // 黑色雪莉造型
                34720, // 黑色蓬鬆髮辮造型
                34240, // 黑色蘋果長髮造型
                34380, // 黑色洛珂公主造型
                34640, // 黑色厚髮上盤造型
                34320, // 黑色日式小僮造型
            })
            ));
        }
        if (!royaCoupon.containsKey(5150103)) { // 補給型髮型卷
            royaCoupon.put(5150103, new Pair(
                    Arrays.asList(new Integer[]{ // 男
                33440, // 黑色勁舞巨星造型
                33520, // 黑色花花公子造型
                33420, // 黑色日式領主造型
                33490, // 黑色千鈞一髮造型
                32160, // 黑色克勞烏造型
                33380, // 黑色嘻哈造型
            }),
                    Arrays.asList(new Integer[]{ // 女
                34380, // 黑色洛珂公主造型
                37030, // 黑色花香波浪造型
                34960, // 黑色丹妮卡造型
                37320, // 黑色紫丁香造型
                34660, // 黑色雙角捲造型
                34760, // 黑色小啾紮髮造型
            })
            ));
        }
        if (!royaCoupon.containsKey(5150080)) { // 聖誕美髮券
            royaCoupon.put(5150080, new Pair(
                    Arrays.asList(new Integer[]{ // 男
                33220, // 黑色漂色造型
                32120, // 黑色機甲戰神造型
                32160, // 黑色克勞烏造型
                36000, // 黑色山下是9造型
                33260, // 黑色那因哈特造型
                33800, // 黑色火焰瀏海造型
            }),
                    Arrays.asList(new Integer[]{ // 女
                32130, // 黑色機甲戰神造型
                34370, // 黑色小櫻造型
                34720, // 黑色蓬鬆髮辮造型
                34210, // 黑色單紮波浪造型
                34940, // 黑色西格諾斯造型
                31760, // 黑色雙長馬尾造型
                32150, // 黑色雪莉造型
            })
            ));
        }
        if (!royaCoupon.containsKey(5150112)) { // 橘子節髮型卷
            royaCoupon.put(5150112, new Pair(
                    Arrays.asList(new Integer[]{ // 男
                30930, // 黑色瀏海型捲造型
                33000, // 黑色金賢重造型
                33710, // 黑色活力小紮造型
                33580, // 黑色瀏海飛揚造型
                36330, // 黑色午夜狂熱造型
                36510, // 黑色非對稱造型
                30770, // 黑色羅馬捲造型
                33220, // 黑色漂色造型
                33250, // 黑色妖精翁造型
                36340, // 黑色復古瀏海造型
            }),
                    Arrays.asList(new Integer[]{ // 女
                34040, // 黑色短翹造型
                34260, // 黑色率性小包造型
                34750, // 黑色蝴蝶側尾造型
                34850, // 黑色圈圈髮尾造型
                37230, // 黑色含苞待放造型
                34100, // 黑色蝴蝶髮束造型
                34400, // 黑色單辮瀏海造型
                34790, // 黑色華爾滋造型
                37210, // 黑色捲燙髮結造型
                37220, // 黑色包脖長髮造型
            })
            ));
        }
        if (!royaCoupon.containsKey(5150064)) { // 高級皇家美髮券
            royaCoupon.put(5150064, new Pair(
                    Arrays.asList(new Integer[]{ // 男
                30600, // 黑色復古油頭造型
                33100, // 黑色花輪造型
                33150, // 黑色龍魔導士造型
                33250, // 黑色妖精翁造型
                33550, // 黑色帥氣蒼龍造型
                33430, // 黑色垂耳小貓造型
                33210, // 黑色擴散鬃毛造型
                33000, // 黑色金賢重造型
                33200, // 黑色影武者造型
            }),
                    Arrays.asList(new Integer[]{ // 女
                31600, // 黑色修道之人造型
                34230, // 黑色小帽蓋造型
                31990, // 黑色龍魔導士造型
                34110, // 黑色幻影造型
                34210, // 黑色單紮波浪造型
                34240, // 黑色蘋果長髮造型
                34220, // 黑色露娜造型
                31360, // 黑色蓬鬆束髮造型
                31760, // 黑色雙長馬尾造型
            })
            ));
        }
        if (!royaCoupon.containsKey(5150040)) { // 皇家美髮券
            royaCoupon.put(5150040, new Pair(
                    Arrays.asList(new Integer[]{ // 男
                35790, // 黑色艾爾文造型
                36010, // 黑色陰陽道造型
                36680, // 黑色黑桃造型
                33680, // 黑色皇族紮髮造型
                36220, // 黑色半熟男孩造型
                36580, // 黑色棉花糖造型
                30760, // 黑色馬桶蓋造型
                30870, // 黑色街頭雅痞造型
                35070, // 黑色煩惱退散造型
                33470, // 黑色帥氣日系造型
                33770, // 黑色魅力側分造型
            }),
                    Arrays.asList(new Integer[]{ // 女
                38030, // 黑色雙短辮造型
                38430, // 黑色蝴蝶捲尾造型
                34210, // 黑色單紮波浪造型
                37400, // 黑色普希泰伊造型
                37760, // 黑色少女小花造型
                38110, // 黑色清湯掛麵造型
                31820, // 黑色格萊熙造型
                34220, // 黑色露娜造型
                34560, // 黑色自然梳理造型
                34880, // 黑色蝴蝶少女造型
                37640, // 黑色隨風飄逸造型
            })
            ));
        }
        if (!royaCoupon.containsKey(5152080)) { // 聖誕整形券
            royaCoupon.put(5152080, new Pair(
                    Arrays.asList(new Integer[]{ // 男
                20046, // 黑色朦朧美臉型
                20040, // 黑色明亮的臉型
                20021, // 黑色笑咪咪臉型
                20060, // 黑色模範生臉型
                20047, // 黑色卡通化臉型
                20010, // 黑色不耐煩臉型
                20030, // 黑色有自信臉型
            }),
                    Arrays.asList(new Integer[]{ // 女
                21058, // 黑色乖女孩臉型
                21041, // 黑色雙色瞳臉型
                21011, // 黑色大眼睛臉型
                21028, // 黑色好清純臉型
                21038, // 黑色明亮的臉型
                21022, // 黑色丹鳳眼臉型
                21010, // 黑色不耐煩臉型
            })
            ));
        }
        if (!royaCoupon.containsKey(5152200)) { // 閃亮整型卷
            royaCoupon.put(5152200, new Pair(
                    Arrays.asList(new Integer[]{ // 男
                20077, // 黑色天使臉型
                20080, // 黑色閃星光臉型
                20081, // 黑色很純粹臉型
                20040, // 黑色明亮的臉型
                20043, // 黑色雙色瞳臉型
                20070, // 黑色那因哈特臉型
                20033, // 黑色很搶眼臉型
                20010, // 黑色不耐煩臉型
                20011, // 黑色粗眉毛臉型
            }),
                    Arrays.asList(new Integer[]{ // 女
                21072, // 黑色可愛天使臉型
                21077, // 黑色放大眼臉型
                21078, // 黑色寧靜的臉型
                21064, // 黑色扁嘴的臉型
                21041, // 黑色雙色瞳臉型
                21038, // 黑色明亮的臉型
                21011, // 黑色大眼睛臉型
                21009, // 黑色不理人臉型
                21010, // 黑色不耐煩臉型
            })
            ));
        }
        if (!royaCoupon.containsKey(5152216)) { // 曉之陣改編紀念整型券
            royaCoupon.put(5152216, new Pair(
                    Arrays.asList(new Integer[]{ // 男
                20073, // 黑色熱血劍豪臉型
                20078, // 黑色堅毅劍豪臉型
                23065, // 黑色認真的臉型
                23066, // 黑色冷靜的臉型
            }),
                    Arrays.asList(new Integer[]{ // 女
                24063, // 黑色精幹的臉型
                24064, // 黑色溫柔的臉型
                21068, // 黑色陰陽道師臉型
                21076, // 黑色陰陽術者臉型
            })
            ));
        }
        if (!royaCoupon.containsKey(5152049)) { // 通用整形券
            royaCoupon.put(5152049, new Pair(
                    Arrays.asList(new Integer[]{ // 男
                20000, // 黑色挑戰的臉型
                20001, // 黑色有自信臉型
                20002, // 黑色很謹慎臉型
                20003, // 黑色無所謂臉型
                20004, // 黑色瞪大眼臉型
                20005, // 黑色乾淨的臉型
                20006, // 黑色好性感臉型
                20007, // 黑色下垂眼臉型
                20008, // 黑色小動物臉型
                20012, // 黑色漫畫眼臉型
                20014, // 黑色大眼珠臉型
                20016, // 黑色很專心臉型
                20020, // 黑色懷疑的臉型
                20017, // 黑色看仔細臉型
                20013, // 黑色狠角色臉型
                20022, // 黑色細長眼臉型
                20025, // 黑色很鎮定臉型
                20027, // 黑色好親切臉型
                20028, // 黑色翻白眼臉型
                20029, // 黑色夠性格臉型
                20031, // 黑色頗柔和臉型
            }),
                    Arrays.asList(new Integer[]{ // 女
                21000, // 黑色挑戰的臉型
                21001, // 黑色有自信臉型
                21002, // 黑色很謹慎臉型
                21003, // 黑色無所謂臉型
                21004, // 黑色瞪著你臉型
                21005, // 黑色迷人的臉型
                21006, // 黑色好性感臉型
                21007, // 黑色下垂眼臉型
                21008, // 黑色小動物臉型
                21012, // 黑色漫畫眼臉型
                21014, // 黑色平靜的臉型
                21016, // 黑色很專心臉型
                21020, // 黑色懷疑的臉型
                21017, // 黑色看仔細臉型
                21013, // 黑色無神的臉型
                21021, // 黑色鬥雞眼臉型
                21023, // 黑色很鎮定臉型
                21024, // 黑色好親切臉型
                21026, // 黑色夠性格臉型
                21027, // 黑色有自信臉型
                21029, // 黑色堅毅的臉型
            })
            ));
        }
        if (!royaCoupon.containsKey(5152073)) { // 超級偶像整型券
            royaCoupon.put(5152073, new Pair(
                    Arrays.asList(new Integer[]{ // 男
                20010, // 黑色不耐煩臉型
                20021, // 黑色笑咪咪臉型
                20044, // 黑色惹人愛臉型
                20040, // 黑色明亮的臉型
                20043, // 黑色雙色瞳臉型
                20038, // 黑色好俏皮臉型
                20047, // 黑色卡通化臉型
            }),
                    Arrays.asList(new Integer[]{ // 女
                21010, // 黑色不耐煩臉型
                21022, // 黑色丹鳳眼臉型
                21042, // 黑色惹人愛臉型
                21044, // 黑色朦朧美臉型
                21028, // 黑色好清純臉型
                21036, // 黑色好俏皮臉型
                21043, // 黑色很叛逆臉型
            })
            ));
        }
        if (!royaCoupon.containsKey(5150097)) { // 端午節髮型卷
            royaCoupon.put(5150097, new Pair(
                    Arrays.asList(new Integer[]{ // 男
                36660, // 黑色短髮犬妖造型
                33260, // 黑色那因哈特造型
                30540, // 黑色軍刀頭造型
                33520, // 黑色花花公子造型
                32140, // 黑色髮尾大捲造型
            }),
                    Arrays.asList(new Integer[]{ // 女
                34650, // 黑色帥氣蒼龍造型
                34380, // 黑色洛珂公主造型
                32150, // 黑色雪莉造型
                34270, // 黑色馬奧奈特造型
                31360, // 黑色蓬鬆束髮造型
            })
            ));
        }
        if (!royaCoupon.containsKey(5150105)) { // 酷夏髮型券
            royaCoupon.put(5150105, new Pair(
                    Arrays.asList(new Integer[]{ // 男
                36530, // 黑色黑日梅司造型
                36150, // 黑色衝刺造型
                33940, // 黑色古惑性格造型
                33780, // 黑色自然梳理造型
                36000, // 黑色山下是9造型
                33580, // 黑色瀏海飛揚造型
                33350, // 黑色漂泊浪子造型
                33300, // 黑色雙小包造型
                33790, // 黑色復古側遮造型
            }),
                    Arrays.asList(new Integer[]{ // 女
                37520, // 黑色輕盈雙辮造型
                34850, // 黑色圈圈髮尾造型
                34870, // 黑色繽紛髮尾造型
                34840, // 黑色賽倫誘惑造型
                34970, // 黑色高馬尾造型
                37320, // 黑色紫丁香造型
                34860, // 黑色月光仙子造型
                34660, // 黑色雙角捲造型
                37280, // 黑色便條造型
            })
            ));
        }
        if (!royaCoupon.containsKey(5152201)) { // 補給型整型卷
            royaCoupon.put(5152201, new Pair(
                    Arrays.asList(new Integer[]{ // 男
                20082, // 黑色守護者臉型
                20052, // 黑色仁王眼臉型
                20045, // 黑色很叛逆臉型
                20038, // 黑色好俏皮臉型
                20011, // 黑色粗眉毛臉型
                20033, // 黑色很搶眼臉型
            }),
                    Arrays.asList(new Integer[]{ // 女
                21072, // 黑色可愛天使臉型
                21048, // 黑色看不見臉型
                21043, // 黑色很叛逆臉型
                21036, // 黑色好俏皮臉型
                21010, // 黑色不耐煩臉型
                21028, // 黑色好清純臉型
            })
            ));
        }
        if (!royaCoupon.containsKey(5152050)) { // 通用高級整形券
            royaCoupon.put(5152050, new Pair(
                    Arrays.asList(new Integer[]{ // 男
                20000, // 黑色挑戰的臉型
                20001, // 黑色有自信臉型
                20002, // 黑色很謹慎臉型
                20003, // 黑色無所謂臉型
                20004, // 黑色瞪大眼臉型
                20005, // 黑色乾淨的臉型
                20006, // 黑色好性感臉型
                20007, // 黑色下垂眼臉型
                20008, // 黑色小動物臉型
                20012, // 黑色漫畫眼臉型
                20014, // 黑色大眼珠臉型
                20016, // 黑色很專心臉型
                20020, // 黑色懷疑的臉型
                20017, // 黑色看仔細臉型
                20013, // 黑色狠角色臉型
                20022, // 黑色細長眼臉型
                20025, // 黑色很鎮定臉型
                20027, // 黑色好親切臉型
                20028, // 黑色翻白眼臉型
                20029, // 黑色夠性格臉型
                20031, // 黑色頗柔和臉型
            }),
                    Arrays.asList(new Integer[]{ // 女
                21000, // 黑色挑戰的臉型
                21001, // 黑色有自信臉型
                21002, // 黑色很謹慎臉型
                21003, // 黑色無所謂臉型
                21004, // 黑色瞪著你臉型
                21005, // 黑色迷人的臉型
                21006, // 黑色好性感臉型
                21007, // 黑色下垂眼臉型
                21008, // 黑色小動物臉型
                21012, // 黑色漫畫眼臉型
                21014, // 黑色平靜的臉型
                21016, // 黑色很專心臉型
                21020, // 黑色懷疑的臉型
                21017, // 黑色看仔細臉型
                21013, // 黑色無神的臉型
                21021, // 黑色鬥雞眼臉型
                21023, // 黑色很鎮定臉型
                21024, // 黑色好親切臉型
                21026, // 黑色夠性格臉型
                21027, // 黑色有自信臉型
                21029, // 黑色堅毅的臉型
            })
            ));
        }
        if (!royaCoupon.containsKey(5150042)) { // 通用美髮券
            royaCoupon.put(5150042, new Pair(
                    Arrays.asList(new Integer[]{ // 男
                30160, // 黑色山本頭造型
                30910, // 黑色短小捲造型
                30200, // 黑色微捲設計造型
                30210, // 黑色露耳清爽造型
                30220, // 黑色貓王造型
                30260, // 黑色左旋旁分造型
                30300, // 黑色齊眉自然造型
                30310, // 黑色西瓜皮造型
                30320, // 黑色黑人微捲造型
                30410, // 黑色順風微捲造型
                30460, // 黑色旋風捲造型
                30470, // 黑色天然短捲造型
                30480, // 黑色時髦順髮造型
                30490, // 黑色運動短髮造型
                30510, // 黑色捲翹瀏海造型
                30630, // 黑色蓬鬆散羽造型
                30730, // 黑色迎風飄逸造型
                30760, // 黑色馬桶蓋造型
                30830, // 黑色亞里斯造型
                30880, // 黑色時尚瀏海造型
            }),
                    Arrays.asList(new Integer[]{ // 女
                31020, // 黑色大波浪捲造型
                31060, // 黑色髮尾上翹造型
                31110, // 黑色波浪馬尾造型
                31140, // 黑色傳統蒼龍造型
                31150, // 黑色挑染長髮造型
                31160, // 黑色青春小髻造型
                31220, // 黑色瀏海半遮造型
                31230, // 黑色俏麗捲辮造型
                31240, // 黑色披頭散髮造型
                31880, // 黑色堅強少女造型
                31330, // 黑色濃密直髮造型
                31470, // 黑色中國女孩造型
                31610, // 黑色瑞迪亞造型
                31460, // 黑色洋蔥短剪造型
                31340, // 黑色層次短剪造型
                31490, // 黑色隨意盤髮造型
                31310, // 黑色齊眉波浪造型
                31290, // 黑色活潑短髮造型
                31640, // 黑色迎風柔順造型
                31810, // 黑色蘋果短髮造型
            })
            ));
        }
        if (!royaCoupon.containsKey(5152090)) { // 戰國整形卷
            royaCoupon.put(5152090, new Pair(
                    Arrays.asList(new Integer[]{ // 男
                20080, // 黑色閃星光臉型
                20052, // 黑色仁王眼臉型
                20047, // 黑色卡通化臉型
                20045, // 黑色很叛逆臉型
                20011, // 黑色粗眉毛臉型
                20010, // 黑色不耐煩臉型
            }),
                    Arrays.asList(new Integer[]{ // 女
                21011, // 黑色大眼睛臉型
                21041, // 黑色雙色瞳臉型
                21042, // 黑色惹人愛臉型
                21038, // 黑色明亮的臉型
                21048, // 黑色看不見臉型
                21072, // 黑色可愛天使臉型
            })
            ));
        }
        if (!royaCoupon.containsKey(5152202)) { // 酷夏整型券
            royaCoupon.put(5152202, new Pair(
                    Arrays.asList(new Integer[]{ // 男
                20088, // 黑色讀書人臉型
                20080, // 黑色閃星光臉型
                20077, // 黑色天使臉型
                20081, // 黑色很純粹臉型
                20046, // 黑色朦朧美臉型
                20044, // 黑色惹人愛臉型
                20040, // 黑色明亮的臉型
                20045, // 黑色很叛逆臉型
                20082, // 黑色守護者臉型
            }),
                    Arrays.asList(new Integer[]{ // 女
                21084, // 黑色大小姐臉型
                21072, // 黑色可愛天使臉型
                21077, // 黑色放大眼臉型
                21028, // 黑色好清純臉型
                21022, // 黑色丹鳳眼臉型
                21011, // 黑色大眼睛臉型
                21010, // 黑色不耐煩臉型
                21045, // 黑色卡通化臉型
                21043, // 黑色很叛逆臉型
            })
            ));
        }
        if (!royaCoupon.containsKey(5152067)) { // 高級皇家整型券
            royaCoupon.put(5152067, new Pair(
                    Arrays.asList(new Integer[]{ // 男
                20038, // 黑色好俏皮臉型
                20032, // 黑色平靜的臉型
                20021, // 黑色笑咪咪臉型
                20033, // 黑色很搶眼臉型
                20040, // 黑色明亮的臉型
                20043, // 黑色雙色瞳臉型
                20044, // 黑色惹人愛臉型
                20046, // 黑色朦朧美臉型
                20052, // 黑色仁王眼臉型
            }),
                    Arrays.asList(new Integer[]{ // 女
                21036, // 黑色好俏皮臉型
                21030, // 黑色平靜的臉型
                21009, // 黑色不理人臉型
                21022, // 黑色丹鳳眼臉型
                21028, // 黑色好清純臉型
                21038, // 黑色明亮的臉型
                21041, // 黑色雙色瞳臉型
                21042, // 黑色惹人愛臉型
                21044, // 黑色朦朧美臉型
            })
            ));
        }
    }

    public final List<CashItem> getAllItems() {
        List<CashItem> allItem = new LinkedList();
        for (CashItem ci : itemStats.values()) {
            allItem.add(ci);
        }
        return allItem;
    }

    public final List<CashItem> getHideAllDefaultItems() {
        List<CashItem> allItem = new LinkedList();
        for (CashItem ci : itemStats.values()) {
            CashModItem csMod = getModItem(ci.getSN());
            if ((csMod != null && !csMod.isOnSale()) || ci.isOnSale()) {
                allItem.add(ci);
            }
        }
        return allItem;
    }

    public final List<CashItem> getHideItems() {
        List<CashItem> allItem = new LinkedList();
        for (CashItem ci : itemStats.values()) {
            CashModItem csMod = getModItem(ci.getSN());
            if ((csMod != null && !csMod.isOnSale())) {
                allItem.add(ci);
            }
        }
        return allItem;
    }

    public final List<CashModItem> getAllModItems() {
        List<CashModItem> allItem = new LinkedList();
        for (CashModItem csMod : getAllModInfo()) {
            if (csMod.isOnSale()) {
                allItem.add(csMod);
            }
        }
        return allItem;
    }

    public final List<CashModItem> getMainItems() {
        List<CashModItem> mainItem = new LinkedList();
        for (CashModItem csMod : getAllModInfo()) {
            if (csMod.isMainItem()) {
                mainItem.add(csMod);
            }
        }
        return mainItem;
    }

    public final CashItem getSimpleItem(int sn) {
        return itemStats.get(sn);
    }

    public final CashItem getItem(int sn) {
        final CashItem stats = itemStats.get(Integer.valueOf(sn));
        final CashModItem z = getModItem(sn);
        if (z != null && z.isOnSale()) {
            return stats; //null doesnt matter
        }
        if (stats == null || !stats.isOnSale() || (z != null && !z.isOnSale())) {
            return null;
        }
        return null; // 如果要開啟預設商城列表，就要開這個
    }

    public final List<Integer> getPackageItems(int itemId) {
        return itemPackage.get(itemId);
    }

    public final CashModItem getModItem(int sn) {
        return itemMods.get(sn);
    }

    public final Collection<CashModItem> getAllModInfo() {
        return itemMods.values();
    }

    public final Map<Integer, List<Integer>> getRandomItemInfo() {
        return openBox;
    }

    public final Map<Integer, List<Pair<Integer, Integer>>> getUnkCoupon() {
        return unkCoupon;
    }

    public final Map<Integer, List<List<Pair<Integer, Integer>>>> getUnkCoupon2() {
        return unkCoupon2;
    }

    public final Map<Integer, Pair<List<Integer>, List<Integer>>> getRoyaCoupon() {
        return royaCoupon;
    }

    public final int getItemSN(int itemid) {
        for (Entry<Integer, CashItem> ci : itemStats.entrySet()) {
            if (ci.getValue().getId() == itemid) {
                return ci.getValue().getSN();
            }
        }
        return 0;
    }

    public void addModItem(CashModItem cModItem) {
        if (!itemMods.containsKey(cModItem.getSN())) {
            itemMods.put(cModItem.getSN(), cModItem);
            cModItem.initFlags(itemStats.get(cModItem.getSN()) != null ? itemStats.get(cModItem.getSN()) : null);
            PreparedStatement ps = null;
            Connection con = DatabaseConnection.getConnection();
            try {
                ps = con.prepareStatement("INSERT INTO cashshop_items (SN, Note, ItemId, Count, Price, Period, Gender, Class, OnSale, Main) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                ps.setInt(1, cModItem.getSN());
                ps.setString(2, cModItem.getNote());
                ps.setInt(3, cModItem.getId());
                ps.setInt(4, cModItem.getCount());
                ps.setInt(5, cModItem.getPrice());
                ps.setInt(6, cModItem.getPeriod());
                ps.setInt(7, cModItem.getGender());
                ps.setInt(8, cModItem.getFlage());
                ps.setInt(9, cModItem.isOnSale() ? 1 : 0);
                ps.setInt(10, cModItem.isMainItem() ? 1 : 0);
                ps.execute();
                ps.close();
            } catch (SQLException ex) {
            }
        }
    }

    public void updateModItem(CashModItem cModItem) {
        itemMods.put(cModItem.getSN(), cModItem);
        cModItem.initFlags(itemStats.get(cModItem.getSN()) != null ? itemStats.get(cModItem.getSN()) : null);
        PreparedStatement ps = null;
        Connection con = DatabaseConnection.getConnection();
        try {
            ps = con.prepareStatement("UPDATE cashshop_items SET Note = ?, ItemId = ?, Count = ?, Price = ?, Period = ?, Gender = ?, Class = ?, OnSale = ?, Main = ? WHERE SN = ?");
            ps.setString(1, cModItem.getNote());
            ps.setInt(2, cModItem.getId());
            ps.setInt(3, cModItem.getCount());
            ps.setInt(4, cModItem.getPrice());
            ps.setInt(5, cModItem.getPeriod());
            ps.setInt(6, cModItem.getGender());
            ps.setInt(7, cModItem.getFlage());
            ps.setInt(8, cModItem.isOnSale() ? 1 : 0);
            ps.setInt(9, cModItem.isMainItem() ? 1 : 0);
            ps.setInt(10, cModItem.getSN());
            ps.execute();
            ps.close();
        } catch (SQLException ex) {
        }
    }

    public void deleteModItem(int sn) {
        itemMods.remove(sn);
        PreparedStatement ps = null;
        Connection con = DatabaseConnection.getConnection();
        try {
            ps = con.prepareStatement("DELETE FROM cashshop_items WHERE SN = ?");
            ps.setInt(1, sn);
            ps.execute();
            ps.close();
        } catch (SQLException ex) {
        }
    }
}
