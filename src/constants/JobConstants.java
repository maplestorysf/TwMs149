package constants;

import server.ServerProperties;

public class JobConstants {

    public static final boolean enableJobs = true;

    public enum JobType {
        末日反抗軍(0),
        冒險家(1),
        皇家騎士團(2),
        狂狼勇士(3),
        龍魔導士(4),
        精靈遊俠(5),
        惡魔(6),
        幻影俠盜(7),
        影武者(8),
        米哈逸(9),
        蒼龍俠客(10),
        重砲指揮官(13),;
		
        private final int jobType;
        private final  boolean enableCreate = true;

        private JobType(int jobType) {
            this.jobType = jobType;
        }

        public int getJobType() {
            return jobType;
        }

        public boolean enableCreate() {
            return Boolean.valueOf(ServerProperties.getProperty("JobEnableCreate" + jobType, String.valueOf(enableCreate)));
        }

        public void setEnableCreate(boolean info) {
            if (info == enableCreate) {
                ServerProperties.removeProperty("JobEnableCreate" + jobType);
                return;
            }
            ServerProperties.setProperty("JobEnableCreate" + jobType, String.valueOf(info));
        }
    }
}
