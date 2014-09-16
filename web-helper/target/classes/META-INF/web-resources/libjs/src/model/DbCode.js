Ext.define('Lib.model.DbCode', {
	extend : 'Ext.data.Model',
	requires:['Ext.data.reader.Json'],
	fields : [{
		name : 'id', 
		mapping : 'codeDictionaryId'
	},  {
		name : 'companyCode',
		mapping : 'companyCode'
	},  {
		name : 'table',
		mapping : 'tableName'
	},  {
		name : 'column',
		mapping : 'columnName'
	}, {
		name : 'code',
		mapping : 'code'
	}, {
		name : 'name_zh',
		mapping : 'nameZh'
	}, {
		name : 'name_en',
		mapping : 'nameEn'
	}, {
		name : 'order',
		mapping : 'sortOrder'
	}, {
		name : 'description',
		mapping : 'description'
	}, {
		name: 'name',
		convert: function(value, record) {
			if(Lib.Config.lang == 'zh'){
				return record.get('name_zh');
			}else{
				return record.get('name_en');
			}
		}
	}]

});
