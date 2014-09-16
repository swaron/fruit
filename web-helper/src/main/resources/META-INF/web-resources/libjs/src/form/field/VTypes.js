Ext.define("Lib.form.field.VTypes", {
	override : "Ext.form.field.VTypes",
	daterange : function(val, field) {
		var date = field.parseDate(val);
		var basicValidate = function(datefield) {
			var vtype = datefield.vtype;
			datefield.vtype = undefined;
			var isValid = datefield.isValid();
			datefield.vtype = vtype;
			return isValid;
		};
		if (field.startDateField) {
			var start = Ext.getCmp(field.startDateField) || field.up('container').down('#' + field.startDateField);
			if (!date) {
				if (start.hasActiveError() && basicValidate(start)) {
					start.clearInvalid();
					// to clear form's invalid status
					Ext.defer(start.validate, 100, start);
				}
				return true;
			}
			var startDate = start.getValue();
			if (!startDate) {
				return true;
			}

			if (startDate.getTime() > date.getTime()) {
				return false;
			} else {
				if (start.hasActiveError() && basicValidate(start)) {
					start.clearInvalid();
					// to clear form's invalid status
					Ext.defer(start.validate, 100, start);
				}
				return true;
			}

		} else if (field.endDateField && (!this.dateRangeMin || (date.getTime() != this.dateRangeMin.getTime()))) {
			var end = Ext.getCmp(field.endDateField) || field.up('container').down('#' + field.endDateField);

			if (!date) {
				if (end.hasActiveError() && basicValidate(end)) {
					end.clearInvalid();
					// to clear form's invalid status
					Ext.defer(end.validate, 100, end);
				}
				return true;
			}
			var endDate = end.getValue();
			if (!endDate) {
				return true;
			}

			if (date.getTime() > endDate.getTime()) {
				return false;
			} else {
				if (end.hasActiveError() && basicValidate(end)) {
					end.clearInvalid();
					// to clear form's invalid status
					Ext.defer(end.validate, 100, end);
				}
				return true;
			}
		}
		/*
		 * Always return true since we're only using this vtype to set the min/max allowed values (these are tested for
		 * after the vtype test)
		 */
		return true;
	},
	daterangeText : '结束日期需要大于开始日期',

	numberrange : function(val, field) {
		if (field.startNumberField) {
			var start = Ext.getCmp(field.startNumberField) || field.up('container').down('#' + field.startNumberField);
			if (!val) {
				start.clearInvalid();
				return true;
			}
			var startVal = start.getValue();
			if (!startVal) {
				return true;
			}

			if (startVal > val) {
				return false;
			} else {
				start.clearInvalid();
				return true;
			}

		} else if (field.endNumberField) {
			var end = Ext.getCmp(field.endNumberField) || field.up('container').down('#' + field.endNumberField);

			if (!val) {
				end.clearInvalid();
				return true;
			}
			var endVal = end.getValue();
			if (!endVal) {
				return true;
			}

			if (val > endVal) {
				return false;
			} else {
				end.clearInvalid();
				return true;
			}
		}
		return true;
	},

	numberrangeText: '结束数值需要大于开始数值'
});
