/* ===========================================================
			Resonance
	NPC Name: 		Hen
	Map(s): 		Utah's House: Front Porch(100030102)
	Description: 	Obtain Egg
=============================================================
Version 1.0 - Script Done.(4/6/2010)
=============================================================
*/

var status = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (mode == 1)
			status++;
		else
			status--;
		if(status == 0){
			if(cm.isQuestActive(22007)){
				cm.sendNext("#b(取出雞蛋了。 快點交給猶他。)#k");
				cm.gainItem(4032451, 1);
			}else{
				cm.sendOk("#b你現在不需要採集雞蛋.#k");
			}
			cm.dispose();
		}
	}
}