/*
 *  Branch Bucket Snowman - Happy Ville NPC
 */

function start() {
    cm.sendSimple("安安 我是 #p2001001# 你要去哪個小房間? PS可結伴同行 \n\r #b#L0#月光森林1#l \n\r #L1#月光森林2#l \n\r #L2#月光森林3#l \n\r #L3#月光森林4#l \n\r #L4#月光森林5#l");
}

function action(mode, type, selection) {
	if (mode == 1) {
		if (selection >= 0 && selection <= 4) {
			cm.warp(209000001 + selection, 0);
		}
	}
    cm.dispose();
}
