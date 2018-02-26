define("gcodefx/inline_editor",["require","exports","module","ace/line_widgets","ace/editor","ace/virtual_renderer","ace/lib/dom","ace/commands/default_commands"], function(require, exports, module) {
"use strict";

var LineWidgets = require("ace/line_widgets").LineWidgets;
var Editor = require("ace/editor").Editor;
var Renderer = require("ace/virtual_renderer").VirtualRenderer;
var dom = require("ace/lib/dom");


require("ace/commands/default_commands").commands.push({
	name: "openInlineEditor",
	bindKey: "F3",
	exec: function(editor) {
		var row = editor.getCursorPosition().row;
		var t = editor.session.getLine(row);
		var m = t.match(/M198\s*(?:P(\d+)|<([\+\-\.\w]+)>)\s*(?:\([pP][aA][tT][hH],\s*([#\$&'\*\+\,-\.\/\w:;=@\[\]]+)\s*\))?/);
		if (m) {
			var externalFile = stageBase.getExternalSubProgramDirectory();
			externalFile = externalFile.trim().replace(/\\/g, '/') + '/';
			if (m[3]) {
				if (m[3].lastIndexOf('/', 0) === 0) {
					externalFile = m[3];
				} else if (m[3].match(/^.*:\//)) {
					externalFile = m[3];
				} else {
					externalFile += m[3];
				}
			} else {
				if (m[1]) {
					externalFile += 'O' + parseInt(m[1]) + '.dat';
				} else if (m[2]) {
					externalFile += m[2];
					if (m[2].indexOf('.') === -1) {
						externalFile += '.dat';
					}
				} else {
					externalFile = null;
				}
			}
			stageProgram.addEditor(externalFile);
		} else {
			var split = window.env.split;
			var s = editor.session;
			var inlineEditor = new Editor(new Renderer());
			var splitSession = split.$cloneSession(s);

			if (editor.session.lineWidgets && editor.session.lineWidgets[row]) {
				editor.session.lineWidgets[row].destroy();
				return;
			}

			var rowCount = (editor.renderer.getLastVisibleRow() - editor.renderer.getFirstVisibleRow()) / 2;
			var w = {
				row: row, 
				fixedWidth: true,
				el: dom.createElement("div"),
				editor: inlineEditor
			};
			var el = w.el;
			el.appendChild(inlineEditor.container);

			if (!editor.session.widgetManager) {
				editor.session.widgetManager = new LineWidgets(editor.session);
				editor.session.widgetManager.attach(editor);
			}

			var h = rowCount*editor.renderer.layerConfig.lineHeight;
			inlineEditor.container.style.height = h + "px";

			el.style.position = "absolute";
			el.style.zIndex = "4";
			el.style.borderTop = "solid blue 2px";
			el.style.borderBottom = "solid blue 2px";

			inlineEditor.setSession(splitSession);
			editor.session.widgetManager.addLineWidget(w);

			var kb = {
				handleKeyboard:function(_,hashId, keyString) {
					if (hashId === 0 && keyString === "esc") {
						w.destroy();
						return true;
					}
				}
			};

			w.destroy = function() {
				editor.keyBinding.removeKeyboardHandler(kb);
				s.widgetManager.removeLineWidget(w);
			};

			editor.keyBinding.addKeyboardHandler(kb);
			inlineEditor.keyBinding.addKeyboardHandler(kb);
			inlineEditor.setTheme("ace/theme/solarized_light");
			for (var key in editor.commands.commands) {
				inlineEditor.commands.addCommand(editor.commands.commands[key]);
			}
			inlineEditor.resize(true);
			inlineEditor.setOptions(editor.getOptions());
			inlineEditor.completers = editor.completers;

			m = t.match(/(M98|G65|G66)\s*P([0-9]+)/);
			if(m) {
				var f = inlineEditor.find('[O:][0]*' + Number(m[2]), {wrap: true, regExp: true});
				if (f) {
					inlineEditor.scrollToLine(f.start.row, false, true, function(){});
				}
			}
		}
	}
});
});

define("gcodefx/layout",["require","exports","module","ace/lib/dom","ace/lib/event","ace/edit_session","ace/undomanager","ace/virtual_renderer","ace/editor","ace/multi_select","ace/theme/textmate"], function(require, exports, module) {
"use strict";

var dom = require("ace/lib/dom");
var event = require("ace/lib/event");

var EditSession = require("ace/edit_session").EditSession;
var UndoManager = require("ace/undomanager").UndoManager;
var Renderer = require("ace/virtual_renderer").VirtualRenderer;
var Editor = require("ace/editor").Editor;
var MultiSelect = require("ace/multi_select").MultiSelect;

dom.importCssString("\
splitter {\
    border: 1px solid #C6C6D2;\
    width: 0px;\
    cursor: ew-resize;\
    z-index:10}\
splitter:hover {\
    margin-left: -2px;\
    width:3px;\
    border-color: #B5B4E0;\
}\
", "splitEditor");

exports.edit = function(el) {
    if (typeof(el) == "string")
        el = document.getElementById(el);

    var editor = new Editor(new Renderer(el, require("ace/theme/textmate")));

    editor.resize();
    event.addListener(window, "resize", function() {
        editor.resize();
    });
    return editor;
};


var SplitRoot = function(el, theme, position, getSize) {
    el.style.position = position || "relative";
    this.container = el;
    this.getSize = getSize || this.getSize;
    this.resize = this.$resize.bind(this);

    event.addListener(el.ownerDocument.defaultView, "resize", this.resize);
    this.editor = this.createEditor();
};

(function(){
    this.createEditor = function() {
        var el = document.createElement("div");
        el.className = this.$editorCSS;
        el.style.cssText = "position: absolute; top:0px; bottom:0px";
        this.$container.appendChild(el);
        var session = new EditSession("");
        var editor = new Editor(new Renderer(el, this.$theme));

        this.$editors.push(editor);
        editor.setFontSize(this.$fontSize);
        return editor;
    };
    this.$resize = function() {
        var size = this.getSize(this.container);
        this.rect = {
            x: size.left,
            y: size.top,
            w: size.width,
            h: size.height
        };
        this.item.resize(this.rect);
    };
    this.getSize = function(el) {
        return el.getBoundingClientRect();
    };
    this.destroy = function() {
        var win = this.container.ownerDocument.defaultView;
        event.removeListener(win, "resize", this.resize);
    };


}).call(SplitRoot.prototype);



var Split = function(){

};
(function(){
    this.execute = function(options) {
        this.$u.execute(options);
    };

}).call(Split.prototype);



exports.singleLineEditor = function(el) {
    var renderer = new Renderer(el);
    el.style.overflow = "hidden";

    renderer.screenToTextCoordinates = function(x, y) {
        var pos = this.pixelToScreenCoordinates(x, y);
        return this.session.screenToDocumentPosition(
            Math.min(this.session.getScreenLength() - 1, Math.max(pos.row, 0)),
            Math.max(pos.column, 0)
        );
    };

    renderer.$maxLines = 4;

    renderer.setStyle("ace_one-line");
    var editor = new Editor(renderer);
    editor.session.setUndoManager(new UndoManager());

    editor.setShowPrintMargin(false);
    editor.renderer.setShowGutter(false);
    editor.renderer.setHighlightGutterLine(false);
    editor.$mouseHandler.$focusWaitTimout = 0;

    return editor;
};



});

define("gcodefx/util",["require","exports","module","ace/lib/dom","ace/lib/event","ace/edit_session","ace/undomanager","ace/virtual_renderer","ace/editor","ace/multi_select"], function(require, exports, module) {
"use strict";

var dom = require("ace/lib/dom");
var event = require("ace/lib/event");

var EditSession = require("ace/edit_session").EditSession;
var UndoManager = require("ace/undomanager").UndoManager;
var Renderer = require("ace/virtual_renderer").VirtualRenderer;
var Editor = require("ace/editor").Editor;
var MultiSelect = require("ace/multi_select").MultiSelect;

var urlOptions = {}
try {
    window.location.search.slice(1).split(/[&]/).forEach(function(e) {
        var parts = e.split("=");
        urlOptions[decodeURIComponent(parts[0])] = decodeURIComponent(parts[1]);
    });
} catch(e) {
    console.error(e);
}
exports.createEditor = function(el) {
    return new Editor(new Renderer(el));
};

exports.getOption = function(name) {
    if (urlOptions[name])
        return urlOptions[name];
    return localStorage && localStorage.getItem(name);
};

exports.saveOption = function(name, value) {
    if (value == false)
        value = "";
    localStorage && localStorage.setItem(name, value);
};

exports.createSplitEditor = function(el) {
    if (typeof(el) == "string")
        el = document.getElementById(el);

    var e0 = document.createElement("div");
    var s = document.createElement("splitter");
    var e1 = document.createElement("div");
    el.appendChild(e0);
    el.appendChild(e1);
    el.appendChild(s);
    e0.style.position = e1.style.position = s.style.position = "absolute";
    el.style.position = "relative";
    var split = {$container: el};

    split.editor0 = split[0] = new Editor(new Renderer(e0));
    split.editor1 = split[1] = new Editor(new Renderer(e1));
    split.splitter = s;

    s.ratio = 0.5;

    split.resize = function resize(){
        var height = el.parentNode.clientHeight - el.offsetTop;
        var total = el.clientWidth;
        var w1 = total * s.ratio;
        var w2 = total * (1- s.ratio);
        s.style.left = w1 - 1 + "px";
        s.style.height = el.style.height = height + "px";

        var st0 = split[0].container.style;
        var st1 = split[1].container.style;
        st0.width = w1 + "px";
        st1.width = w2 + "px";
        st0.left = 0 + "px";
        st1.left = w1 + "px";

        st0.top = st1.top = "0px";
        st0.height = st1.height = height + "px";

        split[0].resize();
        split[1].resize();
    };

    split.onMouseDown = function(e) {
        var rect = el.getBoundingClientRect();
        var x = e.clientX;
        var y = e.clientY;

        var button = e.button;
        if (button !== 0) {
            return;
        }

        var onMouseMove = function(e) {
            x = e.clientX;
            y = e.clientY;
        };
        var onResizeEnd = function(e) {
            clearInterval(timerId);
        };

        var onResizeInterval = function() {
            s.ratio = (x - rect.left) / rect.width;
            split.resize();
        };

        event.capture(s, onMouseMove, onResizeEnd);
        var timerId = setInterval(onResizeInterval, 40);

        return e.preventDefault();
    };



    event.addListener(s, "mousedown", split.onMouseDown);
    event.addListener(window, "resize", split.resize);
    split.resize();
    return split;
};
exports.stripLeadingComments = function(str) {
    if(str.slice(0,2)=='/*') {
        var j = str.indexOf('*/')+2;
        str = str.substr(j);
    }
    return str.trim() + "\n";
};
function saveOptionFromElement(el, val) {
    if (!el.onchange && !el.onclick)
        return;

    if ("checked" in el) {
        localStorage && localStorage.setItem(el.id, el.checked ? 1 : 0);
    }
    else {
        localStorage && localStorage.setItem(el.id, el.value);
    }
}

exports.bindCheckbox = function(id, callback, noInit) {
    if (typeof id == "string")
        var el = document.getElementById(id);
    else {
        var el = id;
        id = el.id;
    }
    var el = document.getElementById(id);
    
    if (urlOptions[id])
        el.checked = urlOptions[id] == "1";
    else if (localStorage && localStorage.getItem(id))
        el.checked = localStorage.getItem(id) == "1";

    var onCheck = function() {
        callback(!!el.checked);
        saveOptionFromElement(el);
    };
    el.onclick = onCheck;
    noInit || onCheck();
    return el;
};

exports.bindDropdown = function(id, callback, noInit) {
    if (typeof id == "string")
        var el = document.getElementById(id);
    else {
        var el = id;
        id = el.id;
    }
    
    if (urlOptions[id])
        el.value = urlOptions[id];
    else if (localStorage && localStorage.getItem(id))
        el.value = localStorage.getItem(id);

    var onChange = function() {
        callback(el.value);
        saveOptionFromElement(el);
    };

    el.onchange = onChange;
    noInit || onChange();
};

exports.fillDropdown = function(el, values) {
    if (typeof el == "string")
        el = document.getElementById(el);

    dropdown(values).forEach(function(e) {
        el.appendChild(e);
    });
};

function elt(tag, attributes, content) {
    var el = dom.createElement(tag);
    if (typeof content == "string") {
        el.appendChild(document.createTextNode(content));
    } else if (content) {
        content.forEach(function(ch) {
            el.appendChild(ch);
        });
    }

    for (var i in attributes)
        el.setAttribute(i, attributes[i]);
    return el;
}

function optgroup(values) {
    return values.map(function(item) {
        if (typeof item == "string")
            item = {name: item, caption: item};
        return elt("option", {value: item.value || item.name}, item.caption || item.desc);
    });
}

function dropdown(values) {
    if (Array.isArray(values))
        return optgroup(values);

    return Object.keys(values).map(function(i) {
        return elt("optgroup", {"label": i}, optgroup(values[i]));
    });
}


});

define("gcodefx/gcodefx",["require","exports","module","ace/lib/fixoldbrowsers","ace/multi_select","ace/ext/spellcheck","gcodefx/inline_editor","ace/config","ace/lib/dom","ace/lib/net","ace/lib/lang","ace/lib/useragent","ace/lib/event","ace/theme/textmate","ace/edit_session","ace/undomanager","ace/keyboard/hash_handler","ace/virtual_renderer","ace/editor","ace/ext/whitespace","ace/ext/modelist","ace/ext/themelist","gcodefx/layout","gcodefx/util","ace/ext/elastic_tabstops_lite","ace/incremental_search","ace/worker/worker_client","ace/split","ace/keyboard/vim","ace/ext/statusbar","ace/ext/emmet","ace/snippets","ace/ext/language_tools","ace/ext/beautify"], function(require, exports, module) {
"use strict";

require("ace/lib/fixoldbrowsers");

require("ace/multi_select");
require("ace/ext/spellcheck");
require("ace/ext/language_tools");
require("./inline_editor");

var config = require("ace/config");
config.init();
var env = {};

var dom = require("ace/lib/dom");
var net = require("ace/lib/net");
var lang = require("ace/lib/lang");
var useragent = require("ace/lib/useragent");

var event = require("ace/lib/event");
var theme = require("ace/theme/textmate");
var EditSession = require("ace/edit_session").EditSession;
var UndoManager = require("ace/undomanager").UndoManager;

var HashHandler = require("ace/keyboard/hash_handler").HashHandler;

var Renderer = require("ace/virtual_renderer").VirtualRenderer;
var Editor = require("ace/editor").Editor;

var whitespace = require("ace/ext/whitespace");

var modelist = require("ace/ext/modelist");
var themelist = require("ace/ext/themelist");
var layout = require("gcodefx/layout");
var util = require("gcodefx/util");
var saveOption = util.saveOption;
var fillDropdown = util.fillDropdown;
var bindCheckbox = util.bindCheckbox;
var bindDropdown = util.bindDropdown;

var ElasticTabstopsLite = require("ace/ext/elastic_tabstops_lite").ElasticTabstopsLite;

var IncrementalSearch = require("ace/incremental_search").IncrementalSearch;


var workerModule = require("ace/worker/worker_client");
if (location.href.indexOf("noworker") !== -1) {
	workerModule.WorkerClient = workerModule.UIWorkerClient;
}
var container = document.getElementById("editor-container");
var Split = require("ace/split").Split;
var split = new Split(container, theme, 1);
env.editor = split.getEditor(0);
env.editor.session.setUndoManager(new UndoManager());
env.editor.resize(true);
env.editor.setOptions({
	printMargin: false,
	scrollSpeed: 0.05,
	dragDelay: 150,
	newLineMode: "unix",
    enableBasicAutocompletion: true,
    enableSnippets: true
});
split.on("focus", function(editor) {
	env.editor = editor;
	updateUIEditorOptions();
});
env.split = split;
window.env = env;

var consoleHeight = 20;
var consoleEl = dom.createElement("div");
container.parentNode.appendChild(consoleEl);
consoleEl.style.cssText = "position:absolute; bottom:" + -(consoleHeight - 2) + "px; right:0;\
border:1px solid #baf; z-index:100";

var cmdLine = new layout.singleLineEditor(consoleEl);
cmdLine.editor = env.editor;
env.editor.cmdLine = cmdLine;

env.editor.showCommandLine = function(val) {
	this.cmdLine.focus();
	if (typeof val == "string")
		this.cmdLine.setValue(val, 1);
};
env.editor.commands.addCommands([{
	name: "gotoline",
	bindKey: {win: "Ctrl-L", mac: "Command-L"},
	exec: function(editor, line) {
		if (typeof line == "object") {
			var arg = this.name + " " + editor.getCursorPosition().row;
			editor.cmdLine.setValue(arg, 1);
			editor.cmdLine.focus();
			return;
		}
		line = parseInt(line, 10);
		if (!isNaN(line))
			editor.gotoLine(line);
	},
	readOnly: true
}, {
	name: "snippet",
	bindKey: {win: "Alt-C", mac: "Command-Alt-C"},
	exec: function(editor, needle) {
		if (typeof needle == "object") {
			editor.cmdLine.setValue("snippet ", 1);
			editor.cmdLine.focus();
			return;
		}
		var s = snippetManager.getSnippetByName(needle, editor);
		if (s)
			snippetManager.insertSnippet(editor, s.content);
	},
	readOnly: true
}, {
	name: "focusCommandLine",
	bindKey: "shift-esc|ctrl-`",
	exec: function(editor, needle) { editor.cmdLine.focus(); },
	readOnly: true
}, {
	name: "execute",
	bindKey: "ctrl+enter",
	exec: function(editor) {
		try {
			var r = window.eval(editor.getCopyText() || editor.getValue());
		} catch(e) {
			r = e;
		}
		editor.cmdLine.setValue(r + "");
	},
	readOnly: true
}, {
	name: "showKeyboardShortcuts",
	bindKey: {win: "Ctrl-Alt-h", mac: "Command-Alt-h"},
	exec: function(editor) {
		config.loadModule("ace/ext/keybinding_menu", function(module) {
			module.init(editor);
			editor.showKeyboardShortcuts();
		});
	}
}, {
	name: "increaseFontSize",
	bindKey: "Ctrl-=|Ctrl-+",
	exec: function(editor) {
		var size = parseInt(editor.getFontSize(), 10) || 12;
		editor.setFontSize(size + 1);
	}
}, {
	name: "decreaseFontSize",
	bindKey: "Ctrl+-|Ctrl-_",
	exec: function(editor) {
		var size = parseInt(editor.getFontSize(), 10) || 12;
		editor.setFontSize(Math.max(size - 1 || 1));
	}
}, {
	name: "resetFontSize",
	bindKey: "Ctrl+0|Ctrl-Numpad0",
	exec: function(editor) {
		editor.setFontSize(12);
	}
}]);


env.editor.commands.addCommands(whitespace.commands);

cmdLine.commands.bindKeys({
	"Shift-Return|Ctrl-Return|Alt-Return": function(cmdLine) { cmdLine.insert("\n"); },
	"Esc|Shift-Esc": function(cmdLine){ cmdLine.editor.focus(); },
	"Return": function(cmdLine){
		var command = cmdLine.getValue().split(/\s+/);
		var editor = cmdLine.editor;
		editor.commands.exec(command[0], editor, command[1]);
		editor.focus();
	}
});

cmdLine.commands.removeCommands(["find", "gotoline", "findall", "replace", "replaceall"]);

var keybindings = {
	ace: null, // Null = use "default" keymapping
	vim: require("ace/keyboard/vim").handler,
	emacs: "ace/keyboard/emacs",
	custom: new HashHandler({
		"gotoright":      "Tab",
		"indent":         "]",
		"outdent":        "[",
		"gotolinestart":  "^",
		"gotolineend":    "$"
	})
};

function onResize() {
	var top = env.split.$container.parentElement.offsetTop;
	var left = env.split.$container.parentElement.offsetLeft;
	var width = document.documentElement.clientWidth;
	var height = document.documentElement.clientHeight;
	container.style.width = width - left + "px";
	container.style.height = height - top - consoleHeight + "px";
	env.split.resize();

	consoleEl.style.width = width - left + "px";
	cmdLine.resize();
}

window.onresize = onResize;
onResize();
var modeEl = document.getElementById("mode");
var themeEl = document.getElementById("theme");

fillDropdown(modeEl, modelist.modes);
var modesByName = modelist.modesByName;
bindDropdown("mode", function(value) {
	env.split.forEach(function(editor) {
		editor.session.setMode(modesByName[value].mode || modesByName.text.mode);
		editor.session.modeName = value;
	});
});


function updateUIEditorOptions() {
	var editor = env.editor;
	var session = editor.session;

	saveOption(modeEl, session.modeName || "text");
	saveOption(themeEl, editor.getTheme());
}

themelist.themes.forEach(function(x){ x.value = x.theme });
fillDropdown(themeEl, {
	Bright: themelist.themes.filter(function(x){return !x.isDark}),
	Dark: themelist.themes.filter(function(x){return x.isDark})
});

event.addListener(themeEl, "mouseover", function(e){
	themeEl.desiredValue = e.target.value;
	if (!themeEl.$timer)
		themeEl.$timer = setTimeout(themeEl.updateTheme);
});

event.addListener(themeEl, "mouseout", function(e){
	themeEl.desiredValue = null;
	if (!themeEl.$timer)
		themeEl.$timer = setTimeout(themeEl.updateTheme, 20);
});

themeEl.updateTheme = function(){
	env.split.setTheme((themeEl.desiredValue || themeEl.selectedValue));
	themeEl.$timer = null;
};

bindDropdown("theme", function(value) {
	if (!value)
		return;
	env.split.setTheme(value);
	themeEl.selectedValue = value;
});

bindDropdown("keybinding", function(value) {
	env.split.setKeyboardHandler(keybindings[value]);
});

bindDropdown("fontsize", function(value) {
	env.split.setFontSize(value);
});

bindDropdown("split", function(value) {
	var sp = env.split;
	if (value == "none") {
		sp.setSplits(1);
	} else {
		var newEditor = (sp.getSplits() == 1);
		sp.setOrientation(value == "below" ? sp.BELOW : sp.BESIDE);
		sp.setSplits(2);

		if (newEditor) {
			var session = sp.getEditor(0).session;
			var newSession = sp.setSession(session, 1);
			newSession.name = session.name;

			newSession.setMode(modesByName[session.modeName].mode || modesByName.text.mode);
			newSession.modeName = session.modeName;
			sp.getEditor(1).cmdLine = cmdLine;
			for (var key in sp.getEditor(0).commands.commands) {
				sp.getEditor(1).commands.addCommand(sp.getEditor(0).commands.commands[key]);
			}
			sp.getEditor(1).resize(true);
			sp.getEditor(1).setOptions(sp.getEditor(0).getOptions());
			sp.getEditor(1).completers = sp.getEditor(0).completers;
		}
	}
});

bindCheckbox("live_autocompletion", function(checked) {
	env.split.forEach(function(editor) {
		editor.setOption("enableLiveAutocompletion", checked);
	});
});

var StatusBar = require("ace/ext/statusbar").StatusBar;
new StatusBar(env.editor, cmdLine.container);

var beautify = require("ace/ext/beautify");
env.editor.commands.addCommands(beautify.commands);
});
