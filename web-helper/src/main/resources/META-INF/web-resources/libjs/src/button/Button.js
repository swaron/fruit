Ext.define('Lib.button.Button', {

    /* Begin Definitions */
    alias: 'widget.lib.button',
    extend: 'Ext.Component',

    alternateClassName: 'Lib.Button',
    /* End Definitions */
	childEls: [
        'btnEl', 'iconEl'
    ],
    
    //default style
    btnDefaultCls:'btn-app',

    btnCls:'',
    
    // We have to keep "unselectable" attribute on all elements because it's not inheritable.
    // Without it, clicking anywhere on a button disrupts current selection and cursor position
    // in HtmlEditor.
    renderTpl: [
    	'<a id="{id}-btnEl" class="btn {btnDefaultCls} {btnCls}" ',
    // If the button is to function as a link...
            '<tpl if="href">',
                ' href="{href}"',
                '<tpl if="hrefTarget">',
                    ' target="{hrefTarget}"',
                '</tpl>',
            '</tpl>',
        '>',
        '<i id="{id}-iconEl" class="{iconCls}"></i><tpl if="iconCls">&#160;</tpl>',
    	'{text}</a>'
    ],
 
    /*
     * <i id="{id}-iconEl" class="{iconCls}"></i>',
     * @property {Boolean} isAction
     * `true` in this class to identify an object as an instantiated Button, or subclass thereof.
     */
    isButton: true,

    /**
     * @property {Boolean} hidden
     * True if this button is hidden.
     * @readonly
     */
    hidden: false,

    /**
     * @property {Boolean} disabled
     * True if this button is disabled.
     * @readonly
     */
    disabled: false,

    /**
     * @property {Boolean} pressed
     * True if this button is pressed (only if enableToggle = true).
     * @readonly
     */
    pressed: false,

    /**
     * @cfg {String} text
     * The button text to be used as innerHTML (html tags are accepted).
     */

    /**
     * @cfg {Function} handler
     * A function called when the button is clicked (can be used instead of click event).
     * @cfg {Ext.button.Button} handler.button This button.
     * @cfg {Ext.EventObject} handler.e The click event.
     */

    /**
     * @cfg {Number} tabIndex
     * Set a DOM tabIndex for this button.
     */

    /**
     * @cfg {String} textAlign
     * The text alignment for this button (center, left, right).
     */
    textAlign: 'center',

    /**
     * @cfg {String} type
     * The type of `<input>` to create: submit, reset or button.
     */
    type: 'button',

    /**
     * @cfg {String} clickEvent
     * The DOM event that will fire the handler of the button. This can be any valid event name (dblclick, contextmenu).
     */
    clickEvent: 'click',

    /**
     * @cfg {Boolean} preventDefault
     * True to prevent the default action when the {@link #clickEvent} is processed.
     */
    preventDefault: true,

    /**
     * @cfg {Boolean} handleMouseEvents
     * False to disable visual cues on mouseover, mouseout and mousedown.
     */
    handleMouseEvents: true,

    /**
     * @cfg {String} pressedCls
     * The CSS class to add to a button when it is in the pressed state.
     */
    pressedCls: 'pressed',

    /**
     * @cfg {String} overCls
     * The CSS class to add to a button when it is in the over (hovered) state.
     */
    overCls: 'over',

    /**
     * @cfg {String} focusCls
     * The CSS class to add to a button when it is in the focussed state.
     */
    focusCls: 'focus',

    /**
     * @cfg {String} href
     * The URL to open when the button is clicked. Specifying this config causes the Button to be
     * rendered with the specified URL as the `href` attribute of its `<a>` Element.
     *
     * This is better than specifying a click handler of
     *
     *     function() { window.location = "http://www.sencha.com" }
     *
     * because the UI will provide meaningful hints to the user as to what to expect upon clicking
     * the button, and will also allow the user to open in a new tab or window, bookmark or drag the URL, or directly save
     * the URL stream to disk.
     *
     * See also the {@link #hrefTarget} config.
     */
    
    /**
      * @cfg {String} [hrefTarget="_blank"]
      * The target attribute to use for the underlying anchor. Only used if the {@link #href}
      * property is specified.
      */
     hrefTarget: '_blank',
     
    /**
     * @cfg {"small"/"medium"/"large"} scale
     * The size of the Button. Three values are allowed:
     *
     * - 'small' - Results in the button element being 16px high.
     * - 'medium' - Results in the button element being 24px high.
     * - 'large' - Results in the button element being 32px high.
     */
    scale: 'medium',

    /**
     * @private
     * An array of allowed scales.
     */
    allowedScales: ['small', 'medium', 'large'],

    /**
     * @cfg {String} iconAlign
     * The side of the Button box to render the icon. Four values are allowed:
     *
     * - 'top'
     * - 'right'
     * - 'bottom'
     * - 'left'
     */
    iconAlign: 'left',

    /**
     * @property {Ext.Template} template
     * A {@link Ext.Template Template} used to create the Button's DOM structure.
     *
     * Instances, or subclasses which need a different DOM structure may provide a different template layout in
     * conjunction with an implementation of {@link #getTemplateArgs}.
     */

    /**
     * @cfg {String} cls
     * A CSS class string to apply to the button's main element.
     */

    // inherit docs
    initComponent: function() {
        var me = this;

        // Ensure no selection happens
        me.addCls('x-unselectable');

        me.callParent(arguments);

        me.addEvents(
            /**
             * @event click
             * Fires when this button is clicked, before the configured {@link #handler} is invoked. Execution of the
             * {@link #handler} may be vetoed by returning <code>false</code> to this event.
             * @param {Ext.button.Button} this
             * @param {Event} e The click event
             */
            'click',
			'toggle',
            /**
             * @event mouseover
             * Fires when the mouse hovers over the button
             * @param {Ext.button.Button} this
             * @param {Event} e The event object
             */
            'mouseover',

            /**
             * @event mouseout
             * Fires when the mouse exits the button
             * @param {Ext.button.Button} this
             * @param {Event} e The event object
             */
            'mouseout'
        );

        // Accept url as a synonym for href
        if (me.url) {
            me.href = me.url;
        }

        // preventDefault defaults to false for links
        if (me.href && !me.hasOwnProperty('preventDefault')) {
            me.preventDefault = false;
        }
        
        if (me.html && !me.text) {
            me.text = me.html;
            delete me.html;
        }
    },

    // inherit docs
    getActionEl: function() {
        return this.el;
    },

    // inherit docs
    getFocusEl: function() {
        return this.el;
    },

    // @private
    setComponentCls: function() {
        var me = this,
            cls = me.getComponentCls();

        if (!Ext.isEmpty(me.oldCls)) {
            me.removeClsWithUI(me.oldCls);
            me.removeClsWithUI(me.pressedCls);
        }

        me.oldCls = cls;
        me.addClsWithUI(cls);
    },

    getComponentCls: function() {
        var me = this,
            cls = [];

        // Check whether the button has an icon or not, and if it has an icon, what is the alignment
        if (me.iconCls || me.icon || me.glyph) {
            if (me.text) {
                cls.push('icon-text-' + me.iconAlign);
            } else {
                cls.push('icon');
            }
        } else if (me.text) {
            cls.push('noicon');
        }

        if (me.pressed) {
            cls.push(me.pressedCls);
        }
        return cls;
    },

    beforeRender: function () {
        var me = this;

        me.callParent();

        // Add all needed classes to the protoElement.
        me.oldCls = me.getComponentCls();
        me.addClsWithUI(me.oldCls);
        // Apply the renderData to the template args
        Ext.applyIf(me.renderData, me.getTemplateArgs());
    },

    // @private
    onRender: function() {
        var me = this,
            addOnclick,
            btn,
            btnListeners;

        me.doc = Ext.getDoc();
        me.callParent(arguments);

        // Set btn as a local variable for easy access
        btn = me.el;

        // Add the mouse events to the button
        if (me.handleMouseEvents) {
            btnListeners = {
                scope: me,
                mouseover: me.onMouseOver,
                mouseout: me.onMouseOut,
                mousedown: me.onMouseDown
            };
        } else {
            btnListeners = {
                scope: me
            };
        }


        // If the activation event already has a handler, make a note to add the handler later
        if (btnListeners[me.clickEvent]) {
            addOnclick = true;
        } else {
            btnListeners[me.clickEvent] = me.onClick;
        }

        // Add whatever button listeners we need
        me.mon(btn, btnListeners);

        Ext.button.Manager.register(me);
    },

    /**
     * This method returns an object which provides substitution parameters for the {@link #renderTpl XTemplate} used to
     * create this Button's DOM structure.
     *
     * Instances or subclasses which use a different Template to create a different DOM structure may need to provide
     * their own implementation of this method.
     *
     * @return {Object} Substitution data for a Template. The default implementation which provides data for the default
     * {@link #template} returns an Object containing the following properties:
     * @return {String} return.type The `<button>`'s {@link #type}
     * @return {String} return.splitCls A CSS class to determine the presence and position of an arrow icon.
     * (`'x-btn-arrow'` or `'x-btn-arrow-bottom'` or `''`)
     * @return {String} return.cls A CSS class name applied to the Button's main `<tbody>` element which determines the
     * button's scale and icon alignment.
     * @return {String} return.text The {@link #text} to display ion the Button.
     * @return {Number} return.tabIndex The tab index within the input flow.
     */
    getTemplateArgs: function() {
        var me = this;
        return {
            href     : me.getHref(),
            hrefTarget: me.hrefTarget,
            type     : me.type,
            btnCls : me.btnCls,
            btnDefaultCls : me.btnDefaultCls,
            innerCls : me.getInnerCls(),
            splitCls : me.getSplitCls(),
            iconUrl  : me.icon,
            iconCls  : me.iconCls,
            text     : me.text || '&#160;',
            tabIndex : me.tabIndex == null ? 0 : me.tabIndex
        };
    },

    /**
     * Sets the href of the embedded anchor element to the passed URL.
     *
     * Also appends any configured {@link #cfg-baseParams} and parameters set through {@link #setParams}.
     * @param {String} href The URL to set in the anchor element.
     *
     */
    setHref: function(href) {
        this.href = href;
        this.btnEl.dom.href = this.getHref();
    },

    /**
     * @private
     * If there is a configured href for this Button, returns the href with parameters appended.
     * @return {String/Boolean} The href string with parameters appended.
     */
    getHref: function() {
        var me = this,
            href = me.href;

        return href ? Ext.urlAppend(href, Ext.Object.toQueryString(Ext.apply({}, me.params, me.baseParams))) : false;
    },

    /**
     * Sets the href of the link dynamically according to the params passed, and any {@link #baseParams} configured.
     *
     * **Only valid if the Button was originally configured with a {@link #href}**
     *
     * @param {Object} params Parameters to use in the href URL.
     */
    setParams: function(params) {
        this.params = params;
        this.btnEl.dom.href = this.getHref();
    },

    getSplitCls: function() {
        var me = this;
        return me.split ? (me.baseCls + '-' + me.arrowCls) + ' ' + (me.baseCls + '-' + me.arrowCls + '-' + me.arrowAlign) : '';
    },

    getInnerCls: function() {
        return this.textAlign ? this.baseCls + '-inner-' + this.textAlign : '';
    },

    /**
     * Sets the text alignment for this button.
     * @param {String} align The new alignment of the button text. See {@link #textAlign}.
     */
    setTextAlign: function(align) {
        var me = this,
            btnEl = me.btnEl;

        if (btnEl) {
            btnEl.removeCls(me.baseCls + '-inner-' + me.textAlign);
            btnEl.addCls(me.baseCls + '-inner-' + align);
        }
        me.textAlign = align;
        return me;
    },

    getTipAttr: function(){
        return this.tooltipType == 'qtip' ? 'data-qtip' : 'title';
    },

    // @private
    clearTip: function() {
        var me = this,
            btnEl = me.btnEl;
            
        if (Ext.quickTipsActive && Ext.isObject(me.tooltip)) {
            Ext.tip.QuickTipManager.unregister(btnEl);
        } else {
            btnEl.dom.removeAttribute(me.getTipAttr());
        }
    },

    // @private
    beforeDestroy: function() {
        var me = this;
        if (me.rendered) {
            me.clearTip();
        }
        me.callParent();
    },

    // @private
    onDestroy: function() {
        var me = this;
        if (me.rendered) {
            me.doc.un('mouseover', me.monitorMouseOver, me);
            me.doc.un('mouseup', me.onMouseUp, me);
            delete me.doc;

            Ext.destroy(me.keyMap);
            delete me.keyMap;
        }
        Ext.button.Manager.unregister(me);
        me.callParent();
    },

    /**
     * Assigns this Button's click handler
     * @param {Function} handler The function to call when the button is clicked
     * @param {Object} [scope] The scope (`this` reference) in which the handler function is executed.
     * Defaults to this Button.
     * @return {Ext.button.Button} this
     */
    setHandler: function(handler, scope) {
        this.handler = handler;
        this.scope = scope;
        return this;
    },

    /**
     * Sets this Button's text
     * @param {String} text The button text
     * @return {Ext.button.Button} this
     */
    setText: function(text) {
        text = text || '';
        var me = this,
            oldText = me.text || '';

        if (text != oldText) {
            me.text = text;
            if (me.rendered) {
                me.btnEl.update(text || '&#160;');
                me.setComponentCls();
                if (Ext.isStrict && Ext.isIE8) {
                    // weird repaint issue causes it to not resize
                    me.el.repaint();
                }
                me.updateLayout();
            }
            me.fireEvent('textchange', me, oldText, text);
        }
        return me;
    },

    /**
     * Gets the text for this Button
     * @return {String} The button text
     */
    getText: function() {
        return this.text;
    },
    /**
     * If a state it passed, it becomes the pressed state otherwise the current state is toggled.
     * @param {Boolean} [state] Force a particular state
     * @param {Boolean} [suppressEvent=false] True to stop events being fired when calling this method.
     * @return {Ext.button.Button} this
     */
    toggle: function(state, suppressEvent) {
        var me = this;
        state = state === undefined ? !me.pressed: !!state;
        if (state !== me.pressed) {
            if (me.rendered) {
                me[state ? 'addClsWithUI': 'removeClsWithUI'](me.pressedCls);
            }
            me.pressed = state;
            if (!suppressEvent) {
                me.fireEvent('toggle', me, state);
                Ext.callback(me.toggleHandler, me.scope || me, [me, state]);
            }
        }
        return me;
    },
    
    // @private
    onRepeatClick: function(repeat, e) {
        this.onClick(e);
    },

    
    // @private
    onClick: function(e) {
        var me = this;
        if (me.preventDefault || (me.disabled && me.getHref()) && e) {
            e.preventDefault();
        }
        
        // Can be triggered by ENTER or SPACE keydown events which set the button property.
        // Only veto event handling if it's a mouse event with an alternative button.
        if (e.type !== 'keydown' && e.button !== 0) {
            return;
        }
        if (!me.disabled) {
        	me.doToggle();
            me.fireHandler(e);
        }
    },
    doToggle: function(){
        var me = this;    
        if (me.enableToggle && (me.allowDepress !== false || !me.pressed)) {
            me.toggle();
        }
    },
    fireHandler: function(e){
        var me = this,
            handler = me.handler;
            
        if (me.fireEvent('click', me, e) !== false) {
            if (handler) {
                handler.call(me.scope || me, me, e);
            }
            me.blur();
        }
    },
    

    /**
     * @private mouseover handler called when a mouseover event occurs anywhere within the encapsulating element.
     * The targets are interrogated to see what is being entered from where.
     * @param e
     */
    onMouseOver: function(e) {
        var me = this;
        if (!me.disabled && !e.within(me.el, true, true)) {
            me.onMouseEnter(e);
        }
    },

    /**
     * @private
     * mouseout handler called when a mouseout event occurs anywhere within the encapsulating element -
     * or the mouse leaves the encapsulating element.
     * The targets are interrogated to see what is being exited to where.
     * @param e
     */
    onMouseOut: function(e) {
        var me = this;
        if (!e.within(me.el, true, true)) {
            me.onMouseLeave(e);
        }
    },

    addOverCls: function() {
        if (!this.disabled) {
            this.addClsWithUI(this.overCls);
        }
    },
    removeOverCls: function() {
        this.removeClsWithUI(this.overCls);
    },

    /**
     * @private
     * virtual mouseenter handler called when it is detected that the mouseout event
     * signified the mouse entering the encapsulating element.
     * @param e
     */
    onMouseEnter: function(e) {
        // overCls is handled by AbstractComponent
        this.fireEvent('mouseover', this, e);
    },

    /**
     * @private
     * virtual mouseleave handler called when it is detected that the mouseover event
     * signified the mouse entering the encapsulating element.
     * @param e
     */
    onMouseLeave: function(e) {
        // overCls is handled by AbstractComponent
        this.fireEvent('mouseout', this, e);
    },

    // inherit docs
    enable : function(silent) {
        var me = this;

        me.callParent(arguments);

        me.removeClsWithUI('disabled');

        return me;
    },

    // inherit docs
    disable : function(silent) {
        var me = this;

        me.callParent(arguments);

        me.addClsWithUI('disabled');
        me.removeClsWithUI(me.overCls);

        // IE renders disabled text by layering gray text on top of white text, offset by 1 pixel. Normally this is fine
        // but in some circumstances (such as using formBind) it gets confused and renders them side by side instead.
        if (me.btnInnerEl && Ext.isIE7m) {
            me.btnInnerEl.repaint();
        }

        return me;
    },

    /**
     * Method to change the scale of the button. See {@link #scale} for allowed configurations.
     * @param {String} scale The scale to change to.
     */
    setScale: function(scale) {
        var me = this,
            ui = me.ui.replace('-' + me.scale, '');

        //check if it is an allowed scale
        if (!Ext.Array.contains(me.allowedScales, scale)) {
            throw('#setScale: scale must be an allowed scale (' + me.allowedScales.join(', ') + ')');
        }

        me.scale = scale;
        me.setUI(ui);
    },

    // @private
    onMouseDown: function(e) {
        var me = this;
        if (!me.disabled && e.button === 0) {
            me.addClsWithUI(me.pressedCls);
            me.doc.on('mouseup', me.onMouseUp, me);
        }
    },
    // @private
    onMouseUp: function(e) {
        var me = this;
        if (e.button === 0) {
            if (!me.pressed) {
                me.removeClsWithUI(me.pressedCls);
            }
            me.doc.un('mouseup', me.onMouseUp, me);
        }
    },

    // @private
    restoreClick: function() {
        this.ignoreNextClick = 0;
    },
    
    setIconCls: function(cls) {
        cls = cls || '';
        var me = this,
            iconEl = me.iconEl,
            oldCls = me.iconCls || '';
            
        me.iconCls = cls;
        if (oldCls != cls) {
            if (iconEl) {
                // Remove the previous iconCls from the button
                iconEl.removeCls(oldCls);
                iconEl.addCls(cls || '');
                me.setComponentCls();
                if (me.didIconStateChange(oldCls, cls)) {
                    me.updateLayout();
                }
            }
            me.fireEvent('iconchange', me, oldCls, cls);
        }
        return me;
    },
    
    didIconStateChange: function(old, current) {
        var currentEmpty = Ext.isEmpty(current);
        return Ext.isEmpty(old) ? !currentEmpty : currentEmpty;
    },


    // @private
    onDownKey: function() {
        var me = this;
    }

});