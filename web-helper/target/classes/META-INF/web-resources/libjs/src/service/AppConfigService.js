/**
 * ## Example Code
 * 使用字典表存 系统配置
 * Lib.AppConfig.get('config_key');
*/

Ext.define('Lib.service.AppConfigService', {
	alternateClassName: 'Lib.AppConfig',
	singleton:true,
    requires : ['Lib.service.EnumCodeService'],
	get : function(key, companyCode) {
		return Lib.EnumCode.getName('setting','system_config', key,'', companyCode);
	}
});
