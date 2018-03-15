@Grab('org.apache.ivy:ivy:2.3.0')
@Grab('com.aestasit.infrastructure.sshoogr:sshoogr:0.9.26')
import static com.aestasit.infrastructure.ssh.DefaultSsh.*
import  ratpack.groovy.template.MarkupTemplateModule
import static groovy.json.JsonOutput.toJson
import static ratpack.groovy.Groovy.groovyMarkupTemplate
import static ratpack.groovy.Groovy.ratpack
//import  com.jcraft.jsch.*





ratpack {
    bindings {
        module MarkupTemplateModule
    }



    handlers {
        get {
            render groovyMarkupTemplate("index.gtpl", title: "My Ratpack App")
            files { dir "public" }
        }
        get('test') {
            def reqCommand = request.queryParams.cmd
            def reqHost = request.queryParams.rhost
            def cmdResponse
            remoteSession{
                host = 'localhost'
                username = 'vagrant'
                password = 'vagrant'
                port = 2222
                trustUnknownHosts = true
                def result = exec(command: reqCommand, failOnError: false, showOutput: false)
                if (result.exitStatus == 1) {
                    result.output.eachLine { line ->

                        if (line.contains('WARNING')) {
                            throw new RuntimeException("Warning!!!")
                        }
                    }
                } else {
                    cmdResponse = result.output.trim()
                }
            }
//
                def jsonresponse = new sendBack(
                        senthost: reqHost,
                        cmdresponse: cmdResponse
                )

                render toJson(jsonresponse)

            }

        }

    }


class sendBack {
  String senthost
  String cmdresponse
}