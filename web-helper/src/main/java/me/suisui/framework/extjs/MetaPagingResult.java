package me.suisui.framework.extjs;

import java.util.List;

import me.suisui.framework.paging.PagingResult;

import org.springframework.util.CollectionUtils;

/**
 * used for extjs json reader meta support.
 * 
 * <pre>
 * {
 *     "count": 1,
 *     "ok": true,
 *     "msg": "Users found",
 *     "records": [{
 *         "userId": 123,
 *         "name": "Ed Spencer",
 *         "email": "ed@sencha.com"
 *     }],
 *     "metaData": {
 *         "root": "users",
 *         "idProperty": 'userId',
 *         "totalProperty": 'count',
 *         "successProperty": 'ok',
 *         "messageProperty": 'msg',
 *         "fields": [
 *             { "name": "userId", "type": "int" },
 *             { "name": "name", "type": "string" },
 *             { "name": "birthday", "type": "date", "dateFormat": "Y-j-m" },
 *         ],
 *         "columns": [
 *             { "text": "User ID", "dataIndex": "userId", "width": 40 },
 *             { "text": "User Name", "dataIndex": "name", "flex": 1 },
 *             { "text": "Birthday", "dataIndex": "birthday", "flex": 1, "format": 'Y-j-m', "xtype": "datecolumn" }
 *         ]
 *     }
 * 	}
 * </pre>
 * 
 * @author aaron
 * 
 */
public class MetaPagingResult<T> extends PagingResult<T> {
	private static final long serialVersionUID = -3508402596913094824L;
	private static final MetaDataAssembler metaDataAssembler = new MetaDataAssembler();
	
	MetaData metaData;

	public MetaPagingResult() {
	}

	public MetaPagingResult(List<T> records, int total) {
		this.records = records;
		this.total = total;
	}

	public MetaPagingResult(MetaData metaData, List<T> records, int total) {
		this.metaData = metaData;
		this.records = records;
		this.total = total;
	}

	public void setMetaData(MetaData metaData) {
		this.metaData = metaData;
	}

	public MetaData getMetaData() {
		return metaData;
	}
	public static <T> MetaPagingResult<T> from(List<T> records, int total) {
		Class<? extends Object> clazz = Object.class;
		if(!CollectionUtils.isEmpty(records)){
			clazz = records.iterator().next().getClass();
		}
		MetaData metaData = metaDataAssembler.createGridConfigFromClass(clazz);
		return new MetaPagingResult<T>(metaData, records, total);
	}
	public static <T> MetaPagingResult<T> from(List<T> records) {
		return from(records, records.size());
    }
}
