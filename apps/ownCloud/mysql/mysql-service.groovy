/*******************************************************************************
* Copyright (c) 2011 GigaSpaces Technologies Ltd. All rights reserved
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*******************************************************************************/
import groovy.sql.*
import com.mysql.jdbc.Driver

service {
	
	name "mysql"

	icon "mysql.png"
	type "DATABASE"
	elastic true
	numInstances 1
	
	compute {
		template "BIG_MEDIUM"
	}	
	
	lifecycle{
 
		install "mysql_install.groovy"

		start "mysql_start.groovy"
				
		startDetectionTimeoutSecs 900
		startDetection "mysql_startDetection.groovy"
		
		stopDetection {	
			!ServiceUtils.isPortOccupied(jdbcPort)
		}
		
		preStop ([	
			"Win.*":"killAllMysql.bat",		
			"Linux.*":"mysql_stop.groovy"
			])
		shutdown ([			
			"Linux.*":"mysql_uninstall.groovy"
		])
			
			
		locator {	
			//hack to avoid monitoring started processes by cloudify
			  //return  [] as LinkedList	
			 
			def myPids = ServiceUtils.ProcessUtils.getPidsWithQuery("State.Name.re=mysql.*\\.exe|mysqld")
			println ":mysql-service.groovy: current PIDs: ${myPids}"
			return myPids
        }	
	}
	

	userInterface {
		metricGroups = ([
			metricGroup {
				name "server"

				metrics([
					"Server Uptime",
					"Client Connections",
					"Total Queries",
					"Slow Queries",
					"Opens",
					"Current Open Tables",
					"Queries Per Second"
				])
			}
		])

		widgetGroups = ([
			widgetGroup {
           			name "Server Uptime"
            		widgets ([
               		barLineChart{
                  		metric "Server Uptime"
                  		axisYUnit Unit.REGULAR
							},
            		])
						
         }   , 
			widgetGroup {
           			name "Client Connections"
            		widgets ([
               		barLineChart{
                  		metric "Client Connections"
                  		axisYUnit Unit.REGULAR
							},
            		])
						
         }   , 
			widgetGroup {
           			name "Total Queries"
            		widgets ([
               		barLineChart{
                  		metric "Total Queries"
                  		axisYUnit Unit.REGULAR
							},
            		])
						
         }   , 
			widgetGroup {
           			name "Opens"
            		widgets ([
               		barLineChart{
                  		metric "Opens"
                  		axisYUnit Unit.REGULAR
							},
            		])
						
         }   , 
			widgetGroup {
           			name "Current Open Tables"
            		widgets ([
               		barLineChart{
                  		metric "Current Open Tables"
                  		axisYUnit Unit.REGULAR
							},
            		])
						
         }   , 
			widgetGroup {
           			name "Queries Per Second"
            		widgets ([
               		barLineChart{
                  		metric "Queries Per Second"
                  		axisYUnit Unit.REGULAR
							},
            		])
						
         }   , 
		])
	}  
}
