package handling.world;

import client.MapleCoolDownValueHolder;
import client.MapleDiseaseValueHolder;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class PlayerBuffStorage implements Serializable {

    private static final ReentrantLock buffLock = new ReentrantLock();
    private static final ReentrantLock cdLock = new ReentrantLock();
    private static final ReentrantLock disLock = new ReentrantLock();
    private static final Map<Integer, List<PlayerBuffValueHolder>> buffs = new ConcurrentHashMap<>();
    private static final Map<Integer, List<MapleCoolDownValueHolder>> coolDowns = new ConcurrentHashMap<>();
    private static final Map<Integer, List<MapleDiseaseValueHolder>> diseases = new ConcurrentHashMap<>();

    public static final void addBuffsToStorage(final int chrid, final List<PlayerBuffValueHolder> toStore) {

        buffLock.lock();
        try {
            buffs.put(chrid, toStore);
        } finally {
            buffLock.unlock();
        }
    }

    public static final void addCooldownsToStorage(final int chrid, final List<MapleCoolDownValueHolder> toStore) {
        cdLock.lock();
        try {
            coolDowns.put(chrid, toStore);
        } finally {
            cdLock.unlock();
        }
    }

    public static final void addDiseaseToStorage(final int chrid, final List<MapleDiseaseValueHolder> toStore) {
        disLock.lock();
        try {
            diseases.put(chrid, toStore);
        } finally {
            disLock.unlock();
        }
    }

    public static final List<PlayerBuffValueHolder> getBuffsFromStorage(final int chrid) {
        List<PlayerBuffValueHolder> chrbuf = new LinkedList<>();
        buffLock.lock();
        try {
            for (final Map.Entry<Integer, List<PlayerBuffValueHolder>> qs : buffs.entrySet()) {
                int charid = qs.getKey();
                List<PlayerBuffValueHolder> buff = qs.getValue();
                if (charid != chrid) {
                    continue;
                }
                for (PlayerBuffValueHolder buf : buff) {
                    chrbuf.add(buf);
                }
            }
            buffs.remove(chrid);
        } finally {
            buffLock.unlock();
        }
        return chrbuf;
    }

    public static final List<MapleCoolDownValueHolder> getCooldownsFromStorage(final int chrid) {
        List<MapleCoolDownValueHolder> chrcd = new LinkedList<>();
        cdLock.lock();
        try {
            for (final Map.Entry<Integer, List<MapleCoolDownValueHolder>> qs : coolDowns.entrySet()) {
                int charid = qs.getKey();
                List<MapleCoolDownValueHolder> buff = qs.getValue();
                if (charid != chrid) {
                    continue;
                }
                for (MapleCoolDownValueHolder buf : buff) {
                    chrcd.add(buf);
                }
            }
            coolDowns.remove(chrid);
        } finally {
            cdLock.unlock();
        }
        return chrcd;
    }

    public static final List<MapleDiseaseValueHolder> getDiseaseFromStorage(final int chrid) {
        List<MapleDiseaseValueHolder> chrcd = new LinkedList<>();
        disLock.lock();
        try {
            for (final Map.Entry<Integer, List<MapleDiseaseValueHolder>> qs : diseases.entrySet()) {
                int charid = qs.getKey();
                List<MapleDiseaseValueHolder> buff = qs.getValue();
                if (charid != chrid) {
                    continue;
                }
                for (MapleDiseaseValueHolder buf : buff) {
                    chrcd.add(buf);
                }
            }
            diseases.remove(chrid);
        } finally {
            disLock.unlock();
        }
        return chrcd;
    }
}
