Ext.define('Lib.service.Validation', {
	singleton : true,
	requires : ['Ext.layout.component.field.Field'],
	alternateClassName : 'Lib.Validation',
	validation : {
		notblank : function(el) {
			var msgEl = el.parent('.field-group').child(".field-error");
			if (el.getValue() == "") {
				el.addCls('x-form-invalid-field');
				if (msgEl) {
					var html = '<div class="x-form-error-msg x-form-invalid-icon" data-errorqtip="&lt;ul class=&quot;x-list-plain&quot;&gt;&lt;li&gt;该输入项为必输项&lt;/li&gt;&lt;/ul&gt;"><ul class="x-list-plain"><li>该输入项为必输项</li></ul></div>';
					msgEl.update(html);
					msgEl.setDisplayed(true);
				}
				return false;
			} else {
				el.removeCls('x-form-invalid-field');
				if (msgEl) {
					msgEl.setDisplayed(false);
				}
				return true;
			}
		},
		maxLength : function(el) {
			var msgEl = el.parent('.field-group').child(".field-error");
			var max = parseInt(el.getAttribute("data-maxLength"), 10);
			if (el.getValue().length > max) {
				el.addCls('x-form-invalid-field');
				if (msgEl) {
					var html = '<div class="x-form-error-msg x-form-invalid-icon" data-errorqtip="&lt;ul class=&quot;x-list-plain&quot;&gt;&lt;li&gt;该输入项超过最大长度&lt;/li&gt;&lt;/ul&gt;"><ul class="x-list-plain"><li>该输入项超过最大长度</li></ul></div>';
					msgEl.update(html);
					msgEl.setDisplayed(true);
				}
				return false;
			} else {
				el.removeCls('x-form-invalid-field');
				if (msgEl) {
					msgEl.setDisplayed(false);
				}
				return true;
			}
		}
	},
	validate : function(field) {
		var field = Ext.get(field);
		if (field.getAttribute('data-allowBlank') == 'false') {
			var vtype = this.validation['notblank'];
			return vtype(field);
		}
		if (!Ext.isEmpty(field.getAttribute('data-maxLength'))) {
			var vtype = this.validation['maxLength'];
			return vtype(field);
		}
		return true;
	},
	validateCheckBox : function(field) {
		var field = Ext.get(field);
		if (field.getAttribute('data-allowBlank') == 'false') {
			var hasChecked = field.down('input:checked');
			var msgEl = field.parent('.field-group').down(".field-error");
			if (!hasChecked) {
				// field.addCls('x-form-invalid');
				field.addCls('x-form-checkboxgroup-invalid');
				if (msgEl) {
					var html = '<div class="x-form-error-msg x-form-invalid-icon" data-errorqtip="&lt;ul class=&quot;x-list-plain&quot;&gt;&lt;li&gt;该输入项为必输项&lt;/li&gt;&lt;/ul&gt;"><ul class="x-list-plain"><li>该输入项为必输项</li></ul></div>';
					msgEl.update(html);
					msgEl.setDisplayed(true);
				}
				return false;
			} else {
				// field.removeCls('x-form-invalid');
				field.removeCls('x-form-checkboxgroup-invalid');
				if (msgEl) {
					msgEl.setDisplayed(false);
				}
				return true;
			}
		}
		return true;
	},
	doValidation : function(target) {
		// add stop on first error capability?
		var stopOnFistError = false;
		var me = this;
		var targetEl = target ? Ext.get(target) : Ext.getBody();
		var textfields = targetEl.select('input[type="text"]');
		var success = true;
		textfields.each(function(field) {
			var valid = me.validate(field);
			success = success && valid;
			if (stopOnFistError) {
				return valid;
			}
		});
		var textareas = targetEl.select('textarea');
		textareas.each(function(field) {
			var valid = me.validate(field);
			success = success && valid;
			if (stopOnFistError) {
				return valid;
			}
		});
		var radiogroup = targetEl.select('.radiogroup');
		radiogroup.each(function(field) {
			var valid = me.validateCheckBox(field);
			success = success && valid;
			if (stopOnFistError) {
				return valid;
			}
		});
		var checkgroup = targetEl.select('.checkgroup');
		checkgroup.each(function(field) {
			var valid = me.validateCheckBox(field);
			success = success && valid;
			if (stopOnFistError) {
				return valid;
			}
		});
		return success;
	},
	setupValidation : function(target) {
		var me = this;
		var targetEl = target ? Ext.get(target) : Ext.getBody();
		var textfields = targetEl.select('input[type="text"]');
		textfields.each(function(field) {
			if (field.getAttribute('data-allowBlank') == 'false') {
				field.on('blur', function(e, dom) {
					var el = Ext.get(dom);
					var vtype = me.validation['notblank'];
					vtype(el);
				});
			}
			if (!Ext.isEmpty(field.getAttribute('data-maxLength'))) {
				field.on('blur', function(e, dom) {
					var el = Ext.get(dom);
					var vtype = me.validation['maxLength'];
					vtype(el);
				});
			}
		});
		var textareas = targetEl.select('textarea');
		textareas.each(function(field) {
			if (field.getAttribute('data-allowBlank') == 'false') {
				field.on('blur', function(e, dom) {
					var el = Ext.get(dom);
					var vtype = me.validation['notblank'];
					vtype(el);
				});
			}
			if (!Ext.isEmpty(field.getAttribute('data-maxLength'))) {
				field.on('blur', function(e, dom) {
					var el = Ext.get(dom);
					var vtype = me.validation['maxLength'];
					vtype(el);
				});
			}
		});
		var radiogroup = targetEl.select('.radiogroup');
		radiogroup.each(function(field) {
			if (field.getAttribute('data-allowBlank') == 'false') {
				field.select('input[type="radio"]').on('change', function(e, dom) {
					var group = Ext.get(dom).parent('.radiogroup');
					me.validateCheckBox(group);
				});
			}
		});
		var checkgroup = targetEl.select('.checkgroup');
		checkgroup.each(function(field) {
			if (field.getAttribute('data-allowBlank') == 'false') {
				field.select('input[type="checkbox"]').on('change', function(e, dom) {
					var group = Ext.get(dom).parent('.radiogroup');
					me.validateCheckBox(group);
				});
			}
		});
	}
}, function() {
	// for side msgTarget
	Ext.layout.component.field.Field.initTip();
});
