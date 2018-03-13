import  ratpack.groovy.template.MarkupTemplateModule
import static groovy.json.JsonOutput.toJson
import static ratpack.groovy.Groovy.groovyMarkupTemplate
import static ratpack.groovy.Groovy.ratpack
import  com.jcraft.jsch.*





ratpack {
    bindings {
        module MarkupTemplateModule
    }



    handlers {
        get {
            render groovyMarkupTemplate("index.gtpl", title: "My Ratpack App")
        }




        get('test') {
            def reqCommand = request.queryParams.cmd
            def reqHost = request.queryParams.rhost

            java.util.Properties config = new java.util.Properties()
            config.put "StrictHostKeyChecking", "no"

            JSch ssh = new JSch()
            Session sess = ssh.getSession "vagrant", reqHost, 2222

            sess.with {
                setConfig config
                setPassword "vagrant"
                connect()
                Channel chan = openChannel("exec")
                ChannelExec ce = (ChannelExec) chan
                ce.setCommand(reqCommand)
                ce.connect()
                BufferedReader reader = new BufferedReader(new InputStreamReader(ce.getInputStream()));
                String line
                String cmdResponse

                while ((line = reader.readLine()) != null) {
                    cmdResponse = line.toString()
                }

                def jsonresponse = new sendBack(
                        senthost: reqHost,
                        cmdresponse: cmdResponse
                )

                render toJson(jsonresponse)
                ce.disconnect()
                sess.disconnect()
            }







        }
        files { dir "public" }
    }

}
class sendBack {
  String senthost
  String cmdresponse
}