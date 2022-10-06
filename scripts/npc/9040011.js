/* 
 * @Author lrenex
 * 
 * 公會戰留言板NPC
 * 
 * 如何開始公會戰
 */

var msg = "";

function action(mode, type, selection) {
	msg += "#b<公告事項>#k\r\n";
	msg += "您是否已經準備好勇氣了，和公會間的默契了來準備挑戰公會戰?\r\n";
	msg += "#b規則說明：#k\r\n";
	msg += "1. 必須公會人數六個人以上\r\n";
	msg += "2. 必須是公會長或者公會副會長來啟動公會戰\r\n";
	msg += "3. 假如公會戰開始的時候人數低於6人以下或者領導者決定放棄，那麼公會戰將會失敗提早結束。\r\n";
	msg += "4. 公會戰愉快~";
    cm.sendOk(msg);
    cm.safeDispose();
}