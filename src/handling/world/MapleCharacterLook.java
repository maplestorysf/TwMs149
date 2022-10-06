package handling.world;

import java.util.Map;

/**
 *
 * @author AlphaEta
 */
public interface MapleCharacterLook {

    public byte getGender();

    public byte getSkinColor();

    public int getFace();

    public int getHair();

    public int getDemonMarking();

    public short getJob();

    public Map<Byte, Integer> getEquips();

    //public MaplePet getPet2(final int index);

    public Map<Byte, Integer> getTotems();

    public int getElf();
}
