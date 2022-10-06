package tools.packet;

import handling.SendPacketOpcode;
import server.Randomizer;
import tools.data.MaplePacketLittleEndianWriter;

/**
 *
 * @author Itzik
 */
public class JobPacket {

    public static class PhantomPacket {

        public static byte[] addStolenSkill(int jobNum, int index, int skill, int level) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.UPDATE_STOLEN_SKILLS.getValue());
            mplew.write(1);
            mplew.write(0);
            mplew.writeInt(jobNum);
            mplew.writeInt(index);
            mplew.writeInt(skill);
            mplew.writeInt(level);
            mplew.writeInt(0);
            mplew.write(0);

            return mplew.getPacket();
        }

        public static byte[] removeStolenSkill(int jobNum, int index) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.UPDATE_STOLEN_SKILLS.getValue());
            mplew.write(1);
            mplew.write(3);
            mplew.writeInt(jobNum);
            mplew.writeInt(index);
            mplew.write(0);

            return mplew.getPacket();
        }

        public static byte[] replaceStolenSkill(int base, int skill) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.REPLACE_SKILLS.getValue());
            mplew.write(1);
            mplew.write(skill > 0 ? 1 : 0);
            mplew.writeInt(base);
            mplew.writeInt(skill);

            return mplew.getPacket();
        }

        public static byte[] gainCardStack(int oid, int runningId, int color, int skillid, int damage, int times) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.GAIN_FORCE.getValue());
            mplew.write(0);
            mplew.writeInt(oid);
            mplew.writeInt(1);
            mplew.writeInt(damage);
            mplew.writeInt(skillid);
            for (int i = 0; i < times; i++) {
                mplew.write(1);
                mplew.writeInt(damage == 0 ? runningId + i : runningId);
                mplew.writeInt(color);
                mplew.writeInt(Randomizer.rand(15, 29));
                mplew.writeInt(Randomizer.rand(7, 11));
                mplew.writeInt(Randomizer.rand(0, 9));
            }
            mplew.write(0);

            return mplew.getPacket();
        }

        public static byte[] updateCardStack(final int total) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.PHANTOM_CARD.getValue());
            mplew.write(total);

            return mplew.getPacket();
        }
    }
}