var status = -1;

function action(mode, type, selection) {
    switch (mode) {
        case 1:
            status++;
            break;
		default:
			status--;
			break;
    }
	//cm.playerMessage(status);
    switch (status) {
        case 0:
            var content = "請選擇公告項目：\r\n";
            content += "#r";
			//content += "#L0#贊助優惠#l";
			content += "#k\r\n";
			content += ""
            content += "#b#L1#[2017/07/28]遊戲說明#l#k\r\n";
			//content += "#r#L4#[2017/07/20][置頂]7月20日測試伺服器正式開啟#l#k\r\n";
			//content += "#b#L6#[2017/07/21][公告]7月21日更新內容#l#k\r\n";
			//content += "#b#L5#[2017/07/20][公告]7月20日更新內容#l#k\r\n";
			//content += "#b#L3#[2017/07/12][更新]7月12日更新內容#l#k\r\n";
			//content += "#b#L2#[2017/07/11][更新]7月11日更新內容#l#k\r\n";
			//content += "#L3#[2017/07/11][測試]公告測試#l\r\n";
            content += "#k";
            cm.sendSimple(content);
            break;
        case 1:
            switch (selection) {
				case 0:
					var list = "#r如果你有想要其他的贊助獎勵，歡迎提出建議唷！\r\n\r\n#k";
					list += "#b贊助我們，額外贈送不滅卷軸七張唷！#k\r\n\r\n";
					list += "贊助#r300#k獎勵：(#r900樂豆點數#k)\r\n";
					list += "#i2049700:# (#r三張#k)";
					list += "#i2049300:# (#r十張#k)\r\n\r\n";
					list += "贊助#r500#k獎勵：(#r1500樂豆點數#k)\r\n";
					list += "#i2049700:# (#r三張#k)"; // 300
					list += "#i2049300:# (#r十張#k)"; // 300
					list += "#i1112127:#\r\n\r\n"; // 500
					list += "贊助#r1000#k獎勵：(#r3000樂豆點數#k)\r\n";
					list += "#i2049700:# (#r三張#k)"; // 300
					list += "#i2049300:# (#r十張#k)"; // 300
					list += "#i1112127:#"; // 500
					list += "#i1142249:#"; // 1000
					list += "#i1182006:#"; // 1000
					list += "#i5210000:# #r(永久)#k\r\n\r\n"; // 1000
					list += "贊助#r3000#k獎勵：(#r9000樂豆點數#k)\r\n";
					list += "#i2049700:# (#r三張#k)"; // 300
					list += "#i2049300:# (#r十張#k)"; // 300
					list += "#i1112127:#"; // 500
					list += "#i1142249:#"; // 1000
					list += "#i1182006:#"; // 1000
					list += "#i1122034:# (#r對應職業#k)"; // 3000
					list += "#i5210000:# #r(永久)#k\r\n\r\n"; // 1000
					list += "贊助#r5000#k獎勵：(#r15000樂豆點數#k)\r\n";
					list += "#i2049700:# (#r三張#k)"; // 300
					list += "#i2049300:# (#r十張#k)"; // 300
					list += "#i1112127:#"; // 500
					list += "#i1142249:#"; // 1000
					list += "#i1182006:#"; // 1000
					list += "#i1122034:# (#r對應職業#k)"; // 3000
					list += "#i5210000:# #r(永久)#k"; // 1000
					list += "#i1302152:# #r(西格諾斯套裝(全套))#k\r\n\r\n";
					list += "贊助#r10000#k獎勵：(#r30000樂豆點數#k)\r\n";
					list += "#i2049700:# (#r三張#k)"; // 300
					list += "#i2049300:# (#r十張#k)"; // 300
					list += "#i1112127:#"; // 500
					list += "#i1142249:#"; // 1000
					list += "#i1182006:#"; // 1000
					list += "#i1122034:# (#r對應職業#k)\r\n"; // 3000
					list += "#i5210000:# #r(永久)#k"; // 1000
					list += "#i1302152:# #r(西格諾斯套裝(全套))#k";
					list += "#i1202022:# #r(x3)#k\r\n";
					list += "#r如果你有想要其他的贊助獎勵，歡迎提出建議唷！#k";
                    cm.sendOk(list);
					status = -1;
                    break;
                case 1:
					var illustrate = "收集#b溫暖陽光#k可至#r楓樹山丘#k地圖參加活動。\r\n";
					illustrate += "收集#b火藥桶#k可至#r邱比特公園#k地圖參加活動。\r\n";
					illustrate += "收集#b聖誕樹裝飾#k可至#r白色聖誕節之丘#k地圖參加活動。\r\n";
					illustrate += "收集#b香爐#k可至#r不夜城#k地圖參加活動。\r\n";
					illustrate += "完成#b月妙的年糕#k組隊任務可獲得#r1楓葉點數#k。\r\n";
					illustrate += "完成#b第一次同行#k組隊任務可獲得#r3楓葉點數#k。\r\n";
					illustrate += "完成#b其他組隊任務#k可獲得#r5楓葉點數#k。\r\n";
					illustrate += "完成#b武陵道場#k每一道關卡#k可獲得#r1楓葉點數#k。\r\n";
					illustrate += "完成#bBoss組隊任務#k可獲得#r對應難度楓葉點數#k。\r\n";
                    cm.sendOk(illustrate);
					status = -1;
                    break;
				case 2:
					var update = "1.針對一轉轉職腳本進行中文化並修復二轉轉職腳本。\r\n";
					update += "2.新增#r@幫助#k、#r@公告#k指令。\r\n";
					cm.sendOk(update);
					status = -1;
					break;
				case 3:
					var update = "1.新增#r@轉職#k指令。\r\n";
					update += "2.修復怪物不會掉落3轉、4轉任務道具。\r\n";
					update += "3.部分腳本中文化。\r\n";
					cm.sendOk(update);
					status = -1;
					break;
				case 4:
					var update = "歡迎光臨測試伺服器！\r\n測試伺服器預計開放#r3-7日！#k\r\n#r正式開放後會進行刪檔作業！#k\r\n參加測試伺服器者未來會額外贈送獎勵。\r\n目前開放#r冒險家、皇家騎士團、狂狼勇士、末日反抗軍、精靈遊俠、重砲指揮官#k。\r\n請#r不要#k建立其他職業。\r\n使用#b@幫助#k調查指令。\r\n#r遇到Bug請使用#k#b@訊息 <內容>#k#r指令回報給遊戲管理員。#k";
					cm.sendOk(update);
					status = -1;
					break;
				case 5:
					var update = "本次維護後已修正部分地圖異常問題。";
					cm.sendOk(update);
					status = -1;
					break;
				case 6:
					var update = "本次維護後已修正無法補充飛鏢異常、商店販售物品異常、購物商城異常、煉獄巫師超級體技能異常，並且修復銀寶箱、金寶箱。地圖傳送新增白色聖誕節之丘選項。二次維護後已修復破風使者箭雨技能、煉獄巫師防禦姿態技能、烈焰巫師瞬間移動精通技能、機甲戰神一轉技能異常、裝備保護卷軸異常、修復終極冒險家、開放影武者、惡魔殺手轉職。";
					cm.sendOk(update);
					status = -1;
					break;
            }
            break;
		default:
			cm.dispose();
			break;
    }
}