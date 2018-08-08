/*************************************************************************
**** Description :: This groovy code is used to run the sym pipeline ****
**** Created By  :: DevOps Team                                       ****
**** Created On  :: 12/14/2017                                        ****
**** version     :: 1.0                                               ****
**************************************************************************/
import com.sym.devops.scm.*
import com.sym.devops.build.maven.*
import com.sym.devops.sonar.*
//import com.sym.devops.sonar.npm.*
//import com.sym.devops.deploy.*
//import com.sym.devops.notification.*

def call(body) 
{
   def config = [:]
   body.resolveStrategy = Closure.DELEGATE_FIRST
   body.delegate = config
   body()
   timestamps {
     try {
        currentBuild.result = "SUCCESS"
        NEXT_STAGE = "none"
          BRANCH = 'master'

          stage ('\u2776 Code Checkout') {
          def g = new git1()
          g.Checkout("${config.GIT_URL}","${BRANCH}","${config.GIT_CREDENTIALS}")
          NEXT_STAGE='code_scanning'
        }
        stage ('\u2777 Pre-Build Tasks') {
           parallel (
             "\u2460 Code Scanning" : {
                while(NEXT_STAGE != 'code_scanning') {
                  continue
                }    
                def m = new maven()
	            m.mavenBuild()
             },
             failFast: true
           )
	    }
       stage ('\u2778 Build Tasks') {
           parallel (
             "\u2461 Code Analysis" : {
                while(NEXT_STAGE != 'code_analysis') {
                  continue
                }
                def g = new JavaJSAnalysis()
	            g.javaJSSonarAnalysis("${config.SONAR_PROPERTY}")
             },
             failFast: true
           )
	 }
       }
     catch (Exception caughtError) {
        wrap([$class: 'AnsiColorBuildWrapper']) {
            print "\u001B[41mERROR => sym pipeline failed, check detailed logs..."
            currentBuild.result = "FAILURE"
            throw caughtError
        }
     }
   }
}

