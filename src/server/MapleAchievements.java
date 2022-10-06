package server;

import java.util.LinkedHashMap;
import java.util.Map;

import java.util.Map.Entry;

public class MapleAchievements {

    private final Map<Integer, MapleAchievement> achievements = new LinkedHashMap<>();
    private static final MapleAchievements instance = new MapleAchievements();

    protected MapleAchievements() {
        achievements.put(1, new MapleAchievement("獲得第一次的點數", 10, false));
        achievements.put(2, new MapleAchievement("達到 30 級", 80, true));
        achievements.put(3, new MapleAchievement("達到 70 級", 120, true));
        achievements.put(4, new MapleAchievement("達到 120 級", 180, true));
        achievements.put(5, new MapleAchievement("達到 200 級", 399, true));
        achievements.put(7, new MapleAchievement("達到 50 名聲", 100, true));
        achievements.put(9, new MapleAchievement("首次穿戴罕見系列裝備", 120, false));
        achievements.put(10, new MapleAchievement("首次穿戴永恆系列裝備", 120, false));
        achievements.put(11, new MapleAchievement("首次在遊戲說10句伺服器的好話", 5, true));
        achievements.put(12, new MapleAchievement("擊敗大姐頭", 30, true));
        achievements.put(13, new MapleAchievement("擊敗拉圖斯", 10, true));
        achievements.put(14, new MapleAchievement("擊敗右海怒斯", 10, true));
        achievements.put(15, new MapleAchievement("擊敗殘暴炎魔", 100, true));
        achievements.put(16, new MapleAchievement("擊敗闇黑龍王", 120, true));
        achievements.put(17, new MapleAchievement("擊敗皮卡啾", 180, true));
        achievements.put(18, new MapleAchievement("擊敗任意Boss", 30, true));
        achievements.put(19, new MapleAchievement("在<選邊站>活動獲勝", 30, false));
        achievements.put(20, new MapleAchievement("在<障礙競走>活動獲勝", 30, false));
        achievements.put(21, new MapleAchievement("在<向上攀升>活動獲勝", 30, false));
        achievements.put(22, new MapleAchievement("完成Boss任務高難度模式", 180, true));
        achievements.put(23, new MapleAchievement("擊敗混沌殘暴炎魔", 120, true));
        achievements.put(24, new MapleAchievement("擊敗混沌闇黑龍王", 180, true));
        achievements.put(25, new MapleAchievement("在<生存挑戰>活動獲勝", 50, false));
        achievements.put(26, new MapleAchievement("攻擊傷害首次超過 10,000", 10, false));
        achievements.put(27, new MapleAchievement("攻擊傷害首次超過 50,000", 30, false));
        achievements.put(28, new MapleAchievement("攻擊傷害首次超過 100,000", 80, false));
        achievements.put(29, new MapleAchievement("攻擊傷害首次超過 500,000", 120, false));
        achievements.put(30, new MapleAchievement("攻擊傷害首次達到 999,999", 180, false));
        achievements.put(31, new MapleAchievement("首次持有超過 1,000,000 楓幣", 10, false));
        achievements.put(32, new MapleAchievement("首次持有超過 10,000,000 楓幣", 30, false));
        achievements.put(33, new MapleAchievement("首次持有超過 100,000,000 楓幣", 80, false));
        achievements.put(34, new MapleAchievement("首次持有超過 1,000,000,000 楓幣", 120, false));
        achievements.put(35, new MapleAchievement("成功建立公會", 80, false));
        achievements.put(36, new MapleAchievement("成功建立家族", 80, false));
        achievements.put(37, new MapleAchievement("成功挑戰CWK副本", 40, false));
        achievements.put(38, new MapleAchievement("擊敗凡雷恩", 100, true));
        achievements.put(39, new MapleAchievement("擊敗西格諾斯", 180, true));
        achievements.put(40, new MapleAchievement("首次穿戴130級以上裝備", 80, false));
        achievements.put(41, new MapleAchievement("首次穿戴140級以上裝備", 100, false));
        achievements.put(42, new MapleAchievement("擊敗阿卡伊農", 180, true));
        achievements.put(43, new MapleAchievement("擊敗左海怒斯", 10, true));
    }

    public static MapleAchievements getInstance() {
        return instance;
    }

    public MapleAchievement getById(int id) {
        return achievements.get(id);
    }

    public Integer getByMapleAchievement(MapleAchievement ma) {
        for (Entry<Integer, MapleAchievement> achievement : this.achievements.entrySet()) {
            if (achievement.getValue() == ma) {
                return achievement.getKey();
            }
        }
        return null;
    }
}
