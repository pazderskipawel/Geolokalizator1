# Geolokalizator1 (java version)
Fetch current location and save it to internal db
#### First panel shows your current location 
- fetching location using FusedLocationProvider
- showing on a map by MapFragment (API KEY needed)
- 2 buttons allowing to start and stop ForegroundService
#### ForegroundService
- gets location every minute
- store it in room database
#### Second panel shows locations stored in database
- you can also switch between different dates
#### Third panel shows author  
- also have button that allows to delete stored data
