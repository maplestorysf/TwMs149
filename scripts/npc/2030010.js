/* Amon
 * Last Mission : Zakum's Altar (280030000)
 */

function start() {
    cm.sendYesNo("一旦離開，就要重新挑戰，你真的要離開嗎？");
}

function action(mode, type, selection) {
    if (mode == 1) {
	cm.warp(211042200);
    }
    cm.dispose();
}