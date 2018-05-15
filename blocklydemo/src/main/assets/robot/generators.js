'use strict';
Blockly.JavaScript['sound_sensor_value'] = function(block) {
  var dropdown_sound_port_value = block.getFieldValue('sound_port_value');
  var code = 'print('+dropdown_sound_port_value+'\);\n';
  return code;
};


Blockly.JavaScript['control_wait'] = function(block) {
  var number_control_time = block.getFieldValue('control_time');
  var code = 'control.wait('+number_control_time+');\n';
  return code;
};



//运动
Blockly.JavaScript['move_go_back_time'] = function(block) {
  var dropdown_go_or_back = block.getFieldValue('go_or_back');
  var text_move_speed = block.getFieldValue('move_speed');
  var text_move_time = block.getFieldValue('move_time');
  // TODO: Assemble JavaScript into code variable.
  var code = 'move.goBackTime('+dropdown_go_or_back+','+text_move_speed+','+text_move_time+');\n';
  return code;
};
Blockly.JavaScript['move_clockwise'] = function(block) {
  var dropdown_go_or_back = block.getFieldValue('go_or_back');
  var text_move_speed = block.getFieldValue('move_speed');
  var text_move_time = block.getFieldValue('move_time');
  // TODO: Assemble JavaScript into code variable.
  var code = 'move.clockwise('+dropdown_go_or_back+','+text_move_speed+','+text_move_time+');\n';
  return code;
};
Blockly.JavaScript['move_go_back'] = function(block) {
  var dropdown_go_or_back = block.getFieldValue('go_or_back');
  var text_move_speed = block.getFieldValue('move_speed');
  // TODO: Assemble JavaScript into code variable.
  var code = 'move.goBack('+dropdown_go_or_back+','+text_move_speed+');\n';
  return code;
};
Blockly.JavaScript['move_direction'] = function(block) {
  var dropdown_go_or_back = block.getFieldValue('go_or_back');
  var text_move_speed = block.getFieldValue('move_speed');
  // TODO: Assemble JavaScript into code variable.
 var code = 'move.direction('+dropdown_go_or_back+','+text_move_speed+');\n';
  return code;
};
Blockly.JavaScript['move_stop'] = function(block) {
  var code = 'move.stop();\n';
  return code;
};


Blockly.JavaScript['option_start'] = function(block) {
  var code = 'option.command(\'option_start\');\n';
  return code;
};
// 展示
Blockly.JavaScript['option_xiaolian'] = function(block) {
  var code = 'option.command(\'option_xiaolian\');\n';
  return code;
};
Blockly.JavaScript['option_jvsang'] = function(block) {
  var code = 'option.command(\'option_jvsang\');\n';
  return code;
};
Blockly.JavaScript['option_xiangshang'] = function(block) {
  var code = 'option.command(\'option_xiangshang\');\n';
  return code;
};
Blockly.JavaScript['option_xiangxia'] = function(block) {
  var code = 'option.command(\'option_xiangxia\');\n';
  return code;
};
Blockly.JavaScript['option_xiangzuo'] = function(block) {
  var code = 'option.command(\'option_xiangzuo\');\n';
  return code;
};
Blockly.JavaScript['option_xiangyou'] = function(block) {
  var code = 'option.command(\'option_xiangyou\');\n';
  return code;
};
Blockly.JavaScript['option_close_rgb'] = function(block) {
  var code = 'option.command(\'option_close_rgb\');\n';
  return code;
};
Blockly.JavaScript['option_close_dianzhen'] = function(block) {
  var code = 'option.command(\'option_close_dianzhen\');\n';
  return code;
};



Blockly.JavaScript['option_huangse'] = function(block) {
  var code = 'option.command(\'option_huangse\');\n';
  return code;
};
Blockly.JavaScript['option_lanse'] = function(block) {
  var code = 'option.command(\'option_lanse\');\n';
  return code;
};
Blockly.JavaScript['option_lvse'] = function(block) {
  var code = 'option.command(\'option_lvse\');\n';
  return code;
};
Blockly.JavaScript['option_hongse'] = function(block) {
  var code = 'option.command(\'option_hongse\');\n';
  return code;
};
Blockly.JavaScript['option_zise'] = function(block) {
  var code = 'option.command(\'option_zise\');\n';
  return code;
};
Blockly.JavaScript['option_baise'] = function(block) {
  var code = 'option.command(\'option_baise\');\n';
  return code;
};
Blockly.JavaScript['option_fense'] = function(block) {
  var code = 'option.command(\'option_fense\');\n';
  return code;
};


