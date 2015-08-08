/*
 * Grails Dialog plug-in
 * Copyright 2014 Open-T B.V., and individual contributors as indicated
 * by the @author tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License
 * version 3 published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses
 */

/**
 * Generates setup for a dialog app:
 * - [name]Resources.groovy
 * - resources.groovy
 * - index.gsp
 * - main.gsp
 */

includeTargets << grailsScript("_GrailsInit")
includeTargets << grailsScript("_GrailsCreateArtifacts")
includeTargets << grailsScript("_GrailsBootstrap")

target(setupDialogApp: "Set up a dialog-based grails application") {
	depends(checkVersion, parseArguments)

	// AppNameResources.groovy
	def grailsAppTitle=grailsAppName[0].toUpperCase()+grailsAppName.substring(1)
	generateFile "${basedir}/grails-app/conf/${grailsAppTitle}Resources.groovy",
"""
modules = {
    ${grailsAppName} {
        dependsOn 'dialog,dialog-altselect,dialog-dataTables,bootstrap-responsive-css,bootstrap-tooltip,bootstrap-popover,bootstrap-modal,dialog-bootstrap,dialog-autocomplete,dialog-datepicker,dialog-timepicker'
    }
}
"""
	// views/index.gsp

	generateFile "${basedir}/grails-app/views/index.gsp" ,
"""<!doctype html>
<html>
	<head>
		<meta name="layout" content="main"/>
		<title>New Dialog-based Grails application</title>
	</head>
	<body>
		<div id="page-body" role="main" class="body">
			<h1>New Dialog-based Grails application</h1>
			<p>This is the default landing page of a newly created Dialog-based Grails application.</p>
			<p>This page is in grails-app/views/index.gsp .</p>
		</div>
	</body>
</html>
"""

// views/layouts/main.gsp

generateFile "${basedir}/grails-app/views/layouts/main.gsp" ,
"""<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
    <head>
        <title><g:layoutTitle default="${grailsAppTitle}" /></title>
        <r:require modules="${grailsAppName}"/>
		<r:layoutResources/>
		<g:layoutHead />
		<dialog:head />
	</head>
    <body>
   		<div class="navbar navbar-inverse navbar-fixed-top">
			<div class="navbar-inner">
        		<div class="container">
          			<g:render template="/layouts/menu" />
				</div>
			</div>
		</div>
        <div class="container" id="page">
        	<div class="row">
        		<div class="span12" style="margin-top:45px;">
					<div class="row"><div class="span12" id="statusmessage"></div></div>
	        		<g:if test="\${flash.message}">
	        	    	<div class="alert alert-success">
	    					<button type="button" class="close" data-dismiss="alert">×</button>
	    					\${flash.message}
	    				</div>
	   				</g:if>
	        		<g:if test="\${flash.errorMessage}">
	        	    	<div class="alert alert-error">
	    					<button type="button" class="close" data-dismiss="alert">×</button>
	    					\${flash.errorMessage}
	    				</div>
	   				</g:if>
	    			<g:layoutBody />
	    			<r:layoutResources />
                    <dialog:last />
         		</div>
        	</div>
		</div>
    </body>
</html>
"""

    // config.groovy for date format
    def configPath="${basedir}/grails-app/conf/Config.groovy"
    def configText= new File(configPath).text
    if (!configText.contains("grails.databinding.dateFormats")) {
        configText+="""
// Added by setup-dialog-app
grails.databinding.dateFormats = ["yyyy-MM-dd'T'HH:mm:ss"]
        """
        new File(configPath).write(configText)
    }
}

def generateFile (path,text) {
	if (new File(path).exists()) {
		if (!confirmInput("${path} already exists. Overwrite?")) {
			return
		}
	}
	new File(path).write(text)
	grailsConsole.addStatus "File ${path} generated."
}

setDefaultTarget 'setupDialogApp'
