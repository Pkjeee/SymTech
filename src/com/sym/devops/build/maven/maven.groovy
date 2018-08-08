/************************************************************
***** Description :: This Package is used for NPM Build *****
***** Author      :: Prmaod Vishwakarma                 *****
***** Date        :: 08/08/2017                         *****
***** Revision    :: 1.0                                *****
************************************************************/

package com.sym.devops.build.maven

/********************************************
** Function to Mavaen Build 
*********************************************/
def mavenBuild()
{
   try {
      wrap([$class: 'AnsiColorBuildWrapper']) {
	    println "\u001B[32mINFO => Building NPM modules, please wait..."
		sh """
		   mvn clean install
		"""
	  }
   }
   catch (Exception caughtException) {
      wrap([$class: 'AnsiColorBuildWrapper']) {
         println "\u001B[41mERROR => failed to install NPM modules..."
		 currentBuild.result = 'FAILED'
         throw caughtException
      }
   }
}
