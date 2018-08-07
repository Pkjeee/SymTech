/*************************************************************************
**** Description :: This groovy code is used to run the ROR pipeline  ****
**** Created By  :: DevOps Team                                       ****
**** Created On  :: 07-Aug-2018                                       ****
**** version     :: 1.0                                               ****
**************************************************************************/
import com.sym.devops.scm.*

def call(body) 
{
   def config = [:]
   body.resolveStrategy = Closure.DELEGATE_FIRST
   body.delegate = config
   body()
   timestamps {
     try {
       stage ('\u2776 Code Checkout') {
       def git = new git()
       git.Checkout("${config.GIT_URL}","${BRANCH}","${config.GIT_CREDENTIALS}")
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
}
