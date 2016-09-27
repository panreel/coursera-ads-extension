# coursera-ads-extension

Project extension for the course [Advanced Data Structures](https://www.coursera.org/learn/advanced-data-structures "Advanced Data Structures") on Cousera.

## Extension Description

In real life scenarios people use maps to find the best route between two points on a map not only from a distance perspective (i.e. finding the shortest route, minimizing the distance) but also try to avoid traffic jams while driving/moving from a point A to a point B. This is the goal of this extension: detect the jams in the route between two locations and try to avoid them while minimizing the distance.

### Output

![alt tag](https://github.com/panreel/coursera-ads-extension/blob/master/ExamOutput/AStartTrafficVisualization.png)

## Tech implementation

Extended the basic A-Star visiting algorithm by including also traffic information. Created a module which calls some REST APIs (specifically Bing Traffic API) to get information about incidents in the selected area (by calculating the bounding box of the intersections found in the loaded map) and prioritizing in the selection queue the roads which are not in a jammed zone. A jammed zone is calculated from the "severity" value, returned by the Traffic API when an incident is found. The "severity" value is used to calculate how large an incident area is and how far we should move to skip that area in our paths.

## Setting up the environment

### 1. Import project in Eclipse

Following the [Getting Started Guide](https://msdn.microsoft.com/en-us/library/office/dn707383.aspx#sectionSection1 "Getting Started Guide"). To speed up the operation, have a look at [EGit](http://eclipse.github.io/ "EGit").

### 2. Get and Setup your Bing Traffic API key in configuration file

Get a [Bing Maps API key](https://msdn.microsoft.com/en-us/library/ff428642.aspx "Bing Maps API key"). Setup the `app_code` in the `.\week5_apiheuristic\api_configs\BingTraffic.config.json` file with your Bing Maps API code.

### 2. Run MapApp.java file
Enjoy!
