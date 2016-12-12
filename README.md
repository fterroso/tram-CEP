# tram-CEP
##Getting Started
This program is able to detect the itineraries covered by the users of a tram service by using the records of their transit cards. By following the trip-chaining method implemented with Complex Event Processing (CEP) techniques, tram-CEP can make up the origin and destination stations of the users of a tram service. 

Due to its CEP implementation, the system has been designed to operate in real time. However, in its current version only takes the card records from a csv file. Its adaptation for real-time should be quite easy.

## Requirements
###Library dependencies
* Esper 4.11
* Esperio 4.11
* log4j 1.2.16
* Commons-lang3 v.3.3.2
* Commons-math3 v. 3.3.3
* jDOM
* [METHOD](https://github.com/fterroso/method)

## Bibliography
For more information and reference purposes please use the following research paper:

Terroso-Saenz, F., Valdes-Vela, M., & Skarmeta-Gomez, A. F. (2015, September). Tram-Based Mobility Mining with Event Processing of Transit-Card Transactions. In 2015 IEEE 18th International Conference on Intelligent Transportation Systems (pp. 1934-1939). IEEE.
