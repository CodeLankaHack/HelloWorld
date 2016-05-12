# HelloWorld

Heavy Equipment Related Performance Calculation with a Tracking Module 

The main idea behind machine/vehicle tracking is observing and analyzing the real time location updates, previous route details etc. But when it comes to machines/vehicles in building and construction industry this scenario is not always practically applicable because many machines/vehicles operate without moving. So this will be a solution to overcome that problem. This module consists of
•	Real time GPS tracking
•	Machine state monitoring with sensors
 

Proposed tracking device is consists of 2 components to acquire the machine’s state and location updates. Arduino based vibration sensor is capable of identifying the machine state by considering the vibration factor. GPS tracker is capable of getting all the location updates using latitudes and longitudes of the machinery vehicle. The reason for choosing vibration sensor is to track the machine state of vehicles which are not moving when doing a task. There is a mobile application made for the same task to track and monitor the site movement of field officers and machinery vehicles. Mobile app does not have the facility to get the vibration factor.

The details gathered for tracker device can be useful for many purposes. Back office can check the vehicles location real-time, can analyze past location updates etc.

The details gathered from the tracking device can be used to calculate the availability and efficiency of the machine. To calculate the efficiency of the machine this module uses samples from Overall Equipment Effectiveness theory. When applying Overall Equipment Effectiveness to heavy machinery there are some special factors to be consider because OEE is not directly meant for construction machine monitoring. To measure Availability is a major metrics and it is totally based on time. The output getting from the tracking device can be directly used as valued for the availability factor. There is a special data entry also exists to insert the machine availability and downtimes to the system. By getting all the required values system is able to predict the 
Equipment Utilization
Process Equipment Utilization
Potential Equipment Utilization
Lost Capasity 
and finally the overall availability of the machine.
