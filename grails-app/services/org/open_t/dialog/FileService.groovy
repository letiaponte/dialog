/*
* Grails Dialog plug-in
* Copyright 2013 Open-T B.V., and individual contributors as indicated
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
package org.open_t.dialog

import java.text.SimpleDateFormat

import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.FileFileFilter
import org.apache.commons.io.IOUtils

class FileService {

	static transactional = false

	def grailsApplication
    def g = new org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib()

    /**
     * upload file in response to the dialog:upload tag
     *
     * @param request request as provided to from the controller
     * @param params params as provided to the controller
     * @fileCategory The file category, default is "images"
     * @dc The domain class or String representing the domain class
     * @return Map to be rendered as JSON
     */
    def uploadFile(request,params,fileCategory="images",dc=null) {
        def mimetype
        def is

        def filename=java.net.URLDecoder.decode(request.getHeader("X-File-Name")?:"unknown-file-name.bin", "UTF-8");

        is =request.getInputStream()
        mimetype=request.getHeader("Content-Type")

        log.debug "MIMETYPE: ${mimetype}"

        def tempFile=File.createTempFile("upload", "bin");
        OutputStream os=new FileOutputStream(tempFile)
        IOUtils.copy(is,os)
        os.flush()

		is.close()
		os.close()

        def isDirect=(params.direct==true || params.direct=="true")
        if (isDirect && dc!=null && (params.identifier!=null && params.identifier!="null" && params.identifier!="undefined")) {
            def diPath=filePath(dc,params.identifier,fileCategory)
            def destFile= new File("${diPath}/${filename}")
            FileUtils.copyFile(tempFile,destFile)
            tempFile.delete()
        }

        def res=[path:tempFile.absolutePath,name:tempFile.name,success:true,mimetype:mimetype,identifier:params.identifier,sFileName:params.sFileName,message:"Upload completed"]
        return res
    }

    /*
     * Pack a value and convert it to a 8-byte 36-based formatted number
     * @param n The value
     * @return The formatted String
     */
	def pack(n) {
        if (!n || n=="null") {
            n=0
        }
		String s= Long.toString(new Long(n),36)
		return String.format("%1\$8s", s).replace(' ', '0')
	}

    /**
     * Create a Packed path from a value
     * This is a short path that is used to create a balanced folder structure to store files that are attached to domain objects
     * The path has the form xx/xx/xx/xx
     * This is sufficient for 2.8T objects
     *
     * @param n The Value
     * @return the Packed path
     */
	def packedPath(n) {
		String s=pack(n)
		return s.substring(0,2)+"/"+s.substring(2,4)+"/"+s.substring(4,6)+"/"+s.substring(6,8)
	}

	// TODO offer possibility to provide alternate location per category.

    /**
     * This calculates the relative path on the filesystem for the files of a domain object
     *
     * @param dc The domain class or a String representing the domain class. A string is allowed so there is no need to have access to the actual domain class
     * @param id The id of the domain object
     * @param fileCategory The file category. This is a string allowing a top-level tree split which is helpful if permissions of categories are different
     * @return The path
     */
	def relativePath(dc,id,fileCategory) {
        def name
        def hasFolderPathMethod=false
        if (dc.class==java.lang.String) {
            name=dc
        } else {
            name=dc.getName()
            hasFolderPathMethod = dc.methods.collect { method -> method.name }.contains("getFolderPath")
        }

		name=name.replaceAll (".*\\.", "")

		if (hasFolderPathMethod) {
			def dcInstance=dc.get(id)
			return dcInstance.getFolderPath(fileCategory)
		} else {
			return "${fileCategory}/${name}/${packedPath(id)}"
		}
	}

    /**
     * This calculates the full path on the filesystem for the files of a domain object
     *
     * @param dc The domain class or a String representing the domain class. A string is allowed so there is no need to have access to the actual domain class
     * @param id The id of the domain object
     * @param fileCategory The file category. This is a string allowing a top-level tree split which is helpful if permissions of categories are different
     * @return The path
     */
	def filePath(dc,id,fileCategory) {
		def basePath=grailsApplication.config.dialog.files.basePath
		return "${basePath}/${relativePath(dc,id,fileCategory)}"
	}

    /**
     * This calculates the full URL for the files of a domain object
     *
     * @param dc The domain class or a String representing the domain class. A string is allowed so there is no need to have access to the actual domain class
     * @param id The id of the domain object
     * @param fileCategory The file category. This is a string allowing a top-level tree split which is helpful if permissions of categories are different
     * @return The URL
     */
	def fileUrl(dc,id,fileCategory) {
		def baseUrl=grailsApplication.config.dialog.files.baseUrl
        def name = dc.class==java.lang.String ? dc : dc.getName()
		name=name.replaceAll (".*\\.", "")
		return "${baseUrl}/${fileCategory}/${name}/${packedPath(id)}"
	}

    /**
     * Return ready-to-be-rendered-as-JSON file list
     * This is used to provide a list of files attached to a domain object
     * The intended use is to place a construct like:
     * def filelist() {
     *   render fileService.filelist(DomainClass,params) as JSON
     * }
     * in the associated controller.
     */
	def filelist(dc,params,fileCategory="images",linkType="external",actions=null) {
		def format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",new Locale('nl'))
        log.debug "PARAMS: ${params}"
		def diUrl=fileUrl(dc,params.id,fileCategory)

		def data=[:]
		Integer recordsTotal=0
		if(params.id&& params.id!="null") {
			File dir = new File(filePath(dc,params.id,fileCategory))
			data=dir.listFiles().collect { file ->
                def downloadLink
                if (linkType=="external") {
                    downloadLink="${diUrl}/${file.name}"
                } else {
                    log.debug "params: ${params}"
                    downloadLink=g.createLink(action:"streamfile",id:params.id,params:[filename:file.name])
                }

                if(!actions) {
        			actions= { aParams,aFile ->
                        def actionsString="""<div class="btn-group">"""
                        def actionsParameter=aParams.actions?:"none"
                        def actionsList=actionsParameter.split(',')
                        // TODO add other actions ('show','edit')
                        if (actionsList.contains("delete")) {
                            actionsString +="""<a class="btn btn-default btn-sm" href="#" onclick="dialog.deleteFile(${aParams.id},'${aParams.controller}','${aFile.name}',null)" title="delete"><i class="fa fa-trash"></a>"""
                        }
                        actionsString+="</div>"
                        return actionsString
                    }
        		}

				[0:"""<a href="${downloadLink}" target="_blank">${file.name}</a>""",
				 1:file.length(),
				 2:format.format(file.lastModified()),
                 3: actions(params,file)]
			}.sort { file -> file[new Integer(params."order[0][column]")] }

			if (params."order[0][dir]"=="desc") {
				data=data.reverse()
			}
			recordsTotal=data.size()

			if (recordsTotal>0) {

				Integer firstResult = params.start ? new Integer(params.start) : 0
				Integer maxResults = params.length ? new Integer(params.length) : 10

				// pagination
				if (firstResult > recordsTotal) {
					firstResult = recordsTotal
				}
				if ((firstResult + maxResults) > recordsTotal) {
					maxResults = recordsTotal - firstResult
				}
				data = data[firstResult..firstResult + maxResults - 1]
			}

		}
		def json = [draw:params.draw,recordsTotal:recordsTotal,recordsFiltered:recordsTotal,data:data]
	}

    /**
     * File map for CKEditor
     *
     * @param dc The domain class
     * @param params The parameters as provided to the controller
     * @param fileCategory The file category, default is "images"
     * @return Map to be rendered as JSON
     */
	def filemap(dc,params,fileCategory="images",linkType="external") {
		def diUrl=fileUrl(dc,params.id,fileCategory)
		def diPath=filePath(dc,params.id,fileCategory)
		File dir = new File(diPath)

		def map = dir.listFiles().collect { file ->
            if (linkType=="external") {
    			[file:file,url:"${diUrl}/${file.name}"]
            } else {
                    [file:file,url:g.createLink(action:"streamfile",id:params.id,params:[filename:file.name])]
            }
		}
		return map
	}

    /**
     * Provide image list for tinyMCE
     *
     * @param dc The domain class
     * @param params The parameters as provided to the controller
     * @param fileCategory The file category, default is "images"
     * @return text for tinyMCE
     */
	def imagelist(dc,params,fileCategory="images") {
		def diUrl=fileUrl(dc,params.id,fileCategory)
		def diPath=filePath(dc,params.id,fileCategory)
		File dir = new File(diPath)

		String text="var tinyMCEImageList = new Array("
		dir.eachFile { file ->
			text+="""\n["${file.name}", "${diUrl}/${file.name}"],"""
		}
		if (text[text.length()-1]==",") {
			text=text.substring(0,text.length()-1)
		}
		text+=");"
		return text
	}

    /**
     * Provide media list for tinyMCE
     *
     * @param dc The domain class
     * @param params The parameters as provided to the controller
     * @param fileCategory The file category, default is "images"
     * @return text for tinyMCE
     */
	def medialist(dc,params,fileCategory="media") {
		def diUrl=fileUrl(dc,params.id,fileCategory)
		def diPath=filePath(dc,params.id,fileCategory)
		File dir = new File(diPath)

		String text="var tinyMCEMediaList = new Array("
		dir.eachFile { file ->
			text+="""\n["${file.name}", "${diUrl}/${file.name}"],"""
		}
		if (text[text.length()-1]==",") {
			text=text.substring(0,text.length()-1)
		}
		text+=");"
		return text
	}

    /**
     * Move uploaded file to domain object folder
     *
     * @param dc The domain class
     * @param id the id of the domain object
     * @param fileupload Information on the uploaded file separated by |
     * @param fileCategory The file category, default is "images"
     */

	def submitFile(dc,id,fileupload,fileCategory="images") {

		def fileInfo=fileupload.split("\\|")

		// create folder structure
		def diPath=filePath(dc,id,fileCategory)
		new File(diPath).mkdirs()
		// upload the file
		File file=new File(fileInfo[1])
		if (file.exists()) {
			def destFile= new File("${diPath}/${fileInfo[0]}")
			FileUtils.copyFile(file,destFile)
			file.delete()
		}
	}

    /**
     * Move uploaded files to domain object folder
     * This allows files to be attached to the original submission of a form after the domain object is created
     *
     * @param dc The domain class
     * @param params the controller params
     * @param fileupload Information on the uploaded file separated by |
     * @param fileCategory The file category, default is "images"
     */
	def submitFiles(dc,params,fileCategory="images") {
		if (params.fileupload) {

			if (params.fileupload.class.name=="java.lang.String") {
				submitFile(dc,params.id,params.fileupload,fileCategory)
			} else {
				params.fileupload.each { fileupload ->
					submitFile(dc,params.id,fileupload,fileCategory)
				}
			}
		}
	}

    /**
     * Delete a file form a domain object's folder
     *
     * @param dc The domain class
     * @param params The parameters as provided to the controller
     * @param fileCategory The file category, default is "images"
     *
     * @return result map
     */
	def deleteFile(dc,params,fileCategory="images") {
		File file = new File(filePath(dc,params.id,fileCategory)+"/"+params.filename);
		Boolean success= file.delete()
		def result=[success:success,mesage:"${params.filename} deleted"]
		return [result:result]
	}

	/**
	 * Stream any given file over an HTTP response as an octet-stream.
	 *
	 * @param file The file to stream.
	 * @param response The HTTP response to write the file to.
	 * @since 09/01/2016
	 */
	def stream(def file, def name, def response, def contentType = "application/pdf") {
		response.setHeader("Content-Disposition", "inline; filename=\"${name}\"")
		response.setHeader("Content-Type", contentType)

        // Check if file is present and readable
        if (file && file.canRead()) {
    		def inputStream = new FileInputStream(file)
    		def bufsize = 100000
    		byte[] bytes = new byte[(int) bufsize]

    		def offset = 0
    		def len = 1
    		while (len > 0) {
    			len = inputStream.read(bytes, 0, bufsize)
    			if (len > 0)
    			response.outputStream.write(bytes, 0, len)
    			offset += bufsize
    		}

    		try {
    			response.outputStream.flush()
    		} catch (Exception e) {
    			/* Do nothing... */
    		}
        }
	}

    /**
	 * Stream a file to the outputstream of a response.
	 *
	 * @param dc The domain class.
     * @param params The parameters as provided to the controller.
     * @param fileCategory The file category.
     * @param name The name of the file.
     * @param response The response object as provided to the controller.
	 */
	def streamFile(def dc, def id, def fileCategory, def name, def response) {
        def filePath = filePath(dc, id, fileCategory) + "/${name}"
        def file = new File(filePath)
		stream(file, file.name, response)
	}

    /**
	 * Copy files from one domain object to another
	 *
	 * @param dc The domain class
     * @param fileCategory The file category,default is "images"
     * @param fromId The id of the source domain objetc
     * @param toId The id of the target domain object
	 */
    def copyFiles(dc=null, fileCategory="images", fromId, toId) {
        if( (fromId != null) && (toId != null) ) {
            File fromDir = new File(filePath(dc,fromId,fileCategory))
            File toDir = new File(filePath(dc,toId,fileCategory))
            FileUtils.copyDirectory(fromDir,toDir,FileFileFilter.FILE)
        }
    }

	/**
	 * Fetch a file input stream from a fileupload that was generated by JS.
	 *
	 * @param dc The domain class type.
	 * @param fileupload The information that was given by the JS.
	 * @return A FileInputStream instance if there was a file found at the fileupload
	 * location. Null if otherwise.
	 * @since 08/30/2016
	 */
	def fetchFileStream(def dc, def fileupload) {
		def info = fileupload.split("\\|")
		def file = new File(info[1])
		if (file.exists()) {
			return new FileInputStream(file)
		}

		return null
	}
}
