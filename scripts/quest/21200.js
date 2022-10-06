var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 0) {
	    qm.sendNext("真的很緊急，如果你拒絕，你會後悔的。#b與巨大的矛有關#k，這意味著它與你的過去有關。誰知道...？也許巨大的矛是重新喚醒你能力的關鍵？");
	    qm.dispose();
	    return;
	}
	status--;
    }
    if (status == 0) {
	qm.askAcceptDecline("修練還順利嗎？哎呀，沒想到等級已經這麼高了。果然有出去修練有差…對了！現在不是說這個的時候。您這麼忙真的很抱歉，不過您暫時要跟我回去島上。");
    } else if (status == 1) {
	qm.forceStartQuest(21200, "3"); //??
	qm.forceCompleteQuest();
	qm.forceStartQuest(21202); //skip just in case
	qm.forceStartQuest(21203, "0");
	qm.sendOk("保管於#b瑞恩村#k的#b巨大的矛#k突然產生了奇怪的反應。根據紀錄，只有矛呼叫主人時會出現這樣的反應。#b可能是想傳達給您#k。快點去島上確認吧！");
	qm.dispose();
    }
}

function end(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 11) {
	    qm.sendNext("Hey, at least you tell me you tried!");
	    qm.dispose();
	    return;
	} else if (status == 13) {
	    qm.MovieClipIntroUI(true);
	    qm.warp(914090200, 0);
	    qm.dispose();
	    return;
	}
	status--;
    }
    if (status == 0) {
	qm.sendNextS("Hmmmmmm mmmm mmmmm....", 2);
    } else if (status == 1) {
	qm.sendNextPrevS("#b(Giant Pole Arm is buzzing, but who's that boy standing there?)#k", 2);
    } else if (status == 2) {
	qm.sendNextPrevS("#b(I've never met him before. He doesn't look human.)#k", 2);
    } else if (status == 3) {
	qm.sendNextPrev("Hey Aran! Do you still not hear me? Seriously, can't you hear me? Ahhh, this is frustrating!");
    } else if (status == 4) {
	qm.sendNextPrevS("#b(Whoa, who was that? Sounds like an angry boy...)#k", 2);
    } else if (status == 5) {
	qm.sendNextPrev("Seriously, the one master I had turned out to be trapped in ice for hundreds of years, abandoning the weapon, and now the 'master' can't even hear me?");
    } else if (status == 6) {
	qm.sendNextPrevS("Who are you?", 2);
    } else if (status == 7) {
	qm.sendNextPrev("Aran? Do you hear me now? It's me, it's me! I'm your weapon #bMaha the pole arm!#k!");
    } else if (status == 8) {
	qm.sendNextPrevS("#b(...Maha? Giant pole Arm actually talks?)#k", 2);
    } else if (status == 9) {
	qm.sendNextPrev("Why do you have that look on your face like you can't believe it? I see that you have lost all your memories, but... did you also forget about me? How can you do that to me??");
    } else if (status == 10) {
	qm.sendNextPrevS("I'm sorry, but seriously... I don't remember a thing.", 2);
    } else if (status == 11) {
	qm.sendYesNo("Is that all you can say after all those years? I'm sorry? Do you understand how bored I was all by myself for hundreds of years? Bring it out if you can. Bring your memories out! Bring them all out! Dig them up if you need to!");
    } else if (status == 12) {
	qm.sendNextS("#b(The voice that claims to be Maha the Giant Pole Arm seem quite perturbed. This conversation is going nowhere. I better talk to Lirin first.)#k", 2);
	qm.forceCompleteQuest();
	qm.forceStartQuest(21202); //skip just in case
	qm.forceStartQuest(21203, "0");
    } else if (status == 13) {
	qm.sendYesNo("Would you like to skip the video clip?  Even if you skip the scene, game play will not be affected.");
    } else if (status == 14) {
	qm.dispose();
    }
}