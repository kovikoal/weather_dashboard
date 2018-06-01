weather_dashboard
==============

Application, which displays weather for the list of cities (any can be added) and actual currencies.
Also shows user's IP-address, last request time and total connections number (which stored local mongoDB).

Workflow
========

To compile the entire project, run "mvn package".
This will create .WAR file, which can be deployed to the application server (tested for Tomcat 8.5),
and then accessed (the way depends on your server config).

Application log is created in home/dashboard/ directory by default, which can be changed in log4j.properties file.


Used API
-------------------------

- For weather information used http://openweathermap.org free subscription type.
- For currency information used http://www.apilayer.net, also free subscription type.

You can upgrade this app by using premium types of API, cause they're giving more information;

Author
-------------------

This application is made by Victor Kopyl and is free for use.
For any question, feel free to contact me by e-mail - madscientistfromhell@gmail.com
