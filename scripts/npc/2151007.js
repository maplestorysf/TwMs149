function start() {
    cm.sendSimple("#b\r\n#L0#訓練室 A#l\r\n#L1#訓練室 B#l\r\n#L2#訓練室 C#l\r\n#L3#訓練室 D#l#k");
}

function action(mode, type, selection) {
    if (mode == 1) { //or 931000400 + selection..?
        switch (selection) {
            case 0:
                cm.warp(310010100, 0);
                break;
            case 1:
                cm.warp(310010200, 0);
                break;
            case 2:
                cm.warp(310010300, 0);
                break;
            case 3:
                cm.warp(310010400, 0);
                break;
            case 4:
                cm.warp(310010500, 0);
                break;
        }
    }
    cm.dispose();
}