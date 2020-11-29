package io.github.kmakma.adventofcode.utils

// TODO
//  1. collect year controllers
//      * 2015..currentYear
//      * omit last not implemented years
//      * have a dummy year controller for not implemented years
//  2. create version string
//      * bit string/array/list
//      * bit = one task of a day, 1 = solved, 0 = not solved
//  3. compress version string
//      * remember positions of switches from 0/1 to 1/0 and the last position
//      * convert to Base36 (https://en.wikipedia.org/wiki/Base36)
