load('nashorn:mozilla_compat.js');
importPackage(Packages.client);

var status = -1;

function start(mode, type, selection) {
    status++;
	if (mode != 1) {
	    if(type == 1 && mode == 0)
		    status -= 2;
		else{
			qm.sendNext("真的沒有另一種方法可以爬過城堡了嗎??如果真的不知道的話那就來請教我吧!");
			qm.dispose();
			return;
		}
	}
	if (status == 0)
		qm.sendYesNo("就像我告訴你的,只要克服障礙就行了,首先你可以調查城堡外牆?");
	if (status == 1)
		qm.sendNext("走過蘑菇森林,走向城堡,祝您好運!");
	if (status == 2){
		qm.gainExp(11000);
		qm.sendOk("通過這個區域吧!");
		qm.forceCompleteQuest();
		qm.dispose();
	}
}

function end(mode, type, selection) {
    status++;
	if (mode != 1) {
	    if(type == 1 && mode == 0)
		    status -= 2;
		else{
			qm.dispose();
			return;
		}
	}
	if (status == 0)
		qm.sendOk("嗯，我明白了...所以他們已經完全關閉入口了");
	if (status == 1){
		qm.gainExp(11000);
		qm.sendOk("通過這個區域吧!");
		qm.forceCompleteQuest();
		qm.dispose();
	}
}