modules = {

    dialog {
        dependsOn 'dialog-jquery'

        resource url: [plugin: 'jquery-dialog', dir:'js', file:'dialog.js']
        resource url: [plugin: 'jquery-dialog', dir:'css', file:'dialog.css']
    }

    'dialog-jquery' {
        resource url: [plugin: 'jquery-dialog', dir:'ext/jquery', file: 'jquery-1.12.4.js']
    }

	'dialog-ckeditor' {
		dependsOn 'dialog'
		resource url: [plugin: 'jquery-dialog', dir: 'ext/ckeditor', file: 'ckeditor.js', linkOverride: "ext/ckeditor"]
		resource id: 'ckeditor', url: [plugin: 'jquery-dialog', dir: 'js', file: 'dialog.ckeditor.js']
	}

	'dialog-codemirror' {
		dependsOn 'dialog'
		resource url: [plugin: 'jquery-dialog', dir:'css',file:'dialog.codemirror.css']
		resource url: [plugin: 'jquery-dialog', dir:'ext/codemirror/lib/',file:'codemirror.css']
		resource url: [plugin: 'jquery-dialog', dir:'ext/codemirror/lib/',file:'codemirror.js']

		resource url: [plugin: 'jquery-dialog', dir:'ext/codemirror/addon/runmode/',file:'runmode.js']
		resource url: [plugin: 'jquery-dialog', dir:'ext/codemirror/addon/runmode/',file:'colorize.js']
		resource url: [plugin: 'jquery-dialog', dir:'ext/codemirror/mode/xml/',file:'xml.js']
		resource url: [plugin: 'jquery-dialog', dir:'ext/codemirror/mode/javascript/',file:'javascript.js']
		resource url: [plugin: 'jquery-dialog', dir:'ext/codemirror/mode/css/',file:'css.js']
		resource url: [plugin: 'jquery-dialog', dir:'ext/codemirror/mode/htmlmixed/',file:'htmlmixed.js']

		resource url: [plugin: 'jquery-dialog', dir:'ext/codemirror/mode/groovy/',file:'groovy.js']
		resource url: [plugin: 'jquery-dialog', dir:'ext/codemirror/mode/shell/',file:'shell.js']
		resource url: [plugin: 'jquery-dialog', dir:'ext/codemirror/mode/clike/',file:'clike.js']
        resource url: [plugin: 'jquery-dialog', dir:'ext/codemirror/mode/sql/',file:'sql.js']
		resource url: [plugin: 'jquery-dialog', dir:'js',file:'dialog.codemirror.js']

	}

	'dialog-fileuploader' {
		dependsOn 'dialog'
		resource url: [plugin: 'jquery-dialog', dir:'/js/',file:'dialog.uploader.js']
	}

	'dialog-dataTables' {
		dependsOn 'dialog'
		resource url: [plugin: 'jquery-dialog', dir: 'ext/DataTables/css/', file: 'dataTables.bootstrap.css']
		resource url: [plugin: 'jquery-dialog', dir: 'ext/DataTables/css/', file: 'rowReorder.bootstrap.css']
		resource url: [plugin: 'jquery-dialog', dir: 'ext/DataTables/css/', file: 'responsive.bootstrap.css']
		resource url: [plugin: 'jquery-dialog', dir: 'ext/DataTables/js/', file: 'jquery.dataTables.js']
		resource url: [plugin: 'jquery-dialog', dir: 'ext/DataTables/js/', file: 'dataTables.bootstrap.js']
		resource url: [plugin: 'jquery-dialog', dir: 'ext/DataTables/js/', file: 'dataTables.rowReorder.js']
		resource url: [plugin: 'jquery-dialog', dir: 'ext/DataTables/js/', file: 'dataTables.responsive.js']
		resource url: [plugin: 'jquery-dialog', dir: 'ext/DataTables/js/', file: 'responsive.bootstrap.js']
		resource url: [plugin: 'jquery-dialog', dir: '/js/', file: 'dialog.datatables.js']
	}

    'dialog-tree' {
        dependsOn 'dialog-jquery'
        resource url: [plugin: 'jquery-dialog', dir: 'ext/jstree/themes/default', file: 'style.css']
        resource url: [plugin: 'jquery-dialog', dir: 'ext/jstree', file: 'jstree.js']
        resource url: [plugin: 'jquery-dialog', dir: 'js', file: 'dialog.tree.js']
    }

	'dialog-bootstrap' {
		dependsOn 'dialog'
	    resource url: [plugin: 'jquery-dialog', dir:'ext/bootstrap/css', file:'bootstrap.css']
	    resource url: [plugin: 'jquery-dialog', dir:'ext/bootstrap/js', file:'bootstrap.js']
	    resource url: [plugin: 'jquery-dialog', dir:'ext/respondjs', file:'respondjs.min.js']
	}

	'dialog-fontawesome' {
		dependsOn 'dialog'
	    resource url: [plugin: 'jquery-dialog', dir: 'ext/font-awesome/css', file: 'font-awesome.css']
	}

	'dialog-last' {
		dependsOn 'dialog'
		resource url: [plugin: 'jquery-dialog', dir:'/js/',file:'dialog.last.js']
	}

	'dialog-flot' {
		dependsOn 'dialog-jquery'
		resource url: [plugin: 'jquery-dialog', dir:'ext/flot', file:'jquery.flot.js']
	}

    'dialog-x2js' {
		resource url: [plugin: 'jquery-dialog', dir: '/ext/x2js', file: 'xml2json.min.js']
    }

    'dialog-select2' {
        dependsOn 'dialog,dialog-x2js'
        resource url: [plugin: 'jquery-dialog', dir:'/ext/select2/js/',file:'select2.full.min.js']
		resource url: [plugin: 'jquery-dialog', dir:'/ext/select2/js/i18n',file:'nl.js']
		resource url: [plugin: 'jquery-dialog', dir:'/ext/select2/css',file:'select2.min.css']
        resource url: [plugin: 'jquery-dialog', dir:'/js',file:'dialog.select2.js']
    }

    'dialog-autocomplete' {
        dependsOn 'dialog-select2'
    }

    'dialog-altselect' {
		dependsOn 'dialog-select2'
	}

    'dialog-maskedinput' {
		dependsOn 'dialog-jquery'
		resource url: [plugin: 'jquery-dialog', dir: 'ext/maskedinput', file: 'jquery.maskedinput.js']
	}

    'dialog-momentjs' {
        dependsOn 'dialog-jquery'
        resource url: [plugin: 'jquery-dialog', dir: 'ext/momentjs', file: 'moment-with-locales.js']
    }


    'dialog-datepicker' {
        dependsOn 'dialog-datetimepicker'
    }


    'dialog-datetimepicker' {
        dependsOn 'dialog-jquery, dialog-momentjs, dialog-bootstrap, dialog-modernizr, dialog-fontawesome, dialog-maskedinput'
        resource url: [plugin: 'jquery-dialog', dir: 'ext/bootstrap-datetimepicker/css', file: 'bootstrap-datetimepicker.min.css']
        resource url: [plugin: 'jquery-dialog', dir: 'ext/bootstrap-datetimepicker/js', file: 'bootstrap-datetimepicker.min.js']
        resource url: [plugin: 'jquery-dialog', dir: 'js', file: 'dialog-datetimepicker.js']
    }

	'dialog-modernizr' {
		dependsOn 'dialog'
		resource url: [plugin: 'jquery-dialog', dir: 'ext/modernizr', file:'modernizr.js']
	}

	'dialog-angularjs' {
		dependsOn 'dialog-jquery'
		resource url: [plugin: 'jquery-dialog', dir:'ext/angularjs', file:'angular.js']
	}

}
