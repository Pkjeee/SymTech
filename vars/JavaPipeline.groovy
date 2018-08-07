/*************************************************************************
**** Description :: This groovy code is used to run the ROR pipeline  ****
**** Created By  :: DevOps Team                                       ****
**** Created On  :: 07-Aug-2018                                       ****
**** version     :: 1.0                                               ****
**************************************************************************/
import com.sym.devops.scm.*
import com.sym.devops.build.maven.*
import com.sym.devops.report.*

def call(body) 
{
   def config = [:]
   body.resolveStrategy = Closure.DELEGATE_FIRST
   body.delegate = config
   body()
   timestamps {
     try {
       def ruby = new MavenBuild()
       ruby.createReportDirectory("${config.REPORT_DIRECTORY}")
       def html = new htmlReport()
       currentBuild.result = "SUCCESS"
       NEXT_STAGE = "none"
       branch_name = new ChoiceParameterDefinition('BRANCH', ['master','staging'] as String[],'')
       value = input(message: 'Please select specified inputs', parameters: [branch_name])
        if(value == 'master') {
               BRANCH = 'master'
        }
	if(value == 'staging') {
	       BRANCH = 'staging'
	}
       stage ('\u2776 Code Checkout') {
          def git = new git()
          git.Checkout("${config.GIT_URL}","${BRANCH}","${config.GIT_CREDENTIALS}")
          NEXT_STAGE="security"
       }
       stage ('\u2777 Pre-Build Tasks') {
           parallel (
	   "\u2460 Security Scan" : {
              while (NEXT_STAGE != "security") {
           	continue
           }
           ruby.scanSecurityVulnerabilities("${config.BRAKEMAN_REPORT_FILE}","${config.REPORT_DIRECTORY}")
           html.publishHtmlReport("${config.BRAKEMAN_REPORT_FILE}","${config.REPORT_DIRECTORY}","${config.BRAKEMAN_REPORT_TITLE}")
         },
       failFast: true
       )
      }
     }
    catch (Exception caughtError) {
        wrap([$class: 'AnsiColorBuildWrapper']) {
            print "\u001B[41mERROR => GIT Checkout via pipeline failed, check detailed logs..."
            currentBuild.result = "FAILURE"
            throw caughtError
        }
     }
  }
}
